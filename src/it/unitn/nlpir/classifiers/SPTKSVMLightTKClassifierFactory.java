package it.unitn.nlpir.classifiers;

import svmlighttk.SVMLightSPTK;

public class SPTKSVMLightTKClassifierFactory implements ClassifierFactory {

	@Override
	public Classifier createClassifier(String path) {
		return new SVMLightSPTK(path);
	}

}
