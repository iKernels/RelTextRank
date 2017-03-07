package it.unitn.nlpir.nodematchers.lct;

import it.unitn.nlpir.nodematchers.MatchingStrategy;
import it.unitn.nlpir.projectors.MatchedNode;

import java.util.List;
import java.util.regex.Pattern;

import edu.stanford.nlp.trees.Tree;

public class TokenAndAllNonTokenChildrenMatchingStrategy implements MatchingStrategy {
	private static final String TOKEN_PATTERN = "[0-9]+";
	
	@Override
	public void doMatching(Tree tree, Tree matchingNode,
			List<MatchedNode> matches, String tag) {
		matches.add(new MatchedNode(matchingNode, tag));
		Tree[] children = matchingNode.children();
		Pattern p = Pattern.compile(TOKEN_PATTERN);
		for (int i = 0; i < children.length; i++){
			if (!(p.matcher((children[i].label().value())).find()))
				matches.add(new MatchedNode(children[i], tag));
		}

	}

}
