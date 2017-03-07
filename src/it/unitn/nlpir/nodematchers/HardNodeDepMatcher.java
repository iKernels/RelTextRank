package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.UIMAUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.cleartk.syntax.dependency.type.DependencyNode;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

/**
 * This class performs a hard match on the tokens of the type specified by the
 * {@matchingTokenTextType} passed in the constructor.
 * By default matching is done on the lemmas.
 * The matches are marked by the dependency role of the same lemma in the question.
 * 
 */
public class HardNodeDepMatcher extends HardNodeMatcher implements NodeMatcher {
	

	public HardNodeDepMatcher(MatchingStrategy strategy) {
		this(defaultMatchingTokenTextType, defaultRelTag, strategy);
	}

	public HardNodeDepMatcher(String matchingTokenTextType, String relTag, MatchingStrategy strategy) {
		super(matchingTokenTextType, relTag, strategy);
		
	}
	
	protected String getRelTag(Token token, Tree tree, Tree node, JCas cas){
		String depRole = "NULL";
		for (DependencyNode dn : JCasUtil.selectCovered(cas, DependencyNode.class, token)){
			if (dn.getHeadRelations().size()>0)
				depRole = dn.getHeadRelations(0).getRelation();
		}
		return this.relTag+"-"+depRole;
	}
	
	
	
	@Override
	public List<MatchedNode> getMatches(JCas questionCas, JCas documentCas, Tree questionTree, Tree documentTree) {
		
		Map<Integer, Tree> docMap = TreeUtil.getTokenIdToTreeNodeMap(documentTree);
		Map<Integer, Tree> qMap = TreeUtil.getTokenIdToTreeNodeMap(questionTree);
		List<Token> qTokens = UIMAUtil.getAnnotationsAsList(questionCas, Token.class);
		List<Token> docTokens = UIMAUtil.getAnnotationsAsList(documentCas, Token.class);
		Map<Integer, Token> docIdMap = UIMAUtil.getIDTokenMap(documentCas);
		Map<Integer, Token> qIdMap = UIMAUtil.getIDTokenMap(questionCas);
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
				if (qValue.equals(docValue)) {
					logger.debug("Matched pair: ({}, {})", qValue, docValue);
					strategy.doMatching(documentTree, docLeaf, matches, getRelTag(qToken, questionTree, qLeaf, questionCas));
				}
			}
		}
		return matches;
	}
	
}
