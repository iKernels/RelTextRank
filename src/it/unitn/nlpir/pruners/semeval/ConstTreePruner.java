package it.unitn.nlpir.pruners.semeval;

import it.unitn.nlpir.pruners.Pruner;
import it.unitn.nlpir.pruners.PruningRule;
import it.unitn.nlpir.util.TreeUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class ConstTreePruner implements Pruner {
	private static final Logger logger = LoggerFactory.getLogger(ConstTreePruner.class);
	
	private PruningRule pruningRule;
	private int pruningRay;
	private boolean keepNoRelSentence;
	
	public ConstTreePruner(PruningRule rule, int pruningRay) {
		this(rule, pruningRay, false);
	}
	
	public ConstTreePruner(PruningRule rule, int pruningRay, boolean keepNoRelSentence) {
		this.pruningRule = rule;
		this.pruningRay = pruningRay;
		this.keepNoRelSentence = keepNoRelSentence;
	}
	
	public Tree pruneSentence(Tree tree) {
		List<Tree> words = TreeUtil.getTreeIntegerNodes(tree);
		Set<Integer> wordsToKeep = new HashSet<>();
		for(int i = 0; i < words.size(); i++) {
			Tree parent = words.get(i).parent(tree).parent(tree);
			if(pruningRule.isSatisfiedOn(parent)) {
				int beginIndex = Math.max(i - this.pruningRay, 0);
				int endIndex = Math.min(i + this.pruningRay, words.size() - 1);
				for(int j = beginIndex; j <= endIndex; j++) {
					wordsToKeep.add(Integer.valueOf(words.get(j).value()));
				}
			}
		}
		
		// If there is no relational information return the original tree
		if(this.keepNoRelSentence && words.size() == 0) {
			return tree;
		}
		
		for (Tree w : words){
			if (!wordsToKeep.contains(Integer.valueOf(w.value()))){
				w.setValue(TreeUtil.REMOVE_LABEL);
			}
		}
		Tree prunedTree = TreeUtil.pruneLeavesToRemove(tree);
		if (prunedTree==null){
			prunedTree = TreeUtil.createNode("ROOT");
		}
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
