package it.unitn.nlpir.system.core;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.system.datagen.ClassifierDataGen;
import it.unitn.nlpir.system.datagen.RerankingDataGen;



import java.util.HashMap;
import java.util.List;

import org.apache.uima.jcas.JCas;

import com.google.common.base.Stopwatch;



public class ClassCVTextPairConversion extends CVRERTextPairConversion {
	
	@Argument(description = "When generating training data do not skip questions with now correct answers", required = false)
	protected static boolean keepAllNegatives = false;

	protected void writeExamples(HashMap<Question, List<Candidate>> qid2candidates,
			List<Question> questions, String mode, String outputDir) {
		logger.info(String.format("Experiment mode: %s; KeepAllNegatives: %s",mode, keepAllNegatives));
		RerankingDataGen rerankingDataGenTest = new ClassifierDataGen(outputDir, mode,verboseResultset, !keepAllNegatives);
		for (Question q : questions) {
			rerankingDataGenTest.handleData(qid2candidates.get(q));
		}
		rerankingDataGenTest.cleanUp();
	}


	protected void additionalQARelatedProcessing(JCas questionCas, JCas documentCas, int qNum){

	}

	public static void main(String[] args) {


		try{
			Args.parse(ClassCVTextPairConversion.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(ClassCVTextPairConversion.class);
			
			System.exit(0);
		}
		

		ClassCVTextPairConversion application = new ClassCVTextPairConversion();
		
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
