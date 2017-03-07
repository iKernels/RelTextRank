package it.unitn.nlpir.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class SPTKFullTokenNodesNoNERFinalizer implements ITreePostprocessor {
	//private static LeavesTransformer lf = new LeavesTransformer(new Alphabet());
	protected boolean toLowerCase = true; 
	public SPTKFullTokenNodesNoNERFinalizer(boolean toLowerCase){
		this.toLowerCase = toLowerCase;
	}
	
	public SPTKFullTokenNodesNoNERFinalizer(){
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
		//get all NERS
		
		Set<Integer> tokensToIgnore = new HashSet<Integer>();
		for (NER ner : JCasUtil.select(cas, NER.class)) {
			for (Token t : JCasUtil.selectCovered(cas, Token.class, ner))
				tokensToIgnore.add(t.getId());
		}
		
		
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
			if (!tokensToIgnore.contains(id))
				leafText = String.format("%s::%s", leafText, token.getPostag().substring(0, 1).toLowerCase());
			
			TreeUtil.setNodeLabel(subtree, leafText);
		}
		
	}

}
