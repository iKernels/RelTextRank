package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.NERUtil;
import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.HashSet;
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
public class FocusEntityNodeThreshNoFirstWordFocMatcher extends FocusEntityNodeMatcher implements NodeMatcher {
	private final static Logger logger = LoggerFactory.getLogger(FocusEntityNodeThreshNoFirstWordFocMatcher.class);
	
	private final static String defaultRelTag = "REL";
	

	public FocusEntityNodeThreshNoFirstWordFocMatcher(MatchingStrategy strategy) {
		this(strategy, defaultRelTag, false, false);
	}
	
	public FocusEntityNodeThreshNoFirstWordFocMatcher(MatchingStrategy strategy, boolean typedRelTag) {
		this(strategy, defaultRelTag, typedRelTag, false);
	}
	
	public FocusEntityNodeThreshNoFirstWordFocMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag);
	}
	
	public FocusEntityNodeThreshNoFirstWordFocMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, boolean markQuestionTag) {
		super(strategy, relTag, typedRelTag, markQuestionTag);
	}

	
	protected void matchQuestionTree(Tree questionTree, Tree qFocusNode, List<MatchedNode> matches, String focusTag){
		//questionStrategy.doMatching(questionTree, qFocusNode, matches, focusTag);
		strategy.doMatching(questionTree, qFocusNode, matches, focusTag);
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
			qFocusNode = null;
		}
		return qFocusNode;
	}
	
	
}
