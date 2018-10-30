package it.unitn.nlpir.features.nouima;

import dkpro.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure;
import dkpro.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
import dkpro.similarity.algorithms.lexical.string.GreedyStringTiling;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;
import dkpro.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;
import it.unitn.nlpir.features.builder.nouima.NoUIMAFeaturesBuilder;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainToken;
;

public class NoUIMAFeatureSets {

	public static NoUIMAFeaturesBuilder buildDKProWordOverlapFeatures() {
		NoUIMAFeaturesBuilder featureBuilder = new NoUIMAFeaturesBuilder();
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new LongestCommonSubsequenceComparator()));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new LongestCommonSubstringComparator()));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true,PlainToken.LEMMA), new LongestCommonSubsequenceComparator()));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true,PlainToken.LEMMA), new LongestCommonSubstringComparator()));
		
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramJaccardMeasure(1)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramJaccardMeasure(2)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramJaccardMeasure(3)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramJaccardMeasure(4)));
		
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true, PlainToken.LEMMA), new WordNGramJaccardMeasure(1)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true, PlainToken.LEMMA), new WordNGramJaccardMeasure(2)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true, PlainToken.LEMMA), new WordNGramJaccardMeasure(3)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true, PlainToken.LEMMA), new WordNGramJaccardMeasure(4)));
		
		
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramContainmentMeasure(2)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramContainmentMeasure(2)));
		

		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true, PlainToken.LEMMA), new GreedyStringTiling(3)));
		return featureBuilder;
	}
	
	public static NoUIMAFeaturesBuilder buildDKProWordOverlapFeaturesNoTiling() {
		NoUIMAFeaturesBuilder featureBuilder = new NoUIMAFeaturesBuilder();
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new LongestCommonSubsequenceComparator()));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new LongestCommonSubstringComparator()));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true,PlainToken.LEMMA), new LongestCommonSubsequenceComparator()));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true,PlainToken.LEMMA), new LongestCommonSubstringComparator()));
		
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramJaccardMeasure(1)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramJaccardMeasure(2)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramJaccardMeasure(3)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramJaccardMeasure(4)));
		
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true, PlainToken.LEMMA), new WordNGramJaccardMeasure(1)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true, PlainToken.LEMMA), new WordNGramJaccardMeasure(2)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true, PlainToken.LEMMA), new WordNGramJaccardMeasure(3)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(true, PlainToken.LEMMA), new WordNGramJaccardMeasure(4)));
		
		
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramContainmentMeasure(2)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramContainmentMeasure(1)));
		featureBuilder.add(new NoUIMADKProSimilarity(new NoUIMAOrderedLexicalUnitProvider(PlainToken.LEMMA), new WordNGramContainmentMeasure(2)));
		

		
		return featureBuilder;
	}
	
	

	
	public static NoUIMAFeaturesBuilder buildBowFeatures() {
		NoUIMAFeaturesBuilder featureBuilder = new NoUIMAFeaturesBuilder()
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.LEMMA))) 
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.LEMMA, new int[] {
						1, 2 }, false)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.LEMMA, new int[] {
						1, 2 }, true)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.LEMMA, new int[] {
						1, 2, 3 }, false)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.LEMMA, new int[] {
						1, 2, 3 }, true)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.LEMMA, new int[] {
						2, 3 }, false)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.LEMMA, new int[] {
						2, 3 }, true)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.LEMMA, new int[] {
						2, 3, 4 }, false)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.LEMMA, new int[] {
						2, 3, 4 }, true)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.LEMMA, new int[] {
						3, 4 }, false)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.POSTAG, new int[] {
						1, 2, 3 }, false)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.POSTAG, new int[] {
						1, 2, 3, 4 }, false)))
				.add(new NoUIMACosineSimilarity(new NoUIMABowProvider(PlainToken.POSTAG, new int[] {
						2, 3, 4 }, false)));
		return featureBuilder;
	}
		
}
