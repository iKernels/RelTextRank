package it.unitn.nlpir.experiment.rer.cl.qc.tois.fsfromfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.IFeatsFromFile;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.features.FeatureSets;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.projectors.LCTProjectors;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * <li> DT3_Q + DT2_A + FOCUS + REL
 * <li> Features read from the external file
 * <li> SPTK fine-grained QC
 * <li> <b>Example</b>: [ROOT [be::v [who::w [GR-attr] [POS-WP]] [author::n [the::d [GR-det] [POS-DT]] [of::i [book::n [the::d [GR-det] [POS-DT]] [lady::n [the::d [GR-det] [POS-DT]] [iron::n [REL-GR-nn] [REL-POS-NN]] [biography::n [a::d [GR-det] [POS-DT]] [of::i [thatcher::n [margaret::n [REL-GR-nn] [REL-POS-NNP]] [REL-GR-pobj] [REL-POS-NNP]] [GR-prep] [POS-IN]] [REL-GR-dep] [REL-POS-NN]] [REL-GR-appos] [REL-POS-NN]] [GR-pobj] [POS-NN]] [GR-prep] [POS-IN]] [REL-FOCUS-HUM-GR-nsubj] [POS-NN]] [ROOT] [POS-VBZ]]]	[ROOT [root [REL-NP [DT [the]] [REL-NNP [iron]] [REL-NN [lady]]] [dep [REL-NP [DT [a]] [REL-NN [biography]]] [prep [REL-FOCUS-HUM-NP [IN [of]] [REL-NNP [margaret]] [REL-NNP [thatcher]]] [prep [REL-FOCUS-HUM-NP [IN [by]] [NNP [hugo]] [NNP [young]]] [appos [REL-FOCUS-HUM-NP [NNP [farrar]]] [dep [REL-FOCUS-HUM-NP [NNP [straus]] [CC [&]] [NNP [giroux]]]]]]]]]]
* @author IKernels group
 * 
 *it.unitn.nlpir.experiment.rer.lct.LCTTypedFocusMarkGrammarForQDepPhForASPTKExperiment
 *
 */
public class CachedFeatsLCTTypedFocusMarkGrammarForQDepPhForASPTKExperiment extends TrecQAExperiment implements IFeatsFromFile  {
	private final String MODELS_FILE = "data/question-classifier/lct-dep-fine/expsptk_fine_sptk_lsa";
	protected static final Logger logger = LoggerFactory.getLogger(CachedFeatsLCTTypedFocusMarkGrammarForQDepPhForASPTKExperiment.class);
	protected String modelsFile;
	
	protected void setupProjector() {
		this.pruningRay = 0;
		this.projector = LCTProjectors.getMixedFocusProjectorLCTandDepPH();
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
