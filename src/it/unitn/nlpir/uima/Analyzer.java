package it.unitn.nlpir.uima;

import it.unitn.nlpir.types.AnnotatorRun;
import it.unitn.nlpir.types.DocumentId;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.factory.JCasFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.util.JCasUtil;

/**
 * 
 * Class for programmatically executing a UIMA pipeline
 * 
 */
public class Analyzer {
	private static Logger logger = LoggerFactory.getLogger(Analyzer.class);

	private AnalysisEngineList aes;
	private UIMAPersistence persistence;
	private Set<String> disabledAnalysisEngines;
	private Set<String> forcedToExecuteAnalysisEngines;

	private boolean redoSerialization = false;
	
	
	protected boolean storeAnnotations = true;
	
	// More concise constructor that uses UIMANoPersistence by default.
	public Analyzer(AnalysisEngineList aes) {
		this(aes, new UIMANoPersistence());
	}

	public Analyzer(AnalysisEngineList aes, UIMAPersistence persistence) {
		this(aes, persistence, true);
	}
	
	public Analyzer(AnalysisEngineList aes, UIMAPersistence persistence, boolean storeAnnotations) {
		this.aes = aes;
		this.persistence = persistence;
		this.disabledAnalysisEngines = new HashSet<>();
		this.forcedToExecuteAnalysisEngines = aes.forceExecution;
		this.storeAnnotations = storeAnnotations;
		logger.info("Store annotations: "+this.storeAnnotations);
	}

	public JCas getNewJCas() {
		JCas cas = null;
		try {
			String[] typeSystems = this.aes.getTypeSystemsForCas();
			if (typeSystems.length == 0) {
				return JCasFactory.createJCas();
				/*throw new IllegalStateException(
						"No typesystem specified in the Analysis Engine list.");*/
			}
			//System.out.println("TYPE SYSTEMS: "+typeSystems[0]);
			try {
			
				cas = JCasFactory.createJCas(typeSystems);
			}
			catch (Exception e) {
				//e.printStackTrace();
			
				cas = JCasFactory.createJCasFromPath(typeSystems);
			}
		} catch (UIMAException e) {
			e.printStackTrace();
		}
		return cas;
	}

	public void disableAnalysisEngine(String name) {
		this.disabledAnalysisEngines.add(name);
		logger.debug("Disabled "+name);
		this.removeForcedExecutionOfAnalysisEngine(name);
	}

	public void enableAnalysisEngine(String name) {
		if (this.disabledAnalysisEngines.contains(name)) {
			this.disabledAnalysisEngines.remove(name);
		}
	}

	public void enableAllAnalysisEngine() {
		logger.debug("Enabled all engines");
		this.disabledAnalysisEngines.clear();
	}

	public boolean isAnalysisEngineEnabled(String name) {
		return !this.disabledAnalysisEngines.contains(name);
	}

	public void forceExecutionOfAnalysisEngine(String name) {
		this.forcedToExecuteAnalysisEngines.add(name);
		this.enableAnalysisEngine(name);
	}

	public void removeForcedExecutionOfAnalysisEngine(String name) {
		if (this.forcedToExecuteAnalysisEngines.contains(name)) {
			this.forcedToExecuteAnalysisEngines.remove(name);
		}
	}

