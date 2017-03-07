package it.unitn.nlpir.annotators;

import it.unitn.nlpir.types.DocumentId;
import it.unitn.nlpir.types.QuestionClass;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.TypeCapability;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Charsets;
import com.google.common.io.Files;


@TypeCapability(inputs = {  }, outputs = { "it.unitn.nlpir.types.QuestionClass" })
public class FromFileQuestionClassifier extends JCasAnnotator_ImplBase {
	private final Logger logger = LoggerFactory.getLogger(FromFileQuestionClassifier.class);
	
	public static final String QUESTION_CATEGORIES_FILE = "question_categories_file";
	@ConfigurationParameter(name = QUESTION_CATEGORIES_FILE, mandatory = true)
	private String questionCategoriesFile;

	private Map<String, String> questionIdToClass;
	
	
	
	
	public static AnalysisEngineDescription getDescription(String questionCategoriesFile) {
		AnalysisEngineDescription ann = null;
		try {
			ann = AnalysisEngineFactory.createPrimitiveDescription(FromFileQuestionClassifier.class, QUESTION_CATEGORIES_FILE);
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return ann;
	}
	
	private void readQuestionCategories(String fname) throws IOException {
		for (String line : Files.readLines(new File(fname), Charsets.UTF_8)) {
			String[] data = line.trim().split(" ");

			questionIdToClass.put(data[0].trim(), data[1].trim());
		}
	}
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {		
		super.initialize(aContext);
		this.questionIdToClass = new HashMap<>();
		try {
			logger.debug("reading questioncategories from {}", questionCategoriesFile);
			readQuestionCategories(questionCategoriesFile);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {

		String questionId = JCasUtil.selectSingle(cas, DocumentId.class).getId();
		questionId = questionId.split("-")[1];
		String goldClass = questionIdToClass.get(questionId);
		
		logger.info("Question category: {} for question: {}", goldClass, questionId);

		if (JCasUtil.select(cas, QuestionClass.class).size()>0){
			Collection<QuestionClass> qClasses = JCasUtil.select(cas, QuestionClass.class);
			
			for (QuestionClass qclass : qClasses){
				cas.removeFsFromIndexes(qclass);
			}
			
		}
		
		QuestionClass questionClass = new QuestionClass(cas);
		questionClass.setBegin(0);
		questionClass.setEnd(cas.getDocumentText().length());
		questionClass.setQuestionClass(goldClass);
		questionClass.addToIndexes();
	}
}
