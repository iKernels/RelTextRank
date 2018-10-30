package it.unitn.nlpir.experiment.kernmat;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.fqa.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 
* @author IKernels group
 *
 */
public class CHKernMatExperiment extends StanfordAETrecQAWithQCExperiment {
	protected static final Logger logger = LoggerFactory.getLogger(CHKernMatExperiment.class);
	public CHKernMatExperiment(String configFile) {
		super(configFile);
	}
	
	public CHKernMatExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public CHKernMatExperiment(Properties p) {
		super(p);
	}
	
	public CHKernMatExperiment() {
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
