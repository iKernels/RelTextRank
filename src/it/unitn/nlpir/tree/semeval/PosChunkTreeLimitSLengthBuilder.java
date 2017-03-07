package it.unitn.nlpir.tree.semeval;

import it.unitn.nlpir.tree.TreeBuilder;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public class PosChunkTreeLimitSLengthBuilder implements TreeBuilder {
	protected int maxSentenceLength;
	public PosChunkTreeLimitSLengthBuilder(int maxSentenceLength){
		this.maxSentenceLength = maxSentenceLength;
	}
	public Tree getTree(JCas cas) {
		Tree tree = null;
		try {

			tree = TreeUtil.buildTree(UIMAUtil.getPosChunkTree(cas));

			tree = TreeUtil.pruneLongSentences(tree, maxSentenceLength);
		} catch (AnnotationNotFoundException e) {
			e.printStackTrace();
		}
		return tree;
	}
	
}
