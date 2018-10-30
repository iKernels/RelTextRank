package it.unitn.nlpir.system.core.precomputed;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.resultsets.kelp.CandidatePairRepresentation;
import it.unitn.nlpir.system.core.ClassTextPairConversion;
import it.unitn.nlpir.util.Pair;

import org.apache.uima.jcas.JCas;

import com.google.common.base.Stopwatch;



public class ClassTextPairConversionAndTraining extends ClassTextPairConversion {

	@Argument(description = "Number of the answer candidates per question to use for generating examples", required=false)
	protected static Integer candidatesToKeep = 15;

	@Argument(description = "Generation mode of SVM examples: [train, dev, test]", required=false)
	protected static String mode = "train";
	

	public ClassTextPairConversionAndTraining(){
		super();
	}
	
	protected Candidate getCandidate(JCas questionCas, JCas documentCas, Result result) {
		Candidate candidate = new CandidatePairRepresentation(experiment.generateCandidate(questionCas, documentCas, result), new Pair<JCas,JCas>(questionCas, documentCas));
		return candidate;
	}


	
	protected void finalize(){
		
	}
	
	

	public static void main(String[] args) {
		try{
			Args.parse(ClassTextPairConversionAndTraining.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(ClassTextPairConversionAndTraining.class);
			
			System.exit(0);
		}
		
		ClassTextPairConversionAndTraining application = new ClassTextPairConversionAndTraining();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time ({}): {} (ms)", ClassTextPairConversionAndTraining.mode, watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}
	}
}
