package it.unitn.nlpir.features;

import java.io.File;
import java.io.IOException;

import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.core.ResourceFactory;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.ResourceLoaderException;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
import dkpro.similarity.algorithms.lexical.string.GreedyStringTiling;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;
import dkpro.similarity.algorithms.lsr.path.ResnikComparator;
import dkpro.similarity.algorithms.vsm.VectorComparator;
import dkpro.similarity.algorithms.vsm.store.CachingVectorReader;
import dkpro.similarity.algorithms.vsm.store.vectorindex.VectorIndexReader;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.features.cached.FeatureFromFileCache;
import it.unitn.nlpir.features.providers.fvs.BowNEProvider;
import it.unitn.nlpir.features.providers.fvs.BowProvider;
import it.unitn.nlpir.features.providers.fvs.BowSSenseProvider;
import it.unitn.nlpir.features.providers.fvs.BowWordNetHypernymsProvider;
import it.unitn.nlpir.features.providers.fvs.BowWordNetSynonymsProvider;
import it.unitn.nlpir.features.providers.lists.OrderedLexicalUnitFirstSentenceProvider;
import it.unitn.nlpir.features.providers.lists.OrderedLexicalUnitProvider;
import it.unitn.nlpir.features.providers.similarity.CosineSimilarity;
import it.unitn.nlpir.features.providers.similarity.PTKSimilarity;
import it.unitn.nlpir.features.providers.similarity.dkpro.DKProSimilarity;
import it.unitn.nlpir.features.providers.trees.ProjectedTreeProvider;
import it.unitn.nlpir.features.providers.trees.old.PosChunkTreeProvider;
import it.unitn.nlpir.features.providers.trees.old.RelConstituencyTreeProvider;
import it.unitn.nlpir.features.providers.trees.old.RelDependencyTreeProvider;
import it.unitn.nlpir.features.providers.trees.old.RelPhraseDependencyTreeProvider;
import it.unitn.nlpir.uima.TokenTextGetterFactory;

public class FeatureSets {
	private static final double SEARCH_ENGINE_SCORE_NORM_FACTOR = 50.0;
	
