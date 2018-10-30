package it.unitn.nlpir.system.core;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.experiment.Experiment;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.datagen.ClassificationDataGenScoresPrediction;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.system.datagen.RerankingDataGenScoresPrediction;
import it.unitn.nlpir.system.datagen.RerankingDataGenTrain;
import it.unitn.nlpir.system.demo.QAPStructure;
import it.unitn.nlpir.uima.Analyzer;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMLightTK;
import svmlighttk.SVMTKExample;
import svmlighttk.SVMVector;

import com.google.common.base.Stopwatch;



public class TextPairPrediction extends RERTextPairConversion {
	
	public static String RERANKING_MODE = "reranking";
	public static String CLASSIFICATION_MODE = "classification";
	
	protected static final String DOCUMENT_LANGUAGE = "en_us";

	@Argument(description = "Fully qualified path to SVM model. Note that the -expClassName and -featureExtractorClass "
			+ "options must be exactly the same as those used when preparing the data for training this specific model", required = true)
	protected static String svmModel;

	
	@Argument(description = "Do you want to run a reranking (-mode reranking) or classification (-mode classification) model?", required = false)
	protected static String mode = "classification";

	@Argument(description = "File to which output the predictions (folder must be specified in the -outputDir parameter)", required = true)
	protected static String outputFile;



	protected static final Logger logger = LoggerFactory.getLogger(TextPairPrediction.class);

	
	protected Experiment experiment;
	protected Analyzer analyzer;

	protected JCas questionCas;
	protected JCas documentCas;
	protected SVMLightTK classifier;
	
	
	public TextPairPrediction(){
		super();
		classifier = new SVMLightTK(svmModel);
	}

	public void resetClassifier(String svmModel) {
		classifier = new SVMLightTK(svmModel);
	}
	

	
	protected RerankingDataGen instantiateRerankingDataGen(String mode, String outputDir) {
		RerankingDataGen rerankingDataGen = null;

		logger.info(String.format("Generating data in the %s mode", TextPairPrediction.mode));
		
		if (TextPairPrediction.mode.equals(RERANKING_MODE)){
			rerankingDataGen = new RerankingDataGenScoresPrediction(svmModel, outputDir, outputFile);
		}
		else 
			rerankingDataGen = new ClassificationDataGenScoresPrediction(svmModel, outputDir, outputFile);
		

		return rerankingDataGen;
	}
	
	
	private String generateExample(Candidate c){
		Pair<String, String> qa = c.getQa();

		SVMVector pairVectorFeatures = c.getFeatures();
	
		String documentTree = qa.getB();
	
		int numberOfNodes = TreeUtil.numberOfNodes(documentTree);
	
		if(numberOfNodes >= RerankingDataGenTrain.MAX_NUMBER_OF_NODES) {
			logger.warn("Skipping example with (tree has more than {} nodes): {}",
					RerankingDataGenTrain.MAX_NUMBER_OF_NODES, documentTree);
			return null;
		}
	
		String example = new SVMTKExample().positive()
				.addTree(qa.getA())
				.addTree(documentTree)
				.addVector(pairVectorFeatures)
		
				.build();
		return example;
	}

	
	public List<Pair<String,Double>> rerank(QAPStructure structure) {
		List<Pair<String,Double>> rankedIds = new ArrayList<Pair<String,Double>>();
		
		analyzer.enableAllAnalysisEngine();
		analyzer.analyze(questionCas);
		UIMAUtil.setupCas(questionCas, structure.getQ().getId(), structure.getQ().getText());
		for (Result r : structure.getR()) {
			disableQuestionRelevantAnalyzersOnly();
			UIMAUtil.setupCas(documentCas, r.documentId, r.documentText);
			analyzer.analyze(documentCas);
			Candidate c = experiment.generateCandidate(questionCas, documentCas, r);
			String svmlightTKInstance= generateExample(c);
			double prediction = classifier.classify(svmlightTKInstance);
			rankedIds.add(new Pair<String,Double>(r.documentId,prediction));
		}
		Collections.sort(rankedIds, new Comparator<Pair<String,Double>>(){
		     public int compare(Pair<String,Double> o1, Pair<String,Double> o2){
		         if(o1.getB() == o2.getB())
		             return 0;
		         return o1.getB()> o2.getB() ? -1 : 1;
		     }
		});
		return rankedIds;
	}
	
	
	public void executeInteractive(){
		Scanner scanner = new Scanner(System.in);
		SVMLightTK classifier = new SVMLightTK(svmModel);
		logger.info("The output of the system is the distance to the separation hyperplane.");
		while (true) {
			logger.info("Enter a pari of text delimited by tabulation (type \"q\" to exit): ");

			String input = scanner.nextLine();
			
			if (input.equals("q"))
				break;

			String [] tweets = input.split("\t");
			UIMAUtil.setupCas(questionCas, "tweet1", tweets[0]);
			UIMAUtil.setupCas(documentCas, "tweet2", tweets[1]);
			
			analyzer.analyze(questionCas);
			analyzer.analyze(documentCas);
			Result result = new Result("tweet1", "tweet2", "1.0",
					"1.0", "false", tweets[1]);
			Candidate c = experiment.generateCandidate(questionCas, documentCas, result);
			String svmlightTKInstance= generateExample(c);
			double prediction = classifier.classify(svmlightTKInstance);
			
			logger.info(String.format("Prediction: %.5f", prediction));
			//break;
			
		}
		scanner.close();
	}


	public static void main(String[] args) {

		try{
			Args.parse(TextPairPrediction.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(TextPairPrediction.class);
			
			System.exit(0);
		}
		

		TextPairPrediction application = new TextPairPrediction();
		
		try {
			Stopwatch watch = new Stopwatch();
			watch.start();
			application.execute();
			logger.info("Run-time: {} (ms)", watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}

	}


}
