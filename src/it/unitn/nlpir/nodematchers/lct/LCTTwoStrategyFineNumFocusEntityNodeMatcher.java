package it.unitn.nlpir.nodematchers.lct;

import it.unitn.nlpir.nodematchers.MatchingStrategy;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.util.NERUtil;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

/**
 * This class performs a hard match on the tokens of the type specified by the
 * {@matchingTokenTextType} passed in the constructor.
 * By default matching is done on the lemmas.
 * 
 */
public class LCTTwoStrategyFineNumFocusEntityNodeMatcher extends LCTFocusEntityNodeMatcher implements NodeMatcher, EnhancedNodeMatcher {
	private final static Logger logger = LoggerFactory.getLogger(LCTTwoStrategyFineNumFocusEntityNodeMatcher.class);
	
	private final static String defaultRelTag = "REL";
	
	protected MatchingStrategy questionStrategy;
	protected MatchingStrategy answerStrategy;
	
	public LCTTwoStrategyFineNumFocusEntityNodeMatcher(MatchingStrategy questionStrategy, MatchingStrategy answerStrategy) {
		this(questionStrategy, answerStrategy, defaultRelTag, false, false);
	}
	
	public LCTTwoStrategyFineNumFocusEntityNodeMatcher(MatchingStrategy questionStrategy, MatchingStrategy answerStrategy, boolean typedRelTag) {
		this(questionStrategy, answerStrategy,  defaultRelTag, typedRelTag, false);
	}
	
	public LCTTwoStrategyFineNumFocusEntityNodeMatcher(MatchingStrategy questionStrategy, MatchingStrategy answerStrategy, boolean typedRelTag, boolean markQuestionTag) {
		this(questionStrategy, answerStrategy, defaultRelTag, typedRelTag, markQuestionTag);
	}
	
	public LCTTwoStrategyFineNumFocusEntityNodeMatcher(MatchingStrategy questionStrategy, MatchingStrategy answerStrategy, String relTag, boolean typedRelTag, boolean markQuestionTag) {
		super(questionStrategy, defaultRelTag, typedRelTag, markQuestionTag);
		this.questionStrategy = questionStrategy;
		this.answerStrategy = answerStrategy;
	}

	protected List<String> getClassRelatedNERTypes(String questionClass){
		return 	NERUtil
				.resolveFineNumQuestionCategory2EntityClassTypes(questionClass);
	}

	
}