	public static FeaturesBuilder buildDKProWordNoStopwordsOverlapFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder();
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true,TokenTextGetterFactory.LEMMA), new LongestCommonSubsequenceComparator()));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true,TokenTextGetterFactory.LEMMA), new LongestCommonSubstringComparator()));
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(2)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(3)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(4)));
		
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(2)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true,TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(2)));
		
		//featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new GreedyStringTiling(3)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitFirstSentenceProvider(true, TokenTextGetterFactory.LEMMA), new GreedyStringTiling(3)));
		return featureBuilder;
	}
	
	public static FeaturesBuilder buildDKProWordOverlapFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder();
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new LongestCommonSubsequenceComparator()));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new LongestCommonSubstringComparator()));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true,TokenTextGetterFactory.LEMMA), new LongestCommonSubsequenceComparator()));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true,TokenTextGetterFactory.LEMMA), new LongestCommonSubstringComparator()));
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(2)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(3)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(4)));
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(2)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(3)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(4)));
		
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(2)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(2)));
		
		//featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new GreedyStringTiling(3)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitFirstSentenceProvider(true, TokenTextGetterFactory.LEMMA), new GreedyStringTiling(3)));
		return featureBuilder;
	}
	
	
	public static FeaturesBuilder buildDKProWordOverlapFeaturesAfterAblation() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder();
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new LongestCommonSubsequenceComparator())); //42
		//featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new LongestCommonSubstringComparator())); //43
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true,TokenTextGetterFactory.LEMMA), new LongestCommonSubsequenceComparator())); //44
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true,TokenTextGetterFactory.LEMMA), new LongestCommonSubstringComparator())); //45
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(1))); //46
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(2))); //47
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(3))); //48
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(4))); //49
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(1))); //50
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(2))); //51
		//featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(3)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(4)));
		
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(2)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(2)));
		
		//featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new GreedyStringTiling(3)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitFirstSentenceProvider(true, TokenTextGetterFactory.LEMMA), new GreedyStringTiling(3)));
		return featureBuilder;
	}
	
	
	public static FeaturesBuilder buildDKProESAFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder();
		String dkProHome = System.getenv("DKPRO_HOME");
		File wikiESA = new File(dkProHome+"/ESA/VectorIndexes/wp_eng_lem_nc_c");
		String cacheSize = "1000";
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitFirstSentenceProvider(true, TokenTextGetterFactory.LEMMA), 
				new VectorComparator(new CachingVectorReader(new VectorIndexReader(wikiESA),Integer.parseInt(cacheSize)))));
		return featureBuilder;
	}
	
	public static FeaturesBuilder buildDKProESAFullTextFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder();
		String dkProHome = System.getenv("DKPRO_HOME");
		File wikiESA = new File(dkProHome+"/ESA/VectorIndexes/wp_eng_lem_nc_c");
		String cacheSize = "100";
		
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), 
				new VectorComparator(new CachingVectorReader(new VectorIndexReader(wikiESA),Integer.parseInt(cacheSize)))));
		
		return featureBuilder;
	}
	
	public static FeaturesBuilder buildDKProWordNetBasedFeatures() {
		//add wordnet-based similarity measures
		FeaturesBuilder featureBuilder = new FeaturesBuilder();
		LexicalSemanticResource wordnet;
		try {
			wordnet = ResourceFactory.getInstance().get("wordnet3", "en");
			wordnet.setIsCaseSensitive(false);
			featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitFirstSentenceProvider(true, TokenTextGetterFactory.LEMMA), new ResnikComparator(wordnet, wordnet.getRoot())));
		} catch (ResourceLoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LexicalSemanticResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return featureBuilder;		
	}
	
	public static FeaturesBuilder buildDKProFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder();
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new LongestCommonSubsequenceComparator()));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new LongestCommonSubstringComparator()));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true,TokenTextGetterFactory.LEMMA), new LongestCommonSubsequenceComparator()));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true,TokenTextGetterFactory.LEMMA), new LongestCommonSubstringComparator()));
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(2)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(3)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(4)));
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(2)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(3)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(true, TokenTextGetterFactory.LEMMA), new WordNGramJaccardMeasure(4)));
		
		
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(2)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new WordNGramContainmentMeasure(2)));
		
		//featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitProvider(TokenTextGetterFactory.LEMMA), new GreedyStringTiling(3)));
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitFirstSentenceProvider(true, TokenTextGetterFactory.LEMMA), new GreedyStringTiling(3)));
		
		//add esa
		String dkProHome = System.getenv("DKPRO_HOME");
		File wikiESA = new File(dkProHome+"/ESA/VectorIndexes/wp_eng_lem_nc_c");
		String cacheSize = "1000";
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitFirstSentenceProvider(true, TokenTextGetterFactory.LEMMA), 
				new VectorComparator(new CachingVectorReader(new VectorIndexReader(wikiESA),Integer.parseInt(cacheSize)))));
		
		//add wordnet-based similarity measures
		LexicalSemanticResource wordnet;
		try {
			wordnet = ResourceFactory.getInstance().get("wordnet3", "en");
			wordnet.setIsCaseSensitive(false);
			featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitFirstSentenceProvider(true, TokenTextGetterFactory.LEMMA), new ResnikComparator(wordnet, wordnet.getRoot())));
		} catch (ResourceLoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LexicalSemanticResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return featureBuilder;
	}
	
	public static FeaturesBuilder buildDKProSemanticFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder();
	
		
		//add esa
		String dkProHome = System.getenv("DKPRO_HOME");
		File wikiESA = new File(dkProHome+"/ESA/VectorIndexes/wp_eng_lem_nc_c");
		String cacheSize = "1000";
		featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitFirstSentenceProvider(true, TokenTextGetterFactory.LEMMA), 
				new VectorComparator(new CachingVectorReader(new VectorIndexReader(wikiESA),Integer.parseInt(cacheSize)))));
		
		//add wordnet-based similarity measures
		LexicalSemanticResource wordnet;
		try {
			wordnet = ResourceFactory.getInstance().get("wordnet3", "en");
			wordnet.setIsCaseSensitive(false);
			featureBuilder.add(new DKProSimilarity(new OrderedLexicalUnitFirstSentenceProvider(true, TokenTextGetterFactory.LEMMA), new ResnikComparator(wordnet, wordnet.getRoot())));
		} catch (ResourceLoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LexicalSemanticResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return featureBuilder;
	}
	
	public static FeaturesBuilder buildBowFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA))) 
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2 }, true)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2, 3 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2, 3 }, true)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						2, 3 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						2, 3 }, true)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						2, 3, 4 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						2, 3, 4 }, true)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						3, 4 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.POSTAG, new int[] {
						1, 2, 3 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.POSTAG, new int[] {
						1, 2, 3, 4 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.POSTAG, new int[] {
						2, 3, 4 }, false)));
		return featureBuilder;
	}
	
	public static FeaturesBuilder buildBestAEBowFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA))) 
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						2 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2 }, true)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2, 3 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2, 3 }, true)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						2, 3 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						2, 3 }, true)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						2, 3, 4 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						2, 3, 4 }, true)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						3, 4 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.POSTAG, new int[] {
						1, 2, 3 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.POSTAG, new int[] {
						1, 2, 3, 4 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.POSTAG, new int[] {
						2, 3, 4 }, false)));
		return featureBuilder;
	}
	
	public static FeaturesBuilder buildBowFeaturesAfterAblation() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				1, 2 }, false)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				1, 2 }, true)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				1, 2, 3 }, false)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				1, 2, 3 }, true)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				2, 3 }, false)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				2, 3 }, true)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				2, 3, 4 }, false)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				2, 3, 4 }, true)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				3, 4 }, false)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.POSTAG, new int[] {
				1, 2, 3 }, false)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.POSTAG, new int[] {
				1, 2, 3, 4 }, false)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.POSTAG, new int[] {
				2, 3, 4 }, false)));
		return featureBuilder;
	}
	
	
	public static FeaturesBuilder buildSimpleBowFeaturesUnStopWordsLemmas() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
		/*.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {1}, 
				false)))*/
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {1}, 
				true)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				1, 2 }, false)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				1, 2 }, true)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				1, 2, 3 }, false)))
		.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
				1, 2, 3 }, true)));
		return featureBuilder;
	}
	
	
	public static FeaturesBuilder buildSimpleBowFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {1}, 
						false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {1}, 
						true)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2 }, true)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2, 3 }, false)))
				.add(new CosineSimilarity(new BowProvider(TokenTextGetterFactory.LEMMA, new int[] {
						1, 2, 3 }, true)));
		return featureBuilder;
	}

	public static FeaturesBuilder buildWordNetFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
				// .add(new WordNetSimilarityScore())
				// .add(new WordNetSimilarityScore(true))
				.add(new CosineSimilarity(new BowWordNetHypernymsProvider()))
				.add(new CosineSimilarity(new BowWordNetSynonymsProvider()))
				.add(new CosineSimilarity(new BowSSenseProvider()));
		// .add(new StanfordNERFeaturesScore());
		// .add(new CosineSimilarity(new
		// BowStanfordNERFeaturesProvider()));
		return featureBuilder;
	}


	
	
	public static FeaturesBuilder buildNEBowSimilarityFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
		.add(new CosineSimilarity(new BowNEProvider()));
				
		return featureBuilder;
	}
	

	
	public static FeaturesBuilder buildKernelFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
				.add(new StringKernelScore());
		return featureBuilder;
	}

	public static FeaturesBuilder buildFeaturesFromExternalFile(String featureValueFile) {
		FeaturesBuilder featureBuilder = null;
		try {
			featureBuilder = new FeaturesBuilder().add(new FeatureFromFileCache(featureValueFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return featureBuilder;

	}
	
	public static FeaturesBuilder buildFeaturesFromExternalFile(String idFile, String featureValueFile) {
		FeaturesBuilder featureBuilder = null;
		try {
			featureBuilder = new FeaturesBuilder().add(new FeatureFromFileCache(idFile, featureValueFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return featureBuilder;

	}
	
	public static FeaturesBuilder buildDocumentRankingScoreFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder().add(new DocumentRankingScore(SEARCH_ENGINE_SCORE_NORM_FACTOR));
		return featureBuilder;

	}
	

	
	public static FeaturesBuilder buildDocumentRankingScoreFeatures(double factor) {
		FeaturesBuilder featureBuilder = new FeaturesBuilder().add(new DocumentRankingScore(factor));
		return featureBuilder;

	}
	
	
	
	public static FeaturesBuilder buildOldKernelFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
			.add(new PTKSimilarity(new PosChunkTreeProvider(TokenTextGetterFactory.LEMMA)))
			.add(new PTKSimilarity(new ProjectedTreeProvider()));
		return featureBuilder;
	}
	
	public static FeaturesBuilder buildOldKernelFeaturesAfterAblation() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
			.add(new PTKSimilarity(new PosChunkTreeProvider(TokenTextGetterFactory.LEMMA)))
			.add(new PTKSimilarity(new ProjectedTreeProvider()));
		return featureBuilder;
	}
	
	public static FeaturesBuilder buildPosChunkTreeProviderFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
			.add(new PTKSimilarity(new PosChunkTreeProvider(TokenTextGetterFactory.LEMMA)));
			//.add(new PTKSimilarity(new ProjectedTreeProvider()));
		return featureBuilder;
	}
	
	
	public static FeaturesBuilder buildNonProjTreeKernelFeatures() {
		FeaturesBuilder featureBuilder = new FeaturesBuilder()
				.add(new PTKSimilarity(new RelConstituencyTreeProvider()))
				.add(new PTKSimilarity(new RelDependencyTreeProvider()))
				.add(new PTKSimilarity(new RelPhraseDependencyTreeProvider()));
		return featureBuilder;
	}

	


	
}
