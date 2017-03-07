package it.unitn.nlpir.annotators.resources;

import java.util.Properties;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordCoreNLPResourceWithPropsImpl
	implements StanfordCoreNLPResourceWithProps, SharedResourceObject {

	private StanfordCoreNLP pipeline;
	
	@Override
	public StanfordCoreNLP getPipeline(Properties props) {
		if(this.pipeline == null) {
		//	Properties props = new Properties();    
		//	props.put("annotators", annotatorsList);    
		    this.pipeline = new StanfordCoreNLP(props);
		}
		return this.pipeline;
	}
	
	@Override
	public void load(DataResource arg0) throws ResourceInitializationException {
		
	}

	@Override
	public StanfordCoreNLP getPipeline(String annotatorsList) {
		// TODO Auto-generated method stub
		return null;
	}

}
