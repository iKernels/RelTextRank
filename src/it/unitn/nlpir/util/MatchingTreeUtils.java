package it.unitn.nlpir.util;

import it.unitn.nlpir.nodematchers.MatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.types.CoreferenceChain;
import it.unitn.nlpir.types.Mention;
import it.unitn.nlpir.types.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

public class MatchingTreeUtils {
	static final Logger logger = LoggerFactory.getLogger(MatchingTreeUtils.class);
	public static Tree[] buildTokenId2TreeLeafIndex(List<Token> tokens, Tree tree) {
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
	
	public static Map<Integer,List<Tree>> buildTokenId2TreeLeafIndexWithDups(List<Token> tokens, Tree tree) {
		Map<Integer,List<Tree>> leafNodes = new HashMap<Integer,List<Tree>>();
		//Tree[] leafNodes = new Tree[tokens.size()];
		for (Tree qLeaf : tree.getLeaves()) {
			Integer qTokenId;
			try {
				qTokenId = Integer.parseInt(qLeaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			if (!leafNodes.containsKey(qTokenId))
				leafNodes.put(qTokenId, new ArrayList<Tree>());
			//leafNodes[qTokenId] = qLeaf;
			leafNodes.get(qTokenId).add(qLeaf);
		}
		return leafNodes;
	}
	
	/**
	 * 
	 * @param cas
	 * @param documentTree
	 * @param matches
	 * @param relTag
	 * @param tokenId
	 * @param tokenIDToLeafID
	 * @param strategy
	 * @param allowedPOS
	 */
	public static void doCorefMatching(JCas cas, Tree documentTree, List<MatchedNode> matches, String relTag, int tokenId, Tree[] tokenIDToLeafID, MatchingStrategy strategy, List<String> allowedPOS){
		doCorefMatching(cas, documentTree, matches, relTag, tokenId, tokenIDToLeafID, strategy, allowedPOS, null);
	}
	
	public static void doCorefMatching(JCas cas, Tree documentTree, List<MatchedNode> matches, String relTag, int tokenId, Tree[] tokenIDToLeafID, MatchingStrategy strategy, List<String> allowedPOS, Map<String, List<Integer>> map){
		if (map==null)
			map = MatchingTreeUtils.getCoreferenceChains(cas);
		if (map.size()==0) return;
		
		for (String chainID : map.keySet()){
			if (map.get(chainID).contains(tokenId)){
				for (Integer mentID : map.get(chainID)){
					if (mentID!=tokenId) {
						for (String pos : allowedPOS){
							if ((tokenIDToLeafID[mentID]==null)||(tokenIDToLeafID[mentID].parent(documentTree)==null)){
								continue;
							}
							if ((tokenIDToLeafID[mentID].parent(documentTree).value().matches(pos))){
								strategy.doMatching(documentTree, tokenIDToLeafID[mentID], matches, relTag);
								String source = null;
								String coref = null;
								for (Token t : JCasUtil.select(cas, Token.class)){
									if (t.getId()==tokenId){
										source = t.getCoveredText();
									}
									if (t.getId()==mentID){
										coref = t.getCoveredText();
									}
									if ((source!=null)&&(coref!=null)) break;
									
								}
								logger.debug(String.format("COREF: Marked as %s mention '%s' coreferent to '%s'", relTag, coref, source));
							}
							
						}
					
						
						
					}
				} //for (Integer mentID : map.get(chainID)){
			}//if (map.get(chainID).contains(tokenId)){
		} //for (Integer chainID : map.keySet()){
	}
	
	
	public static void doCorefMatching(JCas cas, Tree documentTree, List<MatchedNode> matches, String relTag, int tokenId, Tree[] tokenIDToLeafID, MatchingStrategy strategy, Map<String, List<Integer>> map){
		if (map==null)
			map = MatchingTreeUtils.getCoreferenceChains(cas);
		if (map.size()==0) return;
		
		for (String chainID : map.keySet()){
			if (map.get(chainID).contains(tokenId)){
				for (Integer mentID : map.get(chainID)){
					if (mentID!=tokenId) {
						strategy.doMatching(documentTree, tokenIDToLeafID[mentID], matches, relTag);
						
						String source = null;
						String coref = null;
						for (Token t : JCasUtil.select(cas, Token.class)){
							if (t.getId()==tokenId){
								source = t.getCoveredText();
							}
							if (t.getId()==mentID){
								coref = t.getCoveredText();
							}
							if ((source!=null)&&(coref!=null)) break;
						}
						logger.debug(String.format("COREF: Marked as %s mention '%s' coreferent to '%s'", relTag, coref, source));
					}
				} //for (Integer mentID : map.get(chainID)){
			}//if (map.get(chainID).contains(tokenId)){
		} //for (Integer chainID : map.keySet()){
	}
	
	/**
	 * returns a map with coreference chain ID as key and and values
	 * @return
	 */
	public static Map<String,List<Integer>> getCoreferenceChains(JCas cas){
		Map<String,List<Integer>> map = new HashMap<String,List<Integer>>();
		
		for (CoreferenceChain chain : JCasUtil.select(cas, CoreferenceChain.class)){
			FSArray mentions = chain.getMentions();
			List<Integer> tokenIds = new ArrayList<Integer>();
			for (int i = 0; i < mentions.size(); i++){
				Mention mention = (Mention) mentions.get(i);
				for (Token t : JCasUtil.selectCovered(Token.class, mention)){
					tokenIds.add(t.getId());
				}
			}
			map.put(chain.getId(), tokenIds);
		}
		
		
		return map;
	}
}
