package it.unitn.nlpir.features.providers.similarity.dkpro;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.features.FeatureExtractor;
import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.features.providers.fvs.FVProvider;
import it.unitn.nlpir.features.providers.lists.CollectionProvider;
import it.unitn.nlpir.system.core.TextPairConversionBase;
import it.unitn.nlpir.util.Pair;
import cc.mallet.types.FeatureVector;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

public class DKProSimilarity implements FeatureExtractor {
	
	CollectionProvider fvProvider;
	TextSimilarityMeasureBase textSim;
	protected static final Logger logger = LoggerFactory.getLogger(DKProSimilarity.class);
	public DKProSimilarity(CollectionProvider fvProvider, TextSimilarityMeasureBase textSim) {
		this.fvProvider = fvProvider;
		this.textSim = textSim;
	}
	


	public double computeScore(QAPair qa) {	
		Pair<Collection<String>, Collection<String>> fvs = fvProvider.getCollections(qa);
		Collection<String> q = fvs.getA();
		Collection<String> d = fvs.getB();
		Double score =0.0;
		try {
			
			score = this.textSim.getSimilarity(q, d);
		} catch (SimilarityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (score.isNaN() || score.isInfinite() || score.equals("-1.0") || (score==-1.0))
			score = 0.0;
		return score;
	}

	@Override
	public void extractFeatures(QAPair qa) {
		long begin = System.currentTimeMillis();
		
		double score = computeScore(qa);
		qa.getFeatureVector().addFeature(score);
		long end = System.currentTimeMillis();
		
		logger.debug("Extracted {} features in {} ms", textSim.getName(), end-begin);
	}

	@Override
	public String getFeatureName() {
		return this.getClass().getSimpleName() + "." + fvProvider.getClass().getSimpleName();
	}
}
