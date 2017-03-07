package it.unitn.nlpir.features;

import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.util.Pair;

import org.apache.uima.jcas.JCas;

import svmlighttk.SVMVector;

public class QAPair {
	JCas questionCas;
	JCas documentCas;
	Result result;
	Pair<String, String> qaProj;
	SVMVector featureVector;

	public QAPair(JCas questionCas, JCas documentCas) {
		this(questionCas, documentCas, null, new SVMVector(), null);
	}
	
	public QAPair(JCas questionCas, JCas documentCas, SVMVector vector) {
		this(questionCas, documentCas, null, vector, null);
	}
	
	public QAPair(JCas questionCas, JCas documentCas, Result result, SVMVector featureVector, Pair<String, String> qaProj) {
		this.questionCas = questionCas;
		this.documentCas = documentCas;
		this.result = result;
		this.featureVector = featureVector;
		this.qaProj = qaProj;	
	}
	
	public SVMVector getFeatureVector() {
		return featureVector;
	}

	public void setFeatureVector(SVMVector featureVector) {
		this.featureVector = featureVector;
	}

	public JCas getQuestionCas() {
		return questionCas;
	}

	public void setQuestionCas(JCas questionCas) {
		this.questionCas = questionCas;
	}

	public JCas getDocumentCas() {
		return documentCas;
	}

	public void setDocumentCas(JCas documentCas) {
		this.documentCas = documentCas;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Pair<String, String> getQaProj() {
		return qaProj;
	}

	public void setQaProj(Pair<String, String> qaProj) {
		this.qaProj = qaProj;
	}

}
