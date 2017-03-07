package it.unitn.nlpir.projectors.old;


import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.projectors.Projector;
import it.unitn.nlpir.projectors.nodematchmarkers.NodesMarker;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.PhraseDependencyTree;
import it.unitn.nlpir.util.TreeUtil;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class PhraseDepTreeRelProjector implements Projector {
	private final Logger logger = LoggerFactory.getLogger(PhraseDepTreeRelProjector.class);
	
	private String relationalTag;
	private String leafTextType; 
	private NodeMatcher matcher;

	public PhraseDepTreeRelProjector(NodeMatcher matcher, String leafTextType, 
			String relationalTag) {
		this.matcher = matcher;
		this.leafTextType = leafTextType;
		this.relationalTag = relationalTag;
	}
	
	@Override
	public Pair<String, String> project(JCas questionCas, JCas documentCas)
			throws AnnotationNotFoundException {
		Tree questionTree = null;
		Tree documentTree = null;
		try {
			PhraseDependencyTree qBuilder = new PhraseDependencyTree(questionCas);
			questionTree = qBuilder.buildPhraseDepTree();
			PhraseDependencyTree docBuilder = new PhraseDependencyTree(documentCas);
			documentTree = docBuilder.buildPhraseDepTree();
//			questionTree = TreeUtil.buildPhraseDependencyTree(questionCas);
//			documentTree = TreeUtil.buildPhraseDependencyTree(documentCas);
		} catch (Exception e) {
			logger.error("Failed to build the phrase trees.");
			return new Pair<String, String>("(ROOT)", "(ROOT)");
		}

		// Match the nodes between question and the answer
		List<MatchedNode> matches = this.matcher.getMatches(questionCas, documentCas, questionTree, documentTree);
		
		// Mark the aligned words with the relational tag
		if(!matches.isEmpty()) {
			new NodesMarker().mark(matches);
		}

		TreeUtil.finalizeTreeLeaves(questionCas, questionTree, this.leafTextType);
		TreeUtil.finalizeTreeLeaves(documentCas, documentTree, this.leafTextType);
		
		return new Pair<String, String>(TreeUtil.serializeTree(questionTree),
				TreeUtil.serializeTree(documentTree));
	}
}
