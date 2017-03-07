package it.unitn.nlpir.nodematchers.cqa;

import it.unitn.nlpir.nodematchers.FocusEntityNodeThreshFocMatcher;
import it.unitn.nlpir.nodematchers.MatchingStrategy;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.NERUtil;
import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.Collection;
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
public class MultiFocusEntityNodeThreshFocMatcher extends FocusEntityNodeThreshFocMatcher implements NodeMatcher {
	private final static Logger logger = LoggerFactory.getLogger(MultiFocusEntityNodeThreshFocMatcher.class);
	
	private final static String defaultRelTag = "REL";
	
	
	public MultiFocusEntityNodeThreshFocMatcher(MatchingStrategy strategy) {
		this(strategy, defaultRelTag, false, false);
	}
	
	public MultiFocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag) {
		this(strategy, defaultRelTag, typedRelTag, false);
	}
	
	public MultiFocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag);
	}
	
	public MultiFocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, boolean typedRelTag, boolean markQuestionTag, boolean typeFocusTagInQuestion) {
		this(strategy, defaultRelTag, typedRelTag, markQuestionTag,typeFocusTagInQuestion);
	}
	
	public MultiFocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, boolean markQuestionTag) {
		this(strategy,relTag,typedRelTag,markQuestionTag, true);
	}
	
	public MultiFocusEntityNodeThreshFocMatcher(MatchingStrategy strategy, String relTag, boolean typedRelTag, boolean markQuestionTag, boolean typeFocusTagInQuestion) {
		super(strategy, relTag, typedRelTag, markQuestionTag, typeFocusTagInQuestion);
		
	}

	
	protected void matchQuestionTree(Tree questionTree, Tree qFocusNode, List<MatchedNode> matches, String focusTag){
		//questionStrategy.doMatching(questionTree, qFocusNode, matches, focusTag);
		if (typeFocusTagInQuestion)
			strategy.doMatching(questionTree, qFocusNode, matches, focusTag);
		else
			strategy.doMatching(questionTree, qFocusNode, matches,  String.format("%s-FOCUS", this.relTag));
	}
	
	protected void matchAnswerTree(Tree answerTree, Tree node, List<MatchedNode> matches, String focusTag){
		
		strategy.doMatching(answerTree, node, matches, focusTag);
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

		Collection<QuestionClass> questionClasses = JCasUtil.select(questionCas, QuestionClass.class);
				
		List<MatchedNode> matches = new ArrayList<>();
		for (QuestionClass qc : questionClasses){
			String questionClass = qc.getQuestionClass();
			List<String> qClassRelatedNERTypes = getClassRelatedNERTypes(questionClass);
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
				if ((qFocusNode!=null)&&(markQuestionTag)){
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
		}
		return matches;
	}

	
	
	
	protected Tree getFocusLeafNode(JCas questionCas, Tree[] qLeafNodes) {
		Tree qFocusNode = null;
		QuestionFocus questionFocus = null;
		
		if (JCasUtil.select(questionCas, QuestionFocus.class).size()>0) questionFocus=JCasUtil.selectSingle(questionCas, QuestionFocus.class);
		if ((questionFocus!=null)&&(questionFocus.getConfidence()>0)){
			List<Token> qTokenFocus = JCasUtil.selectCovered(Token.class, questionFocus);
			if (!(qTokenFocus.isEmpty())){
				Token qFocus = qTokenFocus.get(0);
				qFocusNode = qLeafNodes[qFocus.getId()];
			}
			
		}
		else{
			qFocusNode = qLeafNodes[0];
		}
		return qFocusNode;
	}
	
	
}
