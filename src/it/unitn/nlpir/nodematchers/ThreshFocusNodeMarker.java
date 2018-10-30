package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

/**
 *  * The class marks the focus node of the question if the focus predictor score >0
 * 
 */
public class ThreshFocusNodeMarker implements NodeMatcher {
	
	
	private final static String defaultRelTag = "REL";
	

	protected MatchingStrategy focusMarkingStrategy;
	protected String relTag;
	
	public ThreshFocusNodeMarker(MatchingStrategy focusMatchingStrategy) {
		this(defaultRelTag, focusMatchingStrategy);
	}
	public ThreshFocusNodeMarker(String relTag, MatchingStrategy focusMatchingStrategy) {
		this.relTag = relTag;		
		this.focusMarkingStrategy = focusMatchingStrategy;				
	}
	
	protected void matchQuestionTree(Tree questionTree, Tree qFocusNode, List<MatchedNode> matches, String focusTag){
		focusMarkingStrategy.doMatching(questionTree, qFocusNode, matches, focusTag);
	}
	

	
	
	@Override
	public List<MatchedNode> getMatches(JCas questionCas, JCas documentCas, Tree questionTree,
			Tree documentTree) {
		List<Token> qTokens = new ArrayList<>();
		for (Token qToken : JCasUtil.select(questionCas, Token.class)) {
			qTokens.add(qToken);
		}
		
		// Get an index of question leaf nodes
		Tree[] qLeafNodes = buildTokenId2TreeLeafIndex(qTokens, questionTree);
		String focusTag  = String.format("%s-FOCUS", this.relTag);
		List<MatchedNode> matches = new ArrayList<>();
		Tree qFocusNode = getFocusLeafNode(questionCas, qLeafNodes);
		matchQuestionTree(questionTree, qFocusNode, matches, focusTag);
		return matches;
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
	
	

	protected Tree[] buildTokenId2TreeLeafIndex(List<Token> tokens, Tree tree) {
		Tree[] leafNodes = new Tree[tokens.size()];
		for (Tree qLeaf : tree.getLeaves()) {
			Integer qTokenId;
			try {
				qTokenId = Integer.parseInt(qLeaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			leafNodes[qTokenId] = qLeaf;
		}
		return leafNodes;
	}
}
