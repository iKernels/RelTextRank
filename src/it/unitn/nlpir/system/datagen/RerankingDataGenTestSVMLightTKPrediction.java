package it.unitn.nlpir.system.datagen;

import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.reranker.Reranker;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.WriteFile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMTKExample;
import svmlighttk.SVMVector;

public class RerankingDataGenTestSVMLightTKPrediction 
	implements RerankingDataGen {
	private final Logger logger = LoggerFactory
			.getLogger(RerankingDataGenTestSVMLightTKPrediction.class);
	
	private static final String TEST_FILE = "svm.test";
	private static final String RELEVANCY_FILE = "svm.relevancy";
	private static final String PRED_FILE = "svm.lib.pred";

	private WriteFile testFile;
	private WriteFile relevancyFile;
	private WriteFile predFile;
	private boolean verboseResultset = false;

	private Reranker reranker;

	public RerankingDataGenTestSVMLightTKPrediction(String outputDir, String modelFile) {
		this.testFile = new WriteFile(outputDir, TEST_FILE);
		this.relevancyFile = new WriteFile(outputDir, RELEVANCY_FILE);
		this.predFile = new WriteFile(outputDir, PRED_FILE);
		this.reranker = new Reranker(modelFile);
	}

	public RerankingDataGenTestSVMLightTKPrediction(String outputDir, String modelFile,
			boolean verboseResultset) {
		this(outputDir, modelFile);
		this.verboseResultset = verboseResultset;
	}

	@Override
	public void handleData(List<Candidate> candidates) {
		
		this.reranker.clear();
		
		for (Candidate c : candidates) {
			Pair<String, String> qa = c.getQa();
			Result result = c.getResult();
			SVMVector pairVectorFeatures = c.getFeatures();
		
			String documentTree = qa.getB();
	
			int numberOfNodes = TreeUtil.numberOfNodes(documentTree);
	
			if(numberOfNodes >= RerankingDataGenTrain.MAX_NUMBER_OF_NODES) {
				logger.warn("Skipping example with (tree has more than {} nodes): {}",
						RerankingDataGenTrain.MAX_NUMBER_OF_NODES, documentTree);
				continue;
			}
	
			String example = new SVMTKExample().positive()
					.addTree(qa.getA())
					.addTree(documentTree)
					.addTree("")
					.addTree("")
					.addVector(pairVectorFeatures)
					.addVector(new SVMVector())
					.build();
	
			this.testFile.writeLn(example);
			
			String relLine;
			if (verboseResultset) {
				relLine = result.questionId + 
						" " + result.documentId +
						" " + result.rankingPosition +
						" " + result.rankingScore +					
						" " + result.relevantFlag + 
						" " + result.documentText;
			} else {
				relLine = result.questionId + " " + result.documentId + 
						" " + result.relevantFlag;
			}
	
			this.relevancyFile.writeLn(relLine);
			
			Double score = this.reranker.rankExample(example);
			this.predFile.writeLn(score.toString());
		}		
		
	}

	@Override
	public void cleanUp() {
		this.testFile.close();
		this.relevancyFile.close();
		this.predFile.close();
	}

}