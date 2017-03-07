package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.nodematchmarkers.ITreeModifyingNodeMarker;

public interface IMatcherWithTreeModifyingNodesMarker {
	public ITreeModifyingNodeMarker getNodeMarker();
}
