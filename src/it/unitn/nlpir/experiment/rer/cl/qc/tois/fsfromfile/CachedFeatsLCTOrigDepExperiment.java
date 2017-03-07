package it.unitn.nlpir.experiment.rer.cl.qc.tois.fsfromfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.IFeatsFromFile;
import it.unitn.nlpir.experiment.rer.cl.qc.tois.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.features.FeatureSets;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.projectors.LCTProjectors;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * LCT defined by config file, Stanford 3.6.0 preprocessing pipeline, orginal dependencies), 
 * no pruning, question classification defined by the parameters in the configuration file
* @author IKernels group
 *
 */
public class CachedFeatsLCTOrigDepExperiment extends StanfordAETrecQAWithQCExperiment implements IFeatsFromFile {
	protected static final Logger logger = LoggerFactory.getLogger(CachedFeatsLCTOrigDepExperiment.class);
	public CachedFeatsLCTOrigDepExperiment(String configFile) {
		super(configFile);
	}
	
	protected void setupProjector() {
		this.pruningRay = -1;
		this.projector = LCTProjectors.getMixedFocusProjectorLCTandDepPH();
		
		
	}

	protected void setupFeatures() {
		fb = new FeaturesBuilder();
	}

	@Override
	public void setFeaturesSource(String idFile, String featureFile) {
		logger.info(String.format("Reading features from %s, %s", idFile, featureFile));
		fb.extend(FeatureSets.buildFeaturesFromExternalFile(idFile, featureFile));
		
	}

	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		if (this.questionClassifierModelsFolder==null)
			return AnalyzerConfig.getStanfordQAAnalysisEngineListWithSPTKQC(MODELS_FILE, 
				"it.unitn.nlpir.tree.LCTBuilder","it.unitn.nlpir.tree.SPTKFullTokenNodesFinalizer");
		logger.info("use SPTK Library: {}", this.useSPTKLibrary);
		if (this.useSPTKLibrary)
			return AnalyzerConfig.getStanfordQAAnalysisEngineListWithPropsWithSPTKQC(this.questionClassifierModelsFolder, 
					this.questionClassifierTreeBuilderName,this.questionClassifierLeafFinalizerName);
		else
			return AnalyzerConfig.getStanfordWithPropsQAAnalysisEngineList(this.questionClassifierModelsFolder, 
					this.questionClassifierTreeBuilderName,this.questionClassifierLeafFinalizerName);
	}
}
