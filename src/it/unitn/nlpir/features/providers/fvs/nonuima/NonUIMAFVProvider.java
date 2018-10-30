package it.unitn.nlpir.features.providers.fvs.nonuima;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.util.Pair;
import cc.mallet.types.FeatureVector;

public interface NonUIMAFVProvider {
	Pair<FeatureVector, FeatureVector> getFeatureVectors(QAPair qaPair);
	
	
}
