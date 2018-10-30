package it.unitn.nlpir.system.core.precomputed;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.experiment.kernmat.NoUIMAExperiment;
import it.unitn.nlpir.experiment.kernmat.NoUIMAFeatureOnlyExperiment;
import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainDocument;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.core.precomputed.util.ParallelFeatureExtractor;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.system.datagen.kernmat.KernelMatrixDataGen;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;



/**
 * 
 * <p>
 * The class generates the kernel matrix of pairwise similarities for a given corpus.
 * Takes question/answer files as input where the first letter of and ID should be R|D|T (tRain|Dev|Test)
 * </p>
 * <p>
 * 
 * To avoid disc IO overhead, keeps everything in memory. JCases are extremely constly to be held in memory, so I am reading everything into a pseudo-cas struture,
 * and I have created version of feature extractors which work for these data.
 * 
 * 
 * 
 * </p>
* @author IKernels group
 *
 */
public class RamKernelMatrixGenerator extends KernelMatrixGenerator {

	protected static final Logger logger = LoggerFactory.getLogger(RamKernelMatrixGenerator.class);
	protected NoUIMAExperiment noUIMAExperiment;
	
	protected List<NoUIMAExperiment> noUIMAExperiments;
	
	@Argument(description = "parallelize", required=false)
	protected static Integer threads = -1;
	
	@Argument(description = "Preset feature extractor class (see it.unitn.nlpir.features.presets for options). If this parameter is not set, then the feature vector will be empty.", required=false)
	protected static String pairwiseFeatureExtractorClass;
	
	
	@Argument(description = "Maximum sentences to use per answer", required=false)
	protected static Integer maxSentencesPerAnswer=-1;
	
