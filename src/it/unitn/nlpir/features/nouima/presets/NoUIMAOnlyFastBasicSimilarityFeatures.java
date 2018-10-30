package it.unitn.nlpir.features.nouima.presets;

import it.unitn.nlpir.features.builder.nouima.NoUIMAFeaturesBuilder;
import it.unitn.nlpir.features.nouima.NoUIMACosineSimilarity;
import it.unitn.nlpir.features.nouima.NoUIMADependencyTripletsProvider;
import it.unitn.nlpir.features.nouima.NoUIMAFeatureSets;
import it.unitn.nlpir.features.nouima.NoUIMAStringKernelScore;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainToken;

/**
 * 
 * @author kateryna
 *
 */
public class NoUIMAOnlyFastBasicSimilarityFeatures implements INoUIMAVectorFeatureExtractor{

	@Override
	public NoUIMAFeaturesBuilder getFeaturesBuilder() {
		return new NoUIMAFeaturesBuilder()
		
		.extend(NoUIMAFeatureSets.buildBowFeatures())

		.add(new NoUIMACosineSimilarity(new NoUIMADependencyTripletsProvider(
				PlainToken.LEMMA))) 
		.extend(NoUIMAFeatureSets.buildDKProWordOverlapFeatures())
		.add(new NoUIMAStringKernelScore());
	}
}
