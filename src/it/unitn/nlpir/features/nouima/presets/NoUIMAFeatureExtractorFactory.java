package it.unitn.nlpir.features.nouima.presets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import it.unitn.nlpir.features.builder.nouima.NoUIMAFeaturesBuilder;

public class NoUIMAFeatureExtractorFactory {
	
	protected static  Logger logger = LoggerFactory.getLogger(NoUIMAFeatureExtractorFactory.class);
	
	public static NoUIMAFeaturesBuilder getFeatureBuilder(String featureExtractorClassName) {
		return getFeatureBuilder(featureExtractorClassName,null);
	}
	
	public static NoUIMAFeaturesBuilder getFeatureBuilder(String featureExtractorClassName, String featureCacheFileName) {
		NoUIMAFeaturesBuilder fb = new NoUIMAFeaturesBuilder();
		logger.info("Setting the features up");
		
		if (featureExtractorClassName!=null){
		
			logger.info(String.format("Feature extractor: %s", featureExtractorClassName));
			INoUIMAVectorFeatureExtractor vfe = null;
			
				try {
					if (((featureCacheFileName==null))){
						Class<?> c = null;
						c = Class.forName(featureExtractorClassName);
						vfe = (INoUIMAVectorFeatureExtractor) c.newInstance();
					}
					else{
						Constructor<?> c = null;
						c = Class.forName(featureExtractorClassName).getConstructor(String.class);
						vfe = (INoUIMAVectorFeatureExtractor) c.newInstance(featureCacheFileName);
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
