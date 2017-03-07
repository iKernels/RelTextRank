package it.unitn.nlpir.features;


public class DocumentRankingScore implements FeatureExtractor {
	public static final double defaultNormFactor = 1.0;
	private double normFactor;
	
	public DocumentRankingScore() {
		this(defaultNormFactor);
	}
	
	public DocumentRankingScore(double normFactor) {
		this.normFactor = normFactor;
	}
	
	@Override
	public void extractFeatures(QAPair qa) {
		qa.featureVector.addFeature(Double.parseDouble(qa.result.rankingScore)/this.normFactor);
	}

	@Override
	public String getFeatureName() {
		return this.getClass().getSimpleName();
	}

}
