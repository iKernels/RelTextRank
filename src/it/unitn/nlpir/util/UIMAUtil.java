package it.unitn.nlpir.util;


import it.unitn.nlpir.types.Chunk;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.types.WikipediaPage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import org.uimafit.util.JCasUtil;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class UIMAUtil {
	protected static final Logger logger = LoggerFactory.getLogger(UIMAUtil.class);
	public static <T extends TOP> List<T> getAnnotationsAsList(JCas questionCas, Class<T> type){
		List<T> qTokens = new ArrayList<T>();
		
		for (T qToken : JCasUtil.select(questionCas, type)) {
			qTokens.add(qToken);
		}
		return qTokens;
	}
	
	public static Map<Integer, Token> getIDTokenMap(JCas jCas){
		Map<Integer, Token> map = new HashMap<Integer,Token>();
		for (Token t: JCasUtil.select(jCas, Token.class)){
			map.put(t.getId(), t);
		}
		return map;
	}
	
	public static List<WikipediaPage> getCleanWikiAnnotationsAsList(JCas questionCas){
		List<WikipediaPage> pages = new ArrayList<WikipediaPage>();
		
		for (WikipediaPage qToken : JCasUtil.select(questionCas, WikipediaPage.class)) {
			if (qToken.getCoveredText().trim().matches(".*[\\.\\,\\#\\!\\$\\%\\^\\&\\*\\;\\:\\{\\}\\=\\_\\`\\~\\(\\)\\?\\\"]")){
				logger.debug("Filtered from consideration: "+qToken.getCoveredText());
			}
			else{
				pages.add(qToken);
			}
		}
		return pages;
	}
	
	
	public static List<Chunk> getChunks(JCas cas){
		return getChunks(cas, null);
	}
	
	/**
	 * Get chunks
	 * @param cas
	 * @param posFirstLetter optional parameter. If not null, we select only chunks with tags starting with the posFirstLetter
	 * @return
	 */
	public static List<Chunk> getChunks(JCas cas, String posFirstLetter){
		List<Chunk> qNPChunks = new ArrayList<>();
		
		for (Chunk qChunk : JCasUtil.select(cas, Chunk.class)) {
			if ((posFirstLetter==null)||(qChunk.getChunkType().startsWith(posFirstLetter))){
				qNPChunks.add(qChunk);
			}
		}
		return qNPChunks;
	}
	
	public static void addQuestionClassToTheCandidateDocument(String questionClass, JCas documentCas) {
		Collection<QuestionClass> questionClassesInDoc = JCasUtil.select(documentCas, QuestionClass.class);
		if (questionClassesInDoc!=null){
			for (QuestionClass q : questionClassesInDoc){
				q.removeFromIndexes(documentCas);
			}
		}
		QuestionClass qclass = new QuestionClass(documentCas, 0, 1);
		qclass.setQuestionClass(questionClass);
		qclass.addToIndexes(documentCas);
	}
	
	public static void addQuestionClassToTheCandidateDocument(JCas questionCas, JCas documentCas) {
		String questionClass = JCasUtil.selectSingle(questionCas, QuestionClass.class).getQuestionClass();
		Collection<QuestionClass> questionClassesInDoc = JCasUtil.select(documentCas, QuestionClass.class);
		if (questionClassesInDoc!=null){
			for (QuestionClass q : questionClassesInDoc){
				q.removeFromIndexes(documentCas);
			}
		}
		QuestionClass qclass = new QuestionClass(documentCas, 0, 1);
		qclass.setQuestionClass(questionClass);
		qclass.addToIndexes(documentCas);
	}
}
