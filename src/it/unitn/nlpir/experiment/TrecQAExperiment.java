package it.unitn.nlpir.experiment;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.features.presets.IVectorFeatureExtractor;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMVector;

import com.google.common.base.Strings;

public class TrecQAExperiment implements Experiment {
	protected final Logger logger = LoggerFactory.getLogger(TrecQAExperiment.class);

	private boolean lowerCaseOutput = true;
	private String leafTextType = TokenTextGetterFactory.LEMMA;
	private String matchingTokenTextType = TokenTextGetterFactory.LEMMA;
	protected String relTag = "REL";
	protected int pruningRay = 1;
	private boolean topicAsPostag = true;
	private boolean firstTopicAsChunk = false;
	private boolean keepNoRelSentence = true;
	private String topicModelPath;

	
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
		fb = new FeaturesBuilder();
		if (this.featureExtractorClassName!=null){
			IVectorFeatureExtractor vfe = null;
			
				try {
					if (!((this.featureIDCacheFileName!=null)&&(this.featureCacheFileName!=null))){
						Class<?> c = null;
						c = Class.forName(featureExtractorClassName);
						vfe = (IVectorFeatureExtractor) c.newInstance();
					}
					else{
						Constructor<?> c = null;
						c = Class.forName(featureExtractorClassName).getConstructor(String.class, String.class);
						vfe = (IVectorFeatureExtractor) c.newInstance();
					}
					fb = vfe.getFeaturesBuilder();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
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
			}
	}

	protected void init() {
		setupProjector();
		setupFeatures();
	}

	public TrecQAExperiment() {
		init();
	}

	public TrecQAExperiment(String configFile) {
		loadProperties(configFile);
		init();
	}

	
	public TrecQAExperiment(String configFile, Properties p) {
		loadProperties(configFile);
		setVariablesFromProperties(p);
		init();
	}
	
	public TrecQAExperiment(Properties p) {
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
		
		
		logger.info((qaProj.getA()+"\t"+qaProj.getB()).replace("(", "[").replace(")", "]"));
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
		logger.info("lowerCaseOutput = {}", this.lowerCaseOutput);
		logger.info("leafTextType = {}", this.leafTextType);
		logger.info("matchingTokenTextType = {}", this.matchingTokenTextType);
		logger.info("pruningRay = {}", this.pruningRay);
		logger.info("keepNoRelSentence = {}", this.keepNoRelSentence);
		logger.info("topicAsPostag = {}", this.topicAsPostag);
		logger.info("firstTopicAsChunk = {}", this.firstTopicAsChunk);
		logger.info("topicModelPath = {}", this.topicModelPath);
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

		// topicAsPostag
		String proptopicAsPostag = prop.getProperty("topicAsPostag");
		if (!Strings.isNullOrEmpty(proptopicAsPostag)) {
			this.topicAsPostag = Boolean.parseBoolean(proptopicAsPostag);
		}

		// firstTopicAsChunk
		String propFirstTopicAsChunk = prop.getProperty("firstTopicAsChunk");
		if (!Strings.isNullOrEmpty(propFirstTopicAsChunk)) {
			this.firstTopicAsChunk = Boolean.parseBoolean(propFirstTopicAsChunk);
		}

		// keepNoRelSentence
		String propKeepNoRelSentence = prop.getProperty("keepNoRelSentence");
		if (!Strings.isNullOrEmpty(propKeepNoRelSentence)) {
			this.keepNoRelSentence = Boolean.parseBoolean(propKeepNoRelSentence);
		}

		// topicModelPath
		String propTopicModelPath = prop.getProperty("topicModelPath");
		if (!Strings.isNullOrEmpty(propTopicModelPath)) {
			this.topicModelPath = propTopicModelPath;
		}

		// pruningRay
		String propPruningRay = prop.getProperty("pruningRay");
		if (!Strings.isNullOrEmpty(propPruningRay)) {
			this.pruningRay = Integer.parseInt(propPruningRay);
		}
		else{
			this.pruningRay = 0;
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
