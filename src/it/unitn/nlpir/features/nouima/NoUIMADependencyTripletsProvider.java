package it.unitn.nlpir.features.nouima;

import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainDocument;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainToken;
import it.unitn.nlpir.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;

public class NoUIMADependencyTripletsProvider implements NoUIMAFVProvider {
	private final Logger logger = LoggerFactory.getLogger(NoUIMADependencyTripletsProvider.class);

	private Alphabet featureDict;

	public static final int defaultTokenTextType = PlainToken.LEMMA;

	
	
	public NoUIMADependencyTripletsProvider() {
		this(defaultTokenTextType);
	}

	public NoUIMADependencyTripletsProvider(int tokenTextType) {
		featureDict = new Alphabet();
		
	}

	

	private FeatureSequence getFeatureSequenceFromCas(PlainDocument cas) {
		FeatureSequence fs = new FeatureSequence(featureDict);

		for (String depTuple : cas.getDependencies()) {
			fs.add(depTuple);
		}
		return fs;
	}



	@Override
	public String toString() {
		return "DependencyTripletsProvider [Dependency Triplets]";
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
