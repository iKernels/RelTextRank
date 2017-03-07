package it.unitn.nlpir.experiment.rer.cl.noqc.tois;



import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkFullTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;

/**
 * CH + V  , Stanford preprocessing pipeline
* @author IKernels group
 *
 */
public class NoFocNoQCChpNewBslFeatNoPruneExperiment extends StanfordAENoQCExperiment {

	
	
	protected void setupProjector() {
		this.pruningRay = -1;
		
		this.projector = Projectors.getRelTreeProjector(new PosChunkFullTreeBuilder(), pruningRay,
                new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizer());
		
		
	}
	
}
