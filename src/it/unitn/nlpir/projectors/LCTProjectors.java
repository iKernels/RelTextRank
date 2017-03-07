package it.unitn.nlpir.projectors;

import it.unitn.nlpir.nodematchers.AllNonTokenChildrenMatchingStrategy;
import it.unitn.nlpir.nodematchers.LCTHardNodeMatcher;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.SecondParentMatchingStrategy;
import it.unitn.nlpir.nodematchers.TwoParentsMatchingStrategy;
import it.unitn.nlpir.nodematchers.lct.LCTFocusEntityNodeMatcher;
import it.unitn.nlpir.nodematchers.lct.LCTHardNodeTwoStrategiesMatcher;
import it.unitn.nlpir.nodematchers.lct.LCTTwoStrategyFocusEntityNodeMatcher;
import it.unitn.nlpir.nodematchers.lct.TokenAndAllNonTokenChildrenMatchingStrategy;
import it.unitn.nlpir.nodematchers.strategies.AddChildMatchingStrategy;
import it.unitn.nlpir.nodematchers.strategies.MarkGrChildrenMatchingStrategy;
import it.unitn.nlpir.pruners.ChunkTreePruner;
import it.unitn.nlpir.pruners.PrePreTerminalLevelTreePruner;
import it.unitn.nlpir.pruners.StartsWithTagPruningRule;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.LCTWithGrDistinctionBuilder;
import it.unitn.nlpir.tree.PhraseDependencyTreeBuilder;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;
import it.unitn.nlpir.tree.SPTKFullTokenNodesFinalizer;
import it.unitn.nlpir.tree.SPTKFullTokenNodesNoNERFinalizer;
import it.unitn.nlpir.tree.TreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;
import it.unitn.nlpir.tree.leaffinalizers.TreeTokenNodeRelFinalizer;
import it.unitn.nlpir.tree.misc.SPTKFullTokenNodesKeepCustomTagsFinalizer;
import it.unitn.nlpir.uima.TokenTextGetterFactory;


public class LCTProjectors extends Projectors {
	private static String matchingTokenTextType = TokenTextGetterFactory.LEMMA;
	private static String RELTAG = "REL";
	
	public static Projector getProjector(TreeBuilder treeBuilder, NodeMatcher matcher) {
		ITreePostprocessor tp = new SPTKFullTokenNodesFinalizer();
		return new RelTreeProjector(treeBuilder, matcher, tp);
	}
	
	public static Projector getProjector(TreeBuilder treeBuilder) {
		return getProjector(treeBuilder, getHardAllChildrenMatcher());
	}
	
	public static Projector getFocusProjector(TreeBuilder treeBuilder) {
		return getFocusProjector(treeBuilder, getHardAllChildrenMatcher());
	}
	
