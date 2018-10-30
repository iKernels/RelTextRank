package it.unitn.nlpir.experiment.fqa;



import java.util.Properties;

import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkFullTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder;

/**
 * CH + V + FC_thres + QT, Stanford preprocessing pipeline
 * @author IKernels group
 *
 */
public class CHpExperiment extends StanfordAETrecQAWithQCExperiment {
	
	
	public CHpExperiment(String configFile) {
		super(configFile);
	}
	
	public CHpExperiment(String configFile, Properties p) {
		super(configFile,p);
	}
	
	public CHpExperiment(Properties p) {
		super(p);
	}
	
	
	public CHpExperiment() {
		super();
	}
	
	protected void setupProjector() {
		logger.debug(String.format("Focus match = %s, Type focus match = %s,  mark focus in question = %s", doFocusMatch, typeFocusMatch, markFocusInQuestion));
		this.projector = Projectors.getFocusWithThreshFocusUntypedFocusInQProjector(new PosChunkFullTreeBuilder(), pruningRay,
                new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder(), doFocusMatch, typeFocusMatch, markFocusInQuestion);
		
		
	}
	
}
