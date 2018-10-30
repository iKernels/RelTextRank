package it.unitn.nlpir.util.kelp;

import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.learningalgorithm.LearningAlgorithm;
import it.uniroma2.sag.kelp.predictionfunction.PredictionFunction;
import it.uniroma2.sag.kelp.utils.JacksonSerializerWrapper;
import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


public class KelpGramMatrixGetter {
	protected it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier classifier;
	protected static final String POSITIVE_EXAMPLE_LABEL = "+1";
	protected Label positiveLabel;
	
	@Argument(description = "Kelp configuration file to use", required = true)
	protected static String configFile;
	

	@Argument(description = "Train dataset file", required = true)
	protected static String trainDatasetFile;
	
	@Argument(description = "Output model path", required = true)
	protected static String modelPath;
	
	protected LearningAlgorithm learner;
	protected SimpleDataset dataset;
	
	public void initializeDataset(String datasetFile) throws Exception {
		// Read a dataset into a trainingSet variable
		this.dataset = new SimpleDataset();
		this.dataset.populate(datasetFile);
	}
	
	public void initialize(String datasetFile, String configFile) throws Exception {
		
		initializeDataset(datasetFile);
		initializeModel(configFile);
		
		
	}

	public void initializeModel(String configFile) throws JsonParseException, JsonMappingException, IOException {
		JacksonSerializerWrapper serializer = new JacksonSerializerWrapper();
		this.learner = serializer.readValue(new File(configFile), LearningAlgorithm.class);
		// Set classes if not specified in Json file
		if (learner.getLabels() == null || learner.getLabels().size() == 0) {
			List<Label> classes = dataset.getClassificationLabels();
			if (classes.size() == 2) {
				learner.setLabels(classes.subList(0, 1));
			} else {
				learner.setLabels(classes);
			}
		}
	}
	
	/**
	 * Load a model from file
	 * @param modelFile
	 * @throws Exception 
	 */
	public KelpGramMatrixGetter() throws Exception {
		initialize(trainDatasetFile, configFile);
	}

	public void learnAndSaveModel() throws IOException {
		learner.learn(dataset);
		JacksonSerializerWrapper serializer = new JacksonSerializerWrapper();
		// Save the model, a.k.a. PredictionFunction
		PredictionFunction predictionFunction = learner.getPredictionFunction();
		serializer.writeValueOnFile(predictionFunction, modelPath);
	}
	
	public static void main(String[] args) {
		try{
			Args.parse(KelpGramMatrixGetter.class, args);
		}
		catch (Exception e){
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(KelpGramMatrixGetter.class);
			
			System.exit(0);
		}
		
		try {
			KelpGramMatrixGetter matrixBuilder = new KelpGramMatrixGetter();
			matrixBuilder.learnAndSaveModel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
}
