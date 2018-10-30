package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

/**
 *  * The class marks the focus node of the question if the focus predictor score >0
 * 
 */
public class FocusNodeMarker extends ThreshFocusNodeMarker implements NodeMatcher {
	
	
	private final static String defaultRelTag = "REL";
	
	
	public FocusNodeMarker(MatchingStrategy focusMatchingStrategy) {
		this(defaultRelTag, focusMatchingStrategy);
	}
	public FocusNodeMarker(String relTag, MatchingStrategy focusMatchingStrategy) {
		super(relTag, focusMatchingStrategy);			
	}
	
	protected void matchQuestionTree(Tree questionTree, Tree qFocusNode, List<MatchedNode> matches, String focusTag){
		focusMarkingStrategy.doMatching(questionTree, qFocusNode, matches, focusTag);
	}
	

	

	protected Tree getFocusLeafNode(JCas questionCas, Tree[] qLeafNodes) {
		Tree qFocusNode = null;
		QuestionFocus questionFocus = null;
		
		if (JCasUtil.select(questionCas, QuestionFocus.class).size()>0) questionFocus=JCasUtil.selectSingle(questionCas, QuestionFocus.class);
		if ((questionFocus!=null)){
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
