package it.unitn.nlpir.pruners;

import edu.stanford.nlp.trees.Tree;

public interface PruningRule {
	boolean isSatisfiedOn(Tree node);
}
