package it.unitn.nlpir.projectors;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import it.unitn.nlpir.experiment.util.ExperimentComponentFactory;


import it.unitn.nlpir.nodematchers.FocusEntityNodeMatcher;
import it.unitn.nlpir.nodematchers.FocusEntityNodeThreshFocMatcher;
import it.unitn.nlpir.nodematchers.HardNodeMatcher;

import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.ThreshFocusNodeMarker;
import it.unitn.nlpir.nodematchers.strategies.SecondAndThirdParentMatchingStrategy;
import it.unitn.nlpir.nodematchers.strategies.SecondParentMatchingStrategy;
import it.unitn.nlpir.nodematchers.strategies.ThreeParentsMatchingStrategy;
import it.unitn.nlpir.nodematchers.strategies.TwoParentsMatchingStrategy;
import it.unitn.nlpir.projectors.semeval.RelTreePruneBothQARelFocusProjector;
import it.unitn.nlpir.projectors.semeval.RelTreePruneOnlyASentRelFocusAuthorProjector;

import it.unitn.nlpir.pruners.ChunkTreePruner;
import it.unitn.nlpir.pruners.PrePreTerminalLevelTreePruner;
import it.unitn.nlpir.pruners.Pruner;
import it.unitn.nlpir.pruners.PruningRule;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.pruners.StartsWithTagPruningRule;
import it.unitn.nlpir.tree.ConstituencyTreeBuilder;
import it.unitn.nlpir.tree.DependencyPosTagTreeBuilder;
import it.unitn.nlpir.tree.DependencyTreeBuilder;

import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.PhraseDependencyTreeBuilder;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;
import it.unitn.nlpir.tree.TreeBuilder;

import it.unitn.nlpir.tree.TreeLeafFinalizer;
import it.unitn.nlpir.tree.TreeUnlexicalizer;
import it.unitn.nlpir.uima.TokenTextGetterFactory;


public class Projectors {
//	private static boolean lowerCaseOutput = true;
//	private static String leafTextType = TokenTextGetterFactory.LEMMA;
	private static String matchingTokenTextType = TokenTextGetterFactory.LEMMA;
	private static String RELTAG = "REL";
	public static NodeMatcher getHard2ParentMatcher() {
		return new HardNodeMatcher(matchingTokenTextType, RELTAG, new TwoParentsMatchingStrategy());
	}
	

	
	
	
	
	public static NodeMatcher getHard3ParentMatcher() {
		return new HardNodeMatcher(matchingTokenTextType, RELTAG, new ThreeParentsMatchingStrategy());
	}
	
	
	
	
	public static Projector getProjector(TreeBuilder treeBuilder) {
		return getProjector(treeBuilder, getHard2ParentMatcher());
	}
	

	
	public static Projector getNoRELProjector(TreeBuilder treeBuilder) {
		return getProjector(treeBuilder, null);
	}
	
	public static Projector getNoRELProjector(TreeBuilder treeBuilder, ITreePostprocessor tp) {
		return new RelTreeProjector(treeBuilder, null, tp);
	}
	
	
	public static Projector getProjector(TreeBuilder treeBuilder, NodeMatcher matcher) {
		ITreePostprocessor tp = new TreeLeafFinalizer();
		return new RelTreeProjector(treeBuilder, matcher, tp);
	}
	
	public static Projector getProjector(TreeBuilder treeBuilder, int pruneRay) {
		NodeMatcher matcher = getHard2ParentMatcher();
		ITreePostprocessor tp = new TreeLeafFinalizer();
		ChunkTreePruner pruner = new ChunkTreePruner(new StartsWithTagPruningRule(RELTAG), pruneRay);
		return new RelTreeProjector(treeBuilder, matcher, tp, pruner);
	}
	
