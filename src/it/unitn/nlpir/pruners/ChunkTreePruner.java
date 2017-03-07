package it.unitn.nlpir.pruners;


import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class ChunkTreePruner implements Pruner {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ChunkTreePruner.class);
	
	private PruningRule pruningRule;
	private int pruningRay;
	private boolean keepNoRelSentence;
	
	public ChunkTreePruner(PruningRule rule, int pruningRay) {
		this(rule, pruningRay, false);
	}
	
	public ChunkTreePruner(PruningRule rule, int pruningRay, boolean keepNoRelSentence) {
		this.pruningRule = rule;
		this.pruningRay = pruningRay;
		this.keepNoRelSentence = keepNoRelSentence;
	}
	
	public Tree pruneSentence(Tree tree) {
		List<Tree> chunks = tree.getChildrenAsList();
		Set<Integer> chunksToKeep = new HashSet<>();
		
		for(int i = 0; i < chunks.size(); i++) {
			
			if (pruningRule.isSatisfiedOn(chunks.get(i))) {
				int beginIndex = Math.max(i - this.pruningRay, 0);
				int endIndex = Math.min(i + this.pruningRay, chunks.size() - 1);
				for(int j = beginIndex; j <= endIndex; j++) {
					chunksToKeep.add(j);
				}
			}
		}
		
		// If there is no relational information return the original tree
		if(this.keepNoRelSentence && chunksToKeep.size() == 0) {
			return tree;
		}
		
		List<Integer> indexes = new ArrayList<>(chunksToKeep);
		Collections.sort(indexes);
		Tree prunedTree = TreeUtil.createNode(tree.nodeString());
		for(Integer index : indexes) {
			prunedTree.addChild(chunks.get(index));
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
