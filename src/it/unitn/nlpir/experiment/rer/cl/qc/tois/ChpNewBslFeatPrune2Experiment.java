package it.unitn.nlpir.experiment.rer.cl.qc.tois;



import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkFullTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder;

/**
 * <lI> CH + V + FREL + REL
 * <li> Stanford processing pipeline
 * <li> Pruning ray = 2
 * <li> baseline features (IR score, bow-based similarity, kernel similarity) 
 * 
* @author IKernels group
 *
 */
public class ChpNewBslFeatPrune2Experiment extends StanfordAETrecQAWithQCExperiment {
	public ChpNewBslFeatPrune2Experiment(String configFile) {
		super(configFile);
	}
	
	protected void setupProjector() {
		this.pruningRay = 2;
		this.projector = Projectors.getFocusWithThreshFocusUntypedFocusInQProjector(new PosChunkFullTreeBuilder(), pruningRay,
                new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder());
		
		
	}
	

}
