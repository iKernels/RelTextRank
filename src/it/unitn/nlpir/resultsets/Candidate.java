package it.unitn.nlpir.resultsets;

import it.unitn.nlpir.util.Pair;
import svmlighttk.SVMVector;

public class Candidate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Pair<String, String> qa;
	public SVMVector features;
	public Result result;
	
	public Candidate(Result result, Pair<String, String> qa,
			SVMVector features) {
		this.qa = qa;
		this.features = features;
		this.result = result;
	}
	
	public Pair<String, String> getQa() {
		return qa;
	}

	public void setQa(Pair<String, String> qa) {
		this.qa = qa;
	}

	public SVMVector getFeatures() {
		return features;
	}

	public void setFeatures(SVMVector features) {
		this.features = features;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}


}
