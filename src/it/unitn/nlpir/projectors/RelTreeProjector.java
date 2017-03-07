package it.unitn.nlpir.projectors;

import it.unitn.nlpir.nodematchers.HardNodeMatcher;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.TwoParentsMatchingStrategy;
import it.unitn.nlpir.projectors.nodematchmarkers.NodesMarker;
import it.unitn.nlpir.pruners.Pruner;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.TreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class RelTreeProjector implements Projector {
	private static final Logger logger = LoggerFactory.getLogger(RelTreeProjector.class);

	private final ITreePostprocessor treeProcessor; 
	private final NodeMatcher matcher;
	private final TreeBuilder treeBuilder;
	protected final Pruner pruner;
	
	public RelTreeProjector(TreeBuilder treeBuilder) {
		this(treeBuilder, new HardNodeMatcher(new TwoParentsMatchingStrategy()), new TreeLeafFinalizer(), null);
	}
	
	public RelTreeProjector(TreeBuilder treeBuilder, NodeMatcher matcher, ITreePostprocessor treeProcessor) {
		this(treeBuilder, matcher, treeProcessor, null);
	}
	
	public RelTreeProjector(TreeBuilder treeBuilder, NodeMatcher matcher, ITreePostprocessor treeProcessor, Pruner pruner) {
		this.treeBuilder = treeBuilder;
		this.matcher = matcher;
		this.treeProcessor = treeProcessor;
		this.pruner = pruner;
	}

	protected Tree[] prune(Tree questionTree, Tree documentTree){
		Tree [] t = new Tree[2];
		if (this.pruner != null) {
			t[1] = pruner.prune(documentTree);
		}
		else{
			t[1] = documentTree;
		}
		t[0] = questionTree;
		return t;
	}
	@Override
	public Pair<String, String> project(JCas questionCas, JCas documentCas)
			throws AnnotationNotFoundException {
		Tree questionTree = treeBuilder.getTree(questionCas);
		Tree documentTree = treeBuilder.getTree(documentCas);

		// Match the nodes between question and the answer
		if (this.matcher != null){
			List<MatchedNode> matches = this.matcher.getMatches(questionCas, documentCas, questionTree,
				documentTree);

		// Mark the aligned words with the relational tag
			new NodesMarker().mark(matches);
		}
		
		Tree[] tr = prune(questionTree, documentTree);
		questionTree = tr[0];
		documentTree = tr[1];
		treeProcessor.process(questionTree, questionCas);
		treeProcessor.process(documentTree, documentCas);
		logger.debug((TreeUtil.serializeTree(questionTree) +"\t"+	TreeUtil.serializeTree(documentTree)).replace("(", "[").replace(")", "]"));
		return new Pair<String, String>(TreeUtil.serializeTree(questionTree),
				TreeUtil.serializeTree(documentTree));
		
	}
}