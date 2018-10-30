package it.unitn.nlpir.system.nonstruct;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.core.ClassTextPairConversion;
import it.unitn.nlpir.system.datagen.ClassificationFVDataGen;
import it.unitn.nlpir.system.datagen.RerankingDataGen;

import java.util.List;

import com.google.common.base.Stopwatch;


/**
 * This class assumes that  train, dev and test ids start with "R", "D", and "T", respectively
 * @author kateryna
 *
 */
public class NonStructClassTextPairConversion extends ClassTextPairConversion{
	
	//allow overwriting new annotations in the dynamic CASes in RAM (serialization in XMIs is controlled by doNotStoreNew)
	@Argument(description = "When generating training data do not skip questions with now correct answers", required = false)
	protected static boolean keepAllNegatives = false;
	
	@Argument(description = "Number of the train answer candidates per question to use for generating examples", required=false)
	protected static Integer trainCandidatesToKeep = 15;

	
	protected RerankingDataGen instantiateRerankingDataGen(String outputDir) {
		return new ClassificationFVDataGen(outputDir);
	}
	

	protected boolean isTrain(String id) {
		return mode.equals("train");
	}

	protected List<Result> getResults(String id) {
		return (isTrain(id)) ? answers.getResults(id, trainCandidatesToKeep) : answers.getResults(id, candidatesToKeep);
	}

	public static void main(String[] args) {



		try{
			Args.parse(NonStructClassTextPairConversion.class, args);
		}
		catch (Exception e){
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(NonStructClassTextPairConversion.class);
			
			System.exit(0);
		}


		NonStructClassTextPairConversion application = new NonStructClassTextPairConversion();
		
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
