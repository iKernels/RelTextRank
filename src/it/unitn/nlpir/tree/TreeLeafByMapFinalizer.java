package it.unitn.nlpir.tree;

import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;
import edu.stanford.nlp.trees.Tree;

public class TreeLeafByMapFinalizer implements ITreePostprocessor {
	private static final String defaultLeafTextType = TokenTextGetterFactory.LEMMA;
	private String leafTextType;
	protected boolean keepCase;
	public TreeLeafByMapFinalizer() {
		this(defaultLeafTextType, false);
	}
	public TreeLeafByMapFinalizer(boolean keepCase) {
		this(defaultLeafTextType, keepCase);
		
	}
	
	public TreeLeafByMapFinalizer(String leafTextType) {
		this(leafTextType, false);
		
	}
	
	public TreeLeafByMapFinalizer(String leafTextType, boolean keepCase) {
		this.leafTextType = leafTextType;
		this.keepCase = keepCase;
	}
	
	@Override
	public void process(Tree tree, JCas cas) {
		TreeUtil.finalizeTreeLeavesByMap(cas, tree, this.leafTextType, keepCase);
	}

}
