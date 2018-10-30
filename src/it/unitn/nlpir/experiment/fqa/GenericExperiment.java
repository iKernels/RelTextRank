package it.unitn.nlpir.experiment.fqa;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import it.unitn.nlpir.experiment.util.ExperimentComponentFactory;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;

/**
 *All the parameters of this experiment are described externally, nothing is predefined
* @author IKernels group
 *
 */
public class GenericExperiment extends StanfordAETrecQAWithQCExperiment {
	protected static final Logger logger = LoggerFactory.getLogger(GenericExperiment.class);
	
	protected String[] nodeMatcherParameterFiles;
	protected String treeBuilderClassName;
	protected String treePostprocessorClassName;
	protected Properties p;
	public GenericExperiment(String configFile) {
		super(configFile);
	}
	
	public GenericExperiment(String configFile, Properties p) {
		super(configFile, p);
		this.p = p;
	}
	
	public GenericExperiment(Properties p) {
		super(p);
		this.p = p;
	}
	
	public GenericExperiment() {
		super();
	}
	
	protected void setupProjector() {
		logger.info(String.format("pruningRay=%d", pruningRay));
		
		try {
			this.projector = Projectors.getParametrizedProjector(ExperimentComponentFactory.getTreeBuilder(treeBuilderClassName), 
					pruningRay,  new StartsWithOrContainsTagPruningRule(), 
					ExperimentComponentFactory.getTreePostprocessor(treePostprocessorClassName), 
					nodeMatcherParameterFiles,p);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setVariablesFromProperties(Properties prop) {
		this.p = prop;
		super.setVariablesFromProperties(prop);
		// get the property value and print it out
		String nodeMatcherParameterFiles = prop.getProperty("nodeMatcherParameterFiles");
		if (!Strings.isNullOrEmpty(nodeMatcherParameterFiles)) {
			
			this.nodeMatcherParameterFiles = nodeMatcherParameterFiles.split(",");
		}

		// get the property value and print it out
		String treePostprocessorClassName = prop.getProperty("treePostprocessorClassName");
		if (!Strings.isNullOrEmpty(treePostprocessorClassName)) {
			this.treePostprocessorClassName = treePostprocessorClassName;
		}	
		
		// get the property value and print it out
		String treeBuilderClassName = prop.getProperty("treeBuilderClassName");
		if (!Strings.isNullOrEmpty(treeBuilderClassName)) {
			this.treeBuilderClassName = treeBuilderClassName;
		}
		logger.info("Experiment settings: ");
		logger.info("nodeMatcherParameterFiles = {}", StringUtils.join(this.nodeMatcherParameterFiles,","));
		logger.info("treePostprocessorClassName = {}", this.treePostprocessorClassName);
		logger.info("treeBuilderClassName = {}", this.treeBuilderClassName);
		
	}
	

}
