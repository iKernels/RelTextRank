package it.unitn.nlpir.features;


public interface FeatureExtractor {
	String getFeatureName();
	void extractFeatures(QAPair qa);
}
