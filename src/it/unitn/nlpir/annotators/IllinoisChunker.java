package it.unitn.nlpir.annotators;

import it.unitn.nlpir.types.Chunk;
import it.unitn.nlpir.types.Sentence;
import it.unitn.nlpir.types.Token;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import LBJ2.nlp.seg.WordsToTokens;
import LBJ2.parse.LinkedVector;
import edu.illinois.cs.cogcomp.lbj.chunk.Chunker;

public class IllinoisChunker extends JCasAnnotator_ImplBase {
	private final Logger logger = LoggerFactory.getLogger(IllinoisChunker.class);
	private Chunker chunker;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);	
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		
		// Lazy loading
		if(this.chunker == null) {
			init();
		}
		long startTime = System.currentTimeMillis();
		
		for(Sentence sentence : JCasUtil.select(cas, Sentence.class)) {		
			LinkedVector illinoisWords = getIllinoisWords(cas, sentence);
			LinkedVector illinoisTokens = WordsToTokens.convert(illinoisWords);
			
			List<LBJ2.nlp.seg.Token> tokens = new ArrayList<>();		
			for(int i = 0, n = illinoisTokens.size(); i < n; i++) {
				LBJ2.nlp.seg.Token currentToken = (LBJ2.nlp.seg.Token) illinoisTokens.get(i);
				String tag = this.chunker.discreteValue(currentToken);
				// Assign the tag to the token label
				currentToken.label = tag;
				
				if(tag.startsWith("B-")) {
					extractChunk(tokens, cas);
					tokens.add(currentToken);
				} else if(tag.startsWith("I-")) {
					tokens.add(currentToken);
				} else {
					extractChunk(tokens, cas);
				}
			}

			extractChunk(tokens, cas);
		}

		long endTime = System.currentTimeMillis();
		
		logger.debug("Illinois chunker took {} ms to process", String.valueOf(endTime - startTime));
	}

	private LinkedVector getIllinoisWords(JCas cas, Sentence sentence) {
		LinkedVector illinoisWords = new LinkedVector();	
		for(Token token : JCasUtil.selectCovered(cas, Token.class, sentence)) {
			// Build a word with the Token data
			LBJ2.nlp.Word word = new LBJ2.nlp.Word(token.getCoveredText(),
					token.getPostag(), token.getBegin(), token.getEnd());
			// Add the word to the word list
			illinoisWords.add(word);
		}
		return illinoisWords;
	}

	private void extractChunk(List<LBJ2.nlp.seg.Token> tokens, JCas cas) {
		if(!tokens.isEmpty()) {
			int begin = tokens.get(0).start;
			int end = tokens.get(tokens.size() - 1).end;
			
			Chunk chunk = new Chunk(cas);
			chunk.setBegin(begin);
			chunk.setChunkType(tokens.get(0).label.substring(2));
			chunk.setEnd(end);
			chunk.addToIndexes();
			
			tokens.clear();
		}
	}
	
	private void init() {
		this.chunker = new Chunker();
	}

}
