package it.unitn.nlpir.projectors;

import edu.stanford.nlp.trees.Tree;

public class MatchedNode {
	private Tree node;
	private String relTag;
	
	public MatchedNode(Tree node, String relTag) {
		this.node = node;
		this.relTag = relTag;
	}
	
	public Tree getNode() {
		return node;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result + ((relTag == null) ? 0 : relTag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchedNode other = (MatchedNode) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (relTag == null) {
			if (other.relTag != null)
				return false;
		} else if (!relTag.equals(other.relTag))
			return false;
		return true;
	}

	public void setNode(Tree node) {
		this.node = node;
	}
	public String getRelTag() {
		return relTag;
	}
	public void setRelTag(String relTag) {
		this.relTag = relTag;
	}
	
}
