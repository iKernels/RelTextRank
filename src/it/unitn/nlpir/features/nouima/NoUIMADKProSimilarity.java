package it.unitn.nlpir.features.nouima;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.util.Pair;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

public class NoUIMADKProSimilarity implements NoUIMAFeatureExtractor {
	
	NoUIMACollectionProvider fvProvider;
	TextSimilarityMeasureBase textSim;
	protected static final Logger logger = LoggerFactory.getLogger(NoUIMADKProSimilarity.class);
	public NoUIMADKProSimilarity(NoUIMACollectionProvider fvProvider, TextSimilarityMeasureBase textSim) {
		this.fvProvider = fvProvider;
		this.textSim = textSim;
	}
	


	public double computeScore(NoUIMACandidate qa) {	
		Pair<Collection<String>, Collection<String>> fvs = fvProvider.getCollections(qa);
		Collection<String> q = fvs.getA();
		Collection<String> d = fvs.getB();
		Double score =0.0;
		try {
			
			score = this.textSim.getSimilarity(q, d);
		} catch (SimilarityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (score.isNaN() || score.isInfinite() || score.equals("-1.0") || (score==-1.0))
			score = 0.0;
		return score;
	}

	

	@Override
	public String getFeatureName() {
		return String.format("%s.%s.%s (%s)",this.getClass().getSimpleName(), fvProvider.getClass().getSimpleName(), textSim.getName(), fvProvider.toString());
	}



	@Override
	public void extractFeatures(NoUIMACandidate qa) {
	long begin = System.currentTimeMillis();
		
		double score = computeScore(qa);
		qa.getFeatureVector().addFeature(score);
		long end = System.currentTimeMillis();
		
		logger.debug("Extracted {} features in {} ms", textSim.getName(), end-begin);
		
	}
}
