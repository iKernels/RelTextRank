package it.unitn.nlpir.tree.cqa.semeval;

import java.util.HashSet;
import java.util.Set;

import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.types.DocumentId;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.semeval.SemevalTreeUtil;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class TreeLeafFinalizerURLReplacerQSentByTypePruner extends TreeLeafFinalizerURLReplacer implements ITreePostprocessor {
	protected static final String defaultLeafTextType = TokenTextGetterFactory.LEMMA;
	protected TregexPattern tgrepPattern;
	
	
	public TreeLeafFinalizerURLReplacerQSentByTypePruner() {
		this(defaultLeafTextType);
	}
	
	public TreeLeafFinalizerURLReplacerQSentByTypePruner(String leafTextType) {
		super(leafTextType);
		tgrepPattern = TregexPattern.compile("/(SBARQ|SINV|SQ)/");
	}
	
	protected Set<Integer> getQuestionWordsSet(JCas cas){
		Set<Integer> questionLeaves = new HashSet<Integer>();
		//find SINV/SBARQ
		try {
			Tree ctree = TreeUtil.buildTree(UIMAUtil.getConstituencyTree(cas));
			TregexMatcher m = tgrepPattern.matcher(ctree);
			while (m.find()) {
			    Tree subtree = m.getMatch();
			    for (Tree l : TreeUtil.getTreeIntegerNodes(subtree)){
			    	questionLeaves.add(Integer.valueOf(l.value()));
			    }
			}
		} catch (AnnotationNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return questionLeaves;
	}
	
	
	protected void finalizeQuestionLeaves(JCas cas, Tree tree, String type){
		TreeUtil.finalizeTreeLeaves(cas, tree, this.leafTextType);
		replaceURLs(tree);
	}
	
	protected void finalizeDocumentLeaves(JCas cas, Tree tree, String type){
		TreeUtil.finalizeTreeLeaves(cas, tree, this.leafTextType);
		replaceURLs(tree);
	}
	
	@Override
	public void process(Tree tree, JCas cas) {
		
		
		//to be updated in future, as this is unrealiable
		String id = JCasUtil.selectSingle(cas, DocumentId.class).getId();
		
		
		//processing a document
		if (!(id.startsWith("question"))){
			Tree nosign = SemevalTreeUtil.pruneSignatures(cas, tree);
			SemevalTreeUtil.updateTheTree(tree, nosign);
			//TreeUtil.finalizeTreeLeaves(cas, tree, this.leafTextType);
			finalizeDocumentLeaves(cas, tree, this.leafTextType);
			
			return;
		}
		
		//processing a question
		Set<Integer> questionLeaves = getQuestionWordsSet(cas);
		for (Tree s : tree.getChildrenAsList()){
			if (s.value().contains("SUBJ"))
				continue;
			for (Tree l: s.getLeaves()){
				if ((!l.value().matches("[0-9]+"))||(!questionLeaves.contains(Integer.valueOf(l.value()))))
					l.setValue(TreeUtil.REMOVE_LABEL);
			}
		}
		Tree cl = TreeUtil.pruneLeavesToRemove(tree);
		SemevalTreeUtil.updateTheTree(tree, cl);
		finalizeQuestionLeaves(cas, tree, this.leafTextType);
		
	}

}
