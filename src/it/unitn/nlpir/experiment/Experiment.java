package it.unitn.nlpir.experiment;

import it.unitn.nlpir.projectors.Projector;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.uima.AnalysisEngineList;

import org.apache.uima.jcas.JCas;

public interface Experiment {
	public AnalysisEngineList getAnalysisEngineList();
	
	public Candidate generateCandidate(JCas questionCas, JCas documentCas, Result result);
	
	public Projector getProjector();
}
