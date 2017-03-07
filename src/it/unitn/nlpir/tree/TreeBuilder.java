package it.unitn.nlpir.tree;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public interface TreeBuilder {
	public Tree getTree(JCas cas);
}
