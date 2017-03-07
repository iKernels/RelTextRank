package it.unitn.nlpir.experiment.rer.cl.qc.tois;



import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkFullTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder;

/**
 * CH + V + FC_thres + QT, Stanford preprocessing pipeline
* @author IKernels group
 *
 */
public class ChpNewBslFeatNoPruneExperiment extends StanfordAETrecQAWithQCExperiment {
	
	
	public ChpNewBslFeatNoPruneExperiment(String configFile) {
		super(configFile);
	}
	
	
	protected void setupProjector() {
		this.pruningRay = -1;
		
		this.projector = Projectors.getFocusWithThreshFocusUntypedFocusInQProjector(new PosChunkFullTreeBuilder(), pruningRay,
                new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder());
		
		
	}
	
}
