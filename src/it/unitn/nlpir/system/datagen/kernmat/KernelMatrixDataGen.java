package it.unitn.nlpir.system.datagen.kernmat;

import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.system.datagen.NoUIMA.NoUIMARerankingDataGen;
import it.unitn.nlpir.util.WriteFile;
import it.unitn.nlpir.util.ZipWriteFile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMVector;

public class KernelMatrixDataGen implements RerankingDataGen, NoUIMARerankingDataGen {
	private final Logger logger = LoggerFactory.getLogger(KernelMatrixDataGen.class);


	protected WriteFile svmFile;
	protected WriteFile labelsFile;
	
	
	
	public KernelMatrixDataGen(String outputDir) {

		this.svmFile = new ZipWriteFile(outputDir, "similarities.csv");
		this.labelsFile = new ZipWriteFile(outputDir, "labels.csv");
	}

	@Override
	public void cleanUp() {
		this.svmFile.close();
		this.labelsFile.close();

	}
	



	public void handleNoUIMAData(List<NoUIMACandidate> candidates) {
		
		
		
		if (candidates==null){
			logger.warn("Null candidate list");
			return;
		}
		for (NoUIMACandidate c : candidates) {
			
			generateAndWriteExample(c);
		}
	}
	
	protected void generateAndWriteExample(NoUIMACandidate c){
		
		SVMVector pairVectorFeatures = c.getFeatureVector();
		String labelOutput = String.format("%s\t%s", c.getPair().getA().getId(),c.getPair().getB().getId());
		String output = String.format("-1 %s", pairVectorFeatures.toString());
		this.labelsFile.writeLn(labelOutput);
		this.svmFile.writeLn(output);

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

		String output = String.format("-1 %s", pairVectorFeatures.toString());
		String labelOutput = String.format("%s\t%s", result.questionId,result.documentId);
		
		this.labelsFile.writeLn(labelOutput);
		this.svmFile.writeLn(output);

	}


}
