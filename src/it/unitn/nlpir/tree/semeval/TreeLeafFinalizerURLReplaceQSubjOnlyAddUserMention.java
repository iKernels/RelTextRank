package it.unitn.nlpir.tree.semeval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unitn.it.nlpir.types.UserMention;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.cqa.semeval.TreeLeafFinalizerURLReplacerQSentByTypePruner;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;



import edu.stanford.nlp.trees.Tree;

public class TreeLeafFinalizerURLReplaceQSubjOnlyAddUserMention extends TreeLeafFinalizerURLReplacerQSentByTypePruner implements ITreePostprocessor {
	
	
	
	public TreeLeafFinalizerURLReplaceQSubjOnlyAddUserMention() {
		this(defaultLeafTextType);
	}
	
	public TreeLeafFinalizerURLReplaceQSubjOnlyAddUserMention(String leafTextType) {
		super(leafTextType);
		
	}
	
	protected Set<Integer> getQuestionWordsSet(JCas cas){
		Set<Integer> questionLeaves = new HashSet<Integer>();
		return questionLeaves;
	}

	public void process(Tree tree, JCas cas) {
		Map<Integer,String> tokenToUserMentionName = new HashMap<Integer,String>();
		
		
		for (UserMention um : JCasUtil.select(cas, UserMention.class))
			for (Token t : JCasUtil.selectCovered(Token.class,um)){
				tokenToUserMentionName.put(t.getId(), um.getName());
				t.setLemma(um.getName());
			}
		
		for (Tree l : TreeUtil.getTreeIntegerNodes(tree))
			if (tokenToUserMentionName.containsKey(Integer.valueOf(l.value()))){
				l.parent(tree).setValue(l.parent(tree).value()+"-"+tokenToUserMentionName.get(Integer.valueOf(l.value())));
				//l.setValue(tokenToUserMentionName.get(Integer.valueOf(l.value())));
			}
		
		super.process(tree, cas);
	}
	
}
