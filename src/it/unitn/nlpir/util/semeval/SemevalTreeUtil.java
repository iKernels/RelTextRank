package it.unitn.nlpir.util.semeval;

import it.unitn.nlpir.types.CQAUserSignature;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.types.semeval.PostSubject;
import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

public class SemevalTreeUtil {
	public static final String REMOVE_LABEL="REMOVE";
	
	public static void updateTheTree(Tree tree, Tree cl) {
		cl = TreeUtil.pruneLeavesToRemove(tree);
		while (tree.children().length>0){
			tree.removeChild(0);
		}
		for (Tree s : cl.getChildrenAsList()){
			tree.addChild(s);
		}
	}
	public static Tree pruneSignatures(JCas cas, Tree tree) {
		Set<String> signatureTokensToPrune = new HashSet<String>();
		for (CQAUserSignature sign: JCasUtil.select(cas, CQAUserSignature.class)){
			for (Token token : JCasUtil.selectCovered(Token.class, sign)){
				signatureTokensToPrune.add(String.valueOf(token.getId()));
			}
		}
		
		//if we are removing the entire tree, ten smth is wrong
		/*if (signatureTokensToPrune.size() == tree.getLeaves().size()){
			System.out.println("PosChunkTreeLimitSLengthPruneSignaturesBuilder: keeping "+cas.getDocumentText());
			//return tree;
		}*/
		Tree copytree = tree.deepCopy();
		if (signatureTokensToPrune.size()>0){
			for (Tree leaf: copytree.getLeaves()){
				if (signatureTokensToPrune.contains(leaf.value()))
					leaf.setValue("REMOVE");
			}
			copytree = TreeUtil.pruneLeavesToRemove(copytree);
		}
		
		if (copytree==null){
			System.out.println("PosChunkTreeLimitSLengthPruneSignaturesBuilder: keeping "+cas.getDocumentText());
			copytree = tree.deepCopy();
		}
		return copytree;
	}
	
	public static List<Tree> getChunkNodesFromAShallowPostagTree(Tree tree){
		List<Tree> chunks = new ArrayList<Tree>();
		for (Tree s:	tree.getChildrenAsList()){
			for (Tree ch : s.getChildrenAsList())
				chunks.add(ch);
		}
		return chunks;
	}
	
	public static List<Tree> getChunkNodesFromAConstTree(Tree tree){
		List<Tree> chunks = new ArrayList<Tree>();
		
		
		for (Tree s:	TreeUtil.getTreeIntegerNodes(tree)){
			Tree p = s.parent(tree).parent(tree);
			if (!chunks.contains(p)){
				chunks.add(p);
			}
		}
		return chunks;
	}
	
	
	public static Tree splitIntoSubjectAndBody(JCas cas, Tree tree) {
		if (JCasUtil.select(cas, PostSubject.class).size()>0){
			Set<Integer> subjTokens = new HashSet<Integer>();
			PostSubject subj = JCasUtil.selectSingle(cas, PostSubject.class);
			for (Token t : JCasUtil.selectCovered(cas, Token.class,subj)){
				subjTokens.add(t.getId());
			}
			
			Tree bodyTree = tree.deepCopy();
			
			for (Tree l : tree.getLeaves()){
				if (!subjTokens.contains(Integer.valueOf(l.value()))){
					l.setValue("REMOVE");
				}
			}
			for (Tree l : bodyTree.getLeaves()){
				if (subjTokens.contains(Integer.valueOf(l.value()))){
					l.setValue("REMOVE");
				}
			}				
			//rewriting requires changing the interface
			tree = TreeUtil.pruneLeavesToRemove(tree);
			bodyTree = TreeUtil.pruneLeavesToRemove(bodyTree);
			
			for (Tree ch : tree.getChildrenAsList()){
				ch.setValue("SUBJECT-"+ch.value());
			}
			if (bodyTree!=null){
				for (Tree ch : bodyTree.getChildrenAsList()){
					tree.addChild(ch);
				}
			}
		
		}
		return tree;
	}
	
}
