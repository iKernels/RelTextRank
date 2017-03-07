package it.unitn.nlpir.classifiers;

import svmlighttk.SVMLightTK;

public class SVMLightTKClassifierFactory implements ClassifierFactory {

	@Override
	public Classifier createClassifier(String path) {
		return new SVMLightTK(path);
	}

}
