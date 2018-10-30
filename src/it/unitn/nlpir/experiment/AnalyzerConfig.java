package it.unitn.nlpir.experiment;

import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AnalysisEngineFactory;





import it.unitn.nlpir.annotators.FromFileQuestionClassifier;
import it.unitn.nlpir.annotators.PosChunkAnnotator;
import it.unitn.nlpir.annotators.QuestionClassifierWithCustomModels;
import it.unitn.nlpir.annotators.QuestionFocusAnnotator;
import it.unitn.nlpir.annotators.StanfordCoreNLPAnnotator;
import it.unitn.nlpir.annotators.StanfordCoreNLPWithPropsAnnotator;
import it.unitn.nlpir.uima.AnalysisEngineList;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
public class AnalyzerConfig {

	
	
	public static AnalysisEngineList getEmptyAnalysisEngineList() {
		return new AnalysisEngineList().addTypeSystemForCas("desc/PipelineTypeSystem.xml");
	}
	
	public static AnalysisEngineList getQAAnalysisEngineListWithStanfordProps() {
		return new AnalysisEngineList().addTypeSystemForCas("desc/PipelineTypeSystem.xml")
				.addAnalysisEngine(
						//StanfordCoreNLPAnnotator
						StanfordCoreNLPWithPropsAnnotator
						.getDescription("tokenize, ssplit, pos, lemma, ner, parse, dcoref"))
				.addAnalysisEngine("desc/TokenFilter.xml")
				.addAnalysisEngine("desc/IllinoisChunker.xml")
				.addAnalysisEngine("desc/PosChunkAnnotator.xml", PosChunkAnnotator.PARAM_LOWERCASE,
						true, PosChunkAnnotator.PARAM_LEAF_TEXT_TYPE, TokenTextGetterFactory.LEMMA);
	}
	
