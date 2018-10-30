package it.unitn.nlpir.features.presets;

import it.unitn.nlpir.features.EntitiesDistributionInDocument;
import it.unitn.nlpir.features.FeatureSets;
import it.unitn.nlpir.features.StringKernelScore;
import it.unitn.nlpir.features.WordNet3SimilarityScore;
import it.unitn.nlpir.features.WordOverlap;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.features.providers.fvs.DependencyTripletsProvider;
import it.unitn.nlpir.features.providers.similarity.CosineSimilarity;
import it.unitn.nlpir.features.providers.similarity.PTKSimilarity;
import it.unitn.nlpir.features.providers.trees.old.RelConstituencyTreeProvider;
import it.unitn.nlpir.features.providers.trees.old.RelDependencyTreeProvider;
import it.unitn.nlpir.features.providers.trees.old.RelPhraseDependencyTreeProvider;
import it.unitn.nlpir.uima.TokenTextGetterFactory;

public class ESANoQCFeatures implements IVectorFeatureExtractor{

	@Override
	public FeaturesBuilder getFeaturesBuilder() {
		return new FeaturesBuilder()
		
		.extend(FeatureSets.buildBowFeatures())
		.extend(FeatureSets.buildOldKernelFeatures())
		.extend(FeatureSets.buildSimpleBowFeatures())
		.add(new CosineSimilarity(new DependencyTripletsProvider(
				TokenTextGetterFactory.LEMMA))) 
		.add(new PTKSimilarity(new RelConstituencyTreeProvider())) 
		.add(new PTKSimilarity(new RelDependencyTreeProvider()))
		.add(new PTKSimilarity(new RelPhraseDependencyTreeProvider()))
		.extend(FeatureSets.buildDKProWordOverlapFeatures())
		.extend(FeatureSets.buildDKProESAFullTextFeatures())
		.add(new EntitiesDistributionInDocument()) //44
		.add(new StringKernelScore()) //45
		.add(new WordOverlap()) //46
		.add(new WordNet3SimilarityScore()) //47
		.add(new WordNet3SimilarityScore(true)); //48
	}
}
