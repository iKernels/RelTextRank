package it.unitn.nlpir.tree.cqa.semeval;

import it.unitn.nlpir.tree.TreeBuilder;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.semeval.SemevalTreeUtil;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public class ConstituencyTreeLimitSLengthBuilder implements TreeBuilder {
	protected int maxSentenceLength;
	public ConstituencyTreeLimitSLengthBuilder(int maxSentenceLength){
		this.maxSentenceLength = maxSentenceLength;
		
	}
	public Tree getTree(JCas cas) {
		Tree tree = null;
		try {
			tree = TreeUtil.buildTree(UIMAUtil.getConstituencyTree(cas));
		} catch (AnnotationNotFoundException e) {
			e.printStackTrace();
		}
		Tree copy = TreeUtil.createNode("ROOT");
		for (Tree r : tree.getChildrenAsList()){
			for (Tree s : r.getChildrenAsList())
				copy.addChild(s);
		}
			
		tree = copy;
		tree = TreeUtil.pruneLongSentences(tree, maxSentenceLength);
		SemevalTreeUtil.pruneSignatures(cas, tree);
		tree = SemevalTreeUtil.splitIntoSubjectAndBody(cas, tree);
		return tree;
	}
}
