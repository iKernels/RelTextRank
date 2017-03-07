package it.unitn.nlpir.system.datagen;

import it.unitn.nlpir.resultsets.Candidate;

import java.util.List;

public interface RerankingDataGen {
	
	public void handleData(List<Candidate> candidates);
	
	public void cleanUp();
	
}
