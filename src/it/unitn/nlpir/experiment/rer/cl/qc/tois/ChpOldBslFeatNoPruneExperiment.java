package it.unitn.nlpir.experiment.rer.cl.qc.tois;

import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;

/**
 * CH + FC + QT (defined by config file, Stanford 3.6.0 preprocessing pipeline)
* @author IKernels group
 *
 */
public class ChpOldBslFeatNoPruneExperiment extends StanfordAETrecQAWithQCExperiment {
	
	public ChpOldBslFeatNoPruneExperiment(String configFile) {
		super(configFile);
	}
	
	protected void setupProjector() {
		this.pruningRay = -1;
		this.projector = Projectors.getFocusProjector(new PosChunkTreeBuilder(), pruningRay, new StartsWithOrContainsTagPruningRule());
	}
}
