package it.unitn.nlpir.system.classification.semeval;

import com.google.common.base.Stopwatch;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.system.core.RERTextPairConversion;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.system.datagen.SemevalClassifierDataGen;



/**
 * <p>
 * General-purpose pipeline to prepare the SVM Light files for classification on <a href=http://alt.qcri.org/semeval2016/>Semeval 2016 data</a>
 * </p>
 * 
* @author IKernels group
 *
 */
public class QASemevalClassification extends RERTextPairConversion {
	protected RerankingDataGen instantiateRerankingDataGen(String mode, String outputDir) {
		RerankingDataGen rerankingDataGen = null;

		logger.info("Generating data in the train mode");
		rerankingDataGen = new SemevalClassifierDataGen(outputDir, mode, verboseResultset);

		return rerankingDataGen;
	}
	

	public static void main(String[] args) {

		
		

		Args.parse(RERTextPairConversion.class, args);
	
		RERTextPairConversion application = new QASemevalClassification();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time ({}): {} (ms)", RERTextPairConversion.mode, watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}
	}

	
}
