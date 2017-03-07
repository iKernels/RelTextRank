package it.unitn.nlpir.nodematchers;

import it.unitn.nlpir.experiment.TrecQAExperiment;
import it.unitn.nlpir.projectors.MatchedNode;
import it.unitn.nlpir.util.TreeUtil;

import java.util.List;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class TwoParentsMatchingStrategy implements MatchingStrategy {
	private final Logger logger = LoggerFactory.getLogger(TwoParentsMatchingStrategy.class);
	@Override
	public void doMatching(Tree tree, Tree matchingNode, List<MatchedNode> matches, String tag) {
		if (matchingNode==null){
			logger.warn("Matching node is null: "+tree+"\t"+matchingNode);
			return;
		}
		Tree parent = matchingNode.parent(tree);
		if (parent == null){
			logger.warn("Tree node is null: "+tree+"\t"+matchingNode);
			return;
		}
		matches.add(new MatchedNode(parent, tag));
		matches.add(new MatchedNode(parent.parent(tree), tag));
	}

}
