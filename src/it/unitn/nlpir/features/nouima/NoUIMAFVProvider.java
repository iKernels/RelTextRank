package it.unitn.nlpir.features.nouima;


import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.util.Pair;
import cc.mallet.types.FeatureVector;

public interface NoUIMAFVProvider {
	Pair<FeatureVector, FeatureVector> getFeatureVectors(NoUIMACandidate qa);
	
	
}
