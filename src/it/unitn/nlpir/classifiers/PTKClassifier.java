package it.unitn.nlpir.classifiers;

import it.unitn.kernels.ptk.SimplePTKClassifier;


public class PTKClassifier implements Classifier {
	
	private SimplePTKClassifier classifier;
	
	public PTKClassifier(String modelPath) {
		try {
			this.classifier = new SimplePTKClassifier(modelPath);
		} catch (Exception e) {
			System.out.println("Error instantiating PTKClassifier. Terminating...");
			System.exit(1);
		}
	}

	@Override
	public double classify(String line) {
		try {
			line = line.trim();

			String[] split = line.split("\\|BT\\|");
			String example = split[1].replaceAll("\\|ET\\|", "").trim();
			
			return this.classifier.classify(example);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Double.MIN_VALUE;
	}

}
