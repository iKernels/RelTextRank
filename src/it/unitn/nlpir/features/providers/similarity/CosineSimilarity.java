package it.unitn.nlpir.features.providers.similarity;

import it.unitn.nlpir.features.FeatureExtractor;
import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.features.providers.fvs.FVProvider;
import it.unitn.nlpir.util.Pair;
import cc.mallet.types.FeatureVector;

public class CosineSimilarity implements FeatureExtractor {
	
	FVProvider fvProvider;

	public CosineSimilarity(FVProvider fvProvider) {
		this.fvProvider = fvProvider;
	}
	
	public static double computeScore(FeatureVector a, FeatureVector b) {
		Double score = a.dotProduct(b) / (a.twoNorm() * b.twoNorm());
		if (score.isNaN() || score.isInfinite())
			score = 0.0;
		return score;
	}

	public double computeScore(QAPair qa) {	
		Pair<FeatureVector, FeatureVector> fvs = fvProvider.getFeatureVectors(qa);
		FeatureVector q = fvs.getA();
		FeatureVector d = fvs.getB();
		/*Double dotProduct =  q.dotProduct(d) ;
		double normq = q.twoNorm();
		double normd = d.twoNorm();*/ 
		Double score = q.dotProduct(d) / (q.twoNorm() * d.twoNorm());
		if (score.isNaN() || score.isInfinite())
			score = 0.0;
		return score;
	}

	@Override
	public void extractFeatures(QAPair qa) {
		double score = computeScore(qa);
		qa.getFeatureVector().addFeature(score);
	}

	@Override
	public String getFeatureName() {
		return this.getClass().getSimpleName() + "." + fvProvider.getClass().getSimpleName();
	}
}
