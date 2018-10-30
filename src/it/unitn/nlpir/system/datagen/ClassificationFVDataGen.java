package it.unitn.nlpir.system.datagen;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.datagen.kernmat.KernelMatrixDataGen;
import it.unitn.nlpir.util.WriteFile;
import it.unitn.nlpir.util.ZipWriteFile;
import svmlighttk.SVMVector;

public class ClassificationFVDataGen implements RerankingDataGen{
	private final Logger logger = LoggerFactory.getLogger(KernelMatrixDataGen.class);

	
	protected WriteFile svmFile;
	protected WriteFile labelsFile;
	
	
	
	public ClassificationFVDataGen(String outputDir) {

		this.svmFile = new ZipWriteFile(outputDir, "similarities.csv");
		this.labelsFile = new ZipWriteFile(outputDir, "labels.csv");
	}

	@Override
	public void cleanUp() {
		this.svmFile.close();
		this.labelsFile.close();

	}
	




	public void handleData(List<Candidate> candidates) {
		
		
		
		if (candidates==null){
			logger.warn("Null candidate list");
			return;
		}
		for (Candidate c : candidates) {
			
			generateAndWriteExample(c);
		}
	}
	
	protected void generateAndWriteExample(Candidate c){
		Result result = c.getResult();
		SVMVector pairVectorFeatures = c.getFeatures();
		String label = c.getResult().relevantFlag.equals(Result.TRUE) ? "+1" : "-1";
		String output = String.format("%s %s",label, pairVectorFeatures.toString());
		String labelOutput = String.format("%s\t%s", result.questionId,result.documentId);
		
		this.labelsFile.writeLn(labelOutput);
		this.svmFile.writeLn(output);

	}
}
