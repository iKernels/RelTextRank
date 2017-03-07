package it.unitn.nlpir.system.datagen;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.WriteFile;
import svmlighttk.SVMTKExample;
import svmlighttk.SVMVector;

public class RerankingDataGenTest implements RerankingDataGen {
	private final Logger logger = LoggerFactory.getLogger(RerankingDataGenTest.class);
	
	private String test_file = "svm.test";
	private String relevancy_file = "svm.relevancy";

	private WriteFile testFile;
	private WriteFile relevancyFile;
	private boolean verboseResultset = false;
	protected String trueLabel = "true";
	public static String TRUE_LABEL = "true";
	public static String FALSE_LABEL = "false";
	public RerankingDataGenTest(String outputDir) {
		test_file = "svm.test";
		relevancy_file = "svm.relevancy";
		this.testFile = new WriteFile(outputDir, test_file);
		this.relevancyFile = new WriteFile(outputDir, relevancy_file);
		this.trueLabel = "true";
	}

	public RerankingDataGenTest(String outputDir, boolean verboseResultset) {
		this(outputDir);
		this.verboseResultset = verboseResultset;
		this.trueLabel = "true";
	}
	
	public RerankingDataGenTest(String outputDir, boolean verboseResultset,
			String testFile, String relevancyFile) {
		test_file = testFile;
		relevancy_file = relevancyFile;
		this.testFile = new WriteFile(outputDir, test_file);
		this.relevancyFile = new WriteFile(outputDir, relevancy_file);
		this.verboseResultset = verboseResultset;
		this.trueLabel = "true";
	}

	@Override
	public void handleData(List<Candidate> candidates) {
		for (Candidate c : candidates) {
			Pair<String, String> qa = c.getQa();
			Result result = c.getResult();
			SVMVector pairVectorFeatures = c.getFeatures();
		
			String documentTree = qa.getB();
	
			int numberOfNodes = Math.max(TreeUtil.numberOfNodes(qa.getA()),TreeUtil.numberOfNodes(documentTree));
	
			if(numberOfNodes >= RerankingDataGenTrain.MAX_NUMBER_OF_NODES) {
				
				//truncate the result
				
				logger.warn("Skipping example with (tree has more than {} nodes): {}", RerankingDataGenTrain.MAX_NUMBER_OF_NODES, documentTree);
				continue;
			}
			
			boolean label = c.result.relevantFlag.equals(this.trueLabel);
			

			String example = generateExampleTreeAndVector(qa, pairVectorFeatures, documentTree,
					label);
	
			this.testFile.writeLn(example);
			
			String relevantFlag = label ? TRUE_LABEL : FALSE_LABEL;
			String relLine;
			if (verboseResultset) {
				relLine = result.questionId + 
						" " + result.documentId +
						" " + result.rankingPosition +
						" " + result.rankingScore +					
						//" " + result.relevantFlag +
						" " + relevantFlag +
						" " + result.documentText;
			} else {
				relLine = result.questionId + " " + result.documentId + 
						" " + result.relevantFlag;
			}
	
			this.relevancyFile.writeLn(relLine);
		}
	}

	protected String generateExampleTreeAndVector(Pair<String, String> qa,
			SVMVector pairVectorFeatures, String documentTree, boolean label) {
		return new SVMTKExample()
				.setLabel(label)
				.addTree(qa.getA())
				.addTree(documentTree)
				.addTree("")
				.addTree("")
				.addVector(pairVectorFeatures)
				.addVector(new SVMVector())
				.build();
	}

	@Override
	public void cleanUp() {
		this.testFile.close();
		this.relevancyFile.close();
	}

}