	public static AnalysisEngineList getStanfordQAAnalysisEngineListWithQCFromFile(String modelsFolder) {
		try {
			String descFolder = "desc";
			if (System.getProperty("resource.home")!=null)
				descFolder = System.getProperty("resource.home")+"/"+descFolder;
			return getStanfordGenericAnalysisEngineList()
				.addAnalysisEngine(AnalysisEngineFactory.createPrimitiveDescription(FromFileQuestionClassifier.class,
						FromFileQuestionClassifier.QUESTION_CATEGORIES_FILE, modelsFolder))
				.addAnalysisEngine(descFolder+"/QuestionFocusAnnotator.xml");
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static AnalysisEngineList getQAAnalysisEngineList() {
		return new AnalysisEngineList().addTypeSystemForCas("desc/PipelineTypeSystem.xml")
				//it.unitn.nlpir.questionclassifier.DataConversionFromLabel 
				.addAnalysisEngine(
						StanfordCoreNLPAnnotator
						//StanfordCoreNLPWithPropsAnnotator
						.getDescription("tokenize, ssplit, pos, lemma, ner, parse"))
				.addAnalysisEngine("desc/TokenFilter.xml")
				.addAnalysisEngine("desc/IllinoisChunker.xml")
				.addAnalysisEngine("desc/PosChunkAnnotator.xml", PosChunkAnnotator.PARAM_LOWERCASE,
						true, PosChunkAnnotator.PARAM_LEAF_TEXT_TYPE, TokenTextGetterFactory.LEMMA);
	}
	
	
	public static AnalysisEngineList getQAAnalysisEngineList(String modelsFolder, String treeBuilderClassName, 
			String treeLeafFinalizerClassName) {
		return new AnalysisEngineList().addTypeSystemForCas("desc/PipelineTypeSystem.xml")
				//it.unitn.nlpir.questionclassifier.DataConversionFromLabel 
				.addAnalysisEngine(
						StanfordCoreNLPAnnotator
						//StanfordCoreNLPWithPropsAnnotator
						.getDescription("tokenize, ssplit, pos, lemma, ner, parse"))
				.addAnalysisEngine("desc/TokenFilter.xml")
				.addAnalysisEngine("desc/IllinoisChunker.xml")
				.addAnalysisEngine("desc/PosChunkAnnotator.xml", PosChunkAnnotator.PARAM_LOWERCASE,
						true, PosChunkAnnotator.PARAM_LEAF_TEXT_TYPE, TokenTextGetterFactory.LEMMA);
	}
	

	
	public static AnalysisEngineList getStanfordGenericAnalysisEngineList() {
		System.out.println("IAMHERE!!!");
		String descFolder = "desc";
		System.out.println("Reading descriptors from "+descFolder);
		if (System.getProperty("resource.home")!=null)
			descFolder = System.getProperty("resource.home")+"/"+descFolder;
		System.out.println("Reading descriptors from "+descFolder);
		return new AnalysisEngineList().addTypeSystemForCas(descFolder+"/PipelineTypeSystem.xml")
				.addAnalysisEngine(
						StanfordCoreNLPAnnotator
						//StanfordCoreNLPWithPropsAnnotator
						.getDescription("tokenize, ssplit, pos, lemma, ner, parse"))
				.addAnalysisEngine(descFolder+"/TokenFilter.xml")
				.addAnalysisEngine(descFolder+"/IllinoisChunker.xml")
				.addAnalysisEngine(descFolder+"/PosChunkAnnotator.xml", PosChunkAnnotator.PARAM_LOWERCASE,
						true, PosChunkAnnotator.PARAM_LEAF_TEXT_TYPE, TokenTextGetterFactory.LEMMA);
	}
	
	public static AnalysisEngineList getStanfordWithPropsGenericAnalysisEngineList() {
		return new AnalysisEngineList().addTypeSystemForCas("desc/PipelineTypeSystem.xml")
				.addAnalysisEngine(
						StanfordCoreNLPWithPropsAnnotator
						//StanfordCoreNLPWithPropsAnnotator
						.getDescription("tokenize, ssplit, pos, lemma, ner, parse"))
				.addAnalysisEngine("desc/TokenFilter.xml")
				.addAnalysisEngine("desc/IllinoisChunker.xml")
				.addAnalysisEngine("desc/PosChunkAnnotator.xml", PosChunkAnnotator.PARAM_LOWERCASE,
						true, PosChunkAnnotator.PARAM_LEAF_TEXT_TYPE, TokenTextGetterFactory.LEMMA);
	}
	
	public static AnalysisEngineList getActiveQAAnalysisEngineList() {
		return new AnalysisEngineList().addTypeSystemForCas("desc/PipelineTypeSystem.xml")
				.addAnalysisEngine(
						StanfordCoreNLPAnnotator
						//StanfordCoreNLPWithPropsAnnotator
						.getDescription("tokenize, ssplit, pos, lemma, ner, parse"))
				.addAnalysisEngine("desc/TokenFilter.xml")
				.addAnalysisEngine("desc/IllinoisChunker.xml")
				.addAnalysisEngine("desc/PosChunkAnnotator.xml", PosChunkAnnotator.PARAM_LOWERCASE,
						true, PosChunkAnnotator.PARAM_LEAF_TEXT_TYPE, TokenTextGetterFactory.LEMMA);
	}
	
	
	
	
	public static AnalysisEngineList getQAAnalysisEngineListWithCoref() {
		return new AnalysisEngineList().addTypeSystemForCas("desc/PipelineTypeSystem.xml")
				.addAnalysisEngine(
						StanfordCoreNLPAnnotator
						//StanfordCoreNLPWithPropsAnnotator
						.getDescription("tokenize, ssplit, pos, lemma, ner, parse, dcoref"))
				.addAnalysisEngine("desc/TokenFilter.xml")
				.addAnalysisEngine("desc/IllinoisChunker.xml")
				.addAnalysisEngine("desc/PosChunkAnnotator.xml", PosChunkAnnotator.PARAM_LOWERCASE,
						true, PosChunkAnnotator.PARAM_LEAF_TEXT_TYPE, TokenTextGetterFactory.LEMMA);
	}
	
	public static AnalysisEngineList getQAAnalysisEngineListWithLHTParser() {
		return new AnalysisEngineList().addTypeSystemForCas("desc/PipelineTypeSystem.xml")
				.addTypeSystemForCas("desc/types/SentenceTokenTypeSystem.xml")
				.addTypeSystemForCas("desc/types/ParseTreePosTypeSystem.xml")
				.addAnalysisEngine(
						StanfordCoreNLPAnnotator
								//.getDescription("tokenize, ssplit, pos, lemma, ner, parse", "false"))
						.getDescription("tokenize, ssplit, pos, lemma, ner, parse"))
				.addAnalysisEngine("desc/pipelines/LTHParserAnnotator.xml")
				//.addAnalysisEngine("desc/LTHParserFastDescriptor.xml")
				//.addAnalysisEngine("desc/TokenFilter.xml")
				.addAnalysisEngine("desc/IllinoisChunker.xml")
				.addAnalysisEngine("desc/PosChunkAnnotator.xml", PosChunkAnnotator.PARAM_LOWERCASE,
						true, PosChunkAnnotator.PARAM_LEAF_TEXT_TYPE, TokenTextGetterFactory.LEMMA);
	}
	
	
	public static AnalysisEngineList getStanfordQAOnlyFocusAnalysisEngineList(String modelsFolder) {
		try {
			return getStanfordGenericAnalysisEngineList()
				.addAnalysisEngine(AnalysisEngineFactory.createPrimitiveDescription(QuestionClassifierWithCustomModels.class,
						QuestionClassifierWithCustomModels.MODELS_FOLDER, modelsFolder, 
							QuestionClassifierWithCustomModels.FINALIZE_LEAVES,true))
				.addAnalysisEngine("desc/QuestionFocusAnnotator.xml");
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static AnalysisEngineList getStanfordWithPropsQAAnalysisEngineList(String modelsFolder, String treeBuilderClassName, 
			String treeLeafFinalizerClassName) {
		try {
			
			return getStanfordWithPropsGenericAnalysisEngineList()
				.addAnalysisEngine(AnalysisEngineFactory.createPrimitiveDescription(QuestionClassifierWithCustomModels.class,
						QuestionClassifierWithCustomModels.MODELS_FOLDER, modelsFolder, 
						QuestionClassifierWithCustomModels.FINALIZE_LEAVES,true,
						QuestionClassifierWithCustomModels.TREE_BUILDER_CLASS_PARAM, treeBuilderClassName,
						QuestionClassifierWithCustomModels.LEAF_FINALIZER_CLASS_PARAM, treeLeafFinalizerClassName))
				.addAnalysisEngine("desc/QuestionFocusAnnotator.xml");
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	

	public static AnalysisEngineList getStanfordQAAnalysisEngineListWithKelpQC(String modelFile, String treeBuilderClassName, 
			String treeLeafFinalizerClassName) {
		try {
			
			AnalysisEngineList ae = getStanfordGenericAnalysisEngineList();
			ae.addAnalysisEngine(AnalysisEngineFactory.createPrimitiveDescription(QuestionFocusAnnotator.class,
					QuestionFocusAnnotator.CLASSIFIER_FRAMEWORK_TO_USE_PARAM, QuestionFocusAnnotator.KELP_NAME));
			
			if (modelFile!=null)
				ae.addAnalysisEngine(AnalysisEngineFactory.createPrimitiveDescription(QuestionClassifierWithCustomModels.class,
						QuestionClassifierWithCustomModels.MODELS_FOLDER, modelFile, 
						QuestionClassifierWithCustomModels.FINALIZE_LEAVES,true,
						QuestionClassifierWithCustomModels.TREE_BUILDER_CLASS_PARAM, treeBuilderClassName,
						QuestionClassifierWithCustomModels.LEAF_FINALIZER_CLASS_PARAM, treeLeafFinalizerClassName,
						QuestionClassifierWithCustomModels.CLASSIFIER_FRAMEWORK_TO_USE_PARAM, QuestionClassifierWithCustomModels.KELP_NAME));
			return ae;
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static AnalysisEngineList getStanfordQAAnalysisEngineList(String modelsFolder, String treeBuilderClassName, 
			String treeLeafFinalizerClassName) {
		try {
			AnalysisEngineList ae = getStanfordGenericAnalysisEngineList();
			ae.addAnalysisEngine(AnalysisEngineFactory.createPrimitiveDescription(QuestionFocusAnnotator.class));
			/*AnalysisEngineList ae = getStanfordGenericAnalysisEngineList()
				.addAnalysisEngine("desc/QuestionFocusAnnotator.xml");*/
			if (modelsFolder!=null)
				ae.addAnalysisEngine(AnalysisEngineFactory.createPrimitiveDescription(QuestionClassifierWithCustomModels.class,
						QuestionClassifierWithCustomModels.MODELS_FOLDER, modelsFolder, 
						QuestionClassifierWithCustomModels.FINALIZE_LEAVES,true,
						QuestionClassifierWithCustomModels.TREE_BUILDER_CLASS_PARAM, treeBuilderClassName,
						QuestionClassifierWithCustomModels.LEAF_FINALIZER_CLASS_PARAM, treeLeafFinalizerClassName));
			return ae;
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	
	/**
	 * Does only question classification
	 * @param modelsFolder
	 * @param treeBuilderClassName
	 * @param treeLeafFinalizerClassName
	 * @return
	 */
	public static AnalysisEngineList getOnlYQAAnalysisEngineList(String modelsFolder, String treeBuilderClassName, 
			String treeLeafFinalizerClassName) {
		try {
				return new AnalysisEngineList().addTypeSystemForCas("desc/PipelineTypeSystem.xml")
				.addAnalysisEngine(AnalysisEngineFactory.createPrimitiveDescription(QuestionClassifierWithCustomModels.class,
						QuestionClassifierWithCustomModels.MODELS_FOLDER, modelsFolder, 
						QuestionClassifierWithCustomModels.FINALIZE_LEAVES,true,
						QuestionClassifierWithCustomModels.TREE_BUILDER_CLASS_PARAM, treeBuilderClassName,
						QuestionClassifierWithCustomModels.LEAF_FINALIZER_CLASS_PARAM, treeLeafFinalizerClassName));
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public static AnalysisEngineList getStanfordQAAnalysisEngineListWithoutQC() {
		
			return getStanfordGenericAnalysisEngineList()
				.addAnalysisEngine("desc/QuestionFocusAnnotator.xml");
		
		//return null;
	}
	
	
	


	
	
}
