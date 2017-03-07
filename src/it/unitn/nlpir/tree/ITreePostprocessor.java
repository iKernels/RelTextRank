package it.unitn.nlpir.tree;

import org.apache.uima.jcas.JCas;
import edu.stanford.nlp.trees.Tree;

public interface ITreePostprocessor {
	public void process(Tree tree, JCas cas);
}
