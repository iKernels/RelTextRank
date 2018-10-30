package it.unitn.nlpir.system.core.precomputed;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainDocument;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.system.datagen.kernmat.KernelMatrixDataGen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Stopwatch;



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
 * </p>
* @author IKernels group
 *
 */
public class BaselineRamKernelMatrixGenerator extends RamKernelMatrixGenerator {

	
	//this is not optimal, but I need a quick bpathc
	protected Map<String,Set<String>> retrieveQuestionAnswerMappings() {
		
		Map<String,Set<String>> mappings = new HashMap<String,Set<String>>();
		
		for (int i = 0, n = questions.size(); i < n; i++) {
			
			Question question = questions.get(i);
			int numResultsToKeep = isTrainQuestion(question) ? candidatesToKeepTrain : candidatesToKeep;
			List<Result> results_i = answers.getResults(question.getId(), numResultsToKeep);
			if (results_i == null) {
				logger.warn(String.format("No results found for question %s", question.getId()));
				continue;
			}
				
			if ((!keepNegatives) && (!(containsPositive(results_i, numResultsToKeep)))) {
				logger.info(String.format("Skipping question %s with no positive answers", question.getId()));
				continue;
			}
			for (int j = 0, nj = results_i.size(); j < nj; j++) {
				Result result = results_i.get(j);
				if (!mappings.containsKey(question.getId()))
					mappings.put(question.getId(), new HashSet<String>());
				mappings.get(question.getId()).add(result.documentId);
			}
		}
		return mappings;
		
	}
	


	public void execute() {
		
		RerankingDataGen rerankingDataGen = instantiateRerankingDataGen(outputDir);
		
		//question-to-question
		
		
		
		//reducing the time for reading from the hard drive
		long globalStart = System.currentTimeMillis();
		
		logger.info("Started reading the cached data");
		Map<String,PlainDocument> questionCases = preinitQuestionCache();
		
		Map<String,PlainDocument> answerCases = preinitAnswerCache();
		
		Map<String, Set<String>> q2a = retrieveQuestionAnswerMappings();
		
		
		logger.info("Finished reading the cached data");
		logger.info(String.format("Read the CASes in %d ms", System.currentTimeMillis()-globalStart));
		
		globalStart = System.currentTimeMillis();
		logger.info("Processing result-question pairs");
		
		
		
		
		
		
		
		//for here on the things get parallelizable
		execute(rerankingDataGen, questionCases, answerCases,  q2a);
		
		
		finalize();

		rerankingDataGen.cleanUp();
	}



	protected void execute(RerankingDataGen rerankingDataGen, Map<String, PlainDocument> questionCases,
			Map<String, PlainDocument> answerCases,  Map<String,Set<String>> q2a) {
		long globalStart = System.currentTimeMillis();
		int count_time = 0;
		int z = -1;
		
		int total = 0;
		
		for (String qid: q2a.keySet()) {
			total = total + q2a.get(qid).size();
		}
		logger.info(String.format("Total of %d questions to be processed: ", total));
		int prev_count_time=0;
		
		for (String qid : q2a.keySet()) {
			List<NoUIMACandidate> candidates = new ArrayList<NoUIMACandidate>();
			PlainDocument doc_i = questionCases.get(qid);
			for (String aid : q2a.get(qid)) {
				PlainDocument doc_j = answerCases.get(aid);
				z++;
				count_time++;
				if (!(((totalThreadNumber>1) && (z%totalThreadNumber==thread))||(totalThreadNumber<=1))) {
					continue;
				}
				
				NoUIMACandidate candidate = noUIMAExperiment.generateCandidate(doc_i, doc_j);
				
				if (count_time-prev_count_time > 100000 ) {
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
	
	

	public static void main(String[] args) {
		try{
			Args.parse(BaselineRamKernelMatrixGenerator.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(BaselineRamKernelMatrixGenerator.class);
			
			System.exit(0);
		}
		
		BaselineRamKernelMatrixGenerator application = new BaselineRamKernelMatrixGenerator();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time ({}): {} (ms)", BaselineRamKernelMatrixGenerator.mode, watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}
	}
}
