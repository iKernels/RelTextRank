package it.unitn.nlpir.system.core;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.questions.QuestionsFileReader;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.resultsets.ResultSetFileReader;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.uima.Analyzer;
import it.unitn.nlpir.uima.UIMAFilePersistence;
import it.unitn.nlpir.uima.UIMAUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;



/**
 * 
 * <p>
 * The class converts the input files with the raw texts of the text pairs, e.g questions and answers, into the input training/test files for <a href="http://disi.unitn.it/moschitti/Tree-Kernel.htm">SVMLight-TK</a> reranking.
 * 
 * <br> See {@link QuestionsFileReader} for the specification of the input format for <b>text1</b>
 * <br> See {@link ResultSetFileReader} for the specification of the input format for <b>text2</b>
 * </p>
 * <p>
 * This class is the entry point constructing structural representations for the experiments reported in the following paper:
 * <br>
 * <b>Tymoshenko, Kateryna, Daniele Bonadiman, and Alessandro Moschitti. 
 * <i>"Convolutional neural networks vs. convolution kernels: Feature engineering for answer sentence reranking."</i> 
 * Proceedings of NAACL-HLT. 2016.</b> 
 * </p>
 * <p>
 * It was also used for the Trec13/Answerbag experiments without Linked Open Data/Wikipedia knowledge in the following paper:
 * <br>
 * <b>Tymoshenko, Kateryna, and Alessandro Moschitti. 
 * <i>"Assessing the impact of syntactic and semantic structures for answer passages reranking."</i> 
 * Proceedings of the 24th ACM International on Conference on Information and Knowledge Management. ACM, 2015.</b>
 * <br>
 * </p>
* @author IKernels group
 *
 */
public class RERTextPairConversion extends TextPairConversionBase {

	@Argument(description = "Number of the answer candidates per question to use for generating examples", required=false)
	protected static Integer candidatesToKeep = 15;

	@Argument(description = "Generation mode of SVM examples: [train, dev, test]", required=false)
	protected static String mode = "train";
	
	@Argument(description = "Path to the SVM file which contains the feature vectors for the corpus", required=false)
	protected static String featureFile;
	
	@Argument(description = "Path to the SVM file which contains the ids of the examples "
			+ "with the feature vectors for the corpus", required=false)
	protected static String featureLabelFile;
	
	
	public RERTextPairConversion(){
		super();
	

		// Instantiate the analyzer with persistence layer
		if (!Strings.isNullOrEmpty(filePersistence)) {
			analyzer = new Analyzer(experiment.getAnalysisEngineList(), new UIMAFilePersistence(
					filePersistence),!doNotSerializeNewAnnotations);
		} else {
			analyzer = new Analyzer(experiment.getAnalysisEngineList());
		}
		
		logger.info("Do not store new annotation: "+doNotSerializeNewAnnotations);

		// Create CAS for the question
		questionCas = analyzer.getNewJCas();
		
		// Create a CAS for the document
		documentCas = analyzer.getNewJCas();
	}
	
	protected void analyzeQuestion(JCas cas){
		analyzer.analyze(cas);
	}
	
	protected void analyzeDocument(JCas cas){
		analyzer.analyze(cas);
	}
	
	protected void finalize(){
		
	}
	
	protected void additionalProcessing(JCas questionCas, JCas documentCas, int qNum){
		
	}

	protected void disableEngines(){
		
		analyzer.disableAnalysisEngine("it.unitn.nlpir.annotators.old.QuestionClassifierWithCustomModels");	
		analyzer.disableAnalysisEngine("it.unitn.nlpir.annotators.old.SPTKQuestionClassifierWithCustomModels");
		analyzer.disableAnalysisEngine("it.unitn.nlpir.annotators.old.QuestionFocusThresholdAnnotator");
		analyzer.disableAnalysisEngine("QuestionFocusThresholdAnnotator");
		analyzer.disableAnalysisEngine("QuestionFocusAnnotator");

	}

	public void execute() {
		// Instantiate the logic of the generation process
		RerankingDataGen rerankingDataGen = instantiateRerankingDataGen(mode, outputDir);

		int j = 0;
		for (int i = 0, n = questions.size(); i < n; i++) {
			Question question = questions.get(i);
			String id = question.getId();

			logger.info(String.format("Processing question: %s (%s of %s)", id, i + 1, n));
			
			// Setup the question CAS
			UIMAUtil.setupCas(questionCas, "question-" + id, question.getText());
			logger.info(questionCas.getDocumentText());

			// Analyze question
			if (allowOverwriting){
				analyzer.forceExecutionOfAnalysisEngine("it.unitn.nlpir.annotators.old.QuestionClassifierWithCustomModels");
				analyzer.forceExecutionOfAnalysisEngine("it.unitn.nlpir.annotators.old.SPTKQuestionClassifierWithCustomModels");
				analyzer.forceExecutionOfAnalysisEngine("it.unitn.nlpir.annotators.QuestionFocusThresholdAnnotator");
				analyzer.forceExecutionOfAnalysisEngine("QuestionFocusThresholdAnnotator");
			}
			try {
				analyzeQuestion(questionCas);
			}
			catch (Exception e){
				e.printStackTrace();
				logger.error("ERROR when processing question {}, {}", id, question.getText());
				continue;
			}
			analyzer.disableAnalysisEngine("it.unitn.nlpir.annotators.old.QuestionClassifierWithCustomModels");
			analyzer.disableAnalysisEngine("it.unitn.nlpir.annotators.old.SPTKQuestionClassifierWithCustomModels");
			String questionClass = null;
			if (JCasUtil.select(questionCas, QuestionClass.class).size()>0)
					questionClass = JCasUtil.selectSingle(questionCas, QuestionClass.class)
					.getQuestionClass();


			List<Result> results = answers.getResults(id, candidatesToKeep);
			if (results == null) {
				logger.warn("No resultlist found for qid: {}", id);
				continue;
			}
			List<Candidate> candidates = new ArrayList<>();
			for (Result result : results) {
				
				// Setup the document CAS
				UIMAUtil.setupCas(documentCas, "document-" + result.documentId, result.documentText);
				logger.info(String.format("%s: %s", result.relevantFlag.toUpperCase(), documentCas.getDocumentText()));
				disableEngines();
				
				// Analyze document
				try {
					analyzeDocument(documentCas);

					// Re-enable the analysis engines
					analyzer.disableAnalysisEngine("it.unitn.nlpir.annotators.DiscourseFromFileAnnotator");
					if (questionClass!=null){
						
						it.unitn.nlpir.util.UIMAUtil.addQuestionClassToTheCandidateDocument(questionClass, documentCas);
					}

					//add question class to the documentCas as well (it will not be stored, however)
					additionalProcessing(questionCas, documentCas, j);
					j++;
					candidates.add(experiment.generateCandidate(questionCas, documentCas, result));
				}
				catch (Exception e){
					e.printStackTrace();
					logger.error("ERROR when processing result {}, {}", result.documentId, question.getText());
					continue;
				}
				
			}
			analyzer.enableAllAnalysisEngine();

			// Pass the data to the generation logic
			logger.info("Writing the train/test files");
			rerankingDataGen.handleData(candidates);
			
		}
		finalize();

		// Close resources used by the generation logic
		rerankingDataGen.cleanUp();
	}

	public static void main(String[] args) {
		try{
			Args.parse(RERTextPairConversion.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(RERTextPairConversion.class);
			
			System.exit(0);
		}
		
		RERTextPairConversion application = new RERTextPairConversion();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time ({}): {} (ms)", RERTextPairConversion.mode, watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}
	}
}
