package it.unitn.nlpir.experiment.fqa.features;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * CH + FC + QT (defined by config file, Stanford 3.6.0 preprocessing pipeline), pruning ray=2
* @author IKernels group
 *
 */
public class CHPureFeaturesGenericExperiment extends CHPureFeaturesExperiment {
	protected static final Logger logger = LoggerFactory.getLogger(CHPureFeaturesGenericExperiment.class);
	public CHPureFeaturesGenericExperiment(String configFile) {
		super(configFile);
	}
	
	public CHPureFeaturesGenericExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public CHPureFeaturesGenericExperiment(Properties p) {
		super(p);
	}
	
	public CHPureFeaturesGenericExperiment() {
		super();
	}
	
	protected void setupProjector() {
		logger.info(String.format("pruningRay=%d", pruningRay));
		this.projector = Projectors.getRelTreeProjector(new PosChunkTreeBuilder(), pruningRay,  new StartsWithOrContainsTagPruningRule(), 
				new TreeLeafFinalizer());
	}
	
	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		
		return AnalyzerConfig.getStanfordGenericAnalysisEngineList();
	}
	


}
