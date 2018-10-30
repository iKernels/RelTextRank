package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

/**
 * This class performs a hard match on the tokens of the type specified by the
 * {@matchingTokenTextType} passed in the constructor.
 * By default matching is done on the lemmas.
 * 
 */
public class FocusEntityNodeThreshFocMatcher extends FocusEntityNodeMatcher implements NodeMatcher {
	private final static Logger logger = LoggerFactory.getLogger(FocusEntityNodeThreshFocMatcher.class);
	
	private final static String defaultRelTag = "REL";
	protected boolean typeFocusTagInQuestion = true;
	
	public FocusEntityNodeThreshFocMatcher(MatchingStrategy strategy) {
		this(strategy, defaultRelTag, false, false);
	}
	
	public FocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag) {
		this(strategy, defaultRelTag, typedRelTag, false);
	}
	
	public FocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag);
	}
	
	public FocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag, boolean typeFocusTagInQuestion) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag,typeFocusTagInQuestion);
	}
	
	public FocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag,
			boolean typeFocusTagInQuestion, MatchingStrategy focusMatchingStrategy) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag,typeFocusTagInQuestion,focusMatchingStrategy);
	}
	
	public FocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, boolean markQuestionTag) {
		this(strategy,relTag,typedRelTag,markQuestionTag, true);
	}
	
	public FocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, 
			boolean markQuestionTag, boolean typeFocusTagInQuestion, MatchingStrategy focusMatchingStrategy) {
		super(strategy, relTag, typedRelTag, markQuestionTag,focusMatchingStrategy);
		this.typeFocusTagInQuestion =typeFocusTagInQuestion;
		logger.debug(String.format("Type focus tag in question: %s", String.valueOf(this.typeFocusTagInQuestion)));
	}
	public FocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, boolean markQuestionTag, boolean typeFocusTagInQuestion) {
		super(strategy, relTag, typedRelTag, markQuestionTag);
		this.typeFocusTagInQuestion =typeFocusTagInQuestion;
		logger.debug(String.format("Type focus tag in question: %s", String.valueOf(this.typeFocusTagInQuestion)));
	}

	
	protected void matchQuestionTree(Tree questionTree, Tree qFocusNode, List<MatchedNode> matches, String focusTag){
		//questionStrategy.doMatching(questionTree, qFocusNode, matches, focusTag);
		if (typeFocusTagInQuestion)
			focusMarkingStrategy.doMatching(questionTree, qFocusNode, matches, focusTag);
		else
			focusMarkingStrategy.doMatching(questionTree, qFocusNode, matches,  String.format("%s-FOCUS", this.relTag));
	}
	
	protected void matchAnswerTree(Tree answerTree, Tree node, List<MatchedNode> matches, String focusTag){
		
		strategy.doMatching(answerTree, node, matches, focusTag);
	}
	
	protected Tree getFocusLeafNode(JCas questionCas, Tree[] qLeafNodes) {
		Tree qFocusNode = null;
		QuestionFocus questionFocus = null;
		
		if (JCasUtil.select(questionCas, QuestionFocus.class).size()>0) questionFocus=JCasUtil.selectSingle(questionCas, QuestionFocus.class);
		if ((questionFocus!=null)&&(questionFocus.getConfidence()>0)){
			List<Token> qTokenFocus = JCasUtil.selectCovered(Token.class, questionFocus);
			if (!(qTokenFocus.isEmpty())){
				Token qFocus = qTokenFocus.get(0);
				qFocusNode = qLeafNodes[qFocus.getId()];
			}
			
		}
		else{
			qFocusNode = qLeafNodes[0];
		}
		return qFocusNode;
	}
	
	
}
