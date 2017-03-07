package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.DocumentId;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class MapHardNodeMatcher implements NodeMatcher {
	private final Logger logger = LoggerFactory.getLogger(MapHardNodeMatcher.class);
	
	private static final String defaultMatchingTokenTextType = TokenTextGetterFactory.LEMMA;
	private static final String defaultRelTag = "REL";
	
	private TokenTextGetter tokenTextGetter;
	private String relTag;
	MatchingStrategy strategy;

	public MapHardNodeMatcher(MatchingStrategy strategy) {
		this(defaultMatchingTokenTextType, defaultRelTag, strategy);
	}

	public MapHardNodeMatcher(String matchingTokenTextType, String relTag, MatchingStrategy strategy) {
		this.tokenTextGetter = TokenTextGetterFactory
				.getTokenTextGetter(matchingTokenTextType);
		this.strategy = strategy;
		this.relTag = relTag;
	}

	@Override
	public List<MatchedNode> getMatches(JCas questionCas, JCas documentCas, Tree questionTree, Tree documentTree) {
		Map<Integer,Token> qTokens = new HashMap<Integer,Token>();
		for (Token qToken : JCasUtil.select(questionCas, Token.class)) {
			qTokens.put(qToken.getId(), qToken);
		}

		Map<Integer,Token> docTokens = new HashMap<Integer,Token>();
		for (Token docToken : JCasUtil.select(documentCas, Token.class)) {
			docTokens.put(docToken.getId(), docToken);
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
			if (qToken.getIsFiltered())
				continue;

			if (qToken.getCoveredText().matches("[\\s\\-\\ ]*url[\\s\\-\\ ]*"))
				continue;
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
				if (docToken==null){
					System.out.println(docTokenId);
					System.out.println(JCasUtil.selectSingle(documentCas, DocumentId.class).getId());
				}
				if (docToken.getIsFiltered())
					continue;
				if (docToken.getCoveredText().matches("[\\s\\-\\ ]*url[\\s\\-\\ ]*"))
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
