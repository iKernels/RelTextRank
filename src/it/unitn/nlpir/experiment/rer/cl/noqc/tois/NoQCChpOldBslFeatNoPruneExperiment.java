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
public class NoQCChpOldBslFeatNoPruneExperiment extends StanfordAENoQCExperiment {
	
	
	
	protected void setupProjector() {
		this.pruningRay = -1;
		this.projector = Projectors.getFocusMarkAndRELMatchProjector(new PosChunkTreeBuilder(), pruningRay,
				new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizer());
	}
}
