package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
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

/**
 * This class performs a hard match on the tokens of the type specified by the
 * {@matchingTokenTextType} passed in the constructor.
 * By default matching is done on the lemmas.
 * 
 */
public class HardNodeMatcher implements NodeMatcher {
	protected final Logger logger = LoggerFactory.getLogger(HardNodeMatcher.class);
	
	public static final String defaultMatchingTokenTextType = TokenTextGetterFactory.LEMMA;
	public static final String defaultRelTag = "REL";
	
	protected TokenTextGetter tokenTextGetter;
	protected String relTag;
	protected MatchingStrategy strategy;

	public HardNodeMatcher(MatchingStrategy strategy) {
		this(defaultMatchingTokenTextType, defaultRelTag, strategy);
	}

	
	
	public HardNodeMatcher(String matchingTokenTextType, String relTag, MatchingStrategy strategy) {
		this.tokenTextGetter = TokenTextGetterFactory
				.getTokenTextGetter(matchingTokenTextType);
		this.strategy = strategy;
		this.relTag = relTag;
	}

	protected String getRelTag(JCas questionCas, JCas documentCas, Tree qLeaf, Tree dLeaf){
		return this.relTag;
	}
	
	protected boolean skipToken(Token t){
		if (t.getIsFiltered())
			return true;

		if (t.getCoveredText().matches("[\\s\\-\\ ]*url[\\s\\-\\ ]*"))
			return true;
		
		return false;
	}
	
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
		for (Tree qLeaf : questionTree.getLeaves()) {
			Integer qTokenId;
			try {
				qTokenId = Integer.parseInt(qLeaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}

			Token qToken = qTokens.get(qTokenId);
			if (skipToken(qToken)) continue;
			String qValue = tokenTextGetter.getTokenText(qToken).toLowerCase();
			if (qValue == null) {
				qValue = qToken.getLemma();
			}
			
			for (Tree docLeaf : documentTree.getLeaves()) {
				Integer docTokenId;
				try {
					docTokenId = Integer.parseInt(docLeaf.nodeString());
				} catch (NumberFormatException e) {
					continue;
				}
				
				Token docToken = docTokens.get(docTokenId);
				if (skipToken(docToken)) continue;
				String docValue = tokenTextGetter.getTokenText(docToken).toLowerCase();
				if (docValue == null) {
					docValue = docToken.getLemma();
				}
				logger.debug("Comparing pair: ({}, {})", qValue, docValue);
				if (checkEquality(qToken,docToken)) {
					logger.debug("Matched pair: ({}, {})", qValue, docValue);
					strategy.doMatching(questionTree, qLeaf, matches, this.relTag);
					strategy.doMatching(documentTree, docLeaf, matches, this.relTag);
				}
			}
		}
		return matches;
	}
	
	protected boolean checkEquality(Token token1, Token token2){
		String word1 = tokenTextGetter.getTokenText(token1).toLowerCase();
		if (word1 == null) {
			word1 = token1.getLemma();
		}
		
		String word2 = tokenTextGetter.getTokenText(token2).toLowerCase();
		if (word2 == null) {
			word2 = token2.getLemma();
		}
		
		return word1.equals(word2);
	}
}
