package it.unitn.nlpir.features;

import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.NERUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

public class EntitiesDistributionInDocument implements FeatureExtractor {
	protected static final Logger logger = LoggerFactory.getLogger(EntitiesDistributionInDocument.class);
	@Override
	public void extractFeatures(QAPair qa) {
		double score = 0.0;

		int numEntities = JCasUtil.select(qa.documentCas, NER.class).size();

		if (numEntities == 0) {
			qa.featureVector.addFeature(score);
			return;
		}

		String questionClass = null;
		try {
			questionClass = UIMAUtil.getQuestionClass(qa.questionCas);
		} catch (AnnotationNotFoundException e) {
			e.printStackTrace();
		}
		int numerator = NERUtil.getNERsByQuestionCategory(questionClass, qa.documentCas).size();

		score = (double) numerator / numEntities;
		
		qa.featureVector.addFeature(score);
	}

	@Override
	public String getFeatureName() {
		return this.getClass().getSimpleName();
	}

}
