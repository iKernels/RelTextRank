package it.unitn.nlpir.annotators.resources;

import java.util.Properties;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordCoreNLPResourceImpl
	implements StanfordCoreNLPResource, SharedResourceObject {

	private StanfordCoreNLP pipeline;
	
	@Override
	public StanfordCoreNLP getPipeline(String annotatorsList) {
		if(this.pipeline == null) {
			Properties props = new Properties();    
			props.put("annotators", annotatorsList); 
			props.put("threads", "6");
		    this.pipeline = new StanfordCoreNLP(props);
		}
		return this.pipeline;
	}

	@Override
	public void load(DataResource arg0) throws ResourceInitializationException {
		
	}

}
