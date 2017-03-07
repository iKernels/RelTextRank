package it.unitn.nlpir.experiment.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.projectors.LCTProjectors;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * LCT representation of the (defined by config file, Stanford 3.6.0 preprocessing pipeline), no pruning, n
* @author IKernels group
 *
 */
public class BasicLCTExperiment extends TrecQAExperiment  {
	protected static final Logger logger = LoggerFactory.getLogger(BasicLCTExperiment.class);
	public BasicLCTExperiment() {
		super();
	}
	
	protected void setupProjector() {
		this.pruningRay = -1;
		this.projector = LCTProjectors.getLCTProjector();
		
		
	}

	protected void setupFeatures() {
		fb = new FeaturesBuilder();
	}

	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getStanfordGenericAnalysisEngineList();
		
	}

}
