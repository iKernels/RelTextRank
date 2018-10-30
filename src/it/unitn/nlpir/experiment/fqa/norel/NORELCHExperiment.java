package it.unitn.nlpir.experiment.fqa.norel;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.fqa.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;

/**
 * CH + FC + QT (defined by config file, Stanford 3.6.0 preprocessing pipeline), pruning ray=2
* @author IKernels group
 *
 */
public class NORELCHExperiment extends StanfordAETrecQAWithQCExperiment {
	protected static final Logger logger = LoggerFactory.getLogger(NORELCHExperiment.class);
	public NORELCHExperiment(String configFile) {
		super(configFile);
	}
	
	public NORELCHExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public NORELCHExperiment(Properties p) {
		super(p);
	}
	
	public NORELCHExperiment() {
		super();
	}
	
	protected void setupProjector() {
		logger.info(String.format("pruningRay=%d", pruningRay));
		logger.info(String.format("doFocusMatch=%s", doFocusMatch));
		this.projector = Projectors.getNoRELProjector(new PosChunkTreeBuilder(), new TreeLeafFinalizer());
	}



}
