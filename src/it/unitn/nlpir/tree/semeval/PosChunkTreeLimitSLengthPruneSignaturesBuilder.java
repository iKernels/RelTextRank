package it.unitn.nlpir.tree.semeval;

import it.unitn.nlpir.tree.TreeBuilder;
import it.unitn.nlpir.util.semeval.SemevalTreeUtil;

import org.apache.uima.jcas.JCas;
import edu.stanford.nlp.trees.Tree;

public class PosChunkTreeLimitSLengthPruneSignaturesBuilder extends PosChunkTreeLimitSLengthBuilder implements TreeBuilder {
	
	public PosChunkTreeLimitSLengthPruneSignaturesBuilder(int maxSentenceLength){
		super(maxSentenceLength);
		
	}
	public Tree getTree(JCas cas) {
		Tree tree = super.getTree(cas);
		
		tree = SemevalTreeUtil.pruneSignatures(cas, tree);
		return tree;
	}
	
	
}
