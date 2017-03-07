package it.unitn.nlpir.features.providers.trees.old;

import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class DependencyTreeProducer {
	
	private static TreebankLanguagePack tlp = new PennTreebankLanguagePack();
	private static GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	
	private Collection<TypedDependency> tdl;
	private List<String> deps;
	private boolean alternativeTree = false;
	private Map<String, Tree> nodeMap = new HashMap<>();
	
	public DependencyTreeProducer(Tree parse) {
		this.tdl = gsf.newGrammaticalStructure(parse)
				.typedDependenciesCollapsedTree();
		deps = new ArrayList<>();
		for(TypedDependency td : this.tdl) {
			deps.add(td.toString());
		}
	}
	
	public DependencyTreeProducer(List<String> deps) {
		this.deps = deps;
	}
	
	public DependencyTreeProducer(List<String> deps, boolean alternativeTree) {
	this(deps);
		this.alternativeTree = alternativeTree;
	}

	public Tree produceTree() throws DependencyTreeProcessorException {
		for(String d : this.deps) {
			String gov = d.substring(d.indexOf('(') + 1, d.indexOf(", "));
			String rel = d.substring(0, d.indexOf('('));
			String dep = d.substring(d.indexOf(", ") + 2, d.length() - 1);
			
			Tree nodeGov = addNode(gov);
			Tree nodeRel = TreeUtil.createNode(rel.toUpperCase());
			Tree nodeDep = addNode(dep);
			
			nodeRel.addChild(nodeDep);
			nodeGov.addChild(nodeRel);
		}
			
		if(nodeMap.containsKey("ROOT-0")) {
			Tree root = nodeMap.get("ROOT-0").getChild(0);		
			if (this.alternativeTree)
				return produceAlternativeTree(root);
			else
				return root;
		} else {
			throw new DependencyTreeProcessorException();
		}	
	}
	
	public static Tree produceAlternativeTree(Tree tree) {
		return produceAlternativeSubtree(tree);
	}
	
	private static Tree produceAlternativeSubtree(Tree relationNode) {
		Tree newRelationNode = TreeUtil.createNode(relationNode.value());
		
		Tree wordNode = relationNode.getChild(0);
		Tree newWordNode = TreeUtil.createNode(wordNode.value());
		
		newRelationNode.addChild(newWordNode);
		
		List<Tree> children = wordNode.getChildrenAsList();
		if(children != null) {
			for(Tree child : children) {
				newRelationNode.addChild(produceAlternativeSubtree(child));
			}
		}
		
		return newRelationNode;
	}
	
	private Tree addNode(String key) {
		Tree node;
		if(this.nodeMap.containsKey(key)) {
			node = this.nodeMap.get(key);
		} else {
			String label = key.substring(0, key.lastIndexOf('-'));
            node = TreeUtil.createNode(label);
            this.nodeMap.put(key, node);
		}
		return node;
	}
	
	public static class DependencyTreeProcessorException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 605404321636542781L;
		
	}
	
}
