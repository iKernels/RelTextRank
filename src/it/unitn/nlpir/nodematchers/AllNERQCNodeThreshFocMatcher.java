package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.types.Token;
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
public class AllNERQCNodeThreshFocMatcher extends FocusEntityNodeThreshFocMatcher implements NodeMatcher {
	private final static Logger logger = LoggerFactory.getLogger(AllNERQCNodeThreshFocMatcher.class);
	
	private final static String defaultRelTag = "REL";
	
	public AllNERQCNodeThreshFocMatcher(MatchingStrategy strategy) {
		this(strategy, defaultRelTag, false, false);
	}
	
	public AllNERQCNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag) {
		this(strategy, defaultRelTag, typedRelTag, false);
	}
	
	public AllNERQCNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag);
	}
	
	public AllNERQCNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag, boolean typeFocusTagInQuestion) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag,typeFocusTagInQuestion);
	}
	
	public AllNERQCNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag,
			boolean typeFocusTagInQuestion, MatchingStrategy focusMatchingStrategy) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag,typeFocusTagInQuestion,focusMatchingStrategy);
	}
	
	public AllNERQCNodeThreshFocMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, boolean markQuestionTag) {
		this(strategy,relTag,typedRelTag,markQuestionTag, true);
	}
	
	public AllNERQCNodeThreshFocMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, 
			boolean markQuestionTag, boolean typeFocusTagInQuestion, MatchingStrategy focusMatchingStrategy) {
		super(strategy, relTag, typedRelTag, markQuestionTag,typeFocusTagInQuestion, focusMatchingStrategy);
	}
	public AllNERQCNodeThreshFocMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, 
			boolean markQuestionTag, boolean typeFocusTagInQuestion) {
		super(strategy, relTag, typedRelTag, markQuestionTag, typeFocusTagInQuestion);
	}

	@Override
	public List<MatchedNode> getMatches(JCas questionCas, JCas documentCas, Tree questionTree,
			Tree documentTree) {
		List<Token> qTokens = new ArrayList<>();
		for (Token qToken : JCasUtil.select(questionCas, Token.class)) {
			qTokens.add(qToken);
		}
		List<Token> docTokens = new ArrayList<>();
		for (Token docToken : JCasUtil.select(documentCas, Token.class)) {
			docTokens.add(docToken);
		}

		// Get an index of question leaf nodes
		Tree[] qLeafNodes = buildTokenId2TreeLeafIndex(qTokens, questionTree);
		Tree[] docLeafNodes = buildTokenId2TreeLeafIndex(docTokens, documentTree);

		String questionClass = JCasUtil.selectSingle(questionCas, QuestionClass.class)
				.getQuestionClass();

		
		String focusTag;
		if (this.typedRelTag) {
			focusTag = String.format("%s-FOCUS-%s", this.relTag, questionClass);
		} else {
			focusTag = String.format("%s-FOCUS", this.relTag);
		}

		List<MatchedNode> matches = new ArrayList<>();

		Tree qFocusNode = getFocusLeafNode(questionCas, qLeafNodes);

			if ((qFocusNode!=null)&&(this.markQuestionTag)){
				//strategy.doMatching(questionTree, qFocusNode, matches, focusTag);
				matchQuestionTree(questionTree, qFocusNode, matches, focusTag);
			}

			// Add REL nodes from the candidate answer.
			for (NER ner : JCasUtil.select(documentCas, NER.class)) {
					if (this.typedRelTag) {
						focusTag = String.format("%s-%s-%s", this.relTag,ner.getNERtype(), questionClass);
					} else {
						focusTag = String.format("%s-%s", this.relTag, ner.getNERtype());
					}
					List<Token> tokens = JCasUtil.selectCovered(documentCas, Token.class, ner);
					for (Token token : tokens) {
						int id = token.getId();
						Tree node = docLeafNodes[id];
						if (node != null) {
							matchAnswerTree(documentTree, node, matches, focusTag);
					}
				}
			}
		return matches;
	}
	
	
}
