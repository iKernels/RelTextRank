package it.unitn.nlpir.system.core.precomputed.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import it.unitn.nlpir.experiment.kernmat.NoUIMAExperiment;
import it.unitn.nlpir.experiment.kernmat.NoUIMAFeatureOnlyExperiment;
import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainDocument;
import it.unitn.nlpir.util.Pair;

public class ParallelFeatureExtractor implements Callable<List<NoUIMACandidate>> {
	protected NoUIMAExperiment featureExtractor;
	protected List<Pair<PlainDocument,PlainDocument>> examplesList;


	
	public ParallelFeatureExtractor(String featureExtractorClass, String featureCacheFileName, List<Pair<PlainDocument,PlainDocument>> pairsList){
		this.featureExtractor = new NoUIMAFeatureOnlyExperiment(featureExtractorClass, featureCacheFileName);
		this.examplesList = pairsList;

	}
	
	
	public ParallelFeatureExtractor(NoUIMAExperiment featureExtractor,  List<Pair<PlainDocument,PlainDocument>> pairsList){
		this.featureExtractor = featureExtractor;
		this.examplesList = pairsList;

	}
	
	


	@Override
	/**
	 * NOTE: the output list contains the original examples, just with the scoreds added. TODO: make them duplicates.
	 * 
	 */
	public List<NoUIMACandidate> call() throws Exception {

		List<NoUIMACandidate>  candidates = new ArrayList<NoUIMACandidate>();
		for (Pair<PlainDocument,PlainDocument> example : examplesList){
			candidates.add(featureExtractor.generateCandidate(example.getA(), example.getB()));
		}
		
		return candidates;
	}

}