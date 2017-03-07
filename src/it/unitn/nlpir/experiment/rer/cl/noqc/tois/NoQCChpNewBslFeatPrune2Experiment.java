package it.unitn.nlpir.experiment.rer.cl.noqc.tois;



import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkFullTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;

/**
 * <lI> CH + V + FC_thres
 * <li> Stanford processing pipeline
 * <li> Pruning ray = 2
 * <li> baseline features (IR score, bow-based similarity, kernel similarity) 
 * 
* @author IKernels group
 *
 */
public class NoQCChpNewBslFeatPrune2Experiment extends StanfordAENoQCExperiment {
	
	protected void setupProjector() {
		this.pruningRay = 2;
		this.projector = Projectors.getFocusWithThreshToQOnlyNoQCMatchProjector(new PosChunkFullTreeBuilder(), pruningRay,
                new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizer());
		
		
	}
	

}
