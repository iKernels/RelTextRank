package it.unitn.nlpir.classifiers;

public class PTKClassifierFactory implements ClassifierFactory {

	@Override
	public Classifier createClassifier(String path) {
		return new PTKClassifier(path);
	}

}
