package it.unitn.nlpir.experiment.cqa.semeval;

import java.util.Properties;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.cqa.semeval.ConstituencyTreeLimitSLengthBuilder;
import it.unitn.nlpir.tree.cqa.semeval.TreeLeafFinalizerURLReplacerQSubjKeeperPruner;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * 
 * Reference paper:
 * Tymoshenko, Kateryna, Daniele Bonadiman, and Alessandro Moschitti. 
 * "Learning to Rank Non-Factoid Answers: Comment Selection in Web Forums." Proceedings of the 25th ACM International on Conference on Information and Knowledge Management. ACM, 2016.
 * 
 * <li> community question answering, semeval
 * <li> constituency tree
 * <li> stanford analysis pipeline
 * <li> bow features
 * <li> sentences pruned at 70 words
 * <li> keeping only subject in question
 * <li> keeping only first three sentences in the answer
 * <li> replacing http urls with <b>URL</b> in the tree
 *  
* @author IKernels group
 *
 */
public class QuestionToCommentCQAConstExperiment extends TrecQAExperiment {
		
	public QuestionToCommentCQAConstExperiment() {
		super();
	}
	
	public QuestionToCommentCQAConstExperiment(Properties p) {
		super(p);
	}
	
	protected void setupProjector() {
		this.pruningRay = -1;
		this.projector = Projectors.getRelPruneOnlyAProjectorWithAuthor(new ConstituencyTreeLimitSLengthBuilder(70), pruningRay, 
				new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizerURLReplacerQSubjKeeperPruner(), 3);
	}
	
	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getStanfordGenericAnalysisEngineList();
	}

}
