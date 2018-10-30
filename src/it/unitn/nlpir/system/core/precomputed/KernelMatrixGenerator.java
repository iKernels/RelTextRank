package it.unitn.nlpir.system.core.precomputed;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.core.TextPairConversionBase;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.system.datagen.kernmat.KernelMatrixDataGen;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.Pair;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import com.google.common.base.Stopwatch;



/**
 * 
 * <p>
 * The class generates the kernel matrix of pairwise similarities for a given corpus.
 * Takes question/answer files as input where the first letter of and ID should be R|D|T (tRain|Dev|Test)
 * </p>
 * <p>
 * </p>
* @author IKernels group
 *
 */

///NOTE: this IMPLEMENTATION HAS A BUG AS IT IS NOW.
///IT SKIPS Q/AP-FOR-OTHER-Q COMPARISONS
public class KernelMatrixGenerator extends TextPairConversionBase {

	@Argument(description = "Number of the answer candidates per question to use for generating test and dev examples", required=false)
	protected static Integer candidatesToKeep = 1000;

	@Argument(description = "Number of the answer candidates per question to use for generating examples", required=false)
	protected static Integer candidatesToKeepTrain = 1000;
	
	@Argument(description = "Generation mode of SVM examples: [train, dev, test]", required=false)
	protected static String mode = "train";
	
	@Argument(description = "keep examples with only negative answers", required=false)
	protected static Boolean keepNegatives = false;
	
	@Argument(description = "thread number (i.e. process only n-th pair); if -1 then do not parallelize;", required=false)
	protected static Integer thread = -1;
	
	@Argument(description = "total number of threads;", required=false)
	protected static Integer totalThreadNumber = -1;
	
	@Argument(description = "location of already processed pairs;", required=false)
	protected static String alreadyProcessedPairsFile = null;
	
	protected Set<Pair<String,String>> alreadyProcessed = null;
	
	
	
	public KernelMatrixGenerator(){
		super();

		// Create CAS for the question
		//questionCas = analyzer.getNewJCas();
		
		// Create a CAS for the document
		//documentCas = analyzer.getNewJCas();
		
		if (alreadyProcessedPairsFile!=null)
			try {
				alreadyProcessed = readAlreadyProcessed(alreadyProcessedPairsFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	protected Set<Pair<String,String>> readAlreadyProcessed(String fileProcessed) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileProcessed), "UTF8"));
		Set<Pair<String,String>> alreadyProcessed = new HashSet<Pair<String,String>>();
		String line = null;
		logger.info(String.format("Reading the info about the already processed pairs of ids from %s",fileProcessed));
		while ((line = in.readLine()) != null) {
			String [] parts = line.split("\t");
			alreadyProcessed.add(new Pair<String,String>(parts[0],parts[1]));
			if (alreadyProcessed.size() % 1000000 == 0)
				logger.info(String.format("Read %d lines",alreadyProcessed.size()));
		}
		in.close();
		logger.info(String.format("Read %d already processed pairs", alreadyProcessed.size()));
		return alreadyProcessed;
	}
	
	protected void analyze(JCas cas){
		analyzer.analyze(cas);
	}
	
	
	protected void finalize(){
		
	}
	
	
	protected RerankingDataGen instantiateRerankingDataGen(String outputDir) {
		return new KernelMatrixDataGen(outputDir);
	}
	
	public boolean isTrainQuestion(Question q) {
		return (q.getId().startsWith("R"));
	}
	
	
	boolean containsPositive(List<Result> results, int numResultsToKeep) {
		
		for (Result r: results)
			if (r.relevantFlag.equals("true"))
				return true;

		return false;
	}
	
	boolean isProcessed(String id1, String id2) {
		return alreadyProcessed == null ? false : (alreadyProcessed.contains(new Pair<String,String>(id1,id2))||alreadyProcessed.contains(new Pair<String,String>(id2,id1)));
	}
	
	
	private JCas getJCasForID(String id, Map<String,JCas> cache, String text) {
		if (!cache.containsKey(id)) {
			JCas jCas = analyzer.getNewJCas();
			UIMAUtil.setupCas(jCas, id, text);
			//logger.info(text);
			analyze(jCas);
			cache.put(id, jCas);
//			logger.info(String.format("JCas size: %d", jCas.size()));
			return jCas;
		}
		return cache.get(id);
	}
	
	private Map<String,JCas> preinitQuestionCache() {
		Map<String,JCas> questionCases = new HashMap<String,JCas>();
		for (int i = 0, n = questions.size(); i < n; i++) {
			Question question = questions.get(i);
			getJCasForID(question.getId(), questionCases, question.getText());
		}
		return questionCases;
	}
	
	private Map<String,JCas> preinitAnswerCache() {
		Map<String,JCas> resultCases = new HashMap<String,JCas>();
		for (int i = 0, n = questions.size(); i < n; i++) {
			Question question = questions.get(i);
			int numResultsToKeep = isTrainQuestion(question) ? candidatesToKeepTrain : candidatesToKeep;
			List<Result> results_i = answers.getResults(question.getId(), numResultsToKeep);
			if ((!keepNegatives) && (!(containsPositive(results_i, numResultsToKeep)))) {
				logger.info(String.format("Skipping question %s with no positive answers", question.getId()));
				continue;
			}
			for (int j = 0, nj = results_i.size(); j < nj; j++) {
				Result result = results_i.get(j);
				if (isProcessed(question.getId(), result.documentId))
					continue;
				getJCasForID(result.documentId, resultCases, result.documentText);

			}
		}
		return resultCases;
	}
