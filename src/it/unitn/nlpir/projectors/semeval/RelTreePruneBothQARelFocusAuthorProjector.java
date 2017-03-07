package it.unitn.nlpir.projectors.semeval;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import it.unitn.it.nlpir.types.Author;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.projectors.Projector;
import it.unitn.nlpir.projectors.nodematchmarkers.NodesMarker;
import it.unitn.nlpir.pruners.Pruner;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.TreeBuilder;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;
import edu.stanford.nlp.trees.Tree;

public class RelTreePruneBothQARelFocusAuthorProjector extends RelTreePruneBothQARelFocusProjector implements Projector {
	

	public RelTreePruneBothQARelFocusAuthorProjector(TreeBuilder treeBuilder, int numPreRootNodesToKeep) {
		super(treeBuilder,numPreRootNodesToKeep);
		
	}
	
	public RelTreePruneBothQARelFocusAuthorProjector(TreeBuilder treeBuilder, NodeMatcher matcher, NodeMatcher focusMatcher, 
			ITreePostprocessor treeProcessor, int numPreRootNodesToKeep) {
		super(treeBuilder, matcher, focusMatcher, treeProcessor, numPreRootNodesToKeep);
		
	}
	
	public RelTreePruneBothQARelFocusAuthorProjector(TreeBuilder treeBuilder, NodeMatcher matcher, NodeMatcher focusMatcher, 
			ITreePostprocessor treeProcessor, Pruner pruner, int numPreRootNodesToKeep) {
		super(treeBuilder, matcher, focusMatcher, treeProcessor, pruner, numPreRootNodesToKeep);
		
	}
	
	@Override
	public Pair<String, String> project(JCas questionCas, JCas documentCas)
			throws AnnotationNotFoundException {
		Tree questionTree = treeBuilder.getTree(questionCas);
		Tree documentTree = treeBuilder.getTree(documentCas);
		if (documentTree==null){
			return new Pair<String, String>(TreeUtil.serializeTree(questionTree),
					TreeUtil.serializeTree(TreeUtil.createNode("")));
			
		}
		// Match the nodes between question and the answer
		List<MatchedNode> matches = this.matcher.getMatches(questionCas, documentCas, questionTree,
				documentTree);
		// Mark the aligned words with the relational tag
		new NodesMarker().mark(matches);
		
		if (focusMatcher!=null){
			matches = this.focusMatcher.getMatches(questionCas, documentCas, questionTree, documentTree);
			matchFocus(questionTree, documentTree, focusMatcher, matches);
		}
		
				
		
		
		//do projection of the author
		
		if ((JCasUtil.select(questionCas, Author.class).size()>0)&&(JCasUtil.select(documentCas, Author.class).size()>0)){
			String questionAuthor = JCasUtil.selectSingle(questionCas, Author.class).getName();
			String documentAuthor = JCasUtil.selectSingle(documentCas, Author.class).getName();
			if (documentAuthor.equals(questionAuthor)){
				for (Tree preroot : documentTree.getChildrenAsList()){
					preroot.setValue("SAMEAUTH-"+preroot.value()); 
				}
			}
		}
		
		Tree[] tr = prune(questionTree,documentTree);
		questionTree = tr[0];
		documentTree = tr[1];
		
		treeProcessor.process(questionTree, questionCas);
		treeProcessor.process(documentTree, documentCas);
		
		return new Pair<String, String>(TreeUtil.serializeTree(questionTree),
				TreeUtil.serializeTree(documentTree));
	}



}