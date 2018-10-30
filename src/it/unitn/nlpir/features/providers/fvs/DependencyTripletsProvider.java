package it.unitn.nlpir.features.providers.fvs;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.Pair;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.cleartk.syntax.dependency.type.DependencyNode;
import org.cleartk.syntax.dependency.type.DependencyRelation;
import org.cleartk.syntax.dependency.type.TopDependencyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;

import com.google.common.collect.Lists;

public class DependencyTripletsProvider implements FVProvider {
	private final Logger logger = LoggerFactory.getLogger(DependencyTripletsProvider.class);

	private Alphabet featureDict;

	public static final String defaultTokenTextType = TokenTextGetterFactory.LEMMA;

	private TokenTextGetter tGetter;
	
	public DependencyTripletsProvider() {
		this(defaultTokenTextType);
	}

	public DependencyTripletsProvider(String tokenTextType) {
		featureDict = new Alphabet();
		this.tGetter = TokenTextGetterFactory.getTokenTextGetter(tokenTextType);
	}

	public static List<String> getDepTriplets(JCas jCas, TokenTextGetter tGetter) {
		
		List<String> deps = Lists.newArrayList();
		for (DependencyNode depnode : JCasUtil.select(jCas, DependencyNode.class)) {
			Token depToken = JCasUtil.selectCovered(Token.class, depnode).get(0);
			// Skip stop words.
			if (depToken.getIsFiltered())
				continue;
			for (DependencyRelation deprel : JCasUtil.select(depnode.getHeadRelations(),
					DependencyRelation.class)) {
				DependencyNode head = deprel.getHead();
				Token headToken = JCasUtil.selectCovered(Token.class, head).get(0);
				if (headToken.getIsFiltered())
					continue;

				String depTuple = null;
				if (head instanceof TopDependencyNode) {
					depTuple = String.format("%s(ROOT,%s)", deprel.getRelation(),
							tGetter.getTokenText(depToken));
				} else {
					depTuple = String.format("%s(%s,%s)", deprel.getRelation(),
							tGetter.getTokenText(headToken), tGetter.getTokenText(depToken));
				}
				//logger.debug("Triplet: {}", depTuple);
				deps.add(depTuple);
			}
		}
		return deps;
	}
	
	public static List<String> getDepTriplets(JCas jCas, TokenTextGetter tGetter, int begin, int end) {
		
		List<String> deps = Lists.newArrayList();
		for (DependencyNode depnode : JCasUtil.selectCovered(jCas, DependencyNode.class, begin, end)) {
			Token depToken = JCasUtil.selectCovered(Token.class, depnode).get(0);
			// Skip stop words.
			if (depToken.getIsFiltered())
				continue;
			for (DependencyRelation deprel : JCasUtil.select(depnode.getHeadRelations(),
					DependencyRelation.class)) {
				DependencyNode head = deprel.getHead();
				Token headToken = JCasUtil.selectCovered(Token.class, head).get(0);
				if (headToken.getIsFiltered())
					continue;

				String depTuple = null;
				if (head instanceof TopDependencyNode) {
					depTuple = String.format("%s(ROOT,%s)", deprel.getRelation(),
							tGetter.getTokenText(depToken));
				} else {
					depTuple = String.format("%s(%s,%s)", deprel.getRelation(),
							tGetter.getTokenText(headToken), tGetter.getTokenText(depToken));
				}
				//logger.debug("Triplet: {}", depTuple);
				deps.add(depTuple);
			}
		}
		return deps;
	}
	

	private FeatureSequence getFeatureSequenceFromCas(JCas cas) {
		FeatureSequence fs = new FeatureSequence(featureDict);

		for (String depTuple : getDepTriplets(cas,tGetter)) {
			fs.add(depTuple);
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
		return "DependencyTripletsProvider [Dependency Triplets]";
	}
	
	
}
