package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;

import java.util.List;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public interface NodeMatcher {
	public List<MatchedNode> getMatches(JCas questionCas, JCas documentCas, Tree questionTree, Tree documentTree);
	
}
