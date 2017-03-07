package it.unitn.nlpir.projectors;

import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.pruners.Pruner;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.TreeBuilder;
import edu.stanford.nlp.trees.Tree;

public class RelTreePruneBothQAProjector extends RelTreeProjector implements Projector {
	protected int numPreRootNodesToKeep = -1;

	public RelTreePruneBothQAProjector(TreeBuilder treeBuilder,
			NodeMatcher matcher, ITreePostprocessor treeProcessor, Pruner pruner) {
		this(treeBuilder, matcher, treeProcessor, pruner,-1);
	}
	
	public RelTreePruneBothQAProjector(TreeBuilder treeBuilder,
			NodeMatcher matcher, ITreePostprocessor treeProcessor, Pruner pruner, int numPreRootNodesToKeep) {
		super(treeBuilder, matcher, treeProcessor, pruner);
		this.numPreRootNodesToKeep = numPreRootNodesToKeep;
		
	}

	protected Tree[] prune(Tree questionTree, Tree documentTree){
		
		
		
		if (this.numPreRootNodesToKeep>0){
			for (int i = documentTree.children().length-1; i>=numPreRootNodesToKeep; i--){
				documentTree.removeChild(i);
			}
			for (int i = questionTree.children().length-1; i>=numPreRootNodesToKeep; i--){
				questionTree.removeChild(i);
			}
		}
			
		Tree [] t = new Tree[2];
		if (pruner != null) {
			t[1] = pruner.prune(documentTree);
			t[0] = pruner.prune(questionTree);
		}
		else{
			t[0] = questionTree;
			t[1] = documentTree;
		}
		return t;
	}

}