package it.unitn.nlpir.tree;

import it.unitn.nlpir.util.PhraseDependencyTree;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public class PhraseDependencyTreeBuilder implements TreeBuilder {
	public Tree getTree(JCas cas) {
		PhraseDependencyTree builder = new PhraseDependencyTree(cas);
		return builder.buildPhraseDepTree();
	}
}
