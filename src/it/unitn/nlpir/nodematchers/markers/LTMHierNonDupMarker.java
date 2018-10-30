package it.unitn.nlpir.nodematchers.markers;


import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.projectors.nodematchmarkers.ITreeModifyingNodeMarker;
import it.unitn.nlpir.util.AdditionalTreeUtil;
import it.unitn.nlpir.util.TreeUtil;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class LTMHierNonDupMarker implements ITreeModifyingNodeMarker{
	
	public void mark(Tree tree, MatchedNode node) {
		AdditionalTreeUtil.addHierNonDupSibling(tree, node.getNode(), TreeUtil.buildTree(node.getRelTag()));
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
