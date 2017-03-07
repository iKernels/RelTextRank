package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.projectors.MatchedNode;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class CurrentNodeMatchingStrategy implements MatchingStrategy {
	static final Logger logger = LoggerFactory.getLogger(CurrentNodeMatchingStrategy.class);

	@Override
	public void doMatching(Tree tree, Tree matchingNode, List<MatchedNode> matches, String tag) {
		//if (matchingNode.parent(tree)!=null){
			matches.add(new MatchedNode(matchingNode, tag));
		/*}
		else{
			logger.debug("null parent");
		}*/
	}

}
