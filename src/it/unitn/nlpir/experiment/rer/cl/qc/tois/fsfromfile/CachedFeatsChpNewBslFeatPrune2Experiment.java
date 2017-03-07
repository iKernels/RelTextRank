package it.unitn.nlpir.experiment.rer.cl.qc.tois.fsfromfile;



import it.unitn.nlpir.experiment.IFeatsFromFile;
import it.unitn.nlpir.experiment.rer.cl.qc.tois.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.features.FeatureSets;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkFullTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder;

/**
 * <lI> CH + V + FREL + REL
 * <li> Stanford processing pipeline
 * <li> Pruning ray = 2
 * <li> reading advanced features from file
 * 
* @author IKernels group
 *
 */
public class CachedFeatsChpNewBslFeatPrune2Experiment extends StanfordAETrecQAWithQCExperiment  implements IFeatsFromFile {
	public CachedFeatsChpNewBslFeatPrune2Experiment(String configFile) {
		super(configFile);
	}
	
	protected void setupProjector() {
		this.pruningRay = 2;
		this.projector = Projectors.getFocusWithThreshFocusUntypedFocusInQProjector(new PosChunkFullTreeBuilder(), pruningRay,
                new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder());
		
		
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
