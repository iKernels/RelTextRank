package it.unitn.nlpir.system.core;

import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.experiment.Experiment;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.questions.QuestionsFileReader;
import it.unitn.nlpir.resultsets.QAResultSetParser;
import it.unitn.nlpir.resultsets.ResultSetFileReader;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.system.datagen.RerankingDataGenTest;
import it.unitn.nlpir.system.datagen.RerankingDataGenTrain;
import it.unitn.nlpir.uima.Analyzer;
import it.unitn.nlpir.uima.UIMAFilePersistence;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;



/**
 * Abstract base class for the text pair reranking/classification 
* @author IKernels group
 *
 */

public abstract class TextPairConversionBase {
	
	protected static final String DOCUMENT_LANGUAGE = "en_us";
	
	@Argument(description = "Path to the questions file", required = true)
	protected static String questionsPath;

	@Argument(description = "Path to the answers file", required = true)
	protected static String answersPath;

	@Argument(description = "Fully qualified name of the Experiment class instance", required = true)
	protected static String expClassName;

	@Argument(description = "Output directory to write the generated files", required=true)
	protected static String outputDir = ".";

	@Argument(description = "Path to the persistence folder", required=false)
	protected static String filePersistence;
	
	@Argument(description = "Preset feature extractor class (see it.unitn.nlpir.features.presets for options). If this parameter is not set, then the feature vector will be empty.", required=false)
	protected static String featureExtractorClass;
	
	@Argument(description = "If you use it.unitn.nlpir.features.presets.FromFileVectorFeature, you must specify from where to read the features vectors. The feature vector file name is tabe-delimited and has three columns:"
			+ "(1) questionID; (2) answerID; (3) feature vector in SVMLight-TK format (<feature>:<value><blank><feature>:<value><blank>...<blank><feature>:<value>)", required=false)
	protected static String featureCacheFileName;
		
	
	
	
	
	@Argument(description = "Write relevancy file in the verbose format specified by the TREC submission guidelines")
	protected static boolean verboseResultset = true;

	//do not store the new annotations in CAS XMI serializations
	@Argument(description = "Do not update the annotations in the persisted files", required = false)
	protected static boolean doNotSerializeNewAnnotations = false;
	
	//allow overwriting new annotations in the dynamic CASes in RAM (serialization in XMIs is controlled by doNotStoreNew)
	@Argument(description = "Overwrite annotations in the dynamic CASes in RAM when the pipeline is running without serializing the new annotation. False by default.", required = false)
	protected static boolean allowOverwriting = false;
	
	
	protected static final Logger logger = LoggerFactory.getLogger(TextPairConversionBase.class);

	@Argument(description = "Experiment configuration path")
	protected static String expConfigPath;
	
	protected Experiment experiment;
	protected Analyzer analyzer;
	protected List<Question> questions;
	protected ResultSetFileReader answers;
	protected JCas questionCas;
	protected JCas documentCas;
	
	
	
	public TextPairConversionBase() {
		logger.info("Setting up with Experiment instance: {}", expClassName);
		Properties exProperties = null;
		
		if (featureExtractorClass!=null){
			exProperties = new Properties();
			exProperties.put(TrecQAExperiment.FEATURE_EXTRACTOR_CLASS_PROPERTY, featureExtractorClass);
			if (featureCacheFileName!=null){
				exProperties.put(TrecQAExperiment.FEATURES_LOCATION_PROPERTY, featureCacheFileName);
			}
		}
		
		try {
			if (expConfigPath==null){
				if (exProperties==null){
					Class<?> c = null;
					c = Class.forName(expClassName);
					experiment = (Experiment) c.newInstance();
				}
				else{
					Constructor<?> c;
					c = Class.forName(expClassName).getConstructor(Properties.class);
					experiment = (Experiment) c.newInstance(exProperties);
				}
			}
			else{
				if (exProperties==null){
					logger.info("Config path: {}", expConfigPath);
					Constructor<?> c;
					c = Class.forName(expClassName).getConstructor(String.class);
					experiment = (Experiment) c.newInstance(expConfigPath);
				}
				else{
					Constructor<?> c;
					c = Class.forName(expClassName).getConstructor(String.class, Properties.class);
					experiment = (Experiment) c.newInstance(expConfigPath, exProperties);
				}
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Read the questions
		questions = QuestionsFileReader.getQuestions(questionsPath);

		// Read the candidate answers 
		answers = new ResultSetFileReader(answersPath, new QAResultSetParser());

		// Instantiate the analyzer with persistence layer
		if (!Strings.isNullOrEmpty(filePersistence)) {
			analyzer = new Analyzer(experiment.getAnalysisEngineList(), new UIMAFilePersistence(
					filePersistence), !doNotSerializeNewAnnotations);
//			analyzer = new Analyzer(experiment.getAnalysisEngineList(), new UIMAMongoDBPersistence("trec"));
		} else {
			analyzer = new Analyzer(experiment.getAnalysisEngineList());
		}

		// Create CAS for the question
		questionCas = analyzer.getNewJCas();

		// Create a CAS for the document
		documentCas = analyzer.getNewJCas();
		//System.out.println("INIT");
		
		allowOverwriting = false;
	}
	
	
	
	abstract public void execute();

	protected RerankingDataGen instantiateRerankingDataGen(String mode, String outputDir) {
		
		
		
		RerankingDataGen rerankingDataGen = null;
		

			switch (mode) {
			case "train":
				logger.info("Generating data in the train mode");
				rerankingDataGen = new RerankingDataGenTrain(outputDir, verboseResultset);
				break;
			case "test":
				logger.info("Generating data in the test mode");
				rerankingDataGen = new RerankingDataGenTest(outputDir, verboseResultset);
				break;
			case "dev":
				logger.info("Generating data in the dev mode");
				rerankingDataGen = new RerankingDataGenTest(outputDir, verboseResultset, "svm.dev", "dev.relevancy");
				break;
			default:
				logger.error("No corresponding generation mode found.");
				System.exit(1);
				break;
			}


		return rerankingDataGen;
	}
}
