package it.unitn.nlpir.features.presets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import it.unitn.nlpir.features.builder.FeaturesBuilder;

public class FeatureExtractorFactory {
	
	protected static  Logger logger = LoggerFactory.getLogger(FeatureExtractorFactory.class);
	
	public static FeaturesBuilder getFeatureBuilder(String featureExtractorClassName) {
		return getFeatureBuilder(featureExtractorClassName,null);
	}
	
	public static FeaturesBuilder getFeatureBuilder(String featureExtractorClassName, String featureCacheFileName) {
		FeaturesBuilder fb = new FeaturesBuilder();
		logger.info("Setting the features up");
		
		if (featureExtractorClassName!=null){
		
			logger.info(String.format("Feature extractor: %s", featureExtractorClassName));
			IVectorFeatureExtractor vfe = null;
			
				try {
					if (((featureCacheFileName==null))){
						Class<?> c = null;
						c = Class.forName(featureExtractorClassName);
						vfe = (IVectorFeatureExtractor) c.newInstance();
					}
					else{
						Constructor<?> c = null;
						c = Class.forName(featureExtractorClassName).getConstructor(String.class);
						vfe = (IVectorFeatureExtractor) c.newInstance(featureCacheFileName);
					}
					fb = vfe.getFeaturesBuilder();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return fb;
	}
	
}
