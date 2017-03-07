package it.unitn.nlpir.nodematchers.lct;


import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.util.AdditionalTreeUtil;
import it.unitn.nlpir.util.TreeUtil;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class DirectChildHierMarker {
	public void mark(Tree tree, MatchedNode node) {

		AdditionalTreeUtil.addNonDuplicatingSibling(tree, node.getNode(), node.getRelTag());
	}
	
	public void mark(Tree tree, List<MatchedNode> nodes) {
		for(MatchedNode node : nodes) {
			try {
				mark(tree, node);
			}
			catch (Exception e){
				System.err.println(TreeUtil.serializeTree(tree)+" \t "+TreeUtil.serializeTree(node.getNode()));
				e.printStackTrace();
			}
		}
	}
}
