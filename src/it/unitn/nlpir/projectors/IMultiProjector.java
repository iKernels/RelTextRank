package it.unitn.nlpir.projectors;

import it.unitn.nlpir.nodematchers.NodeMatcher;


public interface IMultiProjector  extends Projector {
	
	public void addMatcher(NodeMatcher matcher) ;

}
