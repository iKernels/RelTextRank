package it.unitn.nlpir.resultsets.kelp;

import org.apache.uima.jcas.JCas;

import it.uniroma2.sag.kelp.data.representation.Representation;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.util.Pair;
import svmlighttk.SVMVector;

public class CandidatePairRepresentation extends Candidate implements Representation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Pair<JCas,JCas> qaCASes;
	
	public CandidatePairRepresentation(Result result, Pair<String, String> qa, SVMVector features, Pair<JCas,JCas> qaCases) {
		super(result, qa, features);
		this.qaCASes = qaCases;
	}
	
	public CandidatePairRepresentation(Candidate c, Pair<JCas,JCas> qaCases) {
		super(c.result,c.qa, c.features);
		this.qaCASes = qaCases;
	}

	public Pair<JCas, JCas> getQaCASes() {
		return qaCASes;
	}

	@Override
	public void setDataFromText(String representationDescription) throws Exception {
		
	}

	@Override
	public String getTextFromData() {
		return null;
	}

	@Override
	public boolean isCompatible(Representation rep) {
		// TODO Auto-generated method stub
		return false;
	}

}
