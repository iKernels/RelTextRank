package it.unitn.nlpir.features.providers.fvs;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.Pair;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;

public class BowWordNetSynonymsProvider implements FVProvider {
	private final Logger logger = LoggerFactory.getLogger(BowWordNetSynonymsProvider.class);

	private Alphabet featureDict = new Alphabet();

	private FeatureSequence getFeatureSequenceFromCas(JCas cas) {
		FeatureSequence fs = new FeatureSequence(featureDict);
		for (Token token : JCasUtil.select(cas, Token.class)) {
			if (token.getIsFiltered())
				continue;
			StringArray syns = token.getSynonyms();
			if (syns != null) {
				for (String syn : syns.toArray())
					fs.add(syn);
			}
		}
		return fs;
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
