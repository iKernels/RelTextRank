package it.unitn.nlpir.system.reranker;

import it.unitn.nlpir.util.Pair;

import java.util.Iterator;

import svmlighttk.SVMLightTK;

public class Reranker {
	
	private SVMLightTK classifier;
	private RankedList<String, Double> list;
	
	public Reranker(String modelFile) {
		this.classifier = new SVMLightTK(modelFile);
		this.list = new RankedList<String, Double>();
	}
	
	public Double rankExample(String example) {
		Double score = this.classifier.classify(example);
		this.list.add(example, score);
		return score;
	}
	
	public Iterator<Pair<String, Double>> getRerankedList() {
		return this.list.iterator();
	}
	
	public void clear() {
		this.list.clear();
	}
}
