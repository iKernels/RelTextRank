package it.unitn.nlpir.experiment.rer.cl.qc.tois.fsfromfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.IFeatsFromFile;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.features.FeatureSets;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkFullTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * <li> CHp + FOCUS + REL
 * <li> Features read from the external file
 * <li> SPTK fine-grained QC
 * <li> 
* @author IKernels group
 *
 */
public class CachedFeatsCHPunktTypedFocusStanfordForSPTKExperiment extends TrecQAExperiment implements IFeatsFromFile {
	private final String MODELS_FILE = "data/question-classifier/lct-dep-fine/expsptk_fine_sptk_lsa";
	protected static final Logger logger = LoggerFactory.getLogger(CachedFeatsCHPunktTypedFocusStanfordForSPTKExperiment.class);
	protected String modelsFile;
	
	protected void setupProjector() {
		this.pruningRay = -1;		
		this.projector = Projectors.getFocusWithThreshFocusUntypedFocusInQProjector(new PosChunkFullTreeBuilder(), pruningRay,
                new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder());
		this.modelsFile = MODELS_FILE;
	}
	
	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getStanfordQAAnalysisEngineListWithSPTKQC(modelsFile, 
				"it.unitn.nlpir.tree.LCTBuilder","it.unitn.nlpir.tree.SPTKFullTokenNodesFinalizer");
	}

	protected void setupFeatures() {
		fb = new FeaturesBuilder();
	}

	@Override
	public void setFeaturesSource(String idFile, String featureFile) {
		logger.info(String.format("Reading features from %s, %s", idFile, featureFile));
		fb.extend(FeatureSets.buildFeaturesFromExternalFile(idFile, featureFile));
		
	}
}
