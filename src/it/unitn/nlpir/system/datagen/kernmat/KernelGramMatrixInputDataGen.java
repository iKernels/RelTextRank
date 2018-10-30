package it.unitn.nlpir.system.datagen.kernmat;

import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.system.datagen.NoUIMA.NoUIMARerankingDataGen;
import it.unitn.nlpir.util.WriteFile;
import it.unitn.nlpir.util.ZipWriteFile;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMVector;

public class KernelGramMatrixInputDataGen implements RerankingDataGen, NoUIMARerankingDataGen {
	private final Logger logger = LoggerFactory.getLogger(KernelGramMatrixInputDataGen.class);


	protected WriteFile svmFile;
	protected WriteFile labelsFile;
	
	public static final String TRAIN_MODE_LABEL="train";
	public static final String TEST_MODE_LABEL="test";
	public static final String DEV_MODE_LABEL="dev";
	
	private Map<String,Integer> labelMappings;
	
	public KernelGramMatrixInputDataGen(String outputDir, String similarityModeName, String mode) {
		this.svmFile = new ZipWriteFile(outputDir, String.format("similarities_%s_%s.csv", similarityModeName, mode));
		this.labelsFile = new ZipWriteFile(outputDir, String.format("labels_%s_%s.csv", similarityModeName, mode));

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
		String labelOutput = String.format("%s\t%s\t%d\t%d", c.getPair().getA().getId(),c.getPair().getB().getId(), c.getRow(), c.getCol());
		String output = String.format("%d\t%d\t%s", c.getRow(), c.getCol(), pairVectorFeatures.toString());
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

		String output = String.format("%d\t%d\t%s", labelMappings.get(result.questionId), labelMappings.get(result.documentId), pairVectorFeatures.toString());
		String labelOutput = String.format("%d\t%d\t%s\t%s",  labelMappings.get(result.questionId), labelMappings.get(result.documentId), result.questionId,result.documentId);
		
		this.labelsFile.writeLn(labelOutput);
		this.svmFile.writeLn(output);

	}


}
