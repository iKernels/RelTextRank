package it.unitn.nlpir.experiment.rer.cl.qc.tois.fsfromfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.IFeatsFromFile;
import it.unitn.nlpir.experiment.rer.cl.qc.tois.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.features.FeatureSets;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;

/**
 * CH + FC + QT (defined by config file, Stanford 3.6.0 preprocessing pipeline), do not do pruning
* @author IKernels group
 *
 */
public class CachedFeatsChOldBslNoPruneExperiment extends StanfordAETrecQAWithQCExperiment implements IFeatsFromFile {
	protected static final Logger logger = LoggerFactory.getLogger(CachedFeatsChOldBslNoPruneExperiment.class);
	public CachedFeatsChOldBslNoPruneExperiment(String configFile) {
		super(configFile);
	}
	
	protected void setupProjector() {
		this.pruningRay = -1;
		this.projector = Projectors.getFocusProjector(new PosChunkTreeBuilder(), pruningRay, new StartsWithOrContainsTagPruningRule());
		
		
	}

	protected void setupFeatures() {
		fb = new FeaturesBuilder();
	}

	@Override
	public void setFeaturesSource(String idFile, String featureFile) {
		logger.info(String.format("Reading features from %s, %s", idFile, featureFile));
		fb.extend(FeatureSets.buildFeaturesFromExternalFile(idFile, featureFile));
		
	}

}
