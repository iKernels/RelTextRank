package it.unitn.nlpir.nodematchers.strategies;

import it.unitn.nlpir.nodematchers.HierMatchingStrategy;
import it.unitn.nlpir.nodematchers.MatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.util.TreeUtil;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class AddChildMatchingStrategy implements MatchingStrategy, HierMatchingStrategy {
	static final Logger logger = LoggerFactory.getLogger(AddChildMatchingStrategy.class);
	
	@Override
	public void doMatching(Tree tree, Tree matchingNode, List<MatchedNode> matches, String relTag) {
		if (tree == null) {
			logger.error("Tre NULL: this is a problem!");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (matchingNode == null) {
			logger.error("Matching node NULL: WTF?!");
			try {
				Thread.sleep(3000);
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		matches.add(new MatchedNode(matchingNode, TreeUtil.serializeTree(TreeUtil.createNode(relTag))));
		/*Tree parent = matchingNode.parent(tree);
		if (parent != null) {
			Tree grandParent = parent.parent(tree);
			if (grandParent != null) {
				matches.add(new MatchedNode(grandParent, relTag));
			}
		}*/
	}
}
