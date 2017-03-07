package it.unitn.nlpir.classifiers;

public interface ClassifierFactory {
	public Classifier createClassifier(String path);
}
