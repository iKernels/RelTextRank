package it.unitn.nlpir.projectors.nodematchmarkers;

import java.util.List;

import it.unitn.nlpir.projectors.MatchedNode;
import edu.stanford.nlp.trees.Tree;

public interface ITreeModifyingNodeMarker {
	public void mark(Tree tree, MatchedNode node);
	public void mark(Tree tree, List<MatchedNode> nodes);
}
