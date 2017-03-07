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
import it.unitn.nlpir.tree.DependencyPosTagTreeBuilder;
import it.unitn.nlpir.tree.leaffinalizers.LeafSPTKFullAllLeavesFinalizer;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * <li> DT1 + FOCUS + REL
 * <li> Features read from the external file
 * <li> SPTK fine-grained QC
 * <li> <b>Example</b>: [ROOT [root [attr [WP [who::w]]] [VBZ [be::v]] [REL-FOCUS-HUM-nsubj [det [DT [the::d]]] [NN [author::n]] [prep [IN [of::i]] [pobj [det [DT [the::d]]] [NN [book::n]] [REL-appos [det [DT [the::d]]] [REL-nn [REL-NN [iron::n]]] [REL-NN [lady::n]] [REL-dep [det [DT [a::d]]] [REL-NN [biography::n]] [prep [IN [of::i]] [REL-pobj [REL-nn [REL-NNP [margaret::n]]] [REL-NNP [thatcher::n]]]]]]]]]]]	[ROOT [root [det [DT [the::d]]] [REL-nn [REL-NNP [iron::n]]] [REL-NN [lady::n]] [REL-dep [det [DT [a::d]]] [REL-NN [biography::n]] [prep [IN [of::i]] [REL-FOCUS-HUM-pobj [REL-FOCUS-HUM-nn [REL-NNP [margaret::n]]] [REL-NNP [thatcher::n]] [prep [IN [by::i]] [REL-FOCUS-HUM-pobj [REL-FOCUS-HUM-nn [NNP [hugo::n]]] [NNP [young::n]] [REL-FOCUS-HUM-appos [NNP [farrar::n]] [REL-FOCUS-HUM-dep [NNP [straus::n]] [REL-FOCUS-HUM-cc [CC [&::c]]] [REL-FOCUS-HUM-conj [NNP [giroux::n]]]]]]]]]]]]
* @author IKernels group
 *
 */
public class CachedFeatsDepTreeTypedFocusStanfordForSPTKExperiment extends TrecQAExperiment implements IFeatsFromFile  {
	private final String MODELS_FILE = "data/question-classifier/lct-dep-fine/expsptk_fine_sptk_lsa";
	protected static final Logger logger = LoggerFactory.getLogger(CachedFeatsDepTreeTypedFocusStanfordForSPTKExperiment.class);
	protected String modelsFile;
	
	protected void setupProjector() {
		this.pruningRay = 0;
		this.projector = Projectors.getFocusProjector(new DependencyPosTagTreeBuilder(), pruningRay, new StartsWithOrContainsTagPruningRule(), new LeafSPTKFullAllLeavesFinalizer());
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
