package it.unitn.nlpir.tree.cqa.semeval;

import java.util.HashSet;
import java.util.Set;

import it.unitn.nlpir.tree.ITreePostprocessor;
import org.apache.uima.jcas.JCas;

public class TreeLeafFinalizerURLReplacerQSubjKeeperPruner extends TreeLeafFinalizerURLReplacerQSentByTypePruner implements ITreePostprocessor {
	
	
	
	public TreeLeafFinalizerURLReplacerQSubjKeeperPruner() {
		this(defaultLeafTextType);
	}
	
	public TreeLeafFinalizerURLReplacerQSubjKeeperPruner(String leafTextType) {
		super(leafTextType);
		
	}
	
	protected Set<Integer> getQuestionWordsSet(JCas cas){
		Set<Integer> questionLeaves = new HashSet<Integer>();
		
		
		
		return questionLeaves;
	}

}