/*
 * for (int j = 0, nj = Math.min(limit,results_i.size()); j < nj; j++) {
				Result result = results.get(j);
				if (isProcessed(id, result.documentId))
					continue;
				JCas documentJCas = getJCasForID(result.documentId, answerCases, result.documentText);
				if ((totalThreadNumber<0)||(count % totalThreadNumber == thread )) {
					Result r = new Result(id, result.documentId, "0.0", "0.0", "false", "");
					logger.info(String.format("Processing %s-%s",id,result.documentId));
					candidates.add(experiment.generateCandidate(questionJCas, documentJCas, r));
					count_time++;
				}
				count++;
			
			}(non-Javadoc)
 * @see it.unitn.nlpir.system.core.TextPairConversionBase#execute()
 */
	public void execute() {
		
		RerankingDataGen rerankingDataGen = instantiateRerankingDataGen(outputDir);
		
		//question-to-question
		List<Result> results = new ArrayList<Result>();
		int limit=Integer.MAX_VALUE;
		
		int count=0;
		
		//reducing the time for reading from the hard drive
		long globalStart = System.currentTimeMillis();
		
		logger.info("Started reading the cached data");
		Map<String,JCas> questionCases = preinitQuestionCache();
		Map<String,JCas> answerCases = preinitAnswerCache();
		logger.info("Finished reading the cached data");
		logger.info(String.format("Read the CASes in %d ms", System.currentTimeMillis()-globalStart));
		
		globalStart = System.currentTimeMillis();
		logger.info("Processing result-question pairs");
		int count_time = 0;
		for (int i = 0, n = Math.min(limit,questions.size()); i < n; i++) {
			
			Question question = questions.get(i);
			String id = question.getId();
			int numResultsToKeep = isTrainQuestion(question) ? candidatesToKeepTrain : candidatesToKeep;
			if ((!keepNegatives)) {
				if  (!(containsPositive(answers.getResults(question.getId(), numResultsToKeep), numResultsToKeep))){
					logger.info(String.format("Not processing question %s with no positive answers", id));
					continue;
				}
			}
			
			JCas questionJCas = getJCasForID(id, questionCases, question.getText());
			
			List<Candidate> candidates = new ArrayList<>();
			
			
			List<Result> results_i = answers.getResults(id, numResultsToKeep);
			if ((!keepNegatives) && (!(containsPositive(results_i, numResultsToKeep)))) {
				logger.info(String.format("Skipping question %s with no positive answers", id));
				continue;
			}
			if (results_i!=null)
					results.addAll(results_i);
			
			for (int j = 0, nj = Math.min(limit,results_i.size()); j < nj; j++) {
				Result result = results.get(j);
				if (isProcessed(id, result.documentId))
					continue;
				JCas documentJCas = getJCasForID(result.documentId, answerCases, result.documentText);
				if ((totalThreadNumber<0)||(count % totalThreadNumber == thread )) {
					Result r = new Result(id, result.documentId, "0.0", "0.0", "false", "");
					if ((id.equals("1.4") && result.documentId.equals("1.5-0")))
						System.out.println("Caught");
					logger.info(String.format("Processing %s-%s",id,result.documentId));
					candidates.add(experiment.generateCandidate(questionJCas, documentJCas, r));
					count_time++;
				}
				count++;
			
			}
			
			rerankingDataGen.handleData(candidates);
			
		}
		long globalEnd = System.currentTimeMillis();
		logger.info(String.format("Took %.5f ms average per pair", Double.valueOf(globalEnd-globalStart)/Double.valueOf(count_time)));
		
		globalStart = System.currentTimeMillis();
		count_time = 0;
		logger.info("Processing question-question pairs");
		for (int i = 0, n = Math.min(limit,questions.size()); i < n; i++) {
			Question question_i = questions.get(i);
			
			
			String id = question_i.getId();

			
			
			logger.info(String.format("Processing question: %s (%s of %s)", id, i + 1, n));
			
			JCas questionICas = getJCasForID(id, questionCases, question_i.getText());
			
			/*UIMAUtil.setupCas(questionCas,id, question_i.getText());
			analyze(questionCas);*/
			
			
			
			List<Candidate> candidates = new ArrayList<>();
			for (int j = i+1, nj = Math.min(limit,questions.size()); j < nj; j++) {
				Question question_j = questions.get(j);
				int numResultsToKeep_j = isTrainQuestion(question_j) ? candidatesToKeepTrain : candidatesToKeep;
				
				if (isProcessed(question_i.getId(), question_j.getId()))
					continue;
				
				if ((!keepNegatives)) {
					if  (!(containsPositive(answers.getResults(question_j.getId(), numResultsToKeep_j), numResultsToKeep_j))){
						logger.info(String.format("Not comparing to question %s with no positive answers", question_j.getId()));
						continue;
					}
				}
				
				/*UIMAUtil.setupCas(documentCas, question_j.getId(), question_j.getText());
				analyze(documentCas);
				Result r = new Result(id, question_j.getId(), "0.0", "0.0", "false", "");*/
				JCas questionJCas = getJCasForID(question_j.getId(), questionCases, question_j.getText());
				Result r = new Result(id, question_j.getId(), "0.0", "0.0", "false", "");
				if ((totalThreadNumber<0)||(count % totalThreadNumber == thread )) {
					logger.info(String.format("Processing %s-%s",question_i.getId(),question_j.getId()));
					candidates.add(experiment.generateCandidate(questionICas, questionJCas, r));
					count_time++;
				}
				count++;
			}
			rerankingDataGen.handleData(candidates);
		
		}
		logger.info(String.format("%d results total read", results.size()));
		globalEnd = System.currentTimeMillis();
		logger.info(String.format("Took %.5f ms average per pair", Double.valueOf(globalEnd-globalStart)/Double.valueOf(count_time)));
		
		
		//answer-to-answer
		globalStart = System.currentTimeMillis();
		count_time = 0;
		for (int i = 0, n = Math.min(limit,results.size()); i < n; i++) {
			Result r_i = results.get(i);
			String r_i_id = r_i.documentId;
			
			/*UIMAUtil.setupCas(questionCas, r_i_id, r_i.documentText);
			analyze(questionCas);*/
			JCas documentICas = getJCasForID(r_i_id, answerCases, r_i.documentText);

			List<Candidate> candidates = new ArrayList<>();
			for (int j = i+1, nj = Math.min(limit,results.size()); j < nj; j++) {
				Result r_j = results.get(j);	
				String r_j_id = r_j.documentId;
				logger.info(String.format("processing (%s,%s)",r_i_id,r_j_id ));
				if (isProcessed(r_i_id, r_j_id))
					continue;
				/*UIMAUtil.setupCas(documentCas, r_j_id, r_j.documentText);
				analyze(documentCas);*/
				JCas documentJCas = getJCasForID(r_j_id, answerCases, r_j.documentText);
				
				if ((totalThreadNumber<0)||(count % totalThreadNumber == thread )) {
					Result r = new Result(r_i_id, r_j_id, "0.0", "0.0", "false", "");
					logger.info(String.format("Processing %s-%s",r_i_id,r_j_id));
					candidates.add(experiment.generateCandidate(documentICas, documentJCas, r));
					count_time++;
				}
				count++;
			}
			rerankingDataGen.handleData(candidates);
		}
		
		logger.info("Processed all result-result pairs");
		
		globalEnd = System.currentTimeMillis();
		logger.info(String.format("Took %.5f ms average per pair", Double.valueOf(globalEnd-globalStart)/Double.valueOf(count_time)));
		
		finalize();

		// Close resources used by the generation logic
		rerankingDataGen.cleanUp();
	}

	public static void main(String[] args) {
		try{
			Args.parse(KernelMatrixGenerator.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(KernelMatrixGenerator.class);
			
			System.exit(0);
		}
		
		KernelMatrixGenerator application = new KernelMatrixGenerator();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time ({}): {} (ms)", KernelMatrixGenerator.mode, watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}
	}
}
