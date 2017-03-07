package it.unitn.nlpir.nodematchers.lct;

import it.unitn.nlpir.nodematchers.FocusEntityNodeMatcher;
import it.unitn.nlpir.nodematchers.HierMatchingStrategy;
import it.unitn.nlpir.nodematchers.MatchingStrategy;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.NERUtil;

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
public class LCTFocusEntityNodeMatcher extends FocusEntityNodeMatcher implements NodeMatcher, EnhancedNodeMatcher {
	private final static Logger logger = LoggerFactory.getLogger(LCTFocusEntityNodeMatcher.class);
	
	private final static String defaultRelTag = "REL";
	
	
	
	public LCTFocusEntityNodeMatcher(MatchingStrategy strategy) {
		this(strategy, defaultRelTag, false, false);
	}
	
	public LCTFocusEntityNodeMatcher(MatchingStrategy strategy, boolean typedRelTag) {
		this(strategy, defaultRelTag, typedRelTag, false);
	}
	
	public LCTFocusEntityNodeMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag);
	}
	
	public LCTFocusEntityNodeMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, boolean markQuestionTag) {
		super(strategy, defaultRelTag, typedRelTag, markQuestionTag);
	}

	

	protected Tree[] buildTokenId2TreeLeafIndex(List<Token> tokens, Tree tree) {
		Tree[] leafNodes = new Tree[tokens.size()];
		for (Tree qLeaf : LCTTreeUtils.getTokenNodes(tree)/*tree.getLeaves()*/) {
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

	@Override
	public boolean isHierarchical() {
		if (this.strategy instanceof HierMatchingStrategy)
			return true;
		return false;
	}
}
