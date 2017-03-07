package it.unitn.nlpir.features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.mallet.types.StringKernel;


public class StringKernelScore implements FeatureExtractor {
	private static final Logger logger = LoggerFactory.getLogger(StringKernelScore.class);
	
	private StringKernel sk = new StringKernel();
	
	@Override
	public void extractFeatures(QAPair qa) {
		String sent1 = qa.getQuestionCas().getDocumentText();
		String sent2 = qa.getDocumentCas().getDocumentText();
		logger.debug("Computing kernel between: {} | {}", sent1, sent2);
		double score = sk.K(sent1, sent2);
		qa.featureVector.addFeature(score);
		logger.debug("Score: {}", score);
	}

	@Override
	public String getFeatureName() {
		return this.getClass().getSimpleName();
	}

}
