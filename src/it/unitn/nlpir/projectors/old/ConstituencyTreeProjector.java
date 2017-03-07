package it.unitn.nlpir.projectors.old;

import it.unitn.nlpir.nodematchers.HardNodeMatcher;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.TwoParentsMatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.projectors.Projector;
import it.unitn.nlpir.projectors.nodematchmarkers.NodesMarker;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.TreeLeafFinalizer;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class ConstituencyTreeProjector implements Projector {
	private static final Logger logger = LoggerFactory.getLogger(ConstituencyTreeProjector.class);
	
	private final ITreePostprocessor treeProcessor; 
	private NodeMatcher matcher;

	public ConstituencyTreeProjector() {
		this.matcher = new HardNodeMatcher(TokenTextGetterFactory.LEMMA, "REL", new TwoParentsMatchingStrategy());
		this.treeProcessor = new TreeLeafFinalizer(TokenTextGetterFactory.LEMMA);
	}
	
	public ConstituencyTreeProjector(NodeMatcher matcher, ITreePostprocessor treeProcessor) {
		this.matcher = matcher;
		this.treeProcessor = treeProcessor;
	}

	@Override
	public Pair<String, String> project(JCas questionCas, JCas documentCas)
			throws AnnotationNotFoundException {
		Tree questionTree = TreeUtil.buildTree(UIMAUtil.getConstituencyTree(questionCas));
		Tree documentTree = TreeUtil.buildTree(UIMAUtil.getConstituencyTree(documentCas));

		// Match the nodes between question and the answer
		List<MatchedNode> matches = this.matcher.getMatches(questionCas, documentCas, questionTree,
				documentTree);

		// Mark the aligned words with the relational tag
		new NodesMarker().mark(matches);
		
		treeProcessor.process(questionTree, questionCas);
		treeProcessor.process(documentTree, documentCas);
		
		return new Pair<String, String>(TreeUtil.serializeTree(questionTree),
				TreeUtil.serializeTree(documentTree));
	}
}