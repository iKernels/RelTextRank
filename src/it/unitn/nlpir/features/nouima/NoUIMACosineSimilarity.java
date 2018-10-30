package it.unitn.nlpir.features.nouima;

import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.util.Pair;
import cc.mallet.types.FeatureVector;

public class NoUIMACosineSimilarity implements NoUIMAFeatureExtractor {
	
	NoUIMAFVProvider fvProvider;

	public NoUIMACosineSimilarity(NoUIMAFVProvider fvProvider) {
		this.fvProvider = fvProvider;
	}
	
	public static double computeScore(FeatureVector a, FeatureVector b) {
		Double score = a.dotProduct(b) / (a.twoNorm() * b.twoNorm());
		if (score.isNaN() || score.isInfinite())
			score = 0.0;
		return score;
	}

	public double computeScore(NoUIMACandidate qa) {	
		Pair<FeatureVector, FeatureVector> fvs = fvProvider.getFeatureVectors(qa);
		FeatureVector q = fvs.getA();
		FeatureVector d = fvs.getB();
		Double score = q.dotProduct(d) / (q.twoNorm() * d.twoNorm());
		if (score.isNaN() || score.isInfinite())
			score = 0.0;
		return score;
	}

	@Override
	public void extractFeatures(NoUIMACandidate qa) {
		double score = computeScore(qa);
		qa.getFeatureVector().addFeature(score);
	}

	@Override
	public String getFeatureName() {
		
		return String.format("%s.%s (%s)",this.getClass().getSimpleName(), fvProvider.getClass().getSimpleName(), fvProvider.toString());
	}

	
}
