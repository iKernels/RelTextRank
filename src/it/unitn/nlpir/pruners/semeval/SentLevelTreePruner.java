package it.unitn.nlpir.pruners.semeval;

import it.unitn.nlpir.pruners.Pruner;
import it.unitn.nlpir.pruners.PruningRule;
import it.unitn.nlpir.util.TreeUtil;

import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class SentLevelTreePruner implements Pruner {
	private static final Logger logger = LoggerFactory.getLogger(SentLevelTreePruner.class);
	
	private PruningRule pruningRule;
	public SentLevelTreePruner(PruningRule rule, int pruningRay) {
		this(rule, pruningRay, false);
	}
	
	public SentLevelTreePruner(PruningRule rule, int pruningRay, boolean keepNoRelSentence) {
		this.pruningRule = rule;
	}
	
	public Tree pruneSentence(Tree tree) {
		List<Tree> words = TreeUtil.getTreeIntegerNodes(tree);
		new HashSet<>();
		boolean keepSentence = false;
		for(int i = 0; i < words.size(); i++) {
			Tree parent = words.get(i).parent(tree).parent(tree);
			if(pruningRule.isSatisfiedOn(parent)) {
				keepSentence = true;
			}
		}
		Tree prunedTree = TreeUtil.createNode("ROOT");
		if (keepSentence)
			prunedTree = tree;
		return prunedTree;
	}
	
	public Tree prune(Tree tree) {
		List<Tree> sentences = tree.getChildrenAsList();
		Tree prunedTree = TreeUtil.createNode(tree.nodeString());
		for (Tree sent : sentences) {		
			sent = pruneSentence(sent);
			if (sent.numChildren() > 0)  // Remove dangling sentence root nodes where all chunks were pruned away. 
				prunedTree.addChild(sent);
		}
		return prunedTree;
		
	}
}
