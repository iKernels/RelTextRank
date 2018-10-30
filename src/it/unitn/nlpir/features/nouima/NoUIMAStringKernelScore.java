package it.unitn.nlpir.features.nouima;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.mallet.types.StringKernel;
import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;


public class NoUIMAStringKernelScore implements NoUIMAFeatureExtractor {
	private static final Logger logger = LoggerFactory.getLogger(NoUIMAStringKernelScore.class);
	
	private StringKernel sk = new StringKernel();
	
	@Override
	public void extractFeatures(NoUIMACandidate qa) {
		
		String sent1 = qa.getPair().getA().getText();
		String sent2 = qa.getPair().getB().getText();
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
