package it.unitn.nlpir.system.core;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.system.datagen.ClassifierDataGen;
import it.unitn.nlpir.system.datagen.RerankingDataGen;

import org.apache.uima.jcas.JCas;

import com.google.common.base.Stopwatch;



public class ClassTextPairConversion extends RERTextPairConversion{
	

	
	protected RerankingDataGen instantiateRerankingDataGen(String mode, String outputDir) {
		RerankingDataGen rerankingDataGen = null;

		logger.info("Generating data in the train mode");
		rerankingDataGen = new ClassifierDataGen(outputDir, mode, verboseResultset);
		

		return rerankingDataGen;
	}
	
	



	protected void additionalQARelatedProcessing(JCas questionCas, JCas documentCas, int qNum){

	}

	public static void main(String[] args) {



		try{
			Args.parse(ClassTextPairConversion.class, args);
		}
		catch (Exception e){
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(ClassTextPairConversion.class);
			
			System.exit(0);
		}


		ClassTextPairConversion application = new ClassTextPairConversion();
		
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
