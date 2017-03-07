package it.unitn.nlpir.features.presets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import it.unitn.nlpir.features.FeatureSets;
import it.unitn.nlpir.features.builder.FeaturesBuilder;

public class FromFileVectorFeature implements IVectorFeatureExtractor{

	protected static final Logger logger = LoggerFactory.getLogger(FromFileVectorFeature.class);
	
	protected String featureFile;
	
	public FromFileVectorFeature(String featureFile){
		this.featureFile = featureFile;
	}
	
	@Override
	public FeaturesBuilder getFeaturesBuilder() {
		FeaturesBuilder fb = new FeaturesBuilder();
		logger.info(String.format("Reading features from %s, %s", featureFile));
		fb.extend(FeatureSets.buildFeaturesFromExternalFile(featureFile));
		return fb;
	}
}
