package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.NERUtil;
import it.unitn.nlpir.util.TreeUtil;

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
public class FocusEntityHierNodeMatcher extends FocusEntityNodeMatcher implements NodeMatcher {

	private final static Logger logger = LoggerFactory.getLogger(FocusEntityHierNodeMatcher.class);	

	public FocusEntityHierNodeMatcher(MatchingStrategy strategy, String relTag,
			boolean typedRelTag) {
		super(strategy, relTag, typedRelTag, true);
		// TODO Auto-generated constructor stub
	}
	
	
	public FocusEntityHierNodeMatcher(MatchingStrategy strategy, String relTag,
			boolean typedRelTag, boolean addFocusToQuestion) {
		super(strategy, relTag, typedRelTag, addFocusToQuestion);
		// TODO Auto-generated constructor stub
	}
	
	public FocusEntityHierNodeMatcher(MatchingStrategy strategy,
			boolean typedRelTag, boolean addFocusToQuestion) {
		// TODO Auto-generated constructor stub
		super(strategy, typedRelTag, addFocusToQuestion);
	}


	protected String getLabel(String [] labels){
		Tree root = null;
		Tree curNode = null;
		
		if (labels.length > 0){
			root = TreeUtil.createNode(labels[0]);
			curNode = root;
		}
		
		for (int i = 1; i < labels.length; i++){
			Tree newNode = TreeUtil.createNode(labels[i]);
			curNode.addChild(newNode);
			curNode = newNode;
		}
		

		return TreeUtil.serializeTree(root);
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

		List<String> qClassRelatedNERTypes = NERUtil
				.resolveQuestionCategory2EntityClassTypes(questionClass);
		HashSet<String> nerTypes = new HashSet<>(qClassRelatedNERTypes);

		
		String focusTag;
		if (this.typedRelTag) {
			focusTag = getLabel(new String[]{questionClass});//String.format("%s-FOCUS-%s", this.relTag, questionClass);
		} else {
			focusTag = getLabel(new String[]{String.format(this.relTag)});//String.format("%s-FOCUS", this.relTag);
		}

		List<MatchedNode> matches = new ArrayList<>();

		QuestionFocus questionFocus = JCasUtil.selectSingle(questionCas, QuestionFocus.class);
		List<Token> qTokenFocus = JCasUtil.selectCovered(Token.class, questionFocus);

		if (qTokenFocus.isEmpty()) {
			logger.info("Failed to recover focus token for the sentence: {}", questionCas.getDocumentText());
			return matches;
		}
		Token qFocus = qTokenFocus.get(0);
		Tree qFocusNode = qLeafNodes[qFocus.getId()];
		if (qFocusNode != null) {
			strategy.doMatching(questionTree, qFocusNode, matches, focusTag);

			// Add REL nodes from the candidate answer.
			for (NER ner : JCasUtil.select(documentCas, NER.class)) {
				if (nerTypes.contains(ner.getNERtype())) {
					List<Token> tokens = JCasUtil.selectCovered(documentCas, Token.class, ner);
					for (Token token : tokens) {
						int id = token.getId();
						Tree node = docLeafNodes[id];
						if (node != null) {
							strategy.doMatching(documentTree, node, matches, focusTag);
						}
					}
				}
			}
		}

		return matches;
	}

}
