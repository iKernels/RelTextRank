package it.unitn.nlpir.experiment.rer.cl.qc.tois;

import it.unitn.nlpir.experiment.Experiment;
import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.projectors.Projector;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.google.common.base.Strings;

public class TrecQAWithQCExperiment extends TrecQAExperiment implements Experiment {
	//private final Logger logger = LoggerFactory.getLogger(TrecQAWithQCExperiment.class);
	protected String questionClassifierModelsFolder;
	protected String questionClassifierLeafFinalizerName;
	protected String questionClassifierTreeBuilderName;
	protected boolean useSPTKLibrary=false; //to be removed is future
	protected boolean readCategoriesFromFile; //to be removed in fuure. In this case, the file from which to read the data is specified by questionClassifierModelsFolder 
	
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



	protected void loadProperties(String configFile) {
		super.loadProperties(configFile);
		System.out.println(logger);
		logger.info("reading new properties");
		Properties prop = new Properties();

		try {
			// load a properties file from class path, inside static method
			prop.load(new FileInputStream(configFile));
			
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
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		logger.info("Experiment settings: ");
		logger.info("questionClassifierModelsFolder = {}", this.questionClassifierModelsFolder);
		logger.info("questionClassifierLeafFinalizerName = {}", this.questionClassifierLeafFinalizerName);
		logger.info("questionClassifierTreeBuilderName = {}", this.questionClassifierTreeBuilderName);
		logger.info("useSPTKLibrary = {}", this.useSPTKLibrary);
		logger.info("readCategoriesFromFile = {}", this.readCategoriesFromFile);
	}

}
