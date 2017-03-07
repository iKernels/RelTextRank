package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public class NullMatcher implements NodeMatcher {

	@Override
	public List<MatchedNode> getMatches(JCas questionCas, JCas documentCas,
			Tree questionTree, Tree documentTree) {
		return new ArrayList<MatchedNode>();
	}

}
