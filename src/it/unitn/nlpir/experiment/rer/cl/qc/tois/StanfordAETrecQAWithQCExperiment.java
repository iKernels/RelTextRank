package it.unitn.nlpir.experiment.rer.cl.qc.tois;

import java.util.Properties;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.Experiment;
import it.unitn.nlpir.uima.AnalysisEngineList;
public class StanfordAETrecQAWithQCExperiment extends TrecQAWithQCExperiment implements Experiment {
	protected final String MODELS_FILE = "data/question-classifier/const-fine";
	

	
	public StanfordAETrecQAWithQCExperiment(String configFile) {
		super(configFile);
	}
	
	public StanfordAETrecQAWithQCExperiment() {
		super();
	}
	
	public StanfordAETrecQAWithQCExperiment(Properties p) {
		super(p);
	}
	
	public StanfordAETrecQAWithQCExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public StanfordAETrecQAWithQCExperiment(int mode) {
		super(mode);
	}
	
	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		if (this.questionClassifierModelsFolder==null)
			return AnalyzerConfig.getStanfordQAAnalysisEngineList(MODELS_FILE, 
				"it.unitn.nlpir.tree.ConstituencyTreeBuilder","it.unitn.nlpir.tree.TreeLeafFinalizer");
		//logger.info("use SPTK Library: {}", this.useSPTKLibrary);
		if (this.useSPTKLibrary)
			return AnalyzerConfig.getStanfordQAAnalysisEngineListWithSPTKQC(this.questionClassifierModelsFolder, 
					this.questionClassifierTreeBuilderName,this.questionClassifierLeafFinalizerName);
		else if (this.readCategoriesFromFile)
			return AnalyzerConfig.getStanfordQAAnalysisEngineListWithQCFromFile(this.questionClassifierModelsFolder);
		else
			return AnalyzerConfig.getStanfordQAAnalysisEngineList(this.questionClassifierModelsFolder, 
					this.questionClassifierTreeBuilderName,this.questionClassifierLeafFinalizerName);
	}

}
