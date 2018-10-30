package it.unitn.nlpir.experiment.fcqa;



import java.util.Properties;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.fqa.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.tree.ConstituencyTreeBuilder;
import it.unitn.nlpir.uima.AnalysisEngineList;

/**
 * CH + V + FC_thres + QT, Stanford preprocessing pipeline
 * @author IKernels group
 *
 */
public class ConstGenericExperiment extends StanfordAETrecQAWithQCExperiment {
	
	
	public ConstGenericExperiment(String configFile) {
		super(configFile);
	}
	
	public ConstGenericExperiment(String configFile, Properties p) {
		super(configFile,p);
	}
	
	public ConstGenericExperiment(Properties p) {
		super(p);
	}
	
	
	public ConstGenericExperiment() {
		super();
	}
	
	protected void setupProjector() {
		this.projector = Projectors.getProjector(new ConstituencyTreeBuilder());
	}
	
	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getStanfordGenericAnalysisEngineList();
	}
	
}
