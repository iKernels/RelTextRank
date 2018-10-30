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

import edu.stanford.nlp.trees.Tree;

public class DependencyTreeRelProjector implements Projector {
	
	private String leafTextType; 
	private NodeMatcher matcher;

	public DependencyTreeRelProjector(NodeMatcher matcher, String leafTextType, 
			String relationalTag) {
		this.matcher = matcher;
		this.leafTextType = leafTextType;
		
	}
	
	@Override
	public Pair<String, String> project(JCas questionCas, JCas documentCas)
			throws AnnotationNotFoundException {

		Tree questionTree = null;
		Tree documentTree = null;
		try {
			PhraseDependencyTree qBuilder = new PhraseDependencyTree(questionCas);
			questionTree = qBuilder.buildPosTagDepTree();
			PhraseDependencyTree docBuilder = new PhraseDependencyTree(documentCas);
			documentTree = docBuilder.buildPosTagDepTree();
		} catch (Exception e) {
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
