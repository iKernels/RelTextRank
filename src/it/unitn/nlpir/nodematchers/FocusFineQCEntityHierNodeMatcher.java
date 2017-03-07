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
public class FocusFineQCEntityHierNodeMatcher extends FocusFineQCEntityNodeMatcher implements NodeMatcher {
	

	public FocusFineQCEntityHierNodeMatcher(MatchingStrategy strategy) {
		this(strategy, defaultRelTag, false);
	}
	
	public FocusFineQCEntityHierNodeMatcher(MatchingStrategy strategy, boolean typedRelTag) {
		this(strategy, defaultRelTag, typedRelTag);
	}
	
	public FocusFineQCEntityHierNodeMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag) {
		super(strategy, relTag, typedRelTag);
		
	}
	


	protected String getFocusTag(String questionClass){
		String focusTag;
		
		if (this.typedRelTag) {
			focusTag = String.format("FOCUS-%s", questionClass);
		} else {
			focusTag = String.format("FOCUS", this.relTag);
		}
		Tree t = TreeUtil.createNode(focusTag);
		focusTag = TreeUtil.serializeTree(t);
		return focusTag;
	}
	
	protected void markQuestion(Tree questionTree, Tree qFocusNode, List<MatchedNode> matches, String focusTag){
		//strategy.doMatching(questionTree, qFocusNode, matches, focusTag);
	}
	
	protected void markDocument(Tree documentTree, Tree node, List<MatchedNode> matches, String focusTag){
		strategy.doMatching(documentTree, node, matches, focusTag);
	}
	
}
