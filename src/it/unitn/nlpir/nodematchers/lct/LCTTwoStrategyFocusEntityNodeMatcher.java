package it.unitn.nlpir.nodematchers.lct;

import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

/**
 * This class performs a hard match on the tokens of the type specified by the
 * {@matchingTokenTextType} passed in the constructor.
 * By default matching is done on the lemmas.
 * 
 */
public class LCTTwoStrategyFocusEntityNodeMatcher extends LCTFocusEntityNodeMatcher implements NodeMatcher, EnhancedNodeMatcher {
	
	
	private final static String defaultRelTag = "REL";
	
	protected MatchingStrategy questionStrategy;
	protected MatchingStrategy answerStrategy;
	
	public LCTTwoStrategyFocusEntityNodeMatcher(MatchingStrategy questionStrategy, MatchingStrategy answerStrategy) {
		this(questionStrategy, answerStrategy, defaultRelTag, false, false);
	}
	
	public LCTTwoStrategyFocusEntityNodeMatcher(MatchingStrategy questionStrategy, MatchingStrategy answerStrategy, boolean typedRelTag) {
		this(questionStrategy, answerStrategy,  defaultRelTag, typedRelTag, false);
	}
	
	public LCTTwoStrategyFocusEntityNodeMatcher(MatchingStrategy questionStrategy, MatchingStrategy answerStrategy, boolean typedRelTag, boolean markQuestionTag) {
		this(questionStrategy, answerStrategy, defaultRelTag, typedRelTag, markQuestionTag);
	}
	
	public LCTTwoStrategyFocusEntityNodeMatcher(MatchingStrategy questionStrategy, MatchingStrategy answerStrategy, String relTag, boolean typedRelTag, boolean markQuestionTag) {
		super(questionStrategy, defaultRelTag, typedRelTag, markQuestionTag);
		this.questionStrategy = questionStrategy;
		this.answerStrategy = answerStrategy;
	}

	

	protected void matchQuestionTree(Tree questionTree, Tree qFocusNode, List<MatchedNode> matches, String focusTag){
		questionStrategy.doMatching(questionTree, qFocusNode, matches, focusTag);
	}
	
	protected void matchAnswerTree(Tree answerTree, Tree node, List<MatchedNode> matches, String focusTag){
		answerStrategy.doMatching(answerTree, node, matches, focusTag);
	}
	
	public boolean isHierarchical(){
		return false;
	}
}
