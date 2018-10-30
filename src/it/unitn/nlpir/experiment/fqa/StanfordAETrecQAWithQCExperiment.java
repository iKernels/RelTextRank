package it.unitn.nlpir.experiment.fqa;

import java.io.File;
import java.util.Properties;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.uima.AnalysisEngineList;

public class StanfordAETrecQAWithQCExperiment extends TrecQAWithQCExperiment  {
	protected final String MODELS_FILE = "data/question-classifier/const-fine";
	
	protected final String KELP_MODELS_FILE = "data/question-classifier/kelp-models/kelp_sstbow_coarse.model";
	
	


	
	public StanfordAETrecQAWithQCExperiment(String configFile) {
		super(configFile);
		
	}
	
	public StanfordAETrecQAWithQCExperiment() {
		super();
		System.out.println(this.doFocusMatch);
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
			return AnalyzerConfig.getStanfordQAAnalysisEngineListWithKelpQC(KELP_MODELS_FILE, 
				"it.unitn.nlpir.tree.ConstituencyTreeBuilder","it.unitn.nlpir.tree.TreeLeafFinalizer");
		else if (this.readCategoriesFromFile)
			return AnalyzerConfig.getStanfordQAAnalysisEngineListWithQCFromFile(this.questionClassifierModelsFolder);
		else 
			//if the questionClassifierModelsFolder parameter is a folder, then we assume that it is a folder with SVMLight-TK models,
			//otherwise, we assume it to be a kelp model
			if (new File(this.questionClassifierModelsFolder).isDirectory())
				return AnalyzerConfig.getStanfordQAAnalysisEngineList(this.questionClassifierModelsFolder, 
						this.questionClassifierTreeBuilderName,this.questionClassifierLeafFinalizerName);
			else
				return AnalyzerConfig.getStanfordQAAnalysisEngineListWithKelpQC(this.questionClassifierModelsFolder, 
						this.questionClassifierTreeBuilderName,this.questionClassifierLeafFinalizerName);
	}

}