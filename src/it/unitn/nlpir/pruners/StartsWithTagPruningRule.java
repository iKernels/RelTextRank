package it.unitn.nlpir.pruners;

import it.unitn.nlpir.util.StanfordCustomTag;
import edu.stanford.nlp.trees.Tree;

public class StartsWithTagPruningRule implements PruningRule {
	
	private final String tag;
	
	public StartsWithTagPruningRule(String tag) {
		this.tag = tag;
	}

	@Override
	public boolean isSatisfiedOn(Tree node) {
		String customTag = StanfordCustomTag.getTag(node);
		return customTag != null;
	}

}
