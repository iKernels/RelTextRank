package it.unitn.nlpir.projectors.nodematchmarkers;

import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.util.StanfordCustomTag;
import it.unitn.nlpir.util.TreeUtil;

import java.util.List;

public class NodesMarker {
	protected boolean prependMarkInsteadOfReplace = false;
	
	public NodesMarker(boolean prependMarkInsteadOfReplace){
		this.prependMarkInsteadOfReplace = prependMarkInsteadOfReplace;
	}
	
	public NodesMarker(){
		this(false);
	}
	
	public void mark(MatchedNode node) {
		String tag = node.getRelTag();
		if (this.prependMarkInsteadOfReplace){
			String oldTag = StanfordCustomTag.getTag(node.getNode());
			if (oldTag != null)
				tag = tag +"-"+oldTag;
		}
		TreeUtil.markNode(node.getNode(), tag);
	}
	
	public void mark(List<MatchedNode> nodes) {
		for(MatchedNode node : nodes) {
			mark(node);
		}
	}
	
	
	
}
