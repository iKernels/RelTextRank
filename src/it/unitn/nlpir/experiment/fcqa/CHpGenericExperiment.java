package it.unitn.nlpir.experiment.fcqa;



import java.util.Properties;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.fqa.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.tree.PosChunkFullTreeBuilder;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * CH + V + FC_thres + QT, Stanford preprocessing pipeline
 * @author IKernels group
 *
 */
public class CHpGenericExperiment extends StanfordAETrecQAWithQCExperiment {
	
	
	public CHpGenericExperiment(String configFile) {
		super(configFile);
	}
	
	public CHpGenericExperiment(String configFile, Properties p) {
		super(configFile,p);
	}
	
	public CHpGenericExperiment(Properties p) {
		super(p);
	}
	
	
	public CHpGenericExperiment() {
		super();
	}
	
	protected void setupProjector() {
		this.projector = Projectors.getProjector(new PosChunkFullTreeBuilder());
	}
	
	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getStanfordGenericAnalysisEngineList();
	}
	
}
