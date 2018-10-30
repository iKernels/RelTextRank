package it.unitn.nlpir.nodematchers.strategies;

import it.unitn.nlpir.projectors.MatchedNode;

import java.util.List;
import java.util.regex.Pattern;

import edu.stanford.nlp.trees.Tree;

public class AllNonTokenChildrenMatchingStrategy implements MatchingStrategy {
	private static final String TOKEN_PATTERN = "[0-9]+";
	
	@Override
	public void doMatching(Tree tree, Tree matchingNode,
			List<MatchedNode> matches, String tag) {
		// TODO Auto-generated method stub
		Tree[] children = matchingNode.children();
		Pattern p = Pattern.compile(TOKEN_PATTERN);
		for (int i = 0; i < children.length; i++){
			if (!(p.matcher((children[i].label().value())).find()))
				matches.add(new MatchedNode(children[i], tag));
		}

	}

}
