package it.unitn.nlpir.projectors;

import it.unitn.nlpir.nodematchers.IMatcherWithTreeModifyingNodesMarker;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.projectors.nodematchmarkers.ITreeModifyingNodeMarker;
import it.unitn.nlpir.projectors.nodematchmarkers.NodesMarker;
import it.unitn.nlpir.pruners.Pruner;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.TreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class MultiProjector implements Projector, IMultiProjector {
	protected static final Logger logger = LoggerFactory.getLogger(MultiProjector.class);
	protected final ITreePostprocessor treeProcessor; 
	protected final List<NodeMatcher> matchers;
	protected final TreeBuilder treeBuilder;
	protected final Pruner pruner;

	public MultiProjector(TreeBuilder treeBuilder) {
		//this(treeBuilder, new HardNodeMatcher(new TwoParentsMatchingStrategy()), new TreeLeafFinalizer(), null);
		this(treeBuilder, new TreeLeafFinalizer());
	}
	
	public MultiProjector(TreeBuilder treeBuilder, NodeMatcher matcher, ITreePostprocessor treeProcessor) {
		this(treeBuilder, matcher, treeProcessor, null);
	}
	
	public MultiProjector(TreeBuilder treeBuilder, ITreePostprocessor treeProcessor) {
		this(treeBuilder, null, treeProcessor, null);
	}
	
	public MultiProjector(TreeBuilder treeBuilder, ITreePostprocessor treeProcessor, Pruner pruner) {
		this(treeBuilder, null, treeProcessor, pruner);
	}
	
	public MultiProjector(TreeBuilder treeBuilder, NodeMatcher matcher, ITreePostprocessor treeProcessor, Pruner pruner) {
		this.matchers = new ArrayList<>();
		this.treeBuilder = treeBuilder;
		if (matcher!=null)
			this.matchers.add(matcher);
		this.treeProcessor = treeProcessor;
		this.pruner = pruner;
	}
	
	public void addMatcher(NodeMatcher matcher) {
		this.matchers.add(matcher);
	}

	@Override
	public Pair<String, String> project(JCas questionCas, JCas documentCas)
			throws AnnotationNotFoundException {
		Tree questionTree = treeBuilder.getTree(questionCas);
		Tree documentTree = treeBuilder.getTree(documentCas);
		
		for(NodeMatcher matcher : this.matchers) {
			Tree oldDocumentTreeCopy = documentTree.deepCopy();
			treeProcessor.process(oldDocumentTreeCopy, documentCas);
			
			List<MatchedNode> matches = matcher.getMatches(questionCas, documentCas, questionTree,
					documentTree);		
			
			//marking nodes
			if (matcher instanceof IMatcherWithTreeModifyingNodesMarker){
				ITreeModifyingNodeMarker marker = ((IMatcherWithTreeModifyingNodesMarker) matcher).getNodeMarker();
				marker.mark(questionTree, matches);
				marker.mark(documentTree, matches);
			}
			else
				new NodesMarker().mark(matches);
			
			Tree documentTreeCopy = documentTree.deepCopy();
			treeProcessor.process(documentTreeCopy, documentCas);
			if (matcher.getClass().toString().contains("ChunkLMTQTypeMatcher") && (!(TreeUtil.serializeTree(documentTreeCopy).equals(TreeUtil.serializeTree(oldDocumentTreeCopy))))){
				logger.debug("Running matcher {}", matcher.getClass());
				if (this.pruner != null) {
					documentTreeCopy = pruner.prune(documentTreeCopy);
					oldDocumentTreeCopy = pruner.prune(oldDocumentTreeCopy);
				}
				logger.debug("Before: " + (TreeUtil.serializeTree(oldDocumentTreeCopy)).replace("(", "[").replace(")", "]"));
				logger.debug("After: " + (TreeUtil.serializeTree(documentTreeCopy)).replace("(", "[").replace(")", "]"));
			}
		}
		
		if (this.pruner != null) {
			documentTree = pruner.prune(documentTree);
		}
		
		treeProcessor.process(questionTree, questionCas);
		treeProcessor.process(documentTree, documentCas);
		logger.debug((TreeUtil.serializeTree(questionTree) +"\t"+	TreeUtil.serializeTree(documentTree)).replace("(", "[").replace(")", "]"));
		return new Pair<String, String>(TreeUtil.serializeTree(questionTree),
				TreeUtil.serializeTree(documentTree));
	}

}
