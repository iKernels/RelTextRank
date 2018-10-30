package it.unitn.nlpir.features.nouima;

import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;

public interface NoUIMAFeatureExtractor {
	String getFeatureName();
	void extractFeatures(NoUIMACandidate qa);
}
