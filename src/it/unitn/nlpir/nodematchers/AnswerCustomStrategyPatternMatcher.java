package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.AnswerPattern;
import it.unitn.nlpir.types.Chunk;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.StanfordCustomTag;
import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import edu.stanford.nlp.trees.Tree;

/**
 * This class performs a hard match on the tokens of the type specified by the
 * {@matchingTokenTextType} passed in the constructor.
 * By default matching is done on the lemmas.
 * 
 */
public class AnswerCustomStrategyPatternMatcher implements NodeMatcher {
	private final Logger logger = LoggerFactory.getLogger(AnswerCustomStrategyPatternMatcher.class);
	
	private String answerTag;
	private MatchingStrategy strategy;
	public AnswerCustomStrategyPatternMatcher(MatchingStrategy strategy, String answerTag) {
		this.answerTag = answerTag;
		this.strategy = strategy;
	}

	@Override
	public List<MatchedNode> getMatches(JCas questionCas, JCas documentCas,
			Tree questionTree, Tree documentTree) {
		
		List<MatchedNode> matches = new ArrayList<>();
		
		AnswerPattern annotation = JCasUtil.selectSingle(documentCas, AnswerPattern.class);
		
		if(annotation.getIsMatched()) {
			List<Token> tokens = JCasUtil.selectCovered(Token.class, annotation);
			List<Integer> answerTokenIds = new ArrayList<>(); 
			for (Token t : tokens) {
				answerTokenIds.add(t.getId());
			}
			if (documentTree.numChildren() > 0) {
				for (Tree leaf : documentTree.getLeaves()) {
					Integer id = Integer.parseInt(leaf.nodeString());
					if (answerTokenIds.contains(id)) {
						//Tree node = leaf.parent(documentTree).parent(documentTree);
						//strategy.doMatching(documentTree, node, matches, focusTag);
						

						strategy.doMatching(documentTree, leaf, matches, this.answerTag); 
					}
				}
			}
		}
		
		return matches;
	}
}
