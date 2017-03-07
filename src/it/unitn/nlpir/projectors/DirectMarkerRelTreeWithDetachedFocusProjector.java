package it.unitn.nlpir.projectors;

import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.lct.DirectChildHierMarker;
import it.unitn.nlpir.pruners.ChunkTreePruner;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.TreeBuilder;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class DirectMarkerRelTreeWithDetachedFocusProjector extends RelTreeWithFocusProjector implements Projector {
//	private static final Logger logger = LoggerFactory.getLogger(RelTreeProjector.class);
	
	

	public DirectMarkerRelTreeWithDetachedFocusProjector(TreeBuilder treeBuilder,
			NodeMatcher matcher, NodeMatcher focusMatcher,
			ITreePostprocessor treeProcessor) {
		super(treeBuilder, matcher, focusMatcher, treeProcessor);
		
	}

	public DirectMarkerRelTreeWithDetachedFocusProjector(TreeBuilder treeBuilder,
			NodeMatcher matcher, NodeMatcher focusMatcher,
			ITreePostprocessor tp, ChunkTreePruner pruner) {
		super(treeBuilder, matcher, focusMatcher, tp, pruner);
	}

	protected void matchFocus(Tree questionTree, Tree documentTree, NodeMatcher focusMatcher, List<MatchedNode> matches){
		//if ((focusMatcher instanceof EnhancedNodeMatcher)&& (((EnhancedNodeMatcher) focusMatcher).isHierarchical())){
			new DirectChildHierMarker().mark(questionTree,matches);
			new DirectChildHierMarker().mark(documentTree,matches);
		
	}
	
	
}