package it.unitn.nlpir.experiment;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.features.presets.FeatureExtractorFactory;
import it.unitn.nlpir.projectors.Projector;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.uima.AnalysisEngineList;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMVector;

import com.google.common.base.Strings;

public class TrecQAExperiment implements Experiment {
	protected  Logger logger = LoggerFactory.getLogger(TrecQAExperiment.class);

	protected boolean lowerCaseOutput = true;
	protected String leafTextType = TokenTextGetterFactory.LEMMA;
	protected String matchingTokenTextType = TokenTextGetterFactory.LEMMA;
	protected String relTag = "REL";
	protected int pruningRay = -1;
	

	
	public static final String FEATURE_EXTRACTOR_CLASS_PROPERTY = "featureExtractorClass";
	protected String featureExtractorClassName = null;
	public static final String FEATURES_LOCATION_PROPERTY = "featureCacheFileName";
	protected String featureCacheFileName= null;
	public static final String FEATURES_ID_LOCATION_PROPERTY = "featureIDCacheFileName";
	protected String featureIDCacheFileName= null;
	protected Projector projector;
	protected FeaturesBuilder fb;

	public static final int TRAIN_MODE = 0;
	public static final int TEST_MODE = 1;
	public static final int UNDEFINED_MODE = 2;
	protected int mode = UNDEFINED_MODE;
	
	protected void setupProjector() {
		logger.info("Setting up projector");
		this.projector = Projectors.getDependencyPosTagFocusProjector();
		logger.info("Projector set: {}", this.projector.getClass().getName());
	}
	
	
	
	protected void setupFeatures() {
		fb = FeatureExtractorFactory.getFeatureBuilder(this.featureExtractorClassName, this.featureCacheFileName);
	}

	protected void init() {
		setupProjector();
		setupFeatures();
	}

	protected void initializeDefaultValues() {
		
	}
	
	
	public TrecQAExperiment() {
		super();
		initializeDefaultValues();
		init();
	}

	public TrecQAExperiment(String configFile) {
		initializeDefaultValues();
		loadProperties(configFile);
		init();
	}

	
	public TrecQAExperiment(String configFile, Properties p) {
		initializeDefaultValues();
		loadProperties(configFile);
		setVariablesFromProperties(p);
		init();
	}
	
	public TrecQAExperiment(Properties p) {
		initializeDefaultValues();
		setVariablesFromProperties(p);
		init();
	}
	
	public TrecQAExperiment(int mode) {
		this.mode = mode;
		init();
	}
	
	public void setMode(int mode) {
		this.mode = mode;
		
	}
	
	public Projector getProjector() {
		return this.projector;
	}

	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getQAAnalysisEngineList();
	}

	@Override
	public Candidate generateCandidate(JCas questionCas, JCas documentCas, Result result) {
		Pair<String, String> qaProj = null;
		try {
			qaProj = projector.project(questionCas, documentCas);
		} catch (AnnotationNotFoundException e) {
			logger.warn("No projection carried for this example. "
					+ "Required annotation not found.");
		}
		
		
		SVMVector featureVector = fb.getFeatures(new QAPair(questionCas, documentCas, result,
				new SVMVector(), qaProj));
		
		
		logger.debug((qaProj.getA()+"\t"+qaProj.getB()).replace("(", "[").replace(")", "]"));
		return new Candidate(result, qaProj, featureVector);
	}

	protected void loadProperties(String configFile) {
		Properties prop = new Properties();

		try {
			// load a properties file from class path, inside static method
			prop.load(new FileInputStream(configFile));

			setVariablesFromProperties(prop);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		logger.info("Experiment settings: ");
		for (Object key : prop.keySet()) {
			logger.info("{} = {}", (String)key, prop.get(key));
		}
		/*logger.info("lowerCaseOutput = {}", this.lowerCaseOutput);
		logger.info("leafTextType = {}", this.leafTextType);
		logger.info("matchingTokenTextType = {}", this.matchingTokenTextType);
		logger.info("pruningRay = {}", this.pruningRay);
		logger.info("keepNoRelSentence = {}", this.keepNoRelSentence);
		logger.info("topicAsPostag = {}", this.topicAsPostag);
		logger.info("firstTopicAsChunk = {}", this.firstTopicAsChunk);
		logger.info("topicModelPath = {}", this.topicModelPath);*/
	}

	protected void setVariablesFromProperties(Properties prop) {
		// get the property value and print it out
		String propLowerCaseOutput = prop.getProperty("lowerCaseOutput");
		if (!Strings.isNullOrEmpty(propLowerCaseOutput)) {
			this.lowerCaseOutput = Boolean.parseBoolean(propLowerCaseOutput);
		}

		// leafTextType
		String propLeafTextType = prop.getProperty("leafTextType");
		if (!Strings.isNullOrEmpty(propLeafTextType)) {
			this.leafTextType = propLeafTextType;
		}

		// matchingTokenTextType
		String propMatchingTokenTextType = prop.getProperty("matchingTokenTextType");
		if (!Strings.isNullOrEmpty(propMatchingTokenTextType)) {
			this.matchingTokenTextType = propMatchingTokenTextType;
		}

		// relTag
		String propRelTag = prop.getProperty("relTag");
		if (!Strings.isNullOrEmpty(propRelTag)) {
			this.relTag = propRelTag;
		}

		
		// pruningRay
		String propPruningRay = prop.getProperty("pruningRay");
		
		if (!Strings.isNullOrEmpty(propPruningRay)) {{
			this.pruningRay = Integer.parseInt(propPruningRay);
			
		}
		}
		
		String featureExtractorClassName = prop.getProperty(FEATURE_EXTRACTOR_CLASS_PROPERTY);
		if (!Strings.isNullOrEmpty(featureExtractorClassName)) {
			this.featureExtractorClassName = featureExtractorClassName;
		}
		
		String featureCacheFileName = prop.getProperty(FEATURES_LOCATION_PROPERTY);
		if (!Strings.isNullOrEmpty(featureCacheFileName)) {
			this.featureCacheFileName = featureCacheFileName;
		}
		String featureIDCacheFileName = prop.getProperty(FEATURES_ID_LOCATION_PROPERTY);
		if (!Strings.isNullOrEmpty(featureIDCacheFileName)) {
			this.featureIDCacheFileName = featureIDCacheFileName;
		}
	}

}
