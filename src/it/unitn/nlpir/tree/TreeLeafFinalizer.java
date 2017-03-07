package it.unitn.nlpir.tree;

import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public class TreeLeafFinalizer implements ITreePostprocessor {
	private static final String defaultLeafTextType = TokenTextGetterFactory.LEMMA;
	private String leafTextType;
	
	public TreeLeafFinalizer() {
		this(defaultLeafTextType);
	}
	
	public TreeLeafFinalizer(String leafTextType) {
		this.leafTextType = leafTextType;
	}
	
	@Override
	public void process(Tree tree, JCas cas) {
		TreeUtil.finalizeTreeLeaves(cas, tree, this.leafTextType);
	}

}
