package it.unitn.nlpir.experiment.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.projectors.LCTProjectors;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * LCT tree for the first tree in the pair, DEPPH tree for the second tree in the pair
 * no pruning, REL labels only, no question classification
* @author IKernels group
 *
 */
public class BasicLCTDepPhExperiment extends TrecQAExperiment  {
	protected static final Logger logger = LoggerFactory.getLogger(BasicLCTDepPhExperiment.class);
	public BasicLCTDepPhExperiment() {
		super();
	}
	
	protected void setupProjector() {
		this.pruningRay = -1;
		this.projector = LCTProjectors.getMixedProjectorLCTandDepPH(-1);
		
		
	}

	protected void setupFeatures() {
		fb = new FeaturesBuilder();
	}

	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getStanfordGenericAnalysisEngineList();
		
	}
}
