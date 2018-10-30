package it.unitn.nlpir.nodematchers.strategies;

import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.util.TreeUtil;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class ParentWithHierTagMatchingStrategy implements MatchingStrategy {
	static final Logger logger = LoggerFactory.getLogger(ParentWithHierTagMatchingStrategy.class);

	@Override
	public void doMatching(Tree tree, Tree matchingNode, List<MatchedNode> matches, String tag) {
		if (matchingNode.parent(tree)!=null){
			matches.add(new MatchedNode(matchingNode.parent(tree), TreeUtil.serializeTree(TreeUtil.createNode(tag))));
		}
		else{
			logger.debug("null parent");
		}
	}

}