	public static Projector getProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule pruningRule, ITreePostprocessor tp) {
		NodeMatcher matcher = getHard2ParentMatcher();
		ChunkTreePruner pruner = new ChunkTreePruner(pruningRule, pruneRay);
		return new RelTreeProjector(treeBuilder, matcher, tp, pruner);
	}
	
	
	
	
	public static Projector getFocusProjector(TreeBuilder treeBuilder) {
		NodeMatcher matcher = getHard2ParentMatcher();
		return getFocusProjector(treeBuilder, matcher);
	}

	
	
	public static Projector getFocusProjector(TreeBuilder treeBuilder, int pruneRay) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeMatcher(new SecondParentMatchingStrategy());
		ChunkTreePruner pruner = new ChunkTreePruner(new StartsWithTagPruningRule(RELTAG), pruneRay);
		ITreePostprocessor tp = new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}

		
	
	
	
	public static Projector getFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule) {
		return getFocusProjector(treeBuilder, pruneRay, rule, new TreeLeafFinalizer());
	
	}
	
	
	
	
	public static Projector getUnlexFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule) {
		return getFocusProjector(treeBuilder, pruneRay, rule, new TreeUnlexicalizer());
	}
	
	/**
	 * 
	 * @param treeBuilder tree constructor
	 * @param pruneRay pruning ray (only for the chunk-based systems)
	 * @param rule pruning rule (only for the chunk-based systems)
	 * @param treePostProcessor tree postprocessor after matching
	 * @param addFocus mark focus node
	 * @param typeFocus type focus node with QC
	 * @param addFocusToQ
	 * @return
	 */
	public static Projector getFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule,
			ITreePostprocessor treePostProcessor, boolean addFocus, boolean typeFocus, boolean addFocusToQ) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = null;
		if (addFocus)
				focusMatcher = new FocusEntityNodeMatcher(new SecondParentMatchingStrategy(),typeFocus,addFocusToQ);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	
	//public RelTreeProjector(TreeBuilder treeBuilder, NodeMatcher matcher, ITreePostprocessor treeProcessor, Pruner pruner) {
	
	
	public static Projector getParametrizedProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule,
			ITreePostprocessor treePostProcessor, String[] mcFiles) {
		return getParametrizedProjector(treeBuilder, pruneRay, rule, treePostProcessor, mcFiles, null);
	}
	public static Projector getParametrizedProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule,
			ITreePostprocessor treePostProcessor, String[] mcFiles, Properties p) {
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		MultiProjector projector = new MultiProjector(treeBuilder, treePostProcessor, pruner);
		for (String file : mcFiles) {
			try {
				projector.addMatcher(ExperimentComponentFactory.createNewNodeMatcher(file,p));
			} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return projector;
	}
	
	
	public static Projector getFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule,
			ITreePostprocessor treePostProcessor, boolean addFocusToQ) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeMatcher(new SecondParentMatchingStrategy(),true,addFocusToQ);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	public static Projector getFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		return getFocusProjector(treeBuilder, pruneRay, rule, treePostProcessor, true);

	}
	
	public static Projector getFocusProjectorWithThreeParentMatchers(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard3ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeMatcher(new SecondAndThirdParentMatchingStrategy(),true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	//projector.addMatcher(getAnswerPatternMatcher());
	public static Projector getAnswerPatternFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeMatcher(new SecondParentMatchingStrategy(),true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	
	public static Projector getFocusOnlyAnswerProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeMatcher(new SecondParentMatchingStrategy(),true,false);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	


	public static Projector getFocusWithThreshFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	/**
	 * do not type focus in questions (use simply REL-focus)
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getFocusWithThreshFocusUntypedFocusInQProjectorSentPrune(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,true,false);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}

	/**
	 * do not type focus in questions (use simply REL-focus)
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getFocusWithThreshFocusUntypedFocusInQProjector(TreeBuilder treeBuilder, int pruneRay, 
			PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,true,false);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	


	/**
	 * Do not type focus tag in question, add focus tag to question only if the score by the focus predictor is above 0
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @param addFocus do FREL match
	 * @param typeFocus use the REL-FOCUS label typed by the question class (QC)
	 * @param addFocusToQ mark focus not in question
	 * @return
	 */
	public static Projector getFocusWithThreshFocusUntypedFocusInQProjector(TreeBuilder treeBuilder, int pruneRay, 
			PruningRule rule, ITreePostprocessor treePostProcessor, boolean addFocus, boolean typeFocus, boolean addFocusToQ) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = null;
		if (addFocus)
			focusMatcher = new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),typeFocus,addFocusToQ,false);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}

			
	/**
	 * do not type focus in questions (use simply REL-focus)
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getFocusWithThreshFocusUntypedFocusInQProjector(TreeBuilder treeBuilder, int pruneRay, 
			PruningRule rule, ITreePostprocessor treePostProcessor, List<NodeMatcher> additionalMatchers) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,true,false);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		MultiProjector p = new MultiProjector(treeBuilder, matcher, tp, pruner);
		
		for (NodeMatcher m : additionalMatchers){
			p.addMatcher(m);
		}
		p.addMatcher(focusMatcher);
		return p;
	}
	
	
	
	/**
	 * do not type focus in questions (use simply REL-focus), do not do NE-QC match
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getFocusWithThreshToQOnlyNoQCMatchProjector(TreeBuilder treeBuilder, int pruneRay, 
			PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new ThreshFocusNodeMarker(new SecondParentMatchingStrategy());
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	/**
	 * no focus, not qc-ne match
	 * @return
	 */
	public static Projector getRelTreeProjector(TreeBuilder treeBuilder, int pruneRay, 
			PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, null, tp, pruner);
	}
	
	

	

	
	
	
	/**
	 * do not type focus in questions (use simply REL-focus)
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getFocusWithThreshFocusUntypedFocusInQPruneQandAProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor,
			int sentencesToKeep) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,true,false);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreePruneBothQARelFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner,sentencesToKeep);
	}
	
	/**
	 * do not type focus in questions (use simply REL-focus)
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getFocusWithThreshFocusTypedFocusInQPruneQandAProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor,
			int sentencesToKeep) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreePruneBothQARelFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner,sentencesToKeep);
	}
	
	/**
	 * do not type focus in questions (use simply REL-focus)
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getRelPruneQandAProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor,
			int sentencesToKeep) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = null;//new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreePruneBothQARelFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner,sentencesToKeep);
	}
	
	
	public static Projector getRelPruneOnlyAProjectorWithAuthor(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor,
			int sentencesToKeep) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = null;//new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreePruneOnlyASentRelFocusAuthorProjector(treeBuilder, matcher, focusMatcher, tp, pruner,sentencesToKeep);
	}
	
	
	
	/**
	 * do not type focus in questions (use simply REL-focus), do not do question class matching
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getRelWithThreshFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeProjector(treeBuilder, matcher, tp, pruner);
	}
	
	
	/**
	 * do not type focus in questions (use simply REL-focus)
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getNoFocusNoQCProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeProjector(treeBuilder, matcher, tp, pruner);
	}
	
	
	
	

	
	public static Projector getFocusProjector(TreeBuilder treeBuilder, Pruner pruner, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeMatcher(new SecondParentMatchingStrategy(),true,true);
		
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	public static Projector getFocusProjector(TreeBuilder treeBuilder, NodeMatcher matcher) {
		NodeMatcher focusMatcher = new FocusEntityNodeMatcher(new SecondParentMatchingStrategy());
		ITreePostprocessor tp = new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp);
	}
	
	public static Projector getFocusProjector(TreeBuilder treeBuilder, NodeMatcher matcher, NodeMatcher focusMatcher) {
		ITreePostprocessor tp = new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp);
	}
	


	public static Projector getConstituencyProjector() {
		return getProjector(new ConstituencyTreeBuilder());
	}

	public static Projector getConstituencyFocusProjector() {
		return getFocusProjector(new ConstituencyTreeBuilder());
	}

	public static Projector getDependencyProjector() {
		return getProjector(new DependencyTreeBuilder());
	}
	
	public static Projector getDependencyFocusProjector() {
		return getFocusProjector(new DependencyTreeBuilder());
	}
	
	public static Projector getDependencyPosTagProjector() {
		return getProjector(new DependencyPosTagTreeBuilder());
	}
	
	public static Projector getDependencyPosTagFocusProjector() {
		return getFocusProjector(new DependencyPosTagTreeBuilder());
	}
	
	public static Projector getPhraseDependencyProjector() {
		return getProjector(new PhraseDependencyTreeBuilder());
	}
	
	/**
	 * Returns phraseDependencyProjector, with the pruneRay specified, with getHard2ParentMatcher(), TreeLeafFinalizer()
	 * @param pruneRay
	 * @return
	 */
	public static Projector getPrunePhraseDependencyProjector(int pruneRay) {
		ITreePostprocessor tp = new TreeLeafFinalizer();
		NodeMatcher matcher = getHard2ParentMatcher();
		PrePreTerminalLevelTreePruner pruner = new PrePreTerminalLevelTreePruner(new StartsWithOrContainsTagPruningRule(), pruneRay);
		return new RelTreeProjector(new PhraseDependencyTreeBuilder(), matcher, tp, pruner);
	}
	
	public static Projector getPhraseDependencyFocusProjector() {
		return getFocusProjector(new PhraseDependencyTreeBuilder());
	}
	
	public static Projector getPosChunkProjector() {
		return getProjector(new PosChunkTreeBuilder());
	}



	

	
	
	
	
	
	public static Projector getPosChunkFocusProjector() {
		return getFocusProjector(new PosChunkTreeBuilder());
	}

	

	






	
		
	
}
