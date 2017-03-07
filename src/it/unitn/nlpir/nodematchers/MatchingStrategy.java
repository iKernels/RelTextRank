package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

public interface MatchingStrategy {
	public void doMatching(Tree tree, Tree matchingNode, List<MatchedNode> matches, String tag);
}
