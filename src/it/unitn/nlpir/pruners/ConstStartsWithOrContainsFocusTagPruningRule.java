package it.unitn.nlpir.pruners;

import java.util.List;

import it.unitn.nlpir.util.StanfordCustomTag;
import edu.stanford.nlp.trees.Tree;

/**
 * Acceptable nodes:
 * <li> Nodes containing StanfordCustomTag
 * <li> Nodes containing a given keyword passed through tags 
 * <br> checks only non-leaf nodes
* @author IKernels group
 *
 * 
 *
 */
public class ConstStartsWithOrContainsFocusTagPruningRule implements PruningRule {
	
	private final String [] tags;
	private final String[] tagsToFilter;
	public ConstStartsWithOrContainsFocusTagPruningRule(String [] tags, String[] tagsToFilter){
		this.tags = tags;
		this.tagsToFilter = tagsToFilter;
	}
	
	public ConstStartsWithOrContainsFocusTagPruningRule(){
		this.tags = new String[]{};
		this.tagsToFilter = new String[]{};
	}
	
	
	
	private boolean isSatisfied(Tree node){
		String customTag = StanfordCustomTag.getTag(node);
		//if (node.isLeaf()) return false;//???? KAT why
		
		boolean satisfied = ( customTag != null)&&(customTag.contains("FOCUS"));
		if (!satisfied){
			for (int i = 0; i < tags.length; i++){
				if (node.label().value().contains(tags[i])){
					satisfied = true;
					break;
				}
			}
			
		}
		if (satisfied){
			for (int i = 0; i < tagsToFilter.length; i++){
				String label = customTag == null ? node.label().value() : customTag; 
				if (label.equals(tagsToFilter[i])){
					satisfied = false;
					break;
				}
			}
		}
		return satisfied;
	}
	
	@Override
	public boolean isSatisfiedOn(Tree node) {

		boolean satisfied = isSatisfied(node);
		
		if (!satisfied){
			List<Tree> children = node.getChildrenAsList();
			if (children!=null){
				for (Tree child : children){
					if (!child.isPreTerminal())
						continue;
					satisfied = isSatisfied(child);
					if (satisfied){
						break;
					}
				}
			}
		}
		return satisfied;
	}

}
