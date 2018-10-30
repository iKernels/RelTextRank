package it.unitn.nlpir.features.builder;

import it.unitn.nlpir.features.FeatureExtractor;
import it.unitn.nlpir.features.QAPair;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMVector;

public class FeaturesBuilder {
	static final Logger logger = LoggerFactory.getLogger(FeaturesBuilder.class);

	private List<FeatureExtractor> featureExtractorList;
	private List<String> featureNames;
	private boolean outputInfoMessage;
	private long[] featureExtractionTimes;
	private static int totalCounted=0;
	public FeaturesBuilder() {
		this.featureExtractorList = new ArrayList<>();
		this.featureNames = new ArrayList<>();
		this.outputInfoMessage = true;
		featureExtractionTimes = null;
		totalCounted = 0;
	}
	
	public FeaturesBuilder add(FeatureExtractor f) {
		add(f, f.getFeatureName() + "-" + featureExtractorList.size());
		return this;
	}
	
	public FeaturesBuilder extend(FeaturesBuilder fb) {
		for (FeatureExtractor f: fb.getFeatureExtractorList())
			add(f, f.getFeatureName() + "-" + featureExtractorList.size());
		return this;
	}
	
	public List<FeatureExtractor> getFeatureExtractorList() {
		return featureExtractorList;
	}

	public List<String> getFeatureNames() {
		return featureNames;
	}


	public FeaturesBuilder add(FeatureExtractor f, String name) {
		this.featureExtractorList.add(f);
		this.featureNames.add(name);
		return this;
	}
	
	public SVMVector getFeatures(QAPair qa) {
		if (this.featureExtractionTimes==null)
			this.featureExtractionTimes = new long[this.featureExtractorList.size()];
		long globalStart = System.currentTimeMillis();
		for (FeatureExtractor f : this.featureExtractorList) {
			
			int sizeBefore = qa.getFeatureVector().getFeatures().size();
			long start = System.currentTimeMillis();
			f.extractFeatures(qa);
			int sizeAfter = qa.getFeatureVector().getFeatures().size();
			
			long elapsed = System.currentTimeMillis() - start;
			if (this.outputInfoMessage)
				logger.debug(String.format("Feature name: %s; Range: %d:%d;", f.getFeatureName(), sizeBefore+1, sizeAfter));
			
			featureExtractionTimes[sizeBefore] = featureExtractionTimes[sizeBefore]+ elapsed;
			
			logger.debug(String.format("Feature name: %s; Range: %d:%d; EXTRACTION TIME: %d", f.getFeatureName(), sizeBefore+1, sizeAfter,elapsed));
			
		}
		totalCounted=totalCounted+1;
		
		if (totalCounted%500 == 0) {
			double total_per_feat = 0.0;
			for (int i = 0; i < featureExtractionTimes.length; i++){
				logger.info(String.format("Feature %d: %.5f ms",i,Double.valueOf(featureExtractionTimes[i]/Double.valueOf(totalCounted))));
				total_per_feat += Double.valueOf(featureExtractionTimes[i]);
			}
			logger.info(String.format("ALL FEATURES: %.5f ms",Double.valueOf(total_per_feat/Double.valueOf(totalCounted))));

		}
		this.outputInfoMessage = false;
		long globalElapsed = System.currentTimeMillis() - globalStart;
		logger.debug(String.format("OVERALL EXTRACTION TIME: %d", globalElapsed));
		
		return qa.getFeatureVector();
	}
}
