package it.unitn.nlpir.nodematchers.strategies;

import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.util.StanfordCustomTag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class ParentKeepOldTagMatchingStrategy implements MatchingStrategy {
	static final Logger logger = LoggerFactory.getLogger(ParentKeepOldTagMatchingStrategy.class);

	@Override
	public void doMatching(Tree tree, Tree matchingNode, List<MatchedNode> matches, String tag) {
		Tree parent = matchingNode.parent(tree);
		if (parent == null) {
			logger.debug("null parent");
			return;
		}
		String tagOld = StanfordCustomTag.getTag(parent);
		if (tagOld == null)
			matches.add(new MatchedNode(parent, tag));
		else
			matches.add(new MatchedNode(parent, tag + "-" + tagOld));

	}

}
