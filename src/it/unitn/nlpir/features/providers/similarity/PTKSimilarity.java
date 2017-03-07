package it.unitn.nlpir.features.providers.similarity;

import it.unitn.kernels.ptk.PTKernel;
import it.unitn.nlpir.features.FeatureExtractor;
import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.features.providers.trees.ITreeProvider;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PTKSimilarity implements FeatureExtractor {
	static final Logger logger = LoggerFactory.getLogger(PTKSimilarity.class);

	private ITreeProvider treeProvider;

	static PTKernel ptk = new PTKernel();

	public PTKSimilarity(ITreeProvider treeProvider) {
		this.treeProvider = treeProvider;
	}

	public double computeScore(QAPair qa) {
		double score = 0.0;
		try {
			Pair<String, String> trees = treeProvider.getTrees(qa);
			score = ptk.evaluateKernel(trees.getA(), trees.getB());
		}
		catch (java.lang.ArrayIndexOutOfBoundsException bounds){
			Pair<String, String> trees = treeProvider.getTrees(qa);
			logger.warn("Failed to compute PTK score due to the size of the trees: sizeA={},sizeB={}", TreeUtil.buildTree(trees.getA()).size(),
			TreeUtil.buildTree(trees.getB()).size());
		}
		catch (Exception e) {
			logger.warn("Failed to compute PTK score: {}", e);
		}

		return score;
	}

	
	@Override
	public void extractFeatures(QAPair qa) {
		qa.getFeatureVector().addFeature(computeScore(qa));
	}

	@Override
	public String getFeatureName() {
		return this.getClass().getSimpleName() + "." + treeProvider.getClass().getSimpleName();
	}
}
