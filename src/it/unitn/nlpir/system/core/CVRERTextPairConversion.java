package it.unitn.nlpir.system.core;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.crossvalidation.CrossValidation;
import it.unitn.nlpir.crossvalidation.Fold;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.questions.QuestionsFileReader;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.resultsets.ResultSetFileReader;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.uima.UIMAUtil;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Stopwatch;



/**
 * * 
 * <p>
 * The class converts the input files with the raw texts of the text pairs, e.g questions and answers, and generates the cross-validation folds with the input training/test files for <a href="http://disi.unitn.it/moschitti/Tree-Kernel.htm">SVMLight-TK</a> reranking
 * 
 * <br> See {@link QuestionsFileReader} for the specification of the input format for <b>text1</b>
 * <br> See {@link ResultSetFileReader} for the specification of the input format for <b>text2</b>
 * 
 * </p>
 * 
 * <p>
 * The class was used as the entry point for TREC 2002/2003 cross-validation experiments without the Linked Open Data/Wikipedia knowledge in the following paper:
 * <br>
 * <b>Tymoshenko, Kateryna, and Alessandro Moschitti. 
 * <i>"Assessing the impact of syntactic and semantic structures for answer passages reranking."</i> 
 * Proceedings of the 24th ACM International on Conference on Information and Knowledge Management. ACM, 2015.</b>
 * <br>
 * 
 * </p>
* @author IKernels group
 *
 */
public class CVRERTextPairConversion extends TextPairConversionBase {
	
	@Argument(description = "Number of the answer candidates per question to use for generating train examples")
	private static Integer candidatesToKeepTrain = 10;

	@Argument(description = "Number of the answer candidates per question to use for generating test examples")
	private static Integer candidatesToKeepTest = 5;

	@Argument(description = "Number of cross-validation folds")
	private static Integer numFolds = 5;

	@Argument(description = "Random seed for shuffling the input data in cross-validation")
	private static Integer seed = 123;


	protected void additionalProcessing(JCas questionCas, JCas documentCas, int qNum){
		
	}
	
	protected void additionalQARelatedProcessing(JCas questionCas, JCas documentCas, int qNum){
		if (JCasUtil.select(questionCas,  QuestionClass.class).size()<1)
			return;
		String questionClass = JCasUtil.selectSingle(questionCas, QuestionClass.class)
				.getQuestionClass();
		it.unitn.nlpir.util.UIMAUtil.addQuestionClassToTheCandidateDocument(questionClass, documentCas);
	}
	

	public CVRERTextPairConversion(){
		super();

		
	}
	
	public void execute() {
		CrossValidation<Question> cv = new CrossValidation<>(questions, seed);
		HashMap<Question, List<Candidate>> train = generateExamples(questions, "train",
				candidatesToKeepTrain);
		HashMap<Question, List<Candidate>> test = generateExamples(questions, "test",
				candidatesToKeepTest);

		int foldId = 0;
		for (Fold<Question> fold : cv.getFolds(numFolds)) {
			String outputCVDir = Paths.get(outputDir, "fold" + (foldId++)).toString();
			logger.info("Generating the fold: {}", outputCVDir);

			// Write train
			writeExamples(train, fold.getTrain(), "train", outputCVDir);

			// Write test
			writeExamples(test, fold.getTest(), "test", outputCVDir);
		}
	}

	protected void writeExamples(HashMap<Question, List<Candidate>> qid2candidates,
			List<Question> questions, String mode, String outputDir) {
		RerankingDataGen rerankingDataGenTest = instantiateRerankingDataGen(mode, outputDir);
		for (Question q : questions) {
			rerankingDataGenTest.handleData(qid2candidates.get(q));
		}
		rerankingDataGenTest.cleanUp();
	}

	
	protected HashMap<Question, List<Candidate>> generateExamples(List<Question> questions,
			String mode, int candidatesToKeep) {
		HashMap<Question, List<Candidate>> question2candidates = new HashMap<>();
		logger.info("Generating examples in mode: {}", mode);


		int j = 0;
		for (int i = 0, n = questions.size(); i < n; i++) {
			long startQuestionTime = System.currentTimeMillis();
			Question question = questions.get(i);
			String id = question.getId();

			List<Candidate> candidates = new ArrayList<>();
			question2candidates.put(question, candidates);

			logger.info(String.format("Processing question: %s (%s of %s)", id, i + 1, n));

			// Setup the question CAS
			UIMAUtil.setupCas(questionCas, "question-" + id, question.getText());

			if (allowOverwriting){
				forceEnginesExecution();
			}
			logger.info(questionCas.getDocumentText());
			// Analyze question
			analyzer.analyze(questionCas);
			


			List<Result> results = answers.getResults(id, candidatesToKeep);
			if (results == null) {
				logger.warn("No resultlist found for qid: {}", id);
				//continue;
			}
			
			for (Result result : results) {
				long startTime = System.currentTimeMillis();
				UIMAUtil.setupCas(documentCas, "document-" + result.documentId, result.documentText);

				// Disable analysis engines not desired for documents
				disableForcedEnginesExecution();
				disableQuestionRelevantAnalyzersOnly();
				
				// Analyze document
				analyzer.analyze(documentCas);

				logger.info(String.format("%s: %s", result.relevantFlag.toUpperCase(), documentCas.getDocumentText()));

				additionalProcessing(questionCas, documentCas, j);
				additionalQARelatedProcessing(questionCas, documentCas, j);
				candidates.add(experiment.generateCandidate(questionCas, documentCas, result));
				Candidate c = candidates.get(candidates.size()-1);
				String pair = c.getQa().getA() +" " +c.getQa().getB();
				pair = pair.replace("(", "[").replace(")", "]");
				logger.debug(pair);
				j++;
				long endTime = System.currentTimeMillis();
				logger.info(String.format("Processed answer %s in %d ms", result.documentId, endTime-startTime));
			}
			analyzer.enableAllAnalysisEngine();
			long endQuestionTime = System.currentTimeMillis();
			logger.info(String.format("Processed question %s in %d ms", question.getId(), endQuestionTime-startQuestionTime));
		}

		return question2candidates;
	}

	public static void main(String[] args) {

		try{
			Args.parse(CVRERTextPairConversion.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(CVRERTextPairConversion.class);
			
			System.exit(0);
		}
		


		CVRERTextPairConversion application = new CVRERTextPairConversion();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();
			application.execute();
			logger.info("Run-time: {} (ms)", watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}

	}
}
