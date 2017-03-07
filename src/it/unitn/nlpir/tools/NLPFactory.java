package it.unitn.nlpir.tools;


import java.io.File;
import java.io.FilenameFilter;

import it.unitn.nlpir.classifiers.PTKClassifierFactory;
import it.unitn.nlpir.classifiers.SPTKSVMLightTKClassifierFactory;
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
	private static OneVsAllClassifier sstBowQuestionClassifier;

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
	
	public static OneVsAllClassifier getPTKQuestionClassifier() {
		if(sstBowQuestionClassifier == null) {
			String modelsPath = "data/question-classifier/models-ptk/";
			sstBowQuestionClassifier = new OneVsAllClassifier(new PTKClassifierFactory());
			sstBowQuestionClassifier.addModel("ABBR", modelsPath + "ABBR.model");
			sstBowQuestionClassifier.addModel("DESC", modelsPath + "DESC.model");
			sstBowQuestionClassifier.addModel("ENTY", modelsPath + "ENTY.model");
			sstBowQuestionClassifier.addModel("HUM", modelsPath + "HUM.model");
			sstBowQuestionClassifier.addModel("LOC", modelsPath + "LOC.model");
			sstBowQuestionClassifier.addModel("NUM", modelsPath + "NUM.model");
		}
		return sstBowQuestionClassifier;
	}
	
	public static OneVsAllClassifier getSSTBowQuestionClassifier() {
		String modelsPath = "data/question-classifier/models-sst-bow/";
		OneVsAllClassifier questionClassifier = new OneVsAllClassifier(new SVMLightTKClassifierFactory());
		questionClassifier.addModel("ABBR", modelsPath + "ABBR.model");
		questionClassifier.addModel("DESC", modelsPath + "DESC.model");
		questionClassifier.addModel("ENTY", modelsPath + "ENTY.model");
		questionClassifier.addModel("HUM", modelsPath + "HUM.model");
		questionClassifier.addModel("LOC", modelsPath + "LOC.model");
		questionClassifier.addModel("NUM", modelsPath + "NUM.model");
		return questionClassifier;
	}
	
	public static OneVsAllClassifier getSSTBowQuestionFineNumDurClassifier() {
		//String modelsPath = "data/question-classifier/models-sst-bow-finewithdur-strat/";
		//String modelsPath = "data/question-classifier/models-sst-bow-finewithdur/";
		String modelsPath = "data/question-classifier/models-sst-bow-finewithdur/";
		OneVsAllClassifier questionClassifier = new OneVsAllClassifier(new SVMLightTKClassifierFactory());
		questionClassifier.addModel("ABBR", modelsPath + "ABBR.model");
		questionClassifier.addModel("DESC", modelsPath + "DESC.model");
		questionClassifier.addModel("ENTY", modelsPath + "ENTY.model");
		questionClassifier.addModel("HUM", modelsPath + "HUM.model");
		questionClassifier.addModel("LOC", modelsPath + "LOC.model");
		questionClassifier.addModel("DURATION", modelsPath + "DURATION.model");
		questionClassifier.addModel("QUANTITY", modelsPath + "QUANTITY.model");
		questionClassifier.addModel("CURRENCY", modelsPath + "CURRENCY.model");
		questionClassifier.addModel("DATE", modelsPath + "DATE.model");
		
		return questionClassifier;
	}
	
	public static OneVsAllClassifier getSSTBowQuestionFineNumDurClassifier(String modelsPath) {
		//String modelsPath = "data/question-classifier/models-sst-bow-finewithdur-strat/";
		//String modelsPath = "data/question-classifier/models-sst-bow-finewithdur/";
		
		OneVsAllClassifier questionClassifier = new OneVsAllClassifier(new SVMLightTKClassifierFactory());
		questionClassifier.addModel("ABBR", modelsPath + "ABBR.model");
		questionClassifier.addModel("DESC", modelsPath + "DESC.model");
		questionClassifier.addModel("ENTY", modelsPath + "ENTY.model");
		questionClassifier.addModel("HUM", modelsPath + "HUM.model");
		questionClassifier.addModel("LOC", modelsPath + "LOC.model");
		questionClassifier.addModel("DURATION", modelsPath + "DURATION.model");
		questionClassifier.addModel("QUANTITY", modelsPath + "QUANTITY.model");
		questionClassifier.addModel("CURRENCY", modelsPath + "CURRENCY.model");
		questionClassifier.addModel("DATE", modelsPath + "DATE.model");
		
		return questionClassifier;
	}
	
	
	public static OneVsAllClassifier getClassifiersFromFolder(String modelsPath) {
		//String modelsPath = "data/question-classifier/models-sst-bow-finewithdur-strat/";
		//String modelsPath = "data/question-classifier/models-sst-bow-finewithdur/";
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
	
	
	public static OneVsAllClassifier getSPTKClassifiersFromFolder(String modelsPath) {
		//String modelsPath = "data/question-classifier/models-sst-bow-finewithdur-strat/";
		//String modelsPath = "data/question-classifier/models-sst-bow-finewithdur/";
		File dir = new File(modelsPath);
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".model");
		    }
		});

		//SVMLightSPTK
		//OneVsAllClassifier questionClassifier = new OneVsAllClassifier(new SVMLightTKClassifierFactory());
		OneVsAllClassifier questionClassifier = new OneVsAllClassifier(new SPTKSVMLightTKClassifierFactory());
		for (File modelfile : files) {
			questionClassifier.addModel(modelfile.getName().replace(".model", ""), modelsPath +"/"+ modelfile.getName());
		}
	
		return questionClassifier;
	}
	
	
	
	public static OneVsAllClassifier getSSTBowStratQuestionClassifier() {
		String modelsPath = "data/question-classifier/models-sst-bow-stratified/";
		OneVsAllClassifier questionClassifier = new OneVsAllClassifier(new SVMLightTKClassifierFactory());
		questionClassifier.addModel("ABBR", modelsPath + "ABBR.model");
		questionClassifier.addModel("DESC", modelsPath + "DESC.model");
		questionClassifier.addModel("ENTY", modelsPath + "ENTY.model");
		questionClassifier.addModel("HUM", modelsPath + "HUM.model");
		questionClassifier.addModel("LOC", modelsPath + "LOC.model");
		questionClassifier.addModel("CURRENCY", modelsPath + "CURRENCY.model");
		questionClassifier.addModel("QUANTITY", modelsPath + "QUANTITY.model");
		questionClassifier.addModel("DATE", modelsPath + "DATE.model");
		return questionClassifier;
	}
	
	
	public static OneVsAllClassifier getSSTBowFineNumQuestionClassifier() {
		String modelsPath = "data/question-classifier/models-sst-bow/";
		OneVsAllClassifier questionClassifier = new OneVsAllClassifier(new SVMLightTKClassifierFactory());
		questionClassifier.addModel("ABBR", modelsPath + "ABBR.model");
		questionClassifier.addModel("DESC", modelsPath + "DESC.model");
		questionClassifier.addModel("ENTY", modelsPath + "ENTY.model");
		questionClassifier.addModel("HUM", modelsPath + "HUM.model");
		questionClassifier.addModel("LOC", modelsPath + "LOC.model");
		questionClassifier.addModel("CURRENCY", modelsPath + "CURRENCY.model");
		questionClassifier.addModel("QUANTITY", modelsPath + "QUANTITY.model");
		questionClassifier.addModel("DATE", modelsPath + "DATE.model");
		return questionClassifier;
	}
	
	
}
