package it.unitn.nlpir.features;

import it.unitn.nlpir.types.QuestionClass;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;


public class QuestionCategoryFeature implements FeatureExtractor {
	private static final Logger logger = LoggerFactory.getLogger(QuestionCategoryFeature.class);
	
	public static final List<String> questionTypes = Arrays.asList("ABBR", "DESC", "ENTY", "HUM", "NUM", "LOC", "WHO", "WHY", "WHEN", 
			"QUANTITY", "LOCATION", "ENTITY", "DURATION", "DESC", "CURRENCY", "DATE");
	
	@Override
	public void extractFeatures(QAPair qa) {
		String questionClass = JCasUtil.selectSingle(qa.getQuestionCas(), QuestionClass.class)
				.getQuestionClass();
		Double[] features = new Double[questionTypes.size()];
		Arrays.fill(features, 0.0);
		
		int id = questionTypes.indexOf(questionClass);
		if (id == -1)
			logger.error("Unknown question type: {}", questionClass);
		features[id] = 1.0;
		qa.featureVector.addFeatures(Arrays.asList(features));
	}

	@Override
	public String getFeatureName() {
		return this.getClass().getSimpleName();
	}

}
