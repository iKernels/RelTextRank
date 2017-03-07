package it.unitn.nlpir.tree.leaffinalizers;

import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.util.LeavesTransformer;

import org.apache.uima.jcas.JCas;

import cc.mallet.types.Alphabet;
import edu.stanford.nlp.trees.Tree;

public class LeafSPTKFinalizer implements ITreePostprocessor {
	private static LeavesTransformer lf = new LeavesTransformer(new Alphabet());
	
	@Override
	public void process(Tree tree, JCas cas) {
		lf.leafToLsiMatrixId(tree, cas);
	}

}
