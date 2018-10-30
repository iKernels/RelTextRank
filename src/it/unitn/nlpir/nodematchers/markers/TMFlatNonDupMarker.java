package it.unitn.nlpir.nodematchers.markers;


import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.projectors.nodematchmarkers.ITreeModifyingNodeMarker;
import it.unitn.nlpir.projectors.nodematchmarkers.NodesMarker;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class TMFlatNonDupMarker implements ITreeModifyingNodeMarker{
	
	
	protected NodesMarker marker; 
	
	public TMFlatNonDupMarker(boolean prependMarkInsteadOfReplace){
		marker = new NodesMarker(prependMarkInsteadOfReplace);
	}
	
	public TMFlatNonDupMarker(){
		this(false);
	}
	
	public void mark(Tree tree, MatchedNode node) {
		marker.mark(node);
	}
	
	public void mark(Tree tree, List<MatchedNode> nodes) {
		marker.mark(nodes);
	}
}
