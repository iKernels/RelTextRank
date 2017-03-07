package it.unitn.nlpir.projectors;

import java.util.List;

import it.unitn.nlpir.nodematchers.AllNERQCNodeThreshFocMatcher;
import it.unitn.nlpir.nodematchers.FineQCEntityNodeMatcher;
import it.unitn.nlpir.nodematchers.FocusEntityFineNumNodeMatcher;
import it.unitn.nlpir.nodematchers.FocusEntityNodeMatcher;
import it.unitn.nlpir.nodematchers.FocusEntityNodeNoQuestionFocMatcher;
import it.unitn.nlpir.nodematchers.FocusEntityNodeThreshFocMatcher;
import it.unitn.nlpir.nodematchers.FocusEntityNodeThreshNoFirstWordFocMatcher;
import it.unitn.nlpir.nodematchers.FocusFineQCEntityNodeMatcher;
import it.unitn.nlpir.nodematchers.FocusNodeMarker;
import it.unitn.nlpir.nodematchers.HardNodeMatcher;
import it.unitn.nlpir.nodematchers.MapHardNodeMatcher;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.NullMatcher;
import it.unitn.nlpir.nodematchers.ParentMatchingStrategy;

import it.unitn.nlpir.nodematchers.SecondAndThirdParentMatchingStrategy;
import it.unitn.nlpir.nodematchers.SecondParentMatchingStrategy;
import it.unitn.nlpir.nodematchers.ThreeParentsMatchingStrategy;
import it.unitn.nlpir.nodematchers.ThreshFocusNodeMarker;
import it.unitn.nlpir.nodematchers.TwoParentsMatchingStrategy;
import it.unitn.nlpir.projectors.semeval.RelTreePruneBothQARelFocusAuthorProjector;
import it.unitn.nlpir.projectors.semeval.RelTreePruneBothQARelFocusProjector;
import it.unitn.nlpir.projectors.semeval.RelTreePruneOnlyARelFocusAuthorProjector;
import it.unitn.nlpir.projectors.semeval.RelTreePruneOnlyASentRelFocusAuthorProjector;
import it.unitn.nlpir.projectors.semeval.RelTreePruneOnlyNonDescARelFocusAuthorProjector;
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
import it.unitn.nlpir.tree.TreeLeafByMapFinalizer;
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
	
	public static Projector getArabicProjector(TreeBuilder treeBuilder) {
		ITreePostprocessor tp = new TreeLeafByMapFinalizer(true);
		return new RelTreeProjector(treeBuilder, new MapHardNodeMatcher(matchingTokenTextType, RELTAG, new TwoParentsMatchingStrategy()), tp);
	}
	
	public static Projector getNoRELProjector(TreeBuilder treeBuilder) {
		return getProjector(treeBuilder, null);
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
	
	
	/**
	 * Builds tree only from n
	 * @param treeBuilder
	 * @param pruneRay
	 * @return
	 */
	public static Projector getPruneRelAndSentBothQAProjector(TreeBuilder treeBuilder, int pruneRay, int sentPruneRay) {
		return getPruneRelAndSentBothQAProjector(treeBuilder, pruneRay, sentPruneRay, new TreeLeafFinalizer());
	}
	
	/**
	 * Builds tree only from n
	 * @param treeBuilder
	 * @param pruneRay
	 * @return
	 */
	public static Projector getPruneRelAndSentBothQAProjector(TreeBuilder treeBuilder, int pruneRay, int sentPruneRay, ITreePostprocessor tp) {
		NodeMatcher matcher = getHard2ParentMatcher();
		//ITreePostprocessor tp = new TreeLeafFinalizer();
		ChunkTreePruner pruner = null;
		if (pruneRay>0)
			pruner = new ChunkTreePruner(new StartsWithTagPruningRule(RELTAG), pruneRay);
		
		return new RelTreePruneBothQAProjector(treeBuilder, matcher, tp, pruner, sentPruneRay);
	}
	
	
	
	
	
	public static Projector getPruneBothQAProjector(TreeBuilder treeBuilder, int pruneRay) {
		NodeMatcher matcher = getHard2ParentMatcher();
		ITreePostprocessor tp = new TreeLeafFinalizer();
		ChunkTreePruner pruner = null;
		if (pruneRay>0)
			pruner = new ChunkTreePruner(new StartsWithTagPruningRule(RELTAG), pruneRay);
		return new RelTreePruneBothQAProjector(treeBuilder, matcher, tp, pruner);
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
	
	
	public static Projector getFineNumFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule) {
		return getFineNumFocusProjector(treeBuilder, pruneRay, rule, new TreeLeafFinalizer());
	}
	
	public static Projector getUnlexFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule) {
		return getFocusProjector(treeBuilder, pruneRay, rule, new TreeUnlexicalizer());
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
	
	
	public static Projector getFineNumFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher =  new FocusEntityFineNumNodeMatcher(new SecondParentMatchingStrategy(),true);
		//new FocusEntityNodeMatcher(new SecondParentMatchingStrategy(),true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	public static Projector getFineNumAnswerFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher =  new FocusEntityFineNumNodeMatcher(new SecondParentMatchingStrategy(),true);
		//new FocusEntityNodeMatcher(new SecondParentMatchingStrategy(),true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	
	public static Projector getFocusWithNoQFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeNoQuestionFocMatcher(new SecondAndThirdParentMatchingStrategy(),true,true);
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
	 * do not type focus in questions (use simply REL-focus), do not do NE-QC match
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getFocusMarkAndRELMatchProjector(TreeBuilder treeBuilder, int pruneRay, 
			PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusNodeMarker(new SecondParentMatchingStrategy());
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	/**
	 * do not type focus in questions (use simply REL-focus), add focus match as a new dangling node
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getFocusWithThreshFocusUntypedFocusInQDetachedQCProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshFocMatcher(new ParentMatchingStrategy(),true,true,false, new SecondParentMatchingStrategy());
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new DirectMarkerRelTreeWithDetachedFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	/**
	 * do not type focus in questions (use simply REL-focus), add focus match as a new dangling node
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getFocusWithThreshFocusUntypedFocusNERQCNodesProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new AllNERQCNodeThreshFocMatcher(new ParentMatchingStrategy(),true,true,
				false, new SecondParentMatchingStrategy());
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new DirectMarkerRelTreeWithDetachedFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	/**
	 * 
	 * @param treeBuilder
	 * @param pruneRay
	 * @param rule
	 * @param treePostProcessor
	 * @return
	 */
	public static Projector getUntypedFocusWithThreshFocusUntypedFocusNERQCNodesProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new AllNERQCNodeThreshFocMatcher(new ParentMatchingStrategy(),false,true,
				false, new SecondParentMatchingStrategy());
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new DirectMarkerRelTreeWithDetachedFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
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
	
	public static Projector getRelPruneQandAProjectorWithAuthor(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor,
			int sentencesToKeep) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = null;//new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreePruneBothQARelFocusAuthorProjector(treeBuilder, matcher, focusMatcher, tp, pruner,sentencesToKeep);
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
	
	
	
	public static Projector getRelPruneQandAProjectorWithAuthorAndFocus(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor,
			int sentencesToKeep) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,false,false);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreePruneBothQARelFocusAuthorProjector(treeBuilder, matcher, focusMatcher, tp, pruner,sentencesToKeep);
	}
	
	public static Projector getRelPruneOnlyAProjectorWithAuthorAndFocus(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor,
			int sentencesToKeep) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,false,false);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreePruneOnlyARelFocusAuthorProjector(treeBuilder, matcher, focusMatcher, tp, pruner,sentencesToKeep);
	}
	
	public static Projector getRelPruneOnlyNonDescAProjectorWithAuthorAndFocus(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor,
			int sentencesToKeep) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshFocMatcher(new SecondParentMatchingStrategy(),true,false,false);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreePruneOnlyNonDescARelFocusAuthorProjector(treeBuilder, matcher, focusMatcher, tp, pruner,sentencesToKeep);
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
	
	
	public static Projector getFocusWithNoFirstWordThreshFocusProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeThreshNoFirstWordFocMatcher(new SecondParentMatchingStrategy(),true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	public static Projector getFocusWithNoQFocusNoSecondParProjector(TreeBuilder treeBuilder, int pruneRay, PruningRule rule, ITreePostprocessor treePostProcessor) {
		NodeMatcher matcher = getHard2ParentMatcher();
		NodeMatcher focusMatcher = new FocusEntityNodeNoQuestionFocMatcher(new SecondParentMatchingStrategy(),true,true);
		ChunkTreePruner pruner = null;
		if (pruneRay>0){
			pruner = new ChunkTreePruner(rule, pruneRay);
		}
		ITreePostprocessor tp = treePostProcessor;//new TreeLeafFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp, pruner);
	}
	
	//return 
	

	
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
	
	public static Projector getPosChunkProjectorNoRel() {
		return getProjector(new PosChunkTreeBuilder(), new NullMatcher());
	}


	

	
	
	
	public static Projector getPosChunkWithFocusFineQCProjector() {
		ITreePostprocessor tp = new TreeLeafFinalizer();
		MultiProjector projector = new MultiProjector(new PosChunkTreeBuilder(), getHard2ParentMatcher(), tp);
		NodeMatcher focusMatcher = new FocusFineQCEntityNodeMatcher(new SecondParentMatchingStrategy(), true);
		projector.addMatcher(focusMatcher);
		return projector;
	}
	
	public static Projector getPosChunkWithFineQCProjector() {
		ITreePostprocessor tp = new TreeLeafFinalizer();
		MultiProjector projector = new MultiProjector(new PosChunkTreeBuilder(), getHard2ParentMatcher(), tp);
		NodeMatcher focusMatcher = new FineQCEntityNodeMatcher(new SecondParentMatchingStrategy(), true);
		projector.addMatcher(focusMatcher);
		return projector;
	}
	
	
	public static Projector getPosChunkFocusProjector() {
		return getFocusProjector(new PosChunkTreeBuilder());
	}

	

	






	
		
	
}
