package it.unitn.nlpir.system.datagen;

import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.WriteFile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMTKExample;
import svmlighttk.SVMVector;

public class ClassifierDataGen implements RerankingDataGen {
	private final Logger logger = LoggerFactory.getLogger(ClassifierDataGen.class);

	
	private static final String defaultMode = "train";
	protected String mode;
	
	public static final int MAX_NUMBER_OF_NODES = 10000;

	protected WriteFile svmFile;
	protected WriteFile relevancyFile;
	protected boolean verboseResultset;
	public static String TRUE_LABEL = "true";
	public static String FALSE_LABEL = "false";
	protected String trueLabel = "true";
	protected boolean skipAllNegativesInTrain=true;
	
	public ClassifierDataGen(String outputDir) {
		this(outputDir, defaultMode);
	}

	public ClassifierDataGen(String outputDir, String mode) {
		this(outputDir, mode, true);
	}
	
	
	public ClassifierDataGen(String outputDir, String mode, boolean verboseResultset) {
		this(outputDir, mode, verboseResultset, true);
	}
	
	public ClassifierDataGen(String outputDir, String mode, boolean verboseResultset, boolean skipAllNegativesInTrain) {
		this.mode = mode;
		this.verboseResultset = verboseResultset;
		this.svmFile = new WriteFile(outputDir, "svm." + mode);
		if (this.mode.equals("test"))
			this.relevancyFile = new WriteFile(outputDir, "svm.test.relevancy");
		else if (this.mode.equals("dev"))
			this.relevancyFile = new WriteFile(outputDir, "svm.dev.relevancy");
		else
			this.relevancyFile = new WriteFile(outputDir, "svm." + mode + ".relevancy");
		this.skipAllNegativesInTrain = skipAllNegativesInTrain;
	}

	@Override
	public void cleanUp() {
		this.svmFile.close();
		this.relevancyFile.close();
	}
	
	protected boolean containsCorrectAnswer(List<Candidate> candidates) {
		for (Candidate c : candidates) {
			boolean flag = c.result.relevantFlag.equals(trueLabel);
			if (flag) 
				return true;
		}
		return false;
	}

	public boolean checkNodesNumber(String treeString){
		int numberOfNodes = TreeUtil.numberOfNodes(treeString);
		if (numberOfNodes >= RerankingDataGenTrain.MAX_NUMBER_OF_NODES) {
			logger.warn("Skipping example with (question tree has more than {} nodes): {}",
					RerankingDataGenTrain.MAX_NUMBER_OF_NODES, treeString);
			return false;
			
		}
		return true;
	}
	public void handleData(List<Candidate> candidates) {
		
		// skip examples with no correct answer when generating examples in the training mode
		if (this.mode.equals("train") && !containsCorrectAnswer(candidates) && skipAllNegativesInTrain)
			return;
		
		if (candidates==null){
			logger.warn("Null candidate list");
			return;
		}
		for (Candidate c : candidates) {
			Pair<String, String> qa = c.getQa();

			if (qa!=null) {
				String documentTree = qa.getB();
				
				//check if the documentTree size does not exceed the amount
				if (!checkNodesNumber(documentTree))
					continue;
	
				//check if the questionTree size does not exceed the amount
				int numberOfNodes = TreeUtil.numberOfNodes(qa.getA());
				if (numberOfNodes >= RerankingDataGenTrain.MAX_NUMBER_OF_NODES) {
					logger.warn("Skipping example with (question tree has more than {} nodes): {}",
							RerankingDataGenTrain.MAX_NUMBER_OF_NODES, documentTree);
					continue;
				}
			}
			generateAndWriteExample(c);
		}
	}
	
	protected void generateAndWriteExample(Candidate c){
		boolean flag = c.result.relevantFlag.equals(trueLabel);
		Result result = c.getResult();
		SVMVector pairVectorFeatures = c.getFeatures();
		Pair<String, String> qa = c.getQa();
		
		SVMTKExample builder =  getExampleBuilder();
		if (flag) {
			builder.positive();
		} else {
			builder.negative();
		}
		if (qa!=null) {
			String documentTree = qa.getB();
			generateExampleTreeAndVector(qa, pairVectorFeatures, documentTree,
					builder);
		}
		else {
			generateExampleVector(pairVectorFeatures, builder);
		}

		String example = builder.toString();

		this.svmFile.writeLn(example);

		String relLine;
		if (verboseResultset) {
			relLine = result.questionId + " " + result.documentId + " "
					+ result.rankingPosition + " " + result.rankingScore + " "
					+ result.relevantFlag + " " + result.documentText;
		} else {
			relLine = result.questionId + " " + result.documentId + " " + result.relevantFlag;
		}

		this.relevancyFile.writeLn(relLine);
	}

	protected SVMTKExample getExampleBuilder(){
		return new SVMTKExample();
	}
	protected void generateExampleTreeAndVector(Pair<String, String> qa,
			SVMVector pairVectorFeatures, String documentTree,
			SVMTKExample builder) {
		builder.addTree(qa.getA()).addTree(documentTree).addVector(pairVectorFeatures);
	}
	
	protected void generateExampleVector(SVMVector pairVectorFeatures,
			SVMTKExample builder) {
		if (pairVectorFeatures.getFeatures().size()==0) {
			pairVectorFeatures.addFeature(0.0);
		}
		builder.addVector(pairVectorFeatures);
	}


	

}
