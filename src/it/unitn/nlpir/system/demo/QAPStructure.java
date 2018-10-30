package it.unitn.nlpir.system.demo;

import java.util.List;
import java.util.Map;

import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;

public class QAPStructure {
	Question q;
	List<Result> r;
	Map<String,Candidate> representations;

	
	public Map<String, Candidate> getRepresentations() {
		return representations;
	}

	public void setRepresentations(Map<String, Candidate> representations) {
		this.representations = representations;
	}


	public QAPStructure(Question q, List<Result> r) {
		super();
		this.q = q;
		this.r = r;
	}
	
	public QAPStructure(Question q, List<Result> r,  Map<String,Candidate> representations) {
		super();
		this.q = q;
		this.r = r;
		this.representations = representations;
	}
	

	public Question getQ() {
		return q;
	}
	public void setQ(Question q) {
		this.q = q;
	}
	public List<Result> getR() {
		return r;
	}
	public void setR(List<Result> r) {
		this.r = r;
	}


	
	
	
}
