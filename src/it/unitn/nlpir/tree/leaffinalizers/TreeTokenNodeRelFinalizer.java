package it.unitn.nlpir.tree.leaffinalizers;

import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public class TreeTokenNodeRelFinalizer implements ITreePostprocessor {
	private static final String defaultLeafTextType = TokenTextGetterFactory.LEMMA;
	private String leafTextType;
	
	public TreeTokenNodeRelFinalizer() {
		this(defaultLeafTextType);
	}
	
	public TreeTokenNodeRelFinalizer(String leafTextType) {
		this.leafTextType = leafTextType;
	}
	
	@Override
	public void process(Tree tree, JCas cas) {
		TreeUtil.finalizeTreeTokenNodesRelAsSPTK(cas, tree, this.leafTextType);
	}

}
