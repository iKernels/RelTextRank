package it.unitn.nlpir.experiment.rer.cl.noqc.tois;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.experiment.rer.cl.qc.tois.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.uima.AnalysisEngineList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StanfordAENoQCExperiment extends TrecQAExperiment {
	private final Logger logger = LoggerFactory.getLogger(StanfordAETrecQAWithQCExperiment.class);

	
	
	public StanfordAENoQCExperiment(String configFile) {
		super(configFile);
		logger.debug(configFile);
	}
	
	public StanfordAENoQCExperiment() {
		super();
	}
	
	public StanfordAENoQCExperiment(int mode) {
		super(mode);
	}
	
	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getStanfordQAAnalysisEngineListWithoutQC();
	}
}
