package it.unitn.nlpir.features.providers.fvs;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.util.Pair;
import cc.mallet.types.FeatureVector;

public interface FVProvider {
	Pair<FeatureVector, FeatureVector> getFeatureVectors(QAPair qaPair);
	
	
}
