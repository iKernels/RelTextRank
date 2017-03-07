package it.unitn.nlpir.experiment.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PhraseDependencyTreeBuilder;
import it.unitn.nlpir.tree.leaffinalizers.LeafSPTKFullAllLeavesFinalizer;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * DET2 (old DEPPH) with REL labels, Stanford 3.6.0 preprocessing pipeline
* @author IKernels group
 *
 */
public class BasicDepPhExperiment extends TrecQAExperiment  {
	protected static final Logger logger = LoggerFactory.getLogger(BasicDepPhExperiment.class);
	public BasicDepPhExperiment() {
		super();
	}
	
	protected void setupProjector() {
		this.pruningRay = -1;
		
		this.projector = Projectors.getProjector(new PhraseDependencyTreeBuilder(), pruningRay, new StartsWithOrContainsTagPruningRule(), new LeafSPTKFullAllLeavesFinalizer());
		
	}

	protected void setupFeatures() {
		fb = new FeaturesBuilder();
	}

	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getStanfordGenericAnalysisEngineList();
		
	}
}
