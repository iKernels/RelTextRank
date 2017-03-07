package it.unitn.nlpir.pruners;

import edu.stanford.nlp.trees.Tree;

public interface Pruner {
	public Tree prune(Tree tree);
}
