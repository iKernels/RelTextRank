package it.unitn.nlpir.system.core.precomputed;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.experiment.kernmat.NoUIMAExperiment;
import it.unitn.nlpir.experiment.kernmat.NoUIMAFeatureOnlyExperiment;
import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainDocument;
import it.unitn.nlpir.system.core.precomputed.util.DatasetManager;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.system.datagen.kernmat.KernelGramMatrixInputDataGen;
import it.unitn.nlpir.system.datagen.kernmat.KernelMatrixDataGen;
import it.unitn.nlpir.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 
 * 
 * </p>
* @author IKernels group
 *
 */
public class PreGramRamKernelMatrixGenerator extends RamKernelMatrixGenerator {

	protected static final Logger logger = LoggerFactory.getLogger(PreGramRamKernelMatrixGenerator.class);
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
	
	protected DatasetManager manager;
	
	public PreGramRamKernelMatrixGenerator(){
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
		
		try {
			this.manager = new DatasetManager(answersPath, candidatesToKeepTrain);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	protected RerankingDataGen instantiateRerankingDataGen(String outputDir) {
		return new KernelMatrixDataGen(outputDir);
	}
	

	

	

	public void execute() {

		//reducing the time for reading from the hard drive
		long globalStart = System.currentTimeMillis();
		
		logger.info("Started reading the cached data");
		Map<String,PlainDocument> questionCases = preinitQuestionCache();
		Map<String,PlainDocument> answerCases = preinitAnswerCache();
		
		
		
		logger.info("Finished reading the cached data");
		logger.info(String.format("Read the CASes in %d ms", System.currentTimeMillis()-globalStart));
		
		globalStart = System.currentTimeMillis();
		logger.info("Processing result-question pairs");

		launchDataExtraction(questionCases, answerCases, DatasetManager.TRAIN, KernelGramMatrixInputDataGen.TRAIN_MODE_LABEL, true);
		launchDataExtraction(questionCases, answerCases, DatasetManager.DEV, KernelGramMatrixInputDataGen.DEV_MODE_LABEL, false);
		launchDataExtraction(questionCases, answerCases, DatasetManager.TEST, KernelGramMatrixInputDataGen.TEST_MODE_LABEL, false);

		finalize();
	}



	protected void launchDataExtraction(Map<String, PlainDocument> questionCases,
			Map<String, PlainDocument> answerCases, int mode, String modeLabel, boolean symmetrical) {
		RerankingDataGen rerankingDataGen;
		rerankingDataGen = new KernelGramMatrixInputDataGen(outputDir, GramTypeConstants.QQ_MODE_LABEL, modeLabel);
		execute(rerankingDataGen, questionCases, answerCases, manager.getOrderedIdsList(mode, GramTypeConstants.QQ_MODE_LABEL), symmetrical);
		rerankingDataGen.cleanUp();
		
		//compute similarities for train-train answer-answer gram matrix
		rerankingDataGen = new KernelGramMatrixInputDataGen(outputDir, GramTypeConstants.AA_MODE_LABEL, modeLabel);
		execute(rerankingDataGen, questionCases, answerCases, manager.getOrderedIdsList(mode, GramTypeConstants.AA_MODE_LABEL), symmetrical);
		rerankingDataGen.cleanUp();
		
		//compute features for train-train question-answer gram matrix (this is the baseline)
		rerankingDataGen = new KernelGramMatrixInputDataGen(outputDir, GramTypeConstants.QA_MODE_LABEL, modeLabel);
		executeNonCartesian(rerankingDataGen, questionCases, answerCases, manager.getOrderedIdsList(mode, GramTypeConstants.QA_MODE_LABEL));
		rerankingDataGen.cleanUp();
	}

	protected void executeNonCartesian(RerankingDataGen rerankingDataGen, Map<String, PlainDocument> questionCases,
			Map<String, PlainDocument> answerCases,  Pair<List<String>,List<String>> all_ids) {
		long globalStart = System.currentTimeMillis();
		int count_time = 0;
		int z = -1;
		
		
		List<String> ids1 = all_ids.getA();
		List<String> ids2 = all_ids.getB();
		
		int total = ids1.size();

		logger.info(String.format("Total of %d pairs to be processed: ", total));
		int prev_count_time=0;
		
		List<NoUIMACandidate> candidates = new ArrayList<>();
		for (int i  = 0;  i<ids1.size(); i++) {	
			
			PlainDocument doc_i = questionCases.containsKey(ids1.get(i)) ? questionCases.get(ids1.get(i)) : answerCases.get(ids1.get(i));
			PlainDocument doc_j = questionCases.containsKey(ids2.get(i)) ? questionCases.get(ids2.get(i)) : answerCases.get(ids2.get(i));
			
			z++;
			count_time++;
			if (!(((totalThreadNumber>1) && (z%totalThreadNumber==thread))||(totalThreadNumber<=1))) {
				continue;
			}
				
			NoUIMACandidate candidate = noUIMAExperiment.generateCandidate(doc_i, doc_j);
			candidate.setRow(i);
			candidate.setCol(i);
			if (count_time-prev_count_time > 100000 ) {
				prev_count_time = count_time;
				double speed = Double.valueOf(System.currentTimeMillis()-globalStart)/Double.valueOf(count_time);
				logger.info(String.format("Taking %.5f ms average per pair", speed));
				logger.info(String.format("Total of %d pairs processed, %d remaning, %.2f hours to go with the current speed ", count_time, total-count_time, Double.valueOf(total-count_time)*speed/1000.0/60.0/60.0));
					
			}
			candidates.add(candidate);

		}
		((KernelGramMatrixInputDataGen) rerankingDataGen).handleNoUIMAData(candidates);
		long globalEnd = System.currentTimeMillis();
		logger.info(String.format("Took %.5f ms average per pair", Double.valueOf(globalEnd-globalStart)/Double.valueOf(count_time)));
	}

	protected void execute(RerankingDataGen rerankingDataGen, Map<String, PlainDocument> questionCases,
			Map<String, PlainDocument> answerCases,  Pair<List<String>,List<String>> all_ids, boolean symmetrical) {
		long globalStart = System.currentTimeMillis();
		int count_time = 0;
		int z = -1;
		
		
		List<String> ids1 = all_ids.getA();
		List<String> ids2 = symmetrical ? ids1 : all_ids.getB();
		
		int total = symmetrical ? ids1.size()*ids1.size()/2 : ids1.size()*ids2.size();

		logger.info(String.format("Total of %d pairs to be processed: ", total));
		int prev_count_time=0;
		
		LRUMap cache = new LRUMap(50000);
		for (int i  = 0;  i<ids1.size(); i++) {	
			List<NoUIMACandidate> candidates = new ArrayList<>();
			PlainDocument doc_i = questionCases.containsKey(ids1.get(i)) ? questionCases.get(ids1.get(i)) : answerCases.get(ids1.get(i));
			int start_j = symmetrical ? i : 0;
			
			for (int j = start_j; j < ids2.size(); j++) {
				
				
				PlainDocument doc_j = questionCases.containsKey(ids2.get(j)) ? questionCases.get(ids2.get(j)) : answerCases.get(ids2.get(j));
				z++;
				count_time++;
				if (!(((totalThreadNumber>1) && (z%totalThreadNumber==thread))||(totalThreadNumber<=1))) {
					continue;
				}
				
				NoUIMACandidate candidate = noUIMAExperiment.generateCandidate(doc_i, doc_j);
				candidate.setRow(i);
				candidate.setCol(j);
				if (count_time-prev_count_time > 100000 ) {
					prev_count_time = count_time;
					double speed = Double.valueOf(System.currentTimeMillis()-globalStart)/Double.valueOf(count_time);
					logger.info(String.format("Taking %.5f ms average per pair", speed));
					logger.info(String.format("Total of %d pairs processed, %d remaning, %.2f hours to go with the current speed ", count_time, total-count_time, Double.valueOf(total-count_time)*speed/1000.0/60.0/60.0));
					
				}
				candidates.add(candidate);
				
				if ((symmetrical) && (i!=j)) {
					NoUIMACandidate mirrorCandidate = candidate.clone();
					mirrorCandidate.setRow(j);
					mirrorCandidate.setCol(i);
					candidates.add(mirrorCandidate);
				}
					
				
				
			}
			((KernelGramMatrixInputDataGen) rerankingDataGen).handleNoUIMAData(candidates);
		
		}
		long globalEnd = System.currentTimeMillis();
		logger.info(String.format("Took %.5f ms average per pair", Double.valueOf(globalEnd-globalStart)/Double.valueOf(count_time)));
	}
	
	


	public static void main(String[] args) {
		try{
			Args.parse(PreGramRamKernelMatrixGenerator.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(PreGramRamKernelMatrixGenerator.class);
			
			System.exit(0);
		}
		
		PreGramRamKernelMatrixGenerator application = new PreGramRamKernelMatrixGenerator();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time ({}): {} (ms)", PreGramRamKernelMatrixGenerator.mode, watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}
	}
}
