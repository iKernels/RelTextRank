package it.unitn.nlpir.annotators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unitn.nlpir.types.Chunk;
import it.unitn.nlpir.types.PosChunk;
import it.unitn.nlpir.types.Sentence;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.TypeCapability;
import org.uimafit.util.JCasUtil;

import com.google.common.base.CharMatcher;

import edu.stanford.nlp.trees.Tree;

@TypeCapability(inputs = { "it.unitn.nlpir.types.Chunk", "it.unitn.nlpir.types.Token:begin",
		"it.unitn.nlpir.types.Token:end", "it.unitn.nlpir.types.Token:postag",
		"it.unitn.nlpir.types.Token:lemma" }, outputs = { "it.unitn.nlpir.types.PosChunk" })
public class PosChunkAnnotator extends JCasAnnotator_ImplBase {
	public static final String PARAM_LOWERCASE = "lowercase";
	@ConfigurationParameter(name = PARAM_LOWERCASE, mandatory = false, defaultValue = "true", description = "Lowercase lexicals.")
	private boolean lowercase;

	public static final String PARAM_LEAF_TEXT_TYPE = "leafTextType";
	@ConfigurationParameter(name = PARAM_LEAF_TEXT_TYPE, defaultValue = TokenTextGetterFactory.LEMMA, mandatory = false, description = "Token text type: (raw text, stem, lemma, etc.).")
	private String leafTextType;

	private TokenTextGetter tokenTextGetter;

	private final String sentenceRootLabel = "S";
	private final String paragraphRootLabel = "ROOT";

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		tokenTextGetter = TokenTextGetterFactory.getTokenTextGetter(leafTextType);
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		Tree tree = TreeUtil.createNode(paragraphRootLabel);
		
		//this is a temporary measure
		Set<Integer> beginIdsOfchunks = new HashSet<Integer>();
		for (Sentence sentence : JCasUtil.select(cas, Sentence.class)) {
			// Tree for the sentence
			Tree sentenceTree = TreeUtil.createNode(sentenceRootLabel);
			for (Chunk chunk : JCasUtil.selectCovered(cas, Chunk.class, sentence)) {
				if (beginIdsOfchunks.contains(chunk.getBegin())) continue;
				beginIdsOfchunks.add(chunk.getBegin()); //this is temporary and must be removed
				Tree chunkNode = TreeUtil.createNode(chunk.getChunkType());
				// Add tokens spanned by this chunk
				for (Token token : JCasUtil.selectCovered(cas, Token.class, chunk)) {
					addTokenToCurrentChunk(chunkNode, token);
				}
				// Skip empty chunks
				if (chunkNode.numChildren() > 0) {
					// Update chunk label with the non-null topic of the last token
					sentenceTree.addChild(chunkNode);
				}
			}
			// Collect trees of each sentence under the common root
			if (sentenceTree.numChildren() > 0)
				tree.addChild(sentenceTree);
		}

		if (JCasUtil.select(cas, PosChunk.class).size()>0){
			Collection<PosChunk> posChunks = JCasUtil.select(cas, PosChunk.class);
			
			for (PosChunk chunk : posChunks){
				cas.removeFsFromIndexes(chunk);
			}
			
		}
		
		String posChunkTree = TreeUtil.serializeTree(tree);//tree.toString();  // simpler way to print the tree
		createPosChunkAnnotation(cas, posChunkTree);
	}

	private void createPosChunkAnnotation(JCas cas, String tree) {
		PosChunk posChunkTree = new PosChunk(cas);
		posChunkTree.setTree(tree);
		posChunkTree.addToIndexes();
	}

	private void addTokenToCurrentChunk(Tree currentChunkNode, Token token) {
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

	private String cleanupLexical(String wordText) {
		return CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(wordText);
	}

}
