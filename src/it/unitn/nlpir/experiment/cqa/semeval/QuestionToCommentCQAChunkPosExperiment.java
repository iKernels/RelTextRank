package it.unitn.nlpir.experiment.cqa.semeval;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.cqa.semeval.TreeLeafFinalizerURLReplacer;
import it.unitn.nlpir.tree.semeval.PosChunkTreeLimitSLengthPruneSignaturesBuilder;
import it.unitn.nlpir.uima.AnalysisEngineList;


/**
 * Experiment class used in the SemEval-2016, Task 3.A competition.
 * 
 * Ranked #2nd when combined with the thread-level features.
 * 
 * See paper: Joty, Shafiq, et al. 
 * "ConvKN at SemEval-2016 Task 3: Answer and question selection for question answering on Arabic and English fora." Proceedings of SemEval (2016): 896-903.
 * 
* @author IKernels group
 *
 */
public class QuestionToCommentCQAChunkPosExperiment extends TrecQAExperiment {
	protected void setupProjector() {
		this.pruningRay = -1;
		this.projector = Projectors.getRelPruneQandAProjector(new PosChunkTreeLimitSLengthPruneSignaturesBuilder(70), pruningRay, 
				new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizerURLReplacer(), 3);
	}
	
	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getStanfordGenericAnalysisEngineList();
	}

	protected void setupFeatures() {
		fb = new FeaturesBuilder();
	}
}
