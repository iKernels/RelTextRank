package it.unitn.nlpir.tools;


import java.io.File;
import java.io.FilenameFilter;

import it.unitn.nlpir.classifiers.SVMLightTKClassifierFactory;


import org.tartarus.snowball.ext.englishStemmer;

import edu.stanford.nlp.process.Morphology;

/**
 * 
 * Factory for centralizing the instantiation of NLP components
 *
 */

public class NLPFactory {
	
	private static Morphology lemmatizer;
	private static englishStemmer stemmer;


	public static Morphology getLemmatizer() {
		if(lemmatizer == null) {
			lemmatizer = new Morphology();
		}
		return lemmatizer;
	}
	
	public static englishStemmer getEnglishStemmer() {
		if(stemmer == null) {
			stemmer = new englishStemmer();
		}
		return stemmer;
	}
	
	
	
	
	public static OneVsAllClassifier getClassifiersFromFolder(String modelsPath) {

		File dir = new File(modelsPath);
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".model");
		    }
		});

		//SVMLightSPTK
		OneVsAllClassifier questionClassifier = new OneVsAllClassifier(new SVMLightTKClassifierFactory());
		//OneVsAllClassifier questionClassifier = new OneVsAllClassifier(new SPTKSVMLightTKClassifierFactory());
		for (File modelfile : files) {
			questionClassifier.addModel(modelfile.getName().replace(".model", ""), modelsPath +"/"+ modelfile.getName());
		}
	
		return questionClassifier;
	}
	
	
	
	
}
