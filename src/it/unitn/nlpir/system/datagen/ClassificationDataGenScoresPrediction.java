package it.unitn.nlpir.system.datagen;

import it.unitn.nlpir.util.Pair;
import svmlighttk.SVMTKExample;
import svmlighttk.SVMVector;

public class ClassificationDataGenScoresPrediction extends RerankingDataGenScoresPrediction
	implements RerankingDataGen {
	
	public ClassificationDataGenScoresPrediction(String modelFile, String outputDir, String outputFile) {
		super(modelFile, outputDir, outputFile);
	}

	protected String generateExample(Pair<String, String> qa,
			SVMVector pairVectorFeatures, String documentTree) {
		String example = new SVMTKExample().positive()
				.addTree(qa.getA())
				.addTree(documentTree)
				.addVector(pairVectorFeatures)
				.build();
		return example;
	}

	

}