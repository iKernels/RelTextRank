package it.unitn.nlpir.projectors.semeval;

import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.projectors.Projector;
import it.unitn.nlpir.pruners.Pruner;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.TreeBuilder;
import edu.stanford.nlp.trees.Tree;

public class RelTreePruneOnlyASentRelFocusAuthorProjector extends RelTreePruneBothQARelFocusAuthorProjector implements Projector {
	

	public RelTreePruneOnlyASentRelFocusAuthorProjector(TreeBuilder treeBuilder, int numPreRootNodesToKeep) {
		super(treeBuilder,numPreRootNodesToKeep);
		
	}
	
	public RelTreePruneOnlyASentRelFocusAuthorProjector(TreeBuilder treeBuilder, NodeMatcher matcher, NodeMatcher focusMatcher, 
			ITreePostprocessor treeProcessor, int numPreRootNodesToKeep) {
		super(treeBuilder, matcher, focusMatcher, treeProcessor, numPreRootNodesToKeep);
		
	}
	
	public RelTreePruneOnlyASentRelFocusAuthorProjector(TreeBuilder treeBuilder, NodeMatcher matcher, NodeMatcher focusMatcher, 
			ITreePostprocessor treeProcessor, Pruner pruner, int numPreRootNodesToKeep) {
		super(treeBuilder, matcher, focusMatcher, treeProcessor, pruner, numPreRootNodesToKeep);
		
	}
	
	protected Tree[] prune(Tree questionTree, Tree documentTree){
		if (this.numPreRootNodesToKeep>0){
			for (int i = documentTree.children().length-1; i>=numPreRootNodesToKeep; i--){
				documentTree.removeChild(i);
			}
			/*for (int i = questionTree.children().length-1; i>=numPreRootNodesToKeep; i--){
				questionTree.removeChild(i);
			}*/
		}
			
		Tree [] t = new Tree[2];
		if (pruner != null) {
			//t[1] = pruner.prune(documentTree);
			t[1] = pruner.prune(documentTree);
			t[0] = questionTree;
		}
		else{
			t[0] = questionTree;
			t[1] = documentTree;
		}
		return t;
	}
	



}