	private void runAnalysisPipeline(JCas cas) {
		// Remove the tag associated to annotators which are forced to be
		// executed
		for (String analysisEngineName : this.forcedToExecuteAnalysisEngines) {
			logger.debug("Substituting the annotations of: "+analysisEngineName);
			UIMAUtil.removeAnnotatorRunAnnotation(cas, analysisEngineName);
			this.enableAnalysisEngine(analysisEngineName);
		}

		// Collect the annotators previously run on the cas
		Set<String> annotatorsRun = collectAnnotatorsRunByName(cas);

		
		for (AnalysisEngine ae : this.aes) {
			// If the AE was not previously run and is enabled to perform
			// annotation
			String aeName = ae.getMetaData().getName();
			logger.debug("Running "+aeName);
			
			String annotatorHash = UIMAUtil.getAnalysisEngineDescriptionHash(ae);

			/**
			 * New case:
			 * - if an annotator is rerun, every annotators using its output should be rerun and so on
			 * - possible strategy: for each annotator run we get the names of the features in output,
			 *   then each annotator taking that feature in output should be rerun d491a02b1cce97a91f8aee261ed6fe67ef6fc37f b5fe7cdb467a1bbc41ee5a5d7920c890fa458cb9
			 */
			
			//if (!annotatorsRun.contains(annotatorHash) && this.isAnalysisEngineEnabled(aeName)) {
			if (((!annotatorsRun.contains(aeName) && this.isAnalysisEngineEnabled(aeName)))||
					(this.forcedToExecuteAnalysisEngines.contains(aeName)&&this.isAnalysisEngineEnabled(aeName))) {
				try {
					// Remove previous outputs of this annotator if present it.unitn.limosine.bart.BARTPipelineTypes
					if ((!aeName.contains("Stanford"))&&(!aeName.contains("BART"))){	//TEMPORARY - to remove
						UIMAUtil.removeOutputs(cas, ae);
						UIMAUtil.removeAnnotatorRunAnnotation(cas, aeName);
					}

					// Run the annotator
					logger.debug("Running {}", aeName);
					SimplePipeline.runPipeline(cas, ae);

					// Add annotator run tag in the cas
					// UIMAUtil.addAnnotatorRunAnnotation(cas, aeName);
					UIMAUtil.addAnnotatorRunAnnotation(cas, aeName, annotatorHash);

					// Enable serialization
					redoSerialization = true;
				} catch (UIMAException | IOException e) {
					if (JCasUtil.exists(cas, DocumentId.class)) {
						DocumentId documentId = JCasUtil.selectSingle(cas, DocumentId.class);
						String id = documentId.getId();
						logger.error("Failed to process document: {}", id);
					}
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Processes a question and return the results of the analysis
	 * 
	 * @param cas
	 *            the cas to process
	 * @param contentId
	 *            the content identifier
	 */
	public void analyze(JCas cas) {
		if (JCasUtil.exists(cas, DocumentId.class)) {
			DocumentId documentId = JCasUtil.selectSingle(cas, DocumentId.class);
			String id = documentId.getId();
			// Read a previously serialized CAS if possible
			if (this.persistence.isAlreadySerialized(id)) {
				this.persistence.deserialize(cas, id);
			}
			redoSerialization = false;

			runAnalysisPipeline(cas);
			
			//TODO: a patch to be removed
			Collection<DocumentId> documentId1 = JCasUtil.select(cas, DocumentId.class);
			if (documentId1.size()==0){
				logger.debug("Lost and restoring id for the question: "+id);
				DocumentId docid = new DocumentId(cas);
				docid.setId(id);
				docid.addToIndexes();
			}
				
			
			if (redoSerialization && storeAnnotations) {
				this.persistence.serialize(cas, id);
			}
		} else {
			runAnalysisPipeline(cas);
		}
	}

	/*private Set<String> collectAnnotatorsRun(JCas cas) {
		Set<String> annotatorsRun = new HashSet<>();
		FSIterator<FeatureStructure> iterator = cas.getIndexRepository().getAllIndexedFS(
				cas.getCasType(AnnotatorRun.type));
		while (iterator.hasNext()) {
			AnnotatorRun annotatorRun = (AnnotatorRun) iterator.next();
			annotatorsRun.add(annotatorRun.getHash());
		}
		return annotatorsRun;
	}*/
	
	private Set<String> collectAnnotatorsRunByName(JCas cas) {
		Set<String> annotatorsRun = new HashSet<>();
		FSIterator<FeatureStructure> iterator = cas.getIndexRepository().getAllIndexedFS(
				cas.getCasType(AnnotatorRun.type));
		while (iterator.hasNext()) {
			AnnotatorRun annotatorRun = (AnnotatorRun) iterator.next();
			annotatorsRun.add(annotatorRun.getName());
		}
		return annotatorsRun;
	}
	
}
