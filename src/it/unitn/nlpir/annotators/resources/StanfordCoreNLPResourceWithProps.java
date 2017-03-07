package it.unitn.nlpir.annotators.resources;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public interface StanfordCoreNLPResourceWithProps extends StanfordCoreNLPResource {
	public StanfordCoreNLP getPipeline(Properties p);
}
