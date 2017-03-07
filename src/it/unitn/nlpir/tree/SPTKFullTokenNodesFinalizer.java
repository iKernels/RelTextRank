package it.unitn.nlpir.tree;

import java.util.Collection;

import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class SPTKFullTokenNodesFinalizer implements ITreePostprocessor {
	//private static LeavesTransformer lf = new LeavesTransformer(new Alphabet());
	protected boolean toLowerCase = true; 
	public SPTKFullTokenNodesFinalizer(boolean toLowerCase){
		this.toLowerCase = toLowerCase;
	}
	
	public SPTKFullTokenNodesFinalizer(){
		this(true);
	}
	
	@Override
	public void process(Tree tree, JCas cas) {
		leafToLsiMatrixId(tree, cas);
	}
	
	public void leafToLsiMatrixId(Tree tree, JCas cas) {
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		Token[] tokens = x.toArray(new Token[x.size()]);
		
		TregexPattern tgrepPattern = TregexPattern.compile("/[0-9]+/");
		
		TregexMatcher m = tgrepPattern.matcher(tree);
		while (m.find()) {
		    Tree subtree = m.getMatch();
		    Integer id;
			try {
				id = Integer.parseInt(subtree.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			Token token = tokens[id];
			String leafText = toLowerCase ? token.getLemma().toLowerCase().replace("(", "[").replace(")", "]") : token.getLemma().replace("(", "[").replace(")", "]");
			//String leafText = token.getCoveredText().toLowerCase().replace("(", "[").replace(")", "]");
			//if (!token.getIsFiltered()) {
			leafText = String.format("%s::%s", leafText, token.getPostag().substring(0, 1).toLowerCase());
			
			TreeUtil.setNodeLabel(subtree, leafText);
		}
		
	}

}
