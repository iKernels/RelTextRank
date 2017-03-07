package it.unitn.nlpir.projectors;

import it.unitn.nlpir.nodematchers.FocusEntityNodeMatcher;
import it.unitn.nlpir.nodematchers.HardNodeMatcher;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.TwoParentsMatchingStrategy;
import it.unitn.nlpir.nodematchers.lct.EnhancedNodeMatcher;
import it.unitn.nlpir.nodematchers.lct.LCTChildHierMarker;
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

import edu.stanford.nlp.trees.Tree;

public class RelTreeWithFocusProjector implements Projector {
//	private static final Logger logger = LoggerFactory.getLogger(RelTreeProjector.class);
	
	protected final ITreePostprocessor treeProcessor; 
	protected final NodeMatcher matcher;
	protected final NodeMatcher focusMatcher;
	protected final TreeBuilder treeBuilder;
	protected final Pruner pruner;
	
	private static final NodeMatcher defaultRelMatcher = new HardNodeMatcher(new TwoParentsMatchingStrategy());
	private static final NodeMatcher defaultFocusMatcher = new FocusEntityNodeMatcher(new TwoParentsMatchingStrategy());
	
	public RelTreeWithFocusProjector(TreeBuilder treeBuilder) {
		this(treeBuilder, defaultRelMatcher, defaultFocusMatcher, new TreeLeafFinalizer(), null);
	}
	
	public RelTreeWithFocusProjector(TreeBuilder treeBuilder, NodeMatcher matcher, NodeMatcher focusMatcher, 
			ITreePostprocessor treeProcessor) {
		this(treeBuilder, matcher, focusMatcher, treeProcessor, null);
	}
	
	public RelTreeWithFocusProjector(TreeBuilder treeBuilder, NodeMatcher matcher, NodeMatcher focusMatcher, 
			ITreePostprocessor treeProcessor, Pruner pruner) {
		this.treeBuilder = treeBuilder;
		this.matcher = matcher;
		this.focusMatcher = focusMatcher;
		this.treeProcessor = treeProcessor;
		this.pruner = pruner;
		
	}

	protected void matchFocus(Tree questionTree, Tree documentTree, NodeMatcher focusMatcher, List<MatchedNode> matches){
		if ((focusMatcher instanceof EnhancedNodeMatcher)&& (((EnhancedNodeMatcher) focusMatcher).isHierarchical())){
			new LCTChildHierMarker().mark(questionTree,matches);
			new LCTChildHierMarker().mark(documentTree,matches);
		}
		else
			new NodesMarker().mark(matches);
	}
	
	protected Tree[] prune(Tree questionTree, Tree documentTree){
		Tree [] t = new Tree[2];
		System.out.println(this.pruner);
		if (this.pruner != null) {
			//System.out.println("Before pruning: "+TreeUtil.serializeTree(documentTree));
			t[1] = pruner.prune(documentTree);
			//System.out.println("After pruning: "+TreeUtil.serializeTree(t[1]));
			/*if (!TreeUtil.serializeTree(documentTree).equals(TreeUtil.serializeTree(t[1])))
				System.out.println("DDIFF After pruning: "+TreeUtil.serializeTree(t[1]));*/
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
		if (documentTree==null){
			return new Pair<String, String>(TreeUtil.serializeTree(questionTree),
					TreeUtil.serializeTree(TreeUtil.createNode("")));
			
		}
		// Match the nodes between question and the answer
		List<MatchedNode> matches = this.matcher.getMatches(questionCas, documentCas, questionTree,
				documentTree);

		// Mark the aligned words with the relational tag
		new NodesMarker().mark(matches);
		
		if (focusMatcher!=null){
			matches = this.focusMatcher.getMatches(questionCas, documentCas, questionTree, documentTree);
			matchFocus(questionTree, documentTree, focusMatcher, matches);
		}
		
		
		Tree[] tr = prune(questionTree,documentTree);
		questionTree = tr[0];
		documentTree = tr[1];
		
		
		treeProcessor.process(questionTree, questionCas);
		treeProcessor.process(documentTree, documentCas);
		
		return new Pair<String, String>(TreeUtil.serializeTree(questionTree),
				TreeUtil.serializeTree(documentTree));
	}
}