package it.unitn.nlpir.experiment.kernmat;

import it.unitn.nlpir.features.builder.nouima.NoUIMAFeaturesBuilder;
import it.unitn.nlpir.features.nouima.presets.NoUIMAFeatureExtractorFactory;
import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainDocument;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoUIMAFeatureOnlyExperiment implements NoUIMAExperiment {
	protected  Logger logger = LoggerFactory.getLogger(NoUIMAFeatureOnlyExperiment.class);

	protected boolean lowerCaseOutput = true;
	protected String leafTextType = TokenTextGetterFactory.LEMMA;
	protected String matchingTokenTextType = TokenTextGetterFactory.LEMMA;
	protected String relTag = "REL";
	protected int pruningRay = -1;
	protected static final Pair<String,String> dummyQaPair = new Pair<String,String>(TreeUtil.serializeTree(TreeUtil.createNode("ROOT")),TreeUtil.serializeTree(TreeUtil.createNode("ROOT")));

	
	public static final String FEATURE_EXTRACTOR_CLASS_PROPERTY = "featureExtractorClass";
	protected String featureExtractorClassName = null;
	public static final String FEATURES_LOCATION_PROPERTY = "featureCacheFileName";
	protected String featureCacheFileName= null;
	public static final String FEATURES_ID_LOCATION_PROPERTY = "featureIDCacheFileName";
	protected String featureIDCacheFileName= null;
	
	
	protected NoUIMAFeaturesBuilder fb;

	public static final int TRAIN_MODE = 0;
	public static final int TEST_MODE = 1;
	public static final int UNDEFINED_MODE = 2;
	protected int mode = UNDEFINED_MODE;
	
	
	
	
	protected void setupFeatures() {
		fb = NoUIMAFeatureExtractorFactory.getFeatureBuilder(this.featureExtractorClassName, this.featureCacheFileName);
	}

	protected void init() {
	
		setupFeatures();
	}

	protected void initializeDefaultValues() {
		
	}
	
	public NoUIMAFeatureOnlyExperiment(String featureExtractorClassName) {
		this(featureExtractorClassName, null);
	}
	
	public NoUIMAFeatureOnlyExperiment(String featureExtractorClassName, String featureCacheFileName) {
		super();
		this.featureExtractorClassName = featureExtractorClassName;
		this.featureCacheFileName = featureCacheFileName;
		initializeDefaultValues();
		init();
	}

	
	public NoUIMAFeatureOnlyExperiment(int mode) {
		this.mode = mode;
		init();
	}
	
	public void setMode(int mode) {
		this.mode = mode;
		
	}
	

	
	@Override
	public NoUIMACandidate generateCandidate(PlainDocument questionCas, PlainDocument documentCas) {
		NoUIMACandidate c = new NoUIMACandidate(new Pair<PlainDocument, PlainDocument>(questionCas,documentCas));
		fb.getFeatures(c);
		return c;
	}


}
