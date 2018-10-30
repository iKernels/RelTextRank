package it.unitn.nlpir.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import it.unitn.nlpir.types.Chunk;
import it.unitn.nlpir.types.Sentence;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;


import edu.stanford.nlp.trees.Tree;

public class PosChunkFullTreeBuilder implements TreeBuilder {
	public static Pattern p = Pattern.compile("\\{([0-9]+)\\;([0-9]+)\\}");
	protected static final Logger logger = LoggerFactory.getLogger(PosChunkFullTreeBuilder.class);
	
	private final String sentenceRootLabel = "S";
	private final String paragraphRootLabel = "ROOT";
	private TokenTextGetter tokenTextGetter = TokenTextGetterFactory.getTokenTextGetter(TokenTextGetterFactory.LEMMA);
	
	
	
	public Tree[] generateChunks(JCas cas, Sentence sentence,
			Map<Integer, Integer> tokenChunkMap) {
		Tree[] chunks = new Tree[JCasUtil.selectCovered(cas, Chunk.class, sentence).size()];
		int i = 0;
		
		//creating chunk nodes
		for (Chunk chunk : JCasUtil.selectCovered(cas, Chunk.class, sentence)) {
			//get span
			Tree chunkNode = TreeUtil.createNode(chunk.getChunkType());
			chunks[i] = chunkNode;
			for (Token token : JCasUtil.selectCovered(cas, Token.class, chunk)) {
				tokenChunkMap.put(token.getId(), i);
			}
			
			// Add tokens spanned by this chunk
			for (Token token : JCasUtil.selectCovered(cas, Token.class, chunk)) {
				addTokenToCurrentChunk(chunkNode, token);
			}
			i++;
		}
		return chunks;
	}
	
	protected Tree createPseudoChunk(Token token) {
		Tree chunkNode;
		chunkNode = TreeUtil.createNode(token.getPostag());
		addTokenToCurrentChunk(chunkNode, token);
		return chunkNode;
	}
	public Tree getTree(JCas cas) {
		
		Tree tree = TreeUtil.createNode(paragraphRootLabel);
		
		for (Sentence sentence : JCasUtil.select(cas, Sentence.class)) {
			// Tree for the sentence
			Tree sentenceTree = TreeUtil.createNode(sentenceRootLabel);
			Map<Integer,Integer> tokenChunkMap = new HashMap<Integer,Integer>();
			Tree[] chunks = generateChunks(cas, sentence, tokenChunkMap);

			//start adding token nodes
			Set<Integer> addedChunk = new HashSet<Integer>();
			for (Token token: JCasUtil.selectCovered(cas, Token.class, sentence)) {
				Tree chunkNode = null;
				if (tokenChunkMap.containsKey(token.getId())){
					Integer chunkID = tokenChunkMap.get(token.getId());
					if (!addedChunk.contains(chunkID)){
						chunkNode = chunks[chunkID];
						addedChunk.add(chunkID);
					}
				}
				else {//create a singular pseudo chunk 
					chunkNode = createPseudoChunk(token);
					
				}
				if ((chunkNode != null) && (chunkNode.numChildren() > 0)) {
					sentenceTree.addChild(chunkNode);
				}
				
			}
			
			
			// Collect trees of each sentence under the common root
			if (sentenceTree.numChildren() > 0)
				tree.addChild(sentenceTree);
		}
		
		return tree;
	}

	
	
	private String cleanupLexical(String wordText) {
		//return CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(wordText);
		return wordText;
	}
	
	protected void addTokenToCurrentChunk(Tree currentChunkNode, Token token) {
		Tree tagNode = TreeUtil.createNode(token.getPostag());
		

		// Clean up the lexicals we add to the tree.
		// Remove non-alpha chars and collapse all digits to a single 0.
		String wordText = tokenTextGetter.getTokenText(token);
		wordText = cleanupLexical(wordText);
		if (wordText.isEmpty())
			return;

		// Tree wordNode = TreeUtil.createNode(lowercase ?
		// wordText.toLowerCase() : wordText);
		Tree wordNode = TreeUtil.createNode(String.valueOf(token.getId()));
		tagNode.addChild(wordNode);
		currentChunkNode.addChild(tagNode);
	}
	
	
}
