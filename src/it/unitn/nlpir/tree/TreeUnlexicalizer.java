package it.unitn.nlpir.tree;

import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public class TreeUnlexicalizer implements ITreePostprocessor {
		
	@Override
	public void process(Tree tree, JCas cas) {
		TreeUtil.removeLexicals(cas, tree);
	}

}
