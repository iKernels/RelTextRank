package it.unitn.nlpir.features.nouima;

import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainDocument;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainToken;
import it.unitn.nlpir.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svmlighttk.SVMVector;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;

public class NoUIMABowProvider implements NoUIMAFVProvider {
	private final Logger logger = LoggerFactory.getLogger(NoUIMABowProvider.class);

	private Alphabet featureDict;
	private final int[] ngramSizes;
	private final static int[] defaultNgramSizes = new int[] { 1 };
	private static final int defaultTokenTextType = PlainToken.LEMMA;
	private final boolean filterStopwords;
	private int tokenTextType;
	


	
	@Override
	public String toString() {
		return "BowProvider [ngramSizes=" + Arrays.toString(ngramSizes) + ", filterStopwords=" + filterStopwords + "]";
	}

	public NoUIMABowProvider() {
		this(defaultTokenTextType, defaultNgramSizes, false);
	}

	public NoUIMABowProvider(int tokenTextType) {
		this(tokenTextType, defaultNgramSizes, false);
	}

	public NoUIMABowProvider(int[] ngramSizes) {
		this(defaultTokenTextType, ngramSizes, false);
	}
	
	public NoUIMABowProvider(int[] ngramSizes, boolean filterStopwords, Alphabet featDict) {
		this(defaultTokenTextType, ngramSizes, filterStopwords, featDict);
	}
	
	public NoUIMABowProvider(int tokenTextType, int[] ngramSizes, boolean filterStopwords) {
		this(tokenTextType, ngramSizes, filterStopwords, new Alphabet());
	}
	
	public NoUIMABowProvider(int tokenTextType, int[] ngramSizes, boolean filterStopwords, Alphabet featDict) {
		featureDict = featDict;
		this.ngramSizes = ngramSizes;
		this.filterStopwords = filterStopwords;
		this.tokenTextType = tokenTextType;
	}

	
	
	public SVMVector getFeatureVector(PlainDocument cas) {
		SVMVector vec = new SVMVector(new FeatureVector(getFeatureNGramSequenceFromCas(cas)));
		return vec;
	}
	
	public SVMVector getWeightedWordFeatureVector(PlainDocument cas, Map<String, Double> wordWeights) {		
		FeatureSequence fseq = new FeatureSequence(featureDict);
		for (PlainToken t : cas.getTokens()) {
			String text = t.getProperty(tokenTextType);
			if (text == null || (this.filterStopwords && t.isStopword()))
				continue;
			fseq.add(text);
		}
		FeatureVector fv = new FeatureVector(fseq);
		int[] fids = fv.getIndices();
		
		for (int i = 0; i < fv.numLocations(); i++) {
			int fid = fids[i];
			fv.setValue(fid, 1.0);
		}
		SVMVector vec = new SVMVector(fv);
		return vec;
	}
	
	public FeatureSequence getFeatureNGramSequenceFromCas(PlainDocument cas) {
		String newTerm = null;
		FeatureSequence tmpTS = new FeatureSequence(featureDict);
		
		//
		List<String> tokensData = new ArrayList<String>();
		for (PlainToken t : cas.getTokens()) {
			if (t.getLemma() != null && (!(this.filterStopwords && t.isStopword())))
				tokensData.add(t.getProperty(tokenTextType));
				
		}
				

		for (int i = 0; i < tokensData.size(); i++) {
			String t =tokensData.get(i);
			for (int j = 0; j < this.ngramSizes.length; j++) {
				int len = this.ngramSizes[j];
				if (len <= 0 || len > (i + 1))
					continue;
				if (len == 1) {
					tmpTS.add(t);
					continue;
				}
				newTerm = new String(t);
				for (int k = 1; k < len; k++)
					newTerm = tokensData.get(i - k) + "_" + newTerm;
				tmpTS.add(newTerm);
			}
		}
		return tmpTS;
	}

	private FeatureSequence getFeatureSequenceFromCas(PlainDocument cas) {
		return getFeatureNGramSequenceFromCas(cas);
	}



	@Override
	public Pair<FeatureVector, FeatureVector> getFeatureVectors(NoUIMACandidate qa) {

		FeatureVector q = new FeatureVector(getFeatureSequenceFromCas(qa.getPair().getA()));
		FeatureVector d = new FeatureVector(getFeatureSequenceFromCas(qa.getPair().getB()));

		logger.debug("Question fvec: {}", q.toString(true));
		logger.debug("Document fvec: {}", d.toString(true));

		return new Pair<FeatureVector, FeatureVector>(q, d);
	}
}
