package it.unitn.nlpir.tree;

import it.unitn.nlpir.util.PhraseDependencyTree;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;


/**
 * Builds LCT trees as in the (EMNLP, 2011) paper
* @author IKernels group
 *
 */
public class LCTBuilder implements TreeBuilder {
	public Tree getTree(JCas cas) {
		PhraseDependencyTree builder = new PhraseDependencyTree(cas);
		return builder.buildLCTTree();
	}
}
