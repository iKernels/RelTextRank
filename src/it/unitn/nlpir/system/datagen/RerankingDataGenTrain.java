package it.unitn.nlpir.system.datagen;

import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.WriteFile;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMTKExample;

import com.google.common.base.Joiner;

public class RerankingDataGenTrain implements RerankingDataGen {
	private final Logger logger = LoggerFactory.getLogger(RerankingDataGenTest.class);
	
	public static final int MAX_NUMBER_OF_NODES = 10000;

	private String train_file;
	private String train_file_res;
	private static final String TRAIN_FILE = "svm.train";
	private static final String TRAIN_FILE_RES = "svm.train.res";
	private WriteFile trainFile;
	private WriteFile trainFileRes;
	private boolean verboseResultset;
	protected String trueLabel;
	
	public RerankingDataGenTrain(String outputDir, boolean verboseResultset,
			String trainFile, String trainFileRes) {
		train_file = trainFile;
		train_file_res = trainFileRes;
		this.trainFile = new WriteFile(outputDir, train_file);
		if (verboseResultset) {
			this.verboseResultset = verboseResultset;
			this.trainFileRes = new WriteFile(outputDir, train_file_res);
		}
		this.trueLabel = "true";
		
	}

	public RerankingDataGenTrain(String outputDir) {
		train_file = "svm.train";
		this.trainFile = new WriteFile(outputDir, train_file);
		this.trueLabel = "true";
		System.out.println("TRUE:"+trueLabel);
	}
	
	public RerankingDataGenTrain(String outputDir, boolean verboseResultset) {
		if (train_file==null) train_file = TRAIN_FILE;
		if (train_file_res ==null) train_file_res = TRAIN_FILE_RES;
		this.trainFile = new WriteFile(outputDir, train_file);
		if (verboseResultset) {
			this.verboseResultset = verboseResultset;
			this.trainFileRes = new WriteFile(outputDir, train_file_res);
		}
		this.trueLabel = "true";
		System.out.println("TRUE:"+trueLabel);
	}

	@Override
	public void cleanUp() {
		
		this.trainFile.close();
		if (verboseResultset) {
			this.trainFileRes.close();
		}
	}

	public void handleData(List<Candidate> candidates) {
		List<Candidate> correctDocuments = new ArrayList<>();
		List<Candidate> incorrectDocuments = new ArrayList<>();

		for (Candidate c : candidates) {
			boolean flag = c.result.relevantFlag.equals(trueLabel);
			if (flag) {
				correctDocuments.add(c);
			} else {
				incorrectDocuments.add(c);
			}
		}

		boolean flip = true;
		List<String> res_string = new ArrayList<String>();
		for (Candidate correctDoc : correctDocuments) {
			for (Candidate incorrectDoc : incorrectDocuments) {
				res_string.clear();
				if (verboseResultset)
					res_string.add(correctDoc.getResult().questionId); // qid

				Candidate firstPair, secondPair;
				SVMTKExample builder = new SVMTKExample();
				if (flip) {
					firstPair = correctDoc;
					secondPair = incorrectDoc;
					builder.positive();
					if (verboseResultset) {
						// Positive res_string
						res_string.add(correctDoc.getResult().documentId);
						res_string.add(incorrectDoc.getResult().documentId);
						res_string.add(correctDoc.getResult().rankingPosition);
						res_string.add(incorrectDoc.getResult().rankingPosition);
						res_string.add("+1");
					}
				} else {
					firstPair = incorrectDoc;
					secondPair = correctDoc;
					builder.negative();
					if (verboseResultset) {
						// Negative res_string
						res_string.add(incorrectDoc.getResult().documentId);
						res_string.add(correctDoc.getResult().documentId);
						res_string.add(incorrectDoc.getResult().rankingPosition);
						res_string.add(correctDoc.getResult().rankingPosition);
						res_string.add("-1");
					}
				}

				generateExampleTreeAndVector(firstPair, secondPair, builder);

				String example = builder.build();

				int numberOfNodes = Math.max(
						Math.max(TreeUtil.numberOfNodes(firstPair.getQa().getB()),
						TreeUtil.numberOfNodes(secondPair.getQa().getB())),
						Math.max(TreeUtil.numberOfNodes(firstPair.getQa().getA()),
								TreeUtil.numberOfNodes(secondPair.getQa().getA()))
						);
						

				if (numberOfNodes >= MAX_NUMBER_OF_NODES) {
					logger.warn("Skipping large example:  with {} nodes > max limit of {} nodes", numberOfNodes, MAX_NUMBER_OF_NODES);
					continue;
				}

				this.trainFile.writeLn(example);
				if (verboseResultset) {
					this.trainFileRes.writeLn(Joiner.on(" ").join(res_string));
				}
				flip = !flip;
			}
		}
	}

	protected void generateExampleTreeAndVector(Candidate firstPair, Candidate secondPair, SVMTKExample builder) {
			builder.addTree(firstPair.getQa().getA())
				.addTree(firstPair.getQa().getB())
				.addTree(secondPair.getQa().getA())
				.addTree(secondPair.getQa().getB())
				.addVector(firstPair.getFeatures())
				.addVector(secondPair.getFeatures());
	}

}
