package it.unitn.nlpir.projectors;

import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.util.Pair;

import org.apache.uima.jcas.JCas;

public interface Projector {
	
	/**
	 * Project a question onto an answer
	 * @param questionCas the Cas associated to the question
	 * @param documentCas the Cas associated to the document
	 * @return a pair of strings representing the projected question and document
	 * @throws AnnotationNotFoundException 
	 */
	Pair<String, String> project(JCas questionCas, JCas documentCas)
			throws AnnotationNotFoundException;
}
