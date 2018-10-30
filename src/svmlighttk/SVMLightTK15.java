package svmlighttk;

import it.unitn.nlpir.classifiers.Classifier;

import java.io.File;

/**
 * 
 * Modified line in svm_common.h
 * 
 * #define MAX_NUMBER_OF_PAIRS 50000*10
 * 
 * Max number of pairs is higher because of the error:
 * "The number of identical parse nodes exceeds the current capacity"
 * 
 * This happened for some long paragraphs
 * 
 */

/**
 * 
 * Wrapper class for the native library svmlight_tk.so
 * All the programs which use this class should be invoked
 * giving more memory to the Java Virtual Machine:
 * 
 * -Xss128m
 * 
 */
public class SVMLightTK15 implements Classifier {
	
	/**
	 * svmlight_tk.so path
	 */

	public static final String SVMLIGHT_TK_RELATIVE_PATH = "tools/SVM-Light-1.5-rer/svmlight_tk.so";
	public static final String SVMLIGHT_TK_RELATIVE_PATH_LINUX = "tools/SVM-Light-1.5-rer/svmlight_tk.so";
	public static final String SVMLIGHT_TK_RELATIVE_PATH_WIN = "tools/SVM-Light-1.5-rer/svmlight_tk.dll";
	public static final String SVMLIGHT_TK;
	
	static {
		String modelPath = SVMLIGHT_TK_RELATIVE_PATH_LINUX;
		String path = new File(modelPath).getAbsolutePath();
		SVMLIGHT_TK = path;
		System.load(SVMLIGHT_TK);
	}
																			
	final private int modelHandle;
	final private double modelThreshold;
	
	private static native int load_model(String modelFile);
	private static native double get_threshold();
	private static native double classify_instance(int modelNumber, String instance);

	/**
	 * Load a model from file
	 * @param modelFile
	 */
	public SVMLightTK15(String modelFile) {
		//System.out.println("MODELFILE:"+modelFile);
		this.modelHandle = load_model(modelFile);
		this.modelThreshold = get_threshold();
	}

	/**
	 * Classify an instance (dependency tree string)
	 * @param instance
	 * @return the classification confidence
	 */
	@Override
	public double classify(String instance) {
		
		return classify_instance(this.modelHandle, instance);
		
	}

	/**
	 * Get the threshold of the model
	 * @return the threshold of the model
	 */
	public double getThreshold() {
		return this.modelThreshold;
	}

	
	
	public static Classifier newInstance(String path) {
		return new SVMLightTK15(path);
	}
}
