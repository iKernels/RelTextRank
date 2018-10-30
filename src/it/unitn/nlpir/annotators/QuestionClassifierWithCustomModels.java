package it.unitn.nlpir.annotators;

import it.unitn.nlpir.tools.IOneVsAllClassifier;
import it.unitn.nlpir.tools.KelpClassifierWrapper;
import it.unitn.nlpir.tools.NLPFactory;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.TreeBuilder;
import it.unitn.nlpir.types.QuestionClass;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.descriptor.TypeCapability;
import org.uimafit.util.JCasUtil;

import arq.cmdline.ModDataset;
import edu.stanford.nlp.trees.Tree;
import svmlighttk.SVMTKExample;
import it.unitn.nlpir.types.ConstituencyTree;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.kelp.KelpUtilities;

@TypeCapability(inputs = {  }, outputs = { "it.unitn.nlpir.types.QuestionClass" })
public class QuestionClassifierWithCustomModels extends JCasAnnotator_ImplBase {
	static final Logger logger = LoggerFactory.getLogger(QuestionClassifierWithCustomModels.class);
	protected IOneVsAllClassifier classifier;
	public static final String MODELS_FOLDER = "modelsFolder";
	public static final String FINALIZE_LEAVES= "finalizeleaves";
	
	public static final String TREE_BUILDER_CLASS_PARAM= "treebuilder";
	
	public static final String LEAF_FINALIZER_CLASS_PARAM= "leaffinalizer";
	
	public static final String CLASSIFIER_FRAMEWORK_TO_USE_PARAM = "classifierToUse";
	public static final String KELP_NAME = "kelp";
	
	
	protected String modelsFolder = null;
	protected boolean finalizeTreeLeavesToText = false;
	protected boolean useKelp = false;
	
	protected TreeBuilder builder = null;
	protected ITreePostprocessor leafFinalizer =null;
	
	public static final String DEFAULT_TREE_BUILDER_CLASS ="it.unitn.nlpir.tree.ConstituencyTreeBuilder";
	public static final String DEFAULT_TREE_FINALIZER_CLASS ="it.unitn.nlpir.tree.TreeLeafFinalizer";
	
	protected IOneVsAllClassifier getClassifier(){
		String base = System.getProperty("resource.home")==null ? "" : System.getProperty("resource.home")+"/";
		if (this.useKelp) {
			logger.info("Reading KELP classifier from: "+base+modelsFolder);
			return new KelpClassifierWrapper(base+modelsFolder);
		}
		else {
			logger.info("Reading SVMLight clasisifers from: "+base+modelsFolder);
			return NLPFactory.getClassifiersFromFolder(base+modelsFolder);
		}
	}
	
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {		
		super.initialize(aContext);
		this.modelsFolder = (String)getContext().getConfigParameterValue(MODELS_FOLDER);
		
		
		if (getContext().getConfigParameterValue(CLASSIFIER_FRAMEWORK_TO_USE_PARAM)!=null){
			this.useKelp  = ((String)getContext().getConfigParameterValue(CLASSIFIER_FRAMEWORK_TO_USE_PARAM)).equals(KELP_NAME);
		}
		
		if (getContext().getConfigParameterValue(FINALIZE_LEAVES)!=null){
			this.finalizeTreeLeavesToText = (Boolean)getContext().getConfigParameterValue(FINALIZE_LEAVES);
			String leafFinalizerClassName = DEFAULT_TREE_FINALIZER_CLASS; 

			
			if ((getContext().getConfigParameterValue(LEAF_FINALIZER_CLASS_PARAM)!=null)){
				leafFinalizerClassName = (String) getContext().getConfigParameterValue(LEAF_FINALIZER_CLASS_PARAM);
			}
			Class<?> c = null;
			try {
				c = Class.forName(leafFinalizerClassName);
				
				leafFinalizer = (ITreePostprocessor) c.newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		String treeBuilderClassName = DEFAULT_TREE_BUILDER_CLASS;
		if ((getContext().getConfigParameterValue(TREE_BUILDER_CLASS_PARAM)!=null)){
			treeBuilderClassName = (String) getContext().getConfigParameterValue(TREE_BUILDER_CLASS_PARAM);
		}
		Class<?> c = null;
		try {
			c = Class.forName(treeBuilderClassName);
			builder = (TreeBuilder) c.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		
		// Return early if no ConstituencyTree is present
		if(!JCasUtil.exists(cas, ConstituencyTree.class)) return;

		if(this.classifier == null) {
			this.classifier = getClassifier();
		}
		ConstituencyTree treeAnnot = JCasUtil.selectSingle(cas, ConstituencyTree.class);
		Tree tree = builder.getTree(cas);
		
		if (this.finalizeTreeLeavesToText)
			leafFinalizer.process(tree, cas);
			//TreeUtil.finalizeTreeLeaves(cas, constituencyTree, TokenTextGetterFactory.TEXT);
		
		logger.info(TreeUtil.serializeTree(tree));
		
		String example = (this.useKelp) ? getKelpExampleString(tree) : getExampleString(tree);	
		
		if (StringUtils.countMatches(example, "(") != StringUtils.countMatches(example, ")")){
			logger.error("Mismatching number of parantheses in {}", example);
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null);
		}
		
		//TODO: remove the check above
		String probableClass = this.classifier.getMostConfidentModel(example);
		logger.info(cas.getDocumentText()+": "+probableClass);
		QuestionClass questionClass = new QuestionClass(cas);
		questionClass.setBegin(treeAnnot.getBegin());
		questionClass.setEnd(treeAnnot.getEnd());
		questionClass.setQuestionClass(probableClass);
		questionClass.addToIndexes();
	}


	protected String getExampleString(Tree tree) {
		String example = new SVMTKExample().addTree(TreeUtil.serializeTree(tree)).build();
		return example;
	}
	
	protected String getKelpExampleString(Tree tree) {
		String example = KelpUtilities.generateKelpExample("NONE", TreeUtil.serializeTree(tree)).toString();
		return example;
	}

}
