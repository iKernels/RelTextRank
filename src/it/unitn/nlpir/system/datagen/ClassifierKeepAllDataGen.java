package it.unitn.nlpir.system.datagen;

import it.unitn.nlpir.resultsets.Candidate;
import java.util.List;

public class ClassifierKeepAllDataGen extends ClassifierDataGen implements RerankingDataGen {

	public ClassifierKeepAllDataGen(String outputDir) {
		super(outputDir);
	}

	public ClassifierKeepAllDataGen(String outputDir, String mode) {
		super(outputDir, mode);
	}
	
	public ClassifierKeepAllDataGen(String outputDir, String mode, boolean verboseResultset) {
		super(outputDir, mode, verboseResultset);
	}
	
	protected boolean containsCorrectAnswer(List<Candidate> candidates) {
		return true;
	}


}
