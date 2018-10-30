package it.unitn.nlpir.features.providers.fvs;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.Pair;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;

public class BowNEProvider implements FVProvider {
	private final Logger logger = LoggerFactory.getLogger(BowNEProvider.class);

	private Alphabet featureDict = new Alphabet();

	private FeatureSequence getFeatureSequenceFromCas(JCas cas) {
		FeatureSequence fs = new FeatureSequence(featureDict);
		for (NER ner : JCasUtil.select(cas, NER.class)) {
			String lemma = "";
			for (Token t : JCasUtil.selectCovered(Token.class, ner)){
				lemma = lemma +" "+t.getLemma().toLowerCase();
			}
			lemma = lemma.trim().replace(" ", "_");
			fs.add(lemma);
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Named Entities BOW";
	}
}
