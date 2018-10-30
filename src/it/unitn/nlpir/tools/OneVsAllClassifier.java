package it.unitn.nlpir.tools;

import it.unitn.nlpir.classifiers.Classifier;
import it.unitn.nlpir.classifiers.ClassifierFactory;
import it.unitn.nlpir.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Question classifier using SVMLightTK and question category models
 * The question categories are: ABBR, DESC, ENTY, HUM, LOC, NUM
 * 
 * The class can instantiate new models with their associated
 * identifier/category and add them in a pool of classifiers.
 * 
 * Then the question can be classified using the classifiers in
 * the pool, and the returned category is the one yielding the highest
 * confidence value.
 *
 */
public class OneVsAllClassifier implements IOneVsAllClassifier{
	
	private Map<String, Classifier> models;
	private ClassifierFactory factory;
	
	public OneVsAllClassifier(ClassifierFactory factory) {
		this.models = new HashMap<>();
		this.factory = factory;
	}
	
	/**
	 * Add a model in the pool
	 * The id of the classifier is the id returned on question classification if
	 * the classifier yields the highest confidence
	 * @param id the id of the classifier
	 * @param path the path of the classifier model
	 */
	public void addModel(String id, String path) {
		Classifier model = this.factory.createClassifier(path);
		this.models.put(id, model);
	}
	
	/**
	 * Performs the One-vs-All classification
	 * @param question the question to classify. This is an example of its format
	 * "|BT| (ROOT (SBARQ (WHNP (WHADJP (WRB How)(JJ many))(NNS people))(SQ (VP (VBP live)(PP (IN in)(NP (NNP Chile)))))(. ?))) |ET|"
	 * @return the id of the classifier giving the highest confidence
	 */
	public String getMostConfidentModel(String instance) {
		String cat = "";
		double currentConfidence = Double.NEGATIVE_INFINITY;
		
		for(String id : this.models.keySet()) {
			Classifier svm = this.models.get(id);
			double confidence = svm.classify(instance);
			if(confidence > currentConfidence) {
				currentConfidence = confidence;
				cat = id;
			}
		}
		
		return cat;
	}
	
	/**
	 * Performs the One-vs-All classification
	 * @param question the question to classify. This is an example of its format
	 * "|BT| (ROOT (SBARQ (WHNP (WHADJP (WRB How)(JJ many))(NNS people))(SQ (VP (VBP live)(PP (IN in)(NP (NNP Chile)))))(. ?))) |ET|"
	 * @return the id of the classifier giving the highest confidence
	 */
	public Pair<String,Double> getMostConfidentModelWithConfidence(String instance) {
		String cat = "";
		double currentConfidence = Double.NEGATIVE_INFINITY;
		
		
		for(String id : this.models.keySet()) {
			Classifier svm = this.models.get(id);
			double confidence = svm.classify(instance);
			if(confidence > currentConfidence) {
				currentConfidence = confidence;
				cat = id;
			}
		}
		
		return new Pair<String,Double>(cat,currentConfidence);
	}
	
	/**
	 * Performs the One-vs-All classification
	 * @param question the question to classify. This is an example of its format
	 * "|BT| (ROOT (SBARQ (WHNP (WHADJP (WRB How)(JJ many))(NNS people))(SQ (VP (VBP live)(PP (IN in)(NP (NNP Chile)))))(. ?))) |ET|"
	 * @return the id of the classifier giving the highest confidence
	 */
	public List<Pair<String,Double>> getAllLabelsWithConfidences(String instance) {

		
		List<Pair<String,Double>>labConf = new  ArrayList<Pair<String,Double>>();
		for(String id : this.models.keySet()) {
			Classifier svm = this.models.get(id);
			double confidence = svm.classify(instance);
			labConf.add(new Pair<String,Double>(id, confidence));
			/*if(confidence > currentConfidence) {
				currentConfidence = confidence;
				cat = id;
			}*/
		}
		
		return labConf;
	}
}
