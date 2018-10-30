package it.unitn.nlpir.system.core.precomputed;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.uima.UIMAUtil;

import java.util.ArrayList;
import java.util.List;

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
public class KernelMatrixGeneratorDiagonalOnly extends KernelMatrixGenerator {

	
	

	public KernelMatrixGeneratorDiagonalOnly(){
		super();

	}

	public void execute() {
		
		RerankingDataGen rerankingDataGen = instantiateRerankingDataGen(outputDir);
		
		//question-to-question
		List<Result> results = new ArrayList<Result>();
		int limit=Integer.MAX_VALUE;
		
		for (int i = 0, n = Math.min(limit,questions.size()); i < n; i++) {
			Question question_i = questions.get(i);
			
			String id = question_i.getId();
			logger.info(String.format("Processing question: %s (%s of %s)", id, i + 1, n));
			UIMAUtil.setupCas(questionCas,id, question_i.getText());
			analyze(questionCas);
			
			int numResultsToKeep = isTrainQuestion(question_i) ? candidatesToKeepTrain : candidatesToKeep;
			List<Result> results_i = answers.getResults(id, numResultsToKeep);
			if (results_i!=null)
					results.addAll(results_i);
			
			List<Candidate> candidates = new ArrayList<>();
			for (int j = i, nj = i+1; j < nj; j++) {
				Question question_j = questions.get(j);
				UIMAUtil.setupCas(documentCas, question_j.getId(), question_j.getText());
				analyze(documentCas);
				Result r = new Result(id, question_j.getId(), "0.0", "0.0", "false", "");
				logger.info(String.format("Processing %s-%s",question_i.getId(),question_j.getId()));
				candidates.add(experiment.generateCandidate(questionCas, documentCas, r));
			}
			rerankingDataGen.handleData(candidates);
		
		}
		logger.info(String.format("%d results total read", results.size()));
		//answer-to-answer
		for (int i = 0, n = Math.min(limit,results.size()); i < n; i++) {
			Result r_i = results.get(i);
			String r_i_id = r_i.documentId;
			
			UIMAUtil.setupCas(questionCas, r_i_id, r_i.documentText);
			analyze(questionCas);

			List<Candidate> candidates = new ArrayList<>();
			for (int j = i, nj = i+1; j < nj; j++) {
				Result r_j = results.get(j);	
				String r_j_id = r_j.documentId;
				UIMAUtil.setupCas(documentCas, r_j_id, r_j.documentText);
				analyze(documentCas);
				Result r = new Result(r_i_id, r_j_id, "0.0", "0.0", "false", "");
				logger.info(String.format("Processing %s-%s",r_i_id,r_j_id));
				candidates.add(experiment.generateCandidate(questionCas, documentCas, r));
			}
			rerankingDataGen.handleData(candidates);
		}
		

		
		finalize();

		// Close resources used by the generation logic
		rerankingDataGen.cleanUp();
	}

	public static void main(String[] args) {
		try{
			Args.parse(KernelMatrixGeneratorDiagonalOnly.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(KernelMatrixGeneratorDiagonalOnly.class);
			
			System.exit(0);
		}
		
		KernelMatrixGeneratorDiagonalOnly application = new KernelMatrixGeneratorDiagonalOnly();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time ({}): {} (ms)", KernelMatrixGeneratorDiagonalOnly.mode, watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}
	}
}
