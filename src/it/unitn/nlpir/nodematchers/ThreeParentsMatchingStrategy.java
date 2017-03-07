package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class ThreeParentsMatchingStrategy implements MatchingStrategy {

	@Override
	public void doMatching(Tree tree, Tree matchingNode, List<MatchedNode> matches, String tag) {
		Tree parent = matchingNode.parent(tree);
		if (parent==null)
			return;
		matches.add(new MatchedNode(parent, tag));
		Tree grandParent = parent.parent(tree);
		matches.add(new MatchedNode(grandParent, tag));
		if (grandParent==null)
			return;

		Tree grandGrandParent = grandParent.parent(tree);
		if (grandGrandParent==null)
			return;
		matches.add(new MatchedNode(grandGrandParent, tag));
		
	}

}
