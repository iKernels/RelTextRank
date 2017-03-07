package it.unitn.nlpir.features.providers.fvs;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import svmlighttk.SVMVector;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;

public class BowProvider implements FVProvider {
	private final Logger logger = LoggerFactory.getLogger(BowProvider.class);

	private Alphabet featureDict;
	private final int[] ngramSizes;
	private final static int[] defaultNgramSizes = new int[] { 1 };
	private static final String defaultTokenTextType = TokenTextGetterFactory.LEMMA;
	private final boolean filterStopwords;

	private TokenTextGetter tGetter;

	public BowProvider() {
		this(defaultTokenTextType, defaultNgramSizes, false);
	}

	public BowProvider(String tokenTextType) {
		this(tokenTextType, defaultNgramSizes, false);
	}

	public BowProvider(int[] ngramSizes) {
		this(defaultTokenTextType, ngramSizes, false);
	}
	
	public BowProvider(int[] ngramSizes, boolean filterStopwords, Alphabet featDict) {
		this(defaultTokenTextType, ngramSizes, filterStopwords, featDict);
	}
	
	public BowProvider(String tokenTextType, int[] ngramSizes, boolean filterStopwords) {
		this(tokenTextType, ngramSizes, filterStopwords, new Alphabet());
	}
	
	public BowProvider(String tokenTextType, int[] ngramSizes, boolean filterStopwords, Alphabet featDict) {
		featureDict = featDict;
		this.ngramSizes = ngramSizes;
		this.filterStopwords = filterStopwords;
		this.tGetter = TokenTextGetterFactory.getTokenTextGetter(tokenTextType);
	}

	public SVMVector getFeatureVector(JCas cas, Map<String, Double> idf) {
		SVMVector vec = new SVMVector(new FeatureVector(getFeatureNGramSequenceFromCas(cas)));
		return vec;
	}
	
	public SVMVector getFeatureVector(JCas cas) {
		SVMVector vec = new SVMVector(new FeatureVector(getFeatureNGramSequenceFromCas(cas)));
		return vec;
	}
	
	public SVMVector getWeightedWordFeatureVector(JCas cas, Map<String, Double> wordWeights) {		
		FeatureSequence fseq = new FeatureSequence(featureDict);
		for (Token t : JCasUtil.select(cas, Token.class)) {
			String text = tGetter.getTokenText(t);
			if (text == null || (this.filterStopwords && t.getIsFiltered()))
				continue;
			fseq.add(text);
		}
		FeatureVector fv = new FeatureVector(fseq);
		int[] fids = fv.getIndices();
		double[] values = fv.getValues();
		for (int i = 0; i < fv.numLocations(); i++) {
			int fid = fids[i];
//			String word = (String) featureDict.lookupObject(fid);
//			if (word == null)
//				continue;
//			Double weight = wordWeights.get(word);
//			if (weight == null)
//				weight = 0.0;
//			fv.setValue(fid, values[i] * weight);
			fv.setValue(fid, 1.0);
		}
		SVMVector vec = new SVMVector(fv);
		return vec;
	}
	
	public FeatureSequence getFeatureNGramSequenceFromCas(JCas cas) {
		String newTerm = null;
		FeatureSequence tmpTS = new FeatureSequence(featureDict);
		List<Token> ts = new ArrayList<>(JCasUtil.select(cas, Token.class));

		Iterator<Token> iter = ts.iterator();
		while (iter.hasNext()) {
			Token t = iter.next();
			String text = tGetter.getTokenText(t);
			if (text == null || (this.filterStopwords && t.getIsFiltered()))
				iter.remove();
		}

		for (int i = 0; i < ts.size(); i++) {
			String t = tGetter.getTokenText(ts.get(i));
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
					newTerm = tGetter.getTokenText(ts.get(i - k)) + "_" + newTerm;
				tmpTS.add(newTerm);
			}
		}
		return tmpTS;
	}

	private FeatureSequence getFeatureSequenceFromCas(JCas cas) {
		// FeatureSequence fs = new FeatureSequence(featureDict);
		// for (Token token : JCasUtil.select(cas, Token.class)) {
		// String word = tGetter.getTokenText(token);
		// if (!token.getIsFiltered() && word != null) {
		// fs.add(word);
		// }
		// }
		return getFeatureNGramSequenceFromCas(cas);
	}

	@Override
	public Pair<FeatureVector, FeatureVector> getFeatureVectors(QAPair qaPair) {
		FeatureVector q = new FeatureVector(getFeatureSequenceFromCas(qaPair.getQuestionCas()));
		FeatureVector d = new FeatureVector(getFeatureSequenceFromCas(qaPair.getDocumentCas()));

		logger.debug("Question fvec: {}", q.toString(true));
		logger.debug("Document fvec: {}", d.toString(true));

		return new Pair<FeatureVector, FeatureVector>(q, d);
	}
}