	@Argument(description = "Maximum tokens to use per sentence", required=false)
	protected static Integer maxTokensPerSentence=-1;
	
	
	public RamKernelMatrixGenerator(){
		super();
		if (threads<=1)
			this.noUIMAExperiment = new NoUIMAFeatureOnlyExperiment(pairwiseFeatureExtractorClass);
		else {
			this.noUIMAExperiments = new ArrayList<NoUIMAExperiment>();
			for (int i = 0; i< threads; i++)
				this.noUIMAExperiments.add(new NoUIMAFeatureOnlyExperiment(pairwiseFeatureExtractorClass));
		}
		
		// Create CAS for the question
		questionCas = analyzer.getNewJCas();
		
		// Create a CAS for the document
		documentCas = analyzer.getNewJCas();
		
		if (alreadyProcessedPairsFile!=null)
			try {
				alreadyProcessed = readAlreadyProcessed(alreadyProcessedPairsFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	

	
	
	protected PlainDocument getJCasForID(JCas jCas, String id, Map<String,PlainDocument> cache, String text, int sentnum, int toknum) {
		if (!cache.containsKey(id)) {
			UIMAUtil.setupCas(jCas, id, text);
			analyze(jCas);
			try {
				PlainDocument doc = new PlainDocument(jCas, id, sentnum, toknum);
				cache.put(id, doc);
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.error(String.format("Error when processing doc %s: '%s'",id, jCas.getDocumentText()));
			}
			
		}
		return cache.get(id);
	}
	
	protected Map<String,PlainDocument> preinitQuestionCache() {
		Map<String,PlainDocument> questionCases = new HashMap<String,PlainDocument>();
		for (int i = 0, n = questions.size(); i < n; i++) {
			//if (((totalThreadNumber>1) && (i%totalThreadNumber==thread))||(totalThreadNumber<=1)) {
				Question question = questions.get(i);
				getJCasForID(questionCas, question.getId(), questionCases, question.getText(),-1,-1);
			//}
		}
		logger.info(String.format("%d question CASes read", questionCases.size()));
		return questionCases;
		
	}
	
	protected Map<String,PlainDocument> preinitAnswerCache() {
		Map<String,PlainDocument> resultCases = new HashMap<String,PlainDocument>();
		for (int i = 0, n = questions.size(); i < n; i++) {
			//if (!(((totalThreadNumber>1) && (i%totalThreadNumber==thread))||(totalThreadNumber<=1))) {
			//	continue;
			//}
				
			Question question = questions.get(i);
			int numResultsToKeep = isTrainQuestion(question) ? candidatesToKeepTrain : candidatesToKeep;
			List<Result> results_i = answers.getResults(question.getId(), numResultsToKeep);
			if (results_i==null) {
				logger.warn(String.format("No results found for question %s",question.getId()));
				continue;
			}
				
			if ((!keepNegatives) && (!(containsPositive(results_i, numResultsToKeep)))) {
				logger.info(String.format("Skipping question %s with no positive answers", question.getId()));
				continue;
			}
			for (int j = 0, nj = results_i.size(); j < nj; j++) {
				Result result = results_i.get(j);
				if (isProcessed(question.getId(), result.documentId))
					continue;
				getJCasForID(documentCas,result.documentId, resultCases, result.documentText, maxSentencesPerAnswer, maxTokensPerSentence);
			}
		}
		logger.info(String.format("%d result CASes read", resultCases.size()));
		return resultCases;
	}

	public void execute() {
		
		RerankingDataGen rerankingDataGen = instantiateRerankingDataGen(outputDir);
		
		//question-to-question
		
		
		
		//reducing the time for reading from the hard drive
		long globalStart = System.currentTimeMillis();
		
		logger.info("Started reading the cached data");
		Map<String,PlainDocument> questionCases = preinitQuestionCache();
		Map<String,PlainDocument> answerCases = preinitAnswerCache();
		
		
		
		logger.info("Finished reading the cached data");
		logger.info(String.format("Read the CASes in %d ms", System.currentTimeMillis()-globalStart));
		
		globalStart = System.currentTimeMillis();
		logger.info("Processing result-question pairs");
		
		
		
		
		Pair<List<String>, List<String>> ids = getQuestionAndAnswerIdS();
		List<String> qids = ids.getA();
		List<String> aids = ids.getB();
		
		List<String> all_ids = new ArrayList<String>();
		all_ids.addAll(qids);
		all_ids.addAll(aids);
		
		
		//for here on the things get parallelizable
		if (threads > 1)
			executeParallel(rerankingDataGen, questionCases, answerCases,  all_ids, threads);
		else
			execute(rerankingDataGen, questionCases, answerCases,  all_ids);
		
		
		finalize();

		rerankingDataGen.cleanUp();
	}



	protected void execute(RerankingDataGen rerankingDataGen, Map<String, PlainDocument> questionCases,
			Map<String, PlainDocument> answerCases,  List<String> all_ids) {
		long globalStart = System.currentTimeMillis();
		int count_time = 0;
		int z = -1;
		int total = all_ids.size()*all_ids.size()/2;
		logger.info(String.format("Total of %d pairs to be processed: ", total));
		int prev_count_time=0;
		for (int i  = 0;  i<all_ids.size(); i++) {
			
			List<NoUIMACandidate> candidates = new ArrayList<>();
			PlainDocument doc_i = questionCases.containsKey(all_ids.get(i)) ? questionCases.get(all_ids.get(i)) : answerCases.get(all_ids.get(i));
			for (int j = i+1; j < all_ids.size(); j++) {
				PlainDocument doc_j = questionCases.containsKey(all_ids.get(j)) ? questionCases.get(all_ids.get(j)) : answerCases.get(all_ids.get(j));
				z++;
				count_time++;
				if (!(((totalThreadNumber>1) && (z%totalThreadNumber==thread))||(totalThreadNumber<=1))) {
					continue;
				}
				
				NoUIMACandidate candidate = noUIMAExperiment.generateCandidate(doc_i, doc_j);
				
				if (count_time-prev_count_time > 1000 ) {
					prev_count_time = count_time;
					double speed = Double.valueOf(System.currentTimeMillis()-globalStart)/Double.valueOf(count_time);
					logger.info(String.format("Taking %.5f ms average per pair", speed));
					logger.info(String.format("Total of %d pairs processed, %d remaning, %.2f hours to go with the current speed ", count_time, total-count_time, Double.valueOf(total-count_time)*speed/1000.0/60.0/60.0));
					
				}
				candidates.add(candidate);
			}
			((KernelMatrixDataGen) rerankingDataGen).handleNoUIMAData(candidates);
		
		}
		long globalEnd = System.currentTimeMillis();
		logger.info(String.format("Took %.5f ms average per pair", Double.valueOf(globalEnd-globalStart)/Double.valueOf(count_time)));
	}
	
	

	public List<NoUIMACandidate> generateCandidates(List<Pair<PlainDocument,PlainDocument>> objects){
		List<NoUIMACandidate> predictions = new ArrayList<NoUIMACandidate>();
		
		int mod = objects.size() % threads;
		int splitSize = (objects.size()+threads-mod)/threads;
		
		List<List<Pair<PlainDocument,PlainDocument>>> subLists = Lists.partition(objects, splitSize);
			
		
		if (subLists.size()!=threads)
			logger.error(String.format("Splitting error: %d subsets out of %d objects for %d threads", subLists.size(), objects.size(), threads));
		
		
		Set<Future<List<NoUIMACandidate>>> set = new HashSet<Future<List<NoUIMACandidate>>>();
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		
		int i = 0;
		for (List<Pair<PlainDocument,PlainDocument>> subList: subLists){
			
			Callable<List<NoUIMACandidate>> exampleClassifier = new ParallelFeatureExtractor(noUIMAExperiments.get(i), subList);
			i++;
			
			Future<List<NoUIMACandidate>> future = pool.submit(exampleClassifier);
			set.add(future);
		
		}
		
		for (Future<List<NoUIMACandidate>> future : set) {
			try {
				
				predictions.addAll(future.get());
				
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		
		pool.shutdown();
		return predictions;
	
	}



	
	protected void executeParallel(RerankingDataGen rerankingDataGen, Map<String, PlainDocument> questionCases,
			Map<String, PlainDocument> answerCases,  List<String> all_ids, int threads) {
		List<Pair<PlainDocument,PlainDocument>> list = new ArrayList<Pair<PlainDocument,PlainDocument>>();
		long globalStart = System.currentTimeMillis();
		int batchSize = 100000;
		
		for (int i  = 0;  i<all_ids.size(); i++) {
			globalStart = System.currentTimeMillis();
			List<NoUIMACandidate> candidates = new ArrayList<>();
			PlainDocument doc_i = questionCases.containsKey(all_ids.get(i)) ? questionCases.get(all_ids.get(i)) : answerCases.get(all_ids.get(i));
			for (int j = i+1; j < all_ids.size(); j++) {
				PlainDocument doc_j = questionCases.containsKey(all_ids.get(j)) ? questionCases.get(all_ids.get(j)) : answerCases.get(all_ids.get(j));
				list.add(new Pair<PlainDocument,PlainDocument>(doc_i, doc_j));
				if (list.size()>=batchSize) {
					candidates.addAll(generateCandidates(list));
					
					list = new ArrayList<Pair<PlainDocument,PlainDocument>>();
					((KernelMatrixDataGen) rerankingDataGen).handleNoUIMAData(candidates);
					long globalEnd = System.currentTimeMillis();
					logger.info(String.format("Took %.5f ms average per pair", Double.valueOf(globalEnd-globalStart)/Double.valueOf(candidates.size())));
					globalStart = System.currentTimeMillis();
				}

			}
			
			
		}
		((KernelMatrixDataGen) rerankingDataGen).handleNoUIMAData(generateCandidates(list));
	}



	protected Pair<List<String>,List<String>> getQuestionAndAnswerIdS() {
		List<String> qids = new ArrayList<String>();
		List<String> aids = new ArrayList<String>();
		
		//removing irrelevant questions/collecting names of relevant answers
		for (int i = 0, n = questions.size(); i < n; i++) {
			Question question = questions.get(i);
			/*if (!(((totalThreadNumber>1) && (i%totalThreadNumber==thread))||(totalThreadNumber<=1))) {
				continue;
			}*/
			int numResultsToKeep = isTrainQuestion(question) ? candidatesToKeepTrain : candidatesToKeep;
			if ((!keepNegatives)) {
				if  (!(containsPositive(answers.getResults(question.getId(), numResultsToKeep), numResultsToKeep))){
					logger.info(String.format("Not processing question %s with no positive answers", question.getId()));
					continue;
				}
			}
			
			qids.add(question.getId());
			
			List<Result> results_i = answers.getResults(question.getId(), numResultsToKeep);
			if ((!keepNegatives) && (!(containsPositive(results_i, numResultsToKeep)))) {
				logger.info(String.format("Skipping question %s with no positive answers", question.getId()));
				continue;
			}
			
			for (Result r : results_i) {
				aids.add(r.documentId);
			}
			
		}
		return new Pair<List<String>,List<String>>(qids,aids);
	}

	public static void main(String[] args) {
		try{
			Args.parse(RamKernelMatrixGenerator.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(RamKernelMatrixGenerator.class);
			
			System.exit(0);
		}
		
		RamKernelMatrixGenerator application = new RamKernelMatrixGenerator();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time ({}): {} (ms)", RamKernelMatrixGenerator.mode, watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}
	}
}
