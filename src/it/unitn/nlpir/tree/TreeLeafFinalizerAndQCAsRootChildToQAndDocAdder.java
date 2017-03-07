package it.unitn.nlpir.tree;

import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

public class TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder implements ITreePostprocessor {
	private static final String defaultLeafTextType = TokenTextGetterFactory.LEMMA;
	private String leafTextType;
	
	public TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder() {
		this(defaultLeafTextType);
	}
	
	public TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder(String leafTextType) {
		this.leafTextType = leafTextType;
	}
	
	@Override
	public void process(Tree tree, JCas cas) {
		TreeUtil.finalizeTreeLeaves(cas, tree, this.leafTextType);
		//this is temporary and should be removed
		
		if (JCasUtil.select(cas, QuestionClass.class).size()>0){
			String qClass = JCasUtil.selectSingle(cas, QuestionClass.class).getQuestionClass();
			tree.addChild(TreeUtil.createNode(qClass));
		}
	}

}
