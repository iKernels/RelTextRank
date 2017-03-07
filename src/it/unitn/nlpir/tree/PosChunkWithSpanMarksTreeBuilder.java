package it.unitn.nlpir.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unitn.nlpir.types.Chunk;
import it.unitn.nlpir.types.DiscourseTree;
import it.unitn.nlpir.types.Sentence;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.base.CharMatcher;

import edu.stanford.nlp.trees.Tree;


public class PosChunkWithSpanMarksTreeBuilder implements TreeBuilder {
	public static Pattern p = Pattern.compile("\\{([0-9]+)\\;([0-9]+)\\}");
	protected static final Logger logger = LoggerFactory.getLogger(PosChunkWithSpanMarksTreeBuilder.class);
	
	private final String sentenceRootLabel = "S";
	private final String paragraphRootLabel = "ROOT";
	private TokenTextGetter tokenTextGetter;
	
	
	public Tree getTree(JCas cas) {
		
		Tree tree = TreeUtil.createNode(paragraphRootLabel);
		tokenTextGetter = TokenTextGetterFactory.getTokenTextGetter(TokenTextGetterFactory.LEMMA);
		DiscourseTree discTree = JCasUtil.selectSingle(cas, DiscourseTree.class);
		Tree dTree = TreeUtil.buildTree(discTree.getTree());
		
		List<Coord> spans = new ArrayList<Coord>();
		for (Tree spanLeaf : dTree.getLeaves()){
			Matcher m = p.matcher(spanLeaf.value());
			m.find();
			spans.add(new Coord(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2))));
		}
		
		
		int minStartInd = 0;
		for (Sentence sentence : JCasUtil.select(cas, Sentence.class)) {
			// Tree for the sentence
			Tree sentenceTree = TreeUtil.createNode(sentenceRootLabel);
			
			for (Chunk chunk : JCasUtil.selectCovered(cas, Chunk.class, sentence)) {
				//get span
				
				int beginChunk = chunk.getBegin();
				int chunkIND=-1;
				for (int i = minStartInd; i < spans.size(); i++){
					if ((spans.get(i).getBegin()<=beginChunk)&&(spans.get(i).getEnd()>beginChunk)){
						minStartInd = i;
						chunkIND = i;
					}
				}
				
				if (chunkIND<-1){
					logger.error("Chunk '"+chunk.getCoveredText()+"' uncovered by span, will be skipped");
					continue;
				}
				Tree chunkNode = TreeUtil.createNode(chunk.getChunkType()+"SPAN"+String.valueOf(chunkIND));
				
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
		
		return tree;
	}
	
	private String cleanupLexical(String wordText) {
		return CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(wordText);
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
	
	public class Coord{
		private int begin;
		private int end;
		
		
		
		public Coord(int begin, int end) {
			super();
			this.begin = begin;
			this.end = end;
		}
		
		
		public int getBegin() {
			return begin;
		}


		public void setBegin(int begin) {
			this.begin = begin;
		}


		public int getEnd() {
			return end;
		}


		public void setEnd(int end) {
			this.end = end;
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + begin;
			result = prime * result + end;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Coord other = (Coord) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (begin != other.begin)
				return false;
			if (end != other.end)
				return false;
			return true;
		}
		private PosChunkWithSpanMarksTreeBuilder getOuterType() {
			return PosChunkWithSpanMarksTreeBuilder.this;
		}
		
	}
}
