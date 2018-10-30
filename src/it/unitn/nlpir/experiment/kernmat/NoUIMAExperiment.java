package it.unitn.nlpir.experiment.kernmat;

import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainDocument;

public interface NoUIMAExperiment {
	
	
	public NoUIMACandidate generateCandidate(PlainDocument questionCas, PlainDocument documentCas);
	
	
}
