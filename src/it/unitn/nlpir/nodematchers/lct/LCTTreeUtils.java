package it.unitn.nlpir.nodematchers.lct;

import it.unitn.nlpir.types.Token;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class LCTTreeUtils {
	/**
	 * This procedure assumes that all token nodes are encoded using integers
	 * @return
	 */
	public static List<Tree> getTokenNodes(Tree tree){
		List<Tree> trees = new ArrayList<Tree>();
		TregexPattern tgrepPattern = TregexPattern.compile("/[0-9]+/");
		TregexMatcher m = tgrepPattern.matcher(tree);
		while (m.find()) {
		    Tree subtree = m.getMatch();
		    trees.add(subtree);
		}
		return trees;
	}
	
	public static Tree[] buildTokenId2TreeLeafIndex(List<Token> tokens, Tree tree) {
		Tree[] leafNodes = new Tree[tokens.size()];
		for (Tree qLeaf : LCTTreeUtils.getTokenNodes(tree)) {
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
