package it.unitn.nlpir.system.datagen;

import it.unitn.nlpir.resultsets.Candidate;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SemevalClassifierDataGen extends ClassifierDataGen implements RerankingDataGen {
	private final Logger logger = LoggerFactory.getLogger(SemevalClassifierDataGen.class);
	public static final int MAX_NUMBER_OF_NODES = 500;
	protected boolean containsCorrectAnswer(List<Candidate> candidates) {
		
		return true;
	}
	
	public SemevalClassifierDataGen(String outputDir, String mode, boolean verboseResultset) {
		super(outputDir, mode, verboseResultset);
		logger.debug("Set label to Good");
		this.trueLabel = "Good";
	}


}
