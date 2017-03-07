package it.unitn.nlpir.system.datagen;

import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.WriteFile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMLightTK;
import svmlighttk.SVMTKExample;
import svmlighttk.SVMVector;

public class RerankingDataGenScoresPrediction 
	implements RerankingDataGen {
	private final Logger logger = LoggerFactory
			.getLogger(RerankingDataGenScoresPrediction.class);
	
	
	private WriteFile predFile;

	private SVMLightTK classifier;
	

	

	public RerankingDataGenScoresPrediction(String modelFile, String outputDir, String outputFile) {
		classifier = new SVMLightTK(modelFile);
		predFile = new WriteFile(outputDir, outputFile);
	}

	
	
	@Override
	public void handleData(List<Candidate> candidates) {
		for (Candidate c : candidates) {
			Pair<String, String> qa = c.getQa();
			SVMVector pairVectorFeatures = c.getFeatures();
			String documentTree = qa.getB();
	
			int numberOfNodes = TreeUtil.numberOfNodes(documentTree);
	
			if(numberOfNodes >= RerankingDataGenTrain.MAX_NUMBER_OF_NODES) {
				logger.warn("Skipping example with (tree has more than {} nodes): {}",
						RerankingDataGenTrain.MAX_NUMBER_OF_NODES, documentTree);
				continue;
			}
	
			String example = generateExample(qa, pairVectorFeatures,
					documentTree);
			Double score = classifier.classify(example);
			this.predFile.writeLn(score.toString());
		}		
		
	}



	protected String generateExample(Pair<String, String> qa,
			SVMVector pairVectorFeatures, String documentTree) {
		String example = new SVMTKExample().positive()
				.addTree(qa.getA())
				.addTree(documentTree)
				.addTree("")
				.addTree("")
				.addVector(pairVectorFeatures)
				.addVector(new SVMVector())
				.build();
		return example;
	}

	@Override
	public void cleanUp() {

		this.predFile.close();
	}

}