	public static Projector getFocusProjector(TreeBuilder treeBuilder, NodeMatcher matcher) {
		NodeMatcher focusMatcher = new LCTFocusEntityNodeMatcher(new AddChildMatchingStrategy(),true,true);
		ITreePostprocessor tp = new SPTKFullTokenNodesFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp);
	}
	
	public static Projector getFocusProjectorWithGrMatching(TreeBuilder treeBuilder) {
		return getFocusProjectorWithGrMatching(treeBuilder, getHardAllChildrenMatcher());
	}
	
	public static Projector getFocusProjectorWithGrMatching(TreeBuilder treeBuilder, NodeMatcher matcher) {
		NodeMatcher focusMatcher = new LCTFocusEntityNodeMatcher(new MarkGrChildrenMatchingStrategy(),true,true);
		ITreePostprocessor tp = new SPTKFullTokenNodesFinalizer();
		return new RelTreeWithFocusProjector(treeBuilder, matcher, focusMatcher, tp);
	}
	
	public static Projector getMixedFocusProjectorLCTandDepPH() {
		return getMixedFocusProjectorLCTandDepPH(-1);
	}
	

	public static Projector getLCTProjector() {
		TreeBuilder treeBuilder = new LCTWithGrDistinctionBuilder();
		NodeMatcher matcher = new LCTHardNodeMatcher( new AllNonTokenChildrenMatchingStrategy());
		return getProjector(treeBuilder, matcher);
	}
	
	public static Projector getLCTProjectorMarkTokensAsRel() {
		TreeBuilder treeBuilder = new LCTWithGrDistinctionBuilder();
		NodeMatcher matcher = new LCTHardNodeMatcher( new TokenAndAllNonTokenChildrenMatchingStrategy());
		ITreePostprocessor tp = new SPTKFullTokenNodesKeepCustomTagsFinalizer();
		return new RelTreeProjector(treeBuilder, matcher, tp);
	}
	
	
	public static Projector getMixedProjectorLCTandDepPH(int pruningRay) {
		
		TreeBuilder questionTreeBuilder = new LCTWithGrDistinctionBuilder();
		TreeBuilder answerTreeBuilder = new PhraseDependencyTreeBuilder();
		
		PrePreTerminalLevelTreePruner pruner = null;
		
		if (pruningRay >= 0){
			pruner = new PrePreTerminalLevelTreePruner(new StartsWithTagPruningRule(RELTAG), pruningRay);
		}
		
		NodeMatcher matcher = new LCTHardNodeTwoStrategiesMatcher( new AllNonTokenChildrenMatchingStrategy(), new TwoParentsMatchingStrategy());
		return new MixedRelTreeWithFocusProjector(questionTreeBuilder,answerTreeBuilder, matcher , new SPTKFullTokenNodesFinalizer(), new TreeLeafFinalizer() ,pruner);
	}
	
	
	
	public static Projector getMixedFocusProjectorLCTandDepPH(int pruningRay) {
		NodeMatcher focusMatcher = new LCTTwoStrategyFocusEntityNodeMatcher(new MarkGrChildrenMatchingStrategy(), new SecondParentMatchingStrategy(), true,true);
		
		TreeBuilder questionTreeBuilder = new LCTWithGrDistinctionBuilder();
		TreeBuilder answerTreeBuilder = new PhraseDependencyTreeBuilder();
		
		PrePreTerminalLevelTreePruner pruner = null;
		
		if (pruningRay >= 0){
			pruner = new PrePreTerminalLevelTreePruner(new StartsWithTagPruningRule(RELTAG), pruningRay);
		}
		
		NodeMatcher matcher = new LCTHardNodeTwoStrategiesMatcher( new AllNonTokenChildrenMatchingStrategy(), new TwoParentsMatchingStrategy());
		return new MixedRelTreeWithFocusProjector(questionTreeBuilder,answerTreeBuilder, matcher , focusMatcher, new SPTKFullTokenNodesFinalizer(), new TreeLeafFinalizer() ,pruner);
	}
	
	public static Projector getMixedFocusProjectorLCTandDepPHNoNER(int pruningRay) {
		NodeMatcher focusMatcher = new LCTTwoStrategyFocusEntityNodeMatcher(new MarkGrChildrenMatchingStrategy(), new SecondParentMatchingStrategy(), true,true);
		
		TreeBuilder questionTreeBuilder = new LCTWithGrDistinctionBuilder();
		TreeBuilder answerTreeBuilder = new PhraseDependencyTreeBuilder();
		
		PrePreTerminalLevelTreePruner pruner = null;
		
		if (pruningRay >= 0){
			pruner = new PrePreTerminalLevelTreePruner(new StartsWithTagPruningRule(RELTAG), pruningRay);
		}
		
		NodeMatcher matcher = new LCTHardNodeTwoStrategiesMatcher( new AllNonTokenChildrenMatchingStrategy(), new TwoParentsMatchingStrategy());
		return new MixedRelTreeWithFocusProjector(questionTreeBuilder,answerTreeBuilder, matcher , focusMatcher, new SPTKFullTokenNodesNoNERFinalizer(), new SPTKFullTokenNodesNoNERFinalizer() ,pruner);
	}
	

	

	
	public static Projector getMixedFocusProjectorLCTandDepPHNoFocus(int pruningRay) {
		NodeMatcher focusMatcher = null;
		
		TreeBuilder questionTreeBuilder = new LCTWithGrDistinctionBuilder();
		TreeBuilder answerTreeBuilder = new PhraseDependencyTreeBuilder();
		
		PrePreTerminalLevelTreePruner pruner = null;
		
		if (pruningRay > 0){
			pruner = new PrePreTerminalLevelTreePruner(new StartsWithTagPruningRule(RELTAG), pruningRay);
		}
		
		NodeMatcher matcher = new LCTHardNodeTwoStrategiesMatcher( new AllNonTokenChildrenMatchingStrategy(), new TwoParentsMatchingStrategy());
		return new MixedRelTreeWithFocusProjector(questionTreeBuilder,answerTreeBuilder, matcher , focusMatcher, new SPTKFullTokenNodesFinalizer(), new TreeLeafFinalizer() ,pruner);
	}
	
	
	public static Projector getMixedFocusProjectorLCTandDepPHSTPKAnsRel(int pruningRay) {
		NodeMatcher focusMatcher = new LCTTwoStrategyFocusEntityNodeMatcher(new MarkGrChildrenMatchingStrategy(), new SecondParentMatchingStrategy(), true,true);
		
		TreeBuilder questionTreeBuilder = new LCTWithGrDistinctionBuilder();
		TreeBuilder answerTreeBuilder = new PhraseDependencyTreeBuilder();
		
		PrePreTerminalLevelTreePruner pruner = null;
		
		if (pruningRay > 0){
			pruner = new PrePreTerminalLevelTreePruner(new StartsWithTagPruningRule(RELTAG), pruningRay);
		}
		
		NodeMatcher matcher = new LCTHardNodeTwoStrategiesMatcher( new AllNonTokenChildrenMatchingStrategy(), new TwoParentsMatchingStrategy());
		return new MixedRelTreeWithFocusProjector(questionTreeBuilder,answerTreeBuilder, matcher , focusMatcher, new TreeTokenNodeRelFinalizer(), new TreeTokenNodeRelFinalizer() ,pruner);
	}
	
	public static Projector getMixedFocusProjectorDepPHForQLCTForA(int pruningRay) {
		NodeMatcher focusMatcher = new LCTTwoStrategyFocusEntityNodeMatcher(new SecondParentMatchingStrategy(),new MarkGrChildrenMatchingStrategy(),  true,true);
		
		TreeBuilder questionTreeBuilder = new PhraseDependencyTreeBuilder();
		TreeBuilder answerTreeBuilder = new LCTWithGrDistinctionBuilder();
		
		PrePreTerminalLevelTreePruner pruner = null;
		
		if (pruningRay > 0){
			pruner = new PrePreTerminalLevelTreePruner(new StartsWithTagPruningRule(RELTAG), pruningRay);
		}
		
		NodeMatcher matcher = new LCTHardNodeTwoStrategiesMatcher(new TwoParentsMatchingStrategy(),  new AllNonTokenChildrenMatchingStrategy());
		return new MixedRelTreeWithFocusProjector(questionTreeBuilder,answerTreeBuilder, matcher , focusMatcher,  new TreeLeafFinalizer() ,new SPTKFullTokenNodesFinalizer(),pruner);
	}
	

	
	

	
	public static Projector getMixedFocusProjectorLCTandDepPHNoLowerCase(int pruningRay) {
		NodeMatcher focusMatcher = new LCTTwoStrategyFocusEntityNodeMatcher(new MarkGrChildrenMatchingStrategy(), new SecondParentMatchingStrategy(), true,true);
		
		TreeBuilder questionTreeBuilder = new LCTWithGrDistinctionBuilder();
		TreeBuilder answerTreeBuilder = new PhraseDependencyTreeBuilder();
		
		PrePreTerminalLevelTreePruner pruner = null;
		
		if (pruningRay > 0){
			pruner = new PrePreTerminalLevelTreePruner(new StartsWithTagPruningRule(RELTAG), pruningRay);
		}
		
		NodeMatcher matcher = new LCTHardNodeTwoStrategiesMatcher( new AllNonTokenChildrenMatchingStrategy(), new TwoParentsMatchingStrategy());
		return new MixedRelTreeWithFocusProjector(questionTreeBuilder,answerTreeBuilder, matcher , focusMatcher, new SPTKFullTokenNodesFinalizer(false), new TreeLeafFinalizer() ,pruner);
	}
	
	
	
	public static Projector getMixedFocusProjectorLCTandCH() {
		NodeMatcher focusMatcher = new LCTTwoStrategyFocusEntityNodeMatcher(new MarkGrChildrenMatchingStrategy(), new SecondParentMatchingStrategy(), true,true);
		
		TreeBuilder questionTreeBuilder = new LCTWithGrDistinctionBuilder();
		TreeBuilder answerTreeBuilder = new PosChunkTreeBuilder();
		
		
		NodeMatcher matcher = new LCTHardNodeTwoStrategiesMatcher( new AllNonTokenChildrenMatchingStrategy(), new TwoParentsMatchingStrategy());
		int pruneRay = 2; 
		ChunkTreePruner pruner = new ChunkTreePruner(new StartsWithTagPruningRule(RELTAG), pruneRay);
		
		return new MixedRelTreeWithFocusProjector(questionTreeBuilder,answerTreeBuilder, matcher , focusMatcher, new SPTKFullTokenNodesFinalizer(), new TreeLeafFinalizer(), pruner);
	}
	
	
	public static NodeMatcher getHardAllChildrenMatcher() {
		return new LCTHardNodeMatcher(matchingTokenTextType, RELTAG, new AllNonTokenChildrenMatchingStrategy());
	}
	
	
	
	
	
	
	
	
	
}
