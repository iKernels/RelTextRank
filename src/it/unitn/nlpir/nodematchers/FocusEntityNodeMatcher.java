package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.NERUtil;

import java.util.ArrayList;
import java.util.HashSet;
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
public class FocusEntityNodeMatcher implements NodeMatcher {
	private final static Logger logger = LoggerFactory.getLogger(FocusEntityNodeMatcher.class);
	
	private final static String defaultRelTag = "REL";
	
	protected MatchingStrategy strategy;
	protected MatchingStrategy focusMarkingStrategy;
	protected String relTag;
	protected boolean typedRelTag;
	protected boolean markQuestionTag = true;
	
	public FocusEntityNodeMatcher(MatchingStrategy strategy) {
		this(strategy, defaultRelTag, false, false);
	}
	
	public FocusEntityNodeMatcher(MatchingStrategy strategy, boolean typedRelTag) {
		this(strategy, defaultRelTag, typedRelTag, false);
	}
	
	public FocusEntityNodeMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag);
	}
	
	public FocusEntityNodeMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, boolean markQuestionTag) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag, strategy);
				
	}
	public FocusEntityNodeMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, boolean markQuestionTag, MatchingStrategy focusMatchingStrategy) {
		this.strategy = strategy;
		this.relTag = relTag;
		this.typedRelTag = typedRelTag;
		this.markQuestionTag = markQuestionTag;
		this.focusMarkingStrategy = focusMatchingStrategy;
				
	}
	
	protected void matchQuestionTree(Tree questionTree, Tree qFocusNode, List<MatchedNode> matches, String focusTag){
		strategy.doMatching(questionTree, qFocusNode, matches, focusTag);
	}
	
	protected void matchAnswerTree(Tree answerTree, Tree node, List<MatchedNode> matches, String focusTag){
		strategy.doMatching(answerTree, node, matches, focusTag);
	}
	
	
	protected List<String> getClassRelatedNERTypes(String questionClass){
		return  NERUtil
				.resolveQuestionCategory2EntityClassTypes(questionClass);
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
		List<MatchedNode> matches = new ArrayList<>();
		if (JCasUtil.select(questionCas, QuestionClass.class).size() ==0){
			Tree qFocusNode = getFocusLeafNode(questionCas, qLeafNodes);
			if ((qFocusNode!=null)&&(this.markQuestionTag)){
				String focusTag = String.format("%s-FOCUS", this.relTag);
				matchQuestionTree(questionTree, qFocusNode, matches, focusTag);
			}
		}
		
		String questionClass = JCasUtil.selectSingle(questionCas, QuestionClass.class)
				.getQuestionClass();

		List<String> qClassRelatedNERTypes = getClassRelatedNERTypes(questionClass);/*NERUtil
				.resolveQuestionCategory2EntityClassTypes(questionClass);*/
		HashSet<String> nerTypes = new HashSet<>(qClassRelatedNERTypes);

		String focusTag;
		if (this.typedRelTag) {
			focusTag = String.format("%s-FOCUS-%s", this.relTag, questionClass);
		} else {
			focusTag = String.format("%s-FOCUS", this.relTag);
		}

		
		//it.unitn.nlpir.experiment.rer.RERTrecQAWikiExperiment
		//it.unitn.nlpir.experiment.rer.TrecQAPosChunkTypedFocusPruneStanfordExperiment
		Tree qFocusNode = getFocusLeafNode(questionCas, qLeafNodes);
		//if (qFocusNode != null) {
			if ((qFocusNode!=null)&&(this.markQuestionTag)){
				//strategy.doMatching(questionTree, qFocusNode, matches, focusTag);
				matchQuestionTree(questionTree, qFocusNode, matches, focusTag);
			}

			// Add REL nodes from the candidate answer.
			for (NER ner : JCasUtil.select(documentCas, NER.class)) {
				if (nerTypes.contains(ner.getNERtype())) {
					List<Token> tokens = JCasUtil.selectCovered(documentCas, Token.class, ner);
					for (Token token : tokens) {
						int id = token.getId();
						Tree node = docLeafNodes[id];
						if (node != null) {
							//strategy.doMatching(documentTree, node, matches, focusTag);
							matchAnswerTree(documentTree, node, matches, focusTag);
						}
					}
				}
			}
		//}

		return matches;
	}

	protected Tree getFocusLeafNode(JCas questionCas, Tree[] qLeafNodes) {
		QuestionFocus questionFocus = JCasUtil.selectSingle(questionCas, QuestionFocus.class);
		
		List<Token> qTokenFocus = JCasUtil.selectCovered(Token.class, questionFocus);

		if (qTokenFocus.isEmpty()) {
			logger.info("Failed to recover focus token for the sentence: {}", questionCas.getDocumentText());
			return null;
		}
		Token qFocus = qTokenFocus.get(0);
		Tree qFocusNode = qLeafNodes[qFocus.getId()];
		return qFocusNode;
	}

	protected Tree[] buildTokenId2TreeLeafIndex(List<Token> tokens, Tree tree) {
		Tree[] leafNodes = new Tree[tokens.size()];
		for (Tree qLeaf : tree.getLeaves()) {
			Integer qTokenId;
			try {
				qTokenId = Integer.parseInt(qLeaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			leafNodes[qTokenId] = qLeaf;
		}
		return leafNodes;
	}
}
