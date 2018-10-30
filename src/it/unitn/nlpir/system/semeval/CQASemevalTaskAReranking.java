package it.unitn.nlpir.system.semeval;

import com.google.common.base.Stopwatch;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.system.core.RERTextPairConversion;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.system.datagen.RerankingDataGenTest;
import it.unitn.nlpir.system.datagen.RerankingDataGenTrain;

/**
 * <p>
 * Launches a pipeline to prepare the SVM Light files for reranking on <a href=http://alt.qcri.org/semeval2016/>Semeval 2016 data</a>
 * </p>
 * 
 * <p>
 * Add the following information into the cases:
 * <ul>
 * <li> Reads the users' signatures (short texts the users use to represent themselves and which they add at the end of all their messages, and adds information
 * about them directly into the CASes of the original question and answer.
 * <li> Adds the nickname of the text author directly into its respective CAS
 * <li> Enriches the answer CASes with the shallow Pos Chunk trees of the previous and the following answers in the answer thread in the Qatar Living post
 * <li> Marks mentions of the names of the author of the question and the mentions of the names of the users who replied to the question before the answer in consideration
 * </ul>
 * </p>
 * 
 * 
 * <p>
 * This class in the entry point to the following publication:
 * <br> 
 * <b>Tymoshenko, Kateryna, Daniele Bonadiman, and Alessandro Moschitti. 
 * <i>"Learning to Rank Non-Factoid Answers: Comment Selection in Web Forums."</i> Proceedings of the 25th ACM International on Conference on Information and Knowledge Management. ACM, 2016.</b>
 * 
 * </p>
* @author IKernels group
 *
 */

public class CQASemevalTaskAReranking extends CQASemevalTaskA{

	protected RerankingDataGen instantiateRerankingDataGen(String mode, String outputDir) {
		RerankingDataGen rerankingDataGen = null;

		switch (mode) {
		case "train":
			logger.info("Generating data in the train mode");
			rerankingDataGen = new RerankingDataGenTrain(outputDir, verboseResultset);
			break;
		case "test":
			logger.info("Generating data in the test mode");
			rerankingDataGen = new RerankingDataGenTest(outputDir, verboseResultset);
			break;
		default:
			logger.error("No corresponding generation mode found.");
			System.exit(1);
			break;
		}

		return rerankingDataGen;
	}
	
	
	public static void main(String[] args) {
		
		try{
			Args.parse(CQASemevalTaskAReranking.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(CQASemevalTaskAReranking.class);
			
			System.exit(0);
		}
		
		
		RERTextPairConversion application = new CQASemevalTaskAReranking();

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
