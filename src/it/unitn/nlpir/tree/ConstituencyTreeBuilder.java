package it.unitn.nlpir.tree;

import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public class ConstituencyTreeBuilder implements TreeBuilder {
	
	public Tree getTree(JCas cas) {
		Tree tree = null;
		try {
			tree = TreeUtil.buildTree(UIMAUtil.getConstituencyTree(cas));
		} catch (AnnotationNotFoundException e) {
			e.printStackTrace();
		}
		return tree;
	}
}
