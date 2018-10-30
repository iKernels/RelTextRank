package it.unitn.nlpir.experiment.fqa;

import it.unitn.nlpir.experiment.Experiment;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.projectors.Projector;

import java.util.Properties;

import com.google.common.base.Strings;

import org.slf4j.LoggerFactory;

public class TrecQAWithQCExperiment extends TrecQAExperiment implements Experiment {
	
	
	
	protected String questionClassifierModelsFolder;
	protected String questionClassifierLeafFinalizerName;
	protected String questionClassifierTreeBuilderName;
	protected boolean useSPTKLibrary=false; //to be removed is future
	protected boolean readCategoriesFromFile; //to be removed in fuure. In this case, the file from which to read the data is specified by questionClassifierModelsFolder 
	
	protected boolean doFocusMatch=true;
	protected boolean typeFocusMatch=true;
	protected boolean markFocusInQuestion=true;
	
	
	public TrecQAWithQCExperiment(String configFile) {
		super(configFile);
	}
	
	public TrecQAWithQCExperiment(Properties p) {
		super(p);
	}
	
	public TrecQAWithQCExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public TrecQAWithQCExperiment() {
		super();
	}
	
	public TrecQAWithQCExperiment(int mode) {
		
		super(mode);
	}
	
	public void setMode(int mode) {
		this.mode = mode;
		
	}
	
	public Projector getProjector() {
		return this.projector;
	}
	
	protected void initializeDefaultValues() {
		this.doFocusMatch = true;
		this.typeFocusMatch = true;
		this.markFocusInQuestion = true;
		logger = LoggerFactory.getLogger(TrecQAWithQCExperiment.class);
	}


	protected void setVariablesFromProperties(Properties prop)  {
		
		
		super.setVariablesFromProperties(prop);
		// get the property value and print it out
		String questionClassifierModelsFolder = prop.getProperty("questionClassifierModelsFolder");
		if (!Strings.isNullOrEmpty(questionClassifierModelsFolder)) {
			this.questionClassifierModelsFolder = questionClassifierModelsFolder;
		}
		String questionClassifierLeafFinalizerName = prop.getProperty("questionClassifierLeafFinalizerName");
		if (!Strings.isNullOrEmpty(questionClassifierLeafFinalizerName)) {
			this.questionClassifierLeafFinalizerName = questionClassifierLeafFinalizerName;
		}
		String questionClassifierTreeBuilderName = prop.getProperty("questionClassifierTreeBuilderName");
		if (!Strings.isNullOrEmpty(questionClassifierTreeBuilderName)) {
			this.questionClassifierTreeBuilderName = questionClassifierTreeBuilderName;
		}
		String useSPTKLibrary = prop.getProperty("useSPTKLibrary");
		if (!Strings.isNullOrEmpty(useSPTKLibrary)) {
			this.useSPTKLibrary = Boolean.parseBoolean(useSPTKLibrary);
		}
		String readCategoriesFromFile = prop.getProperty("readCategoriesFromFile");
		if (!Strings.isNullOrEmpty(readCategoriesFromFile)) {
			this.readCategoriesFromFile = Boolean.parseBoolean(readCategoriesFromFile);
		}
		else{
			this.readCategoriesFromFile = false;
		}
		
		String doFocusMatch = prop.getProperty("doFocusMatch");
		if (!Strings.isNullOrEmpty(doFocusMatch)) {
			this.doFocusMatch = Boolean.parseBoolean(doFocusMatch);
		}
		else
			this.doFocusMatch = true;
		String typeFocusMatch = prop.getProperty("typeFocusMatch");
		if (!Strings.isNullOrEmpty(typeFocusMatch)) {
			this.typeFocusMatch = Boolean.parseBoolean(typeFocusMatch);
		}
		else
			this.typeFocusMatch = true;
		String markFocusInQuestion = prop.getProperty("markFocusInQuestion");
		
		if (!Strings.isNullOrEmpty(markFocusInQuestion)) {
			this.markFocusInQuestion = Boolean.parseBoolean(markFocusInQuestion);
		}
		else
			this.markFocusInQuestion = true;
		logger.info("Experiment settings: ");
		logger.info("questionClassifierModelsFolder = {}", this.questionClassifierModelsFolder);
		logger.info("questionClassifierLeafFinalizerName = {}", this.questionClassifierLeafFinalizerName);
		logger.info("questionClassifierTreeBuilderName = {}", this.questionClassifierTreeBuilderName);
		logger.info("useSPTKLibrary = {}", this.useSPTKLibrary);
		logger.info("readCategoriesFromFile = {}", this.readCategoriesFromFile);
		logger.info("doFocusMatch = {}", this.doFocusMatch);
		logger.info("typeFocusMatch = {}", this.typeFocusMatch);
		logger.info("markFocusInQuestion = {}", this.markFocusInQuestion);
	}

}
