package it.unitn.nlpir.annotators.old;

import it.unitn.nlpir.tools.NLPFactory;
import it.unitn.nlpir.tools.OneVsAllClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.descriptor.TypeCapability;

@TypeCapability(inputs = {  }, outputs = { "it.unitn.nlpir.types.QuestionClass" })
public class SPTKQuestionClassifierWithCustomModels extends QuestionClassifierWithCustomModels {
	static final Logger logger = LoggerFactory.getLogger(SPTKQuestionClassifierWithCustomModels.class);

	
	protected OneVsAllClassifier getClassifier(){
		return NLPFactory.getSPTKClassifiersFromFolder(modelsFolder);
	}
	
	

}
