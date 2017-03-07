package it.unitn.nlpir.annotators.resources;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public interface StanfordCoreNLPResource {
	public StanfordCoreNLP getPipeline(String annotatorsList);
}
