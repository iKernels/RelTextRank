package it.unitn.nlpir.features.presets;

import it.unitn.nlpir.features.FeatureSets;
import it.unitn.nlpir.features.StringKernelScore;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.features.providers.fvs.DependencyTripletsProvider;
import it.unitn.nlpir.features.providers.similarity.CosineSimilarity;
import it.unitn.nlpir.uima.TokenTextGetterFactory;

/**
 * 
 * @author kateryna
 *
 */
public class OnlyFastBasicSimilarityFeatures implements IVectorFeatureExtractor{

	@Override
	public FeaturesBuilder getFeaturesBuilder() {
		return new FeaturesBuilder()
		
		.extend(FeatureSets.buildBowFeatures())

		.add(new CosineSimilarity(new DependencyTripletsProvider(
				TokenTextGetterFactory.LEMMA))) 
		.extend(FeatureSets.buildDKProWordOverlapFeatures())
		.add(new StringKernelScore());
	}
}
