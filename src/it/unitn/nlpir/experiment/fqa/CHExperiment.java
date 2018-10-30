package it.unitn.nlpir.experiment.fqa;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;

/**
 * CH + FC + QT (defined by config file, Stanford 3.6.0 preprocessing pipeline), pruning ray=2
* @author IKernels group
 *
 */
public class CHExperiment extends StanfordAETrecQAWithQCExperiment {
	protected static final Logger logger = LoggerFactory.getLogger(CHExperiment.class);
	public CHExperiment(String configFile) {
		super(configFile);
	}
	
	public CHExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public CHExperiment(Properties p) {
		super(p);
	}
	
	public CHExperiment() {
		super();
	}
	
	protected void setupProjector() {
		logger.info(String.format("pruningRay=%d", pruningRay));
		logger.info(String.format("doFocusMatch=%s", doFocusMatch));
		this.projector = Projectors.getFocusProjector(new PosChunkTreeBuilder(), pruningRay,  new StartsWithOrContainsTagPruningRule(), 
				new TreeLeafFinalizer(), doFocusMatch, typeFocusMatch, markFocusInQuestion);
	}



}
