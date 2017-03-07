package it.unitn.nlpir.tree.leaffinalizers;

import java.util.Collection;

import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

public class LeafSPTKFullFinalizer implements ITreePostprocessor {
	//private static LeavesTransformer lf = new LeavesTransformer(new Alphabet());
	
	@Override
	public void process(Tree tree, JCas cas) {
		leafToLsiMatrixId(tree, cas);
	}
	
	public void leafToLsiMatrixId(Tree tree, JCas cas) {
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		Token[] tokens = x.toArray(new Token[x.size()]);
		for (Tree leaf : tree.getLeaves()) {
			Integer id;
			try {
				id = Integer.parseInt(leaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			Token token = tokens[id];
			String leafText = token.getLemma().toLowerCase().replace("(", "[").replace(")", "]");
			//String leafText = token.getCoveredText().toLowerCase().replace("(", "[").replace(")", "]");
			if (!token.getIsFiltered()) {
				leafText = String.format("%s::%s", leafText, token.getPostag().substring(0, 1).toLowerCase());
			}
			TreeUtil.setNodeLabel(leaf, leafText);
		}
	}

}
