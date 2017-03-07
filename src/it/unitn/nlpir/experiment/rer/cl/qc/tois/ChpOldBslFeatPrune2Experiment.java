package it.unitn.nlpir.experiment.rer.cl.qc.tois;

import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;

/**
 * CH + FC + QT (defined by config file, Stanford 3.6.0 preprocessing pipeline), pruning ray=2
* @author IKernels group
 *
 */
public class ChpOldBslFeatPrune2Experiment extends StanfordAETrecQAWithQCExperiment {
	
	public ChpOldBslFeatPrune2Experiment(String configFile) {
		super(configFile);
	}
	
	protected void setupProjector() {
		this.pruningRay = 2;
		this.projector = Projectors.getFocusProjector(new PosChunkTreeBuilder(), pruningRay, new StartsWithOrContainsTagPruningRule());
		
		
	}

}
