package it.unitn.nlpir.projectors;

import it.unitn.nlpir.nodematchers.FocusEntityNodeMatcher;
import it.unitn.nlpir.nodematchers.HardNodeMatcher;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.lct.EnhancedNodeMatcher;
import it.unitn.nlpir.nodematchers.strategies.TwoParentsMatchingStrategy;
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

import edu.stanford.nlp.trees.Tree;

public class MixedRelTreeWithFocusProjector implements IMultiProjector, Projector {
//	private static final Logger logger = LoggerFactory.getLogger(RelTreeProjector.class);
	
	protected final ITreePostprocessor questionTreeProcessor; 
	protected final ITreePostprocessor answerTreeProcessor;
	protected final List<NodeMatcher> matchers;
	//private final NodeMatcher focusMatcher;
	protected final TreeBuilder questionTreeBuilder;
	protected final TreeBuilder answerTreeBuilder;
	protected final Pruner pruner;
	
	protected static final NodeMatcher defaultRelMatcher = new HardNodeMatcher(new TwoParentsMatchingStrategy());
	protected static final NodeMatcher defaultFocusMatcher = new FocusEntityNodeMatcher(new TwoParentsMatchingStrategy());
	
	public MixedRelTreeWithFocusProjector(TreeBuilder questionTreeBuilder, TreeBuilder answerTreeBuilder) {
		this(questionTreeBuilder, answerTreeBuilder, defaultRelMatcher, defaultFocusMatcher, new TreeLeafFinalizer(), new TreeLeafFinalizer());
	}
	
	public MixedRelTreeWithFocusProjector(TreeBuilder questionTreeBuilder, TreeBuilder answerTreeBuilder, NodeMatcher matcher, NodeMatcher focusMatcher, 
			ITreePostprocessor questionTreeProcessor, ITreePostprocessor answerTreeProcessor) {
		this(questionTreeBuilder, answerTreeBuilder, matcher, focusMatcher, questionTreeProcessor, answerTreeProcessor, null);
	}
	
	public MixedRelTreeWithFocusProjector(TreeBuilder questionTreeBuilder, TreeBuilder answerTreeBuilder, NodeMatcher matcher, NodeMatcher focusMatcher, 
			ITreePostprocessor questionTreeProcessor, ITreePostprocessor answerTreeProcessor, Pruner pruner) {
		this.questionTreeBuilder = questionTreeBuilder;
		this.answerTreeBuilder = answerTreeBuilder;
		
		this.matchers = new ArrayList<NodeMatcher>();
		this.matchers.add(matcher);
		
		this.matchers.add(focusMatcher);
		//this.focusMatcher = focusMatcher;
		this.questionTreeProcessor = questionTreeProcessor;
		this.answerTreeProcessor = answerTreeProcessor;
		this.pruner = pruner;
	}

	 public MixedRelTreeWithFocusProjector(TreeBuilder questionTreeBuilder, TreeBuilder answerTreeBuilder, List<NodeMatcher> matchers, 
			ITreePostprocessor questionTreeProcessor, ITreePostprocessor answerTreeProcessor, Pruner pruner) {
		this.questionTreeBuilder = questionTreeBuilder;
		this.answerTreeBuilder = answerTreeBuilder;
		
		this.matchers = new ArrayList<NodeMatcher>();
		for (NodeMatcher matcher : matchers)
			this.matchers.add(matcher);
		//this.matchers.add(focusMatcher);
		//this.focusMatcher = focusMatcher;
		this.questionTreeProcessor = questionTreeProcessor;
		this.answerTreeProcessor = answerTreeProcessor;
		this.pruner = pruner;
	}
	
	public MixedRelTreeWithFocusProjector(TreeBuilder questionTreeBuilder, TreeBuilder answerTreeBuilder, NodeMatcher matcher, 
			ITreePostprocessor questionTreeProcessor, ITreePostprocessor answerTreeProcessor, Pruner pruner) {
		this.questionTreeBuilder = questionTreeBuilder;
		this.answerTreeBuilder = answerTreeBuilder;
		
		this.matchers = new ArrayList<NodeMatcher>();
		this.matchers.add(matcher);
		//this.matchers.add(focusMatcher);
		//this.focusMatcher = focusMatcher;
		this.questionTreeProcessor = questionTreeProcessor;
		this.answerTreeProcessor = answerTreeProcessor;
		this.pruner = pruner;
	}
	
	public void addMatcher(NodeMatcher matcher) {
		this.matchers.add(matcher);
	}
	
	
	public void doMarking(List<MatchedNode> matches, Tree questionTree, Tree documentTree){
		for (MatchedNode match : matches){
			new NodesMarker().mark(match);
		}
	}
	
	@Override
	public Pair<String, String> project(JCas questionCas, JCas documentCas)
			throws AnnotationNotFoundException {
		Tree questionTree = questionTreeBuilder.getTree(questionCas);
		Tree documentTree = answerTreeBuilder.getTree(documentCas);

		// Match the nodes between question and the answer
		
		for (NodeMatcher matcher: matchers){
			if (matcher==null)
				continue;
			/*List<MatchedNode> matches = matcher.getMatches(questionCas, documentCas, questionTree,
				documentTree);
			
			new NodesMarker().mark(matches);*/
		
			/*matches = this.focusMatcher.getMatches(questionCas, documentCas, questionTree, documentTree);
			if ((focusMatcher instanceof EnhancedNodeMatcher)&& (((EnhancedNodeMatcher) focusMatcher).isHierarchical())){
				for (MatchedNode match : matches){
					new NodesMarker().mark(match);
				}
			}
			else
				new NodesMarker().mark(matches);*/
			List<MatchedNode> matches = matcher.getMatches(questionCas, documentCas, questionTree, documentTree);
			if ((matcher instanceof EnhancedNodeMatcher)&& (((EnhancedNodeMatcher) matcher).isHierarchical())){
				doMarking(matches, questionTree, documentTree);
			}
			else
				new NodesMarker().mark(matches);
			
		}
		
				
		if (this.pruner != null) {
			documentTree = pruner.prune(documentTree);
		}
		
		questionTreeProcessor.process(questionTree, questionCas);
		answerTreeProcessor.process(documentTree, documentCas);
		
		return new Pair<String, String>(TreeUtil.serializeTree(questionTree),
				TreeUtil.serializeTree(documentTree));
	}
}