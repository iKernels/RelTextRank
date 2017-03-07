package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.nodematchers.lct.LCTTreeUtils;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

/**
 * This class performs a hard match on the tokens of the type specified by the
 * {@matchingTokenTextType} passed in the constructor.
 * By default matching is done on the lemmas.
 * 
 */
public class LCTHardNodeMatcher implements NodeMatcher {
	private final Logger logger = LoggerFactory.getLogger(LCTHardNodeMatcher.class);
	
	private static final String defaultMatchingTokenTextType = TokenTextGetterFactory.LEMMA;
	private static final String defaultRelTag = "REL";
	
	private TokenTextGetter tokenTextGetter;
	private String relTag;
	MatchingStrategy strategy;

	public LCTHardNodeMatcher(MatchingStrategy strategy) {
		this(defaultMatchingTokenTextType, defaultRelTag, strategy);
	}

	public LCTHardNodeMatcher(String matchingTokenTextType, String relTag, MatchingStrategy strategy) {
		this.tokenTextGetter = TokenTextGetterFactory
				.getTokenTextGetter(matchingTokenTextType);
		this.strategy = strategy;
		this.relTag = relTag;
	}

	
	/**
	 * This procedure assumes that all token nodes are encoded using integers
	 * @return
	 */
	/*protected List<Tree> getTokenNodes(Tree tree){
		List<Tree> trees = new ArrayList<Tree>();
		TregexPattern tgrepPattern = TregexPattern.compile("/[0-9]+/");
		TregexMatcher m = tgrepPattern.matcher(tree);
		while (m.find()) {
		    Tree subtree = m.getMatch();
		    trees.add(subtree);
		}
		return trees;
	}*/
	
	
	@Override
	public List<MatchedNode> getMatches(JCas questionCas, JCas documentCas, Tree questionTree, Tree documentTree) {
		List<Token> qTokens = new ArrayList<>();
		for (Token qToken : JCasUtil.select(questionCas, Token.class)) {
			qTokens.add(qToken);
		}

		List<Token> docTokens = new ArrayList<>();
		for (Token docToken : JCasUtil.select(documentCas, Token.class)) {
			docTokens.add(docToken);
		}
		
		List<MatchedNode> matches = new ArrayList<>();
		
		List<Tree> docTokenNodes = LCTTreeUtils.getTokenNodes(documentTree);
		for (Tree qLeaf : LCTTreeUtils.getTokenNodes(questionTree)) {
			Integer qTokenId;
			try {
				qTokenId = Integer.parseInt(qLeaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}

			Token qToken = qTokens.get(qTokenId);
			if (qToken.getIsFiltered())
				continue;

			String qValue = tokenTextGetter.getTokenText(qToken).toLowerCase();
			if (qValue == null) {
				qValue = qToken.getLemma();
			}
			
			for (Tree docLeaf : docTokenNodes) {
				Integer docTokenId;
				try {
					docTokenId = Integer.parseInt(docLeaf.nodeString());
				} catch (NumberFormatException e) {
					continue;
				}
				
				Token docToken = docTokens.get(docTokenId);
				if (docToken.getIsFiltered())
					continue;

				String docValue = tokenTextGetter.getTokenText(docToken).toLowerCase();
				if (docValue == null) {
					docValue = docToken.getLemma();
				}
				logger.debug("Comparing pair: ({}, {})", qValue, docValue);
				if (qValue.equals(docValue)) {
					logger.debug("Matched pair: ({}, {})", qValue, docValue);
					strategy.doMatching(questionTree, qLeaf, matches, this.relTag);
					strategy.doMatching(documentTree, docLeaf, matches, this.relTag);
				}
			}
		}
		return matches;
	}
}
