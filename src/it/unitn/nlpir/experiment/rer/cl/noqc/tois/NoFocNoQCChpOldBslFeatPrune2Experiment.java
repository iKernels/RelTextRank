package it.unitn.nlpir.experiment.rer.cl.noqc.tois;

import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;

/**
 * CH + FC 
* @author IKernels group
 *
 */
public class NoFocNoQCChpOldBslFeatPrune2Experiment extends StanfordAENoQCExperiment {
	

	
	protected void setupProjector() {
		this.pruningRay = 2;
		this.projector = Projectors.getRelTreeProjector(new PosChunkTreeBuilder(), 
				pruningRay, new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizer());
		
		
	}

}
