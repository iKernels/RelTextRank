package it.unitn.nlpir.system.core;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.system.datagen.ClassifierDataGen;
import it.unitn.nlpir.system.datagen.RerankingDataGen;

import org.apache.uima.jcas.JCas;

import com.google.common.base.Stopwatch;



public class ClassTextPairConversion extends RERTextPairConversion{
	
	//allow overwriting new annotations in the dynamic CASes in RAM (serialization in XMIs is controlled by doNotStoreNew)
	@Argument(description = "When generating training data do not skip questions with now correct answers", required = false)
	protected static boolean keepAllNegatives = false;
	
	protected RerankingDataGen instantiateRerankingDataGen(String mode, String outputDir) {
		RerankingDataGen rerankingDataGen = null;

		logger.info(String.format("Generating data in the %s mode", mode));
		if (mode.equals("train"))
			logger.info(String.format("Keep all negatives: %s",String.valueOf(keepAllNegatives)));
		rerankingDataGen = new ClassifierDataGen(outputDir, mode, verboseResultset, !keepAllNegatives);
		

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
