package it.unitn.nlpir.tree.cqa.semeval;

import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;
import org.apache.uima.jcas.JCas;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class TreeLeafFinalizerURLReplacer implements ITreePostprocessor {
	protected static final String defaultLeafTextType = TokenTextGetterFactory.LEMMA;
	protected String leafTextType;
	
	public TreeLeafFinalizerURLReplacer() {
		this(defaultLeafTextType);
	}
	
	public TreeLeafFinalizerURLReplacer(String leafTextType) {
		this.leafTextType = leafTextType;
	}
	
	@Override
	public void process(Tree tree, JCas cas) {
		
		//this is temporary and should be removed
		
		
		TreeUtil.finalizeTreeLeaves(cas, tree, this.leafTextType);
		replaceURLs(tree);
		
		
	}

	protected void replaceURLs(Tree tree) {
		//go through leaves and if any of them starts with http or www -> substitue with URL
		TregexPattern tgrepPattern = TregexPattern.compile("/(http\\:\\/\\/|https\\:\\/\\/)?([a-z0-9][a-z0-9\\-]*\\.)+[a-z]+/");
		//TregexPattern tgrepPattern = TregexPattern.compile("/(http\\:\\/\\/|https\\:\\/\\/)([a-z0-9][a-z0-9\\-]*\\.)+[a-z]+/");
		
		TregexMatcher m = tgrepPattern.matcher(tree);
		
		while (m.find()) {
			 Tree urlNode = m.getMatch();
			 Tree urlParent = urlNode.parent(tree);
			 System.out.println(String.format("Found URL: %s", urlNode.value()));
			 urlNode.setValue("URL");
			 urlParent.setValue("URL");
			 
		}
	}

}
