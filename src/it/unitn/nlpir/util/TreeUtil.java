package it.unitn.nlpir.util;

import it.unitn.nlpir.types.Chunk;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.uima.jcas.JCas;
import org.cleartk.syntax.dependency.type.DependencyNode;
import org.cleartk.syntax.dependency.type.DependencyRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.StringUtils;


public class TreeUtil {
	private static final Logger logger = LoggerFactory.getLogger(TreeUtil.class);
	public static final String REMOVE_LABEL="REMOVE";
	public static final int INITIAL_PRINT_STRINGBUILDER_SIZE = 1000;

	/**
	 * Converts token ids in the leaves to a chosen token type.
	 * 
	 * @param cas
	 * @param tree
	 * @param leafTextType
	 */

	public static void addTopicToPostag(JCas cas, Tree tree) {
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		Token[] tokens = x.toArray(new Token[x.size()]);

		for (Tree leaf : tree.getLeaves()) {
			Integer id;
			try {
				id = Integer.parseInt(leaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			String topic = tokens[id].getTopic();

			// Skip empty topics
			if (topic == null)
				continue;

			// Get the pos node
			Tree posNode = leaf.parent(tree);
			if (posNode == null) {
				continue;
			}

			// Create a new node with a topic label.
			Tree topicNode = createNode(topic);
			posNode.addChild(topicNode);
		}
	}

	public static void removeLexicals(JCas cas, Tree tree) {
		for (Tree node : tree) {
			if (node.isPreTerminal()) {
				node.setChildren(new Tree[0]);
			}
		}
	}

	public static void finalizeTreeLeaves(JCas cas, Tree tree) {
		finalizeTreeLeaves(cas, tree, TokenTextGetterFactory.TEXT);
	}

	public static void finalizeTreeLeaves(JCas cas, Tree tree, String leafTextType) {
		TokenTextGetter tGetter = TokenTextGetterFactory.getTokenTextGetter(leafTextType);
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		Token[] tokens = x.toArray(new Token[x.size()]);
		for (Tree leaf : tree.getLeaves()) {
			Integer id;
			try {
				id = Integer.parseInt(leaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			String leafText = tGetter.getTokenText(tokens[id]);
			// Fall back on the raw text if the token annotation is not found
			if (leafText == null) {
				leafText = tokens[id].getLemma();
			}
			leafText = leafText.toLowerCase().replace("(", "[").replace(")", "]");
			
			//leafText = leafText.replace("(", "[").replace(")", "]");
			
			// leafText = cleanupWord(leafText);
			setNodeLabel(leaf, leafText);
		}
	}
	
	/**
	 * finalizes the leaves of the tree and adds pos tag to them
	 * @param cas
	 * @param tree
	 * @param leafTextType
	 */
	public static void finalizeTreeLeavesKeepCaseWithPosTag(JCas cas, Tree tree, String leafTextType) {
		TokenTextGetter tGetter = TokenTextGetterFactory.getTokenTextGetter(leafTextType);
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		Token[] tokens = x.toArray(new Token[x.size()]);
		for (Tree leaf : tree.getLeaves()) {
			Integer id;
			try {
				id = Integer.parseInt(leaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			String leafText = tGetter.getTokenText(tokens[id]);
			// Fall back on the raw text if the token annotation is not found
			if (leafText == null) {
				leafText = tokens[id].getLemma();
			}
			leafText = leafText.replace("(", "[").replace(")", "]");
			leafText = String.format("%s::%s", leafText, tokens[id].getPostag().toLowerCase().substring(0, 1));
			
			//leafText = leafText.replace("(", "[").replace(")", "]");
			
			// leafText = cleanupWord(leafText);
			setNodeLabel(leaf, leafText);
		}
	}
	
	

	public static void finalizeTreeLeavesByMap(JCas cas, Tree tree, String leafTextType) {
		finalizeTreeLeavesByMap(cas, tree, leafTextType,false);
	}
	public static void finalizeTreeLeavesByMap(JCas cas, Tree tree, String leafTextType, boolean keepCase) {
		TokenTextGetter tGetter = TokenTextGetterFactory.getTokenTextGetter(leafTextType);
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		//Token[] tokens = x.toArray(new Token[x.size()]);
		Map<Integer,Token> tokens = new HashMap<Integer,Token>();
		for (Token t : x)
			tokens.put(t.getId(),t);
		for (Tree leaf : tree.getLeaves()) {
			Integer id;
			try {
				id = Integer.parseInt(leaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			String leafText = tGetter.getTokenText(tokens.get(id));
			// Fall back on the raw text if the token annotation is not found
			if (leafText == null) {
				leafText = tokens.get(id).getLemma();
			}
			if (!keepCase)
				leafText = leafText.toLowerCase().replace("(", "[").replace(")", "]");
			else
				leafText = leafText.replace("(", "[").replace(")", "]");
			
			//leafText = leafText.replace("(", "[").replace(")", "]");
			
			// leafText = cleanupWord(leafText);
			setNodeLabel(leaf, leafText);
		}
	}

	public static List<Tree> getTreeIntegerNodes(Tree tree){
		TregexPattern tgrepPattern = TregexPattern.compile("/^[0-9]+/");
		
		TregexMatcher m = tgrepPattern.matcher(tree);
		List<Tree> intNodes = new ArrayList<Tree>();
		while (m.find()) {
			 Tree subtree = m.getMatch();
			 intNodes.add(subtree);
		}
		return intNodes;
	}
	
	
	public static List<Tree> getTreeNodesByPattern(Tree tree, String pattern){
		TregexPattern tgrepPattern = TregexPattern.compile(pattern);
		
		TregexMatcher m = tgrepPattern.matcher(tree);
		List<Tree> intNodes = new ArrayList<Tree>();
		while (m.find()) {
			 Tree subtree = m.getMatch();
			 intNodes.add(subtree);
		}
		return intNodes;
	}
	
	
	public static Tree pruneLeavesToRemove(Tree tree) {
		Predicate<Tree> f = new Predicate<Tree>() {
			private static final long serialVersionUID = 1L;
			
			public boolean test(Tree t) {
				boolean a = true;
				if (t.isLeaf())
					a = !t.value().equals(REMOVE_LABEL);
				return a;
			} 
		};
	
		tree = tree.prune(f);
		return tree;
	}
	
	public static Tree pruneLongSentences(Tree tree, int maxSentenceLength) {
		//go through the trees and remove children which go out of boundaries
		
		boolean doprune = false;
		for (Tree s: tree.getChildrenAsList()){
			if (s.getLeaves().size() > maxSentenceLength){
				List<Tree> leaves = s.getLeaves();
				for (int i = maxSentenceLength; i < leaves.size(); i++){
					leaves.get(i).setValue("REMOVE");
				}
				doprune=true;
			}
		}
		
		if (doprune){
			Predicate<Tree> f = new Predicate<Tree>() {
				private static final long serialVersionUID = 1L;
				
				public boolean test(Tree t) {
					boolean a = true;
					if (t.isLeaf())
						a = !t.value().equals("REMOVE");
					return a;
				} 
			};
		
			tree = tree.prune(f);
		}
		return tree;
		
	}
	
	public static void finalizeTreeTokenNodes(JCas cas, Tree tree, String leafTextType) {
		TokenTextGetter tGetter = TokenTextGetterFactory.getTokenTextGetter(leafTextType);
		
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		Token[] tokens = x.toArray(new Token[x.size()]);
		for (Tree leaf : getTreeIntegerNodes(tree)) {
			Integer id;
			try {
				id = Integer.parseInt(leaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			String leafText = tGetter.getTokenText(tokens[id]);
			// Fall back on the raw text if the token annotation is not found
			if (leafText == null) {
				leafText = tokens[id].getLemma();
			}
			leafText = leafText.toLowerCase().replace("(", "[").replace(")", "]");
			// leafText = leafText.replace("(", "[").replace(")", "]");
			// leafText = cleanupWord(leafText);
			setNodeLabel(leaf, leafText);
		}
	}
	

	public static void finalizeTreeTokenNodesRelAsSPTK(JCas cas, Tree tree, String leafTextType) {
		TokenTextGetter tGetter = TokenTextGetterFactory.getTokenTextGetter(leafTextType);
		
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		Token[] tokens = x.toArray(new Token[x.size()]);
		String relLabel = "REL";
		for (Tree leaf : getTreeIntegerNodes(tree)) {
			Integer id;
			try {
				id = Integer.parseInt(leaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			String leafText = tGetter.getTokenText(tokens[id]);
			// Fall back on the raw text if the token annotation is not found
			if (leafText == null) {
				leafText = tokens[id].getLemma();
			}
			
			//check if a parent, a grandparent or a child contains REL label
			boolean addPos = false;
			//StanfordCustomTag.getTag(tree);
			
			Tree parent = leaf.parent(tree);
			if (parent != null){
				Tree grandparent = leaf.parent(tree).parent(tree);
				String customTag = StanfordCustomTag.getTag(parent);
				if (parent.value().contains(relLabel)||((customTag!=null) && customTag.contains(relLabel))){
					addPos = true;
				}
				else if (grandparent!=null){
					customTag = StanfordCustomTag.getTag(grandparent);
					if (grandparent.value().contains(relLabel)||((customTag!=null) && customTag.contains(relLabel))){
						addPos =true;
					}
				}
			}
			if (!addPos) {
				Tree[] children = leaf.children();
				for (int i = 0; i < children.length; i++){
					String customTag = StanfordCustomTag.getTag(children[i]);
					if (children[i].value().contains(relLabel)||((customTag!=null) && customTag.contains(relLabel))){
						addPos = true;
						break;
					}
				}
			}
			
			leafText = leafText.toLowerCase().replace("(", "[").replace(")", "]");
			leafText = addPos ? leafText + "::" + tokens[id].getPostag().toLowerCase().substring(0,1) : leafText;
		
			// leafText = leafText.replace("(", "[").replace(")", "]");
			// leafText = cleanupWord(leafText);
			setNodeLabel(leaf, leafText);
		}
	}
	
	
	public static void finalizeTreeLeavesNoLowerCase(JCas cas, Tree tree, String leafTextType) {
		TokenTextGetter tGetter = TokenTextGetterFactory.getTokenTextGetter(leafTextType);
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		Token[] tokens = x.toArray(new Token[x.size()]);
		for (Tree leaf : tree.getLeaves()) {
			Integer id;
			try {
				id = Integer.parseInt(leaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			String leafText = tGetter.getTokenText(tokens[id]);
			// Fall back on the raw text if the token annotation is not found
			if (leafText == null) {
				leafText = tokens[id].getLemma();
			}
			leafText = leafText.replace("(", "[").replace(")", "]");
			// leafText = leafText.replace("(", "[").replace(")", "]");
			// leafText = cleanupWord(leafText);
			setNodeLabel(leaf, leafText);
		}
	}
	
	public static String cleanupWord(String text) {
		String wordText = CharMatcher.DIGIT.collapseFrom(
				CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(text), '0');
		if (wordText.isEmpty())
			return "x";
		return wordText.toLowerCase();
	}

	public static Tree buildTree(String treeString) {
		Tree tree = null;
		try {
			InputStream is = new ByteArrayInputStream(treeString.getBytes());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			tree = (new PennTreeReader(br)).readTree();
		} catch (IOException e) {
			System.err.println(treeString);
			e.printStackTrace();
		}
		return tree;
	}

	
	
	public static Tree buildDiscourceChunkTreeTree(String treeString) {
		Tree tree = null;
		try {
			InputStream is = new ByteArrayInputStream(treeString.getBytes());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			tree = (new PennTreeReader(br)).readTree();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tree;
	}
	
	
	public static void markNode(Tree node, String tag) {
		if (node == null || node.nodeString().toLowerCase().equals("root")) {
			return;
		}
		StanfordCustomTag.setTag(node, tag);
	}

	
	
	
	public static void markNodeExclusive(Tree node, String tag) {
		if (node == null || node.nodeString().toLowerCase().equals("root")) {
			return;
		}
		if (StanfordCustomTag.getTag(node)!=null){
			return;
		}
		StanfordCustomTag.setTag(node, tag);
	}
	
	public static void markNodeNonDupPrepend(Tree node, String tag) {
		if (node == null || node.nodeString().toLowerCase().equals("root")) {
			return;
		}
		
		String customTag = StanfordCustomTag.getTag(node);
		if (customTag!=null){
			
			if (!customTag.contains(tag)){
				tag = tag+"-"+customTag;
				StanfordCustomTag.setTag(node, tag);
			}
		}
		else{	
			StanfordCustomTag.setTag(node, tag);
		}
	}
	
	
	/**
	 * Temporary solution for the mixed matching
	 * @param node
	 * @param tag
	 */
	public static void markNonTreeNode(Tree node, String tag) {
		if (node == null || node.nodeString().toLowerCase().equals("root")) {
			return;
		}
		if ( TreeUtil.buildTree(tag)!=null){
			return;
		}
		StanfordCustomTag.setTag(node, tag);
	}

	
	
	public static void setNodeLabel(Tree node, String label) {
		node.setLabel(CoreLabel.factory().newLabel(label));
	}

	public static Tree createNode(String label) {
		Tree node = new LabeledScoredTreeNode();
		node.setLabel(CoreLabel.factory().newLabel(label));
		return node;
	}

	public static int numberOfNodes(String treeString) {
		int num = 0;

		for (int i = 0; i < treeString.length(); i++) {
			if (treeString.charAt(i) == '(') {
				num++;
			}
		}

		return num;
	}

	private static void serializeTreeSemicolon(Tree tree, StringBuilder sb) {
		if (!tree.isLeaf()) {
			String label = tree.nodeString();
			String tag = StanfordCustomTag.getTag(tree);
			if (!Strings.isNullOrEmpty(tag)) {
				label = String.format("%s;%s", tag, label);
			}
			sb.append("(" + label);
			Tree[] children = tree.children();
			if (children != null) {
				for (Tree child : children) {
					sb.append(" ");
					serializeTreeSemicolon(child, sb);
				}
			}
			sb.append(")");
		} else {
			String label = tree.nodeString();
			String tag = StanfordCustomTag.getTag(tree);
			if (!Strings.isNullOrEmpty(tag)) {
				label = String.format("%s;%s", tag, label);
			}
			sb.append("(" + label + ")");
		}
	}
	
	private static void serializeTree(Tree tree, StringBuilder sb) {
		if (!tree.isLeaf()) {
			String label = tree.nodeString();
			String tag = StanfordCustomTag.getTag(tree);
			if (!Strings.isNullOrEmpty(tag)) {
				label = String.format("%s-%s", tag, label);
			}
			sb.append("(" + label);
			Tree[] children = tree.children();
			if (children != null) {
				for (Tree child : children) {
					sb.append(" ");
					serializeTree(child, sb);
				}
			}
			sb.append(")");
		} else {
			String label = tree.nodeString();
			String tag = StanfordCustomTag.getTag(tree);
			if (!Strings.isNullOrEmpty(tag)) {
				label = String.format("%s-%s", tag, label);
			}
			sb.append("(" + label + ")");
		}
	}

	// Should be used on Tree class from edu.stanford.nlp.tree instead of
	// toString() method.
	// This method produces well-formed trees for the tree parse inside
	// SVM-Light-TK, which
	// was previously causing errors.
	public static String serializeTree(Tree tree) {
		StringBuilder sb = new StringBuilder(INITIAL_PRINT_STRINGBUILDER_SIZE);
		serializeTree(tree, sb);
		return sb.toString();
	}

	// Should be used on Tree class from edu.stanford.nlp.tree instead of
		// toString() method.
		// This method produces well-formed trees for the tree parse inside
		// SVM-Light-TK, which
		// was previously causing errors.
	/**
	 * Delimit stanford tag by ;
	 * @param tree
	 * @return
	 */
	public static String serializeTreeSemicolon(Tree tree) {
			StringBuilder sb = new StringBuilder(INITIAL_PRINT_STRINGBUILDER_SIZE);
			serializeTreeSemicolon(tree, sb);
			return sb.toString();
	}
		
	public static Map<Integer, Tree> getTokenIdToTreeNodeMap(Tree tree){
		Map<Integer,Tree> lMap = new HashMap<Integer,Tree>();
		for (Tree qLeaf : tree.getLeaves()){
			if (StringUtils.isNumeric(qLeaf.value()))
				lMap.put(Integer.valueOf(qLeaf.value()), qLeaf);
		}
		return lMap;
	}
	
	public static Tree[] buildTokenId2TreeLeafIndex(List<Token> tokens, Tree tree) {
		Tree[] leafNodes = new Tree[tokens.size()];
		for (Tree qLeaf : tree.getLeaves()) {
			Integer qTokenId;
			try {
				qTokenId = Integer.parseInt(qLeaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			leafNodes[qTokenId] = qLeaf;
		}
		return leafNodes;
	}
	public static Tree buildPhraseDependencyTree(JCas cas) {
		Collection<Token> tokens = JCasUtil.select(cas, Token.class);
		Integer[] tokenParents = new Integer[tokens.size()];
		String[] relationLabels = new String[tokens.size()];
		String[] nodes = new String[tokens.size()];
		for (DependencyNode node : JCasUtil.select(cas, DependencyNode.class)) {
			Token tNode = JCasUtil.selectCovered(Token.class, node).get(0);
			int idNode = tNode.getId();
			nodes[idNode] = tNode.getCoveredText();
			Collection<DependencyRelation> depRels = JCasUtil.select(node.getHeadRelations(),
					DependencyRelation.class);
			if (depRels.isEmpty()) {
				tokenParents[idNode] = -1;
			}
			for (DependencyRelation rel : depRels) {
				DependencyNode head = rel.getHead();
				String label = rel.getRelation();
				Token tHead = JCasUtil.selectCovered(Token.class, head).get(0);
				int idHead = tHead.getId();
				tokenParents[idNode] = idHead;
				relationLabels[idNode] = label;
				logger.debug(String.format("%s(%s, %s)", label, head.getCoveredText(), node.getCoveredText()));
			}

		}
		logger.debug("Relation ids: %s", Arrays.asList(tokenParents).toString());
		logger.debug("Relation labels: %s", Arrays.asList(relationLabels).toString());

		Integer[] token2chunk = new Integer[tokens.size()];
		Chunk[] chunks = JCasUtil.select(cas, Chunk.class).toArray(new Chunk[0]);

		// Create chunk nodes
		Integer[] chunkParents = new Integer[chunks.length];
		String[] chunkRelationLabels = new String[chunks.length];
		Tree[] chunkNodes = new Tree[chunks.length];
		for (int i = 0; i < chunks.length; i++) {
			Chunk ch = chunks[i];
			Tree node = TreeUtil.createNode(ch.getChunkType());
			chunkNodes[i] = node;
			for (Token t : JCasUtil.selectCovered(Token.class, ch)) {
				int id = t.getId();
				token2chunk[id] = i;
				// Tree leafNode = TreeUtil.createNode(t.getLemma().replace("(",
				// "{").replace(")", "}"));
				Tree leafNode = TreeUtil.createNode(String.valueOf(t.getId()));
				Tree posNode = TreeUtil.createNode(t.getPostag().replace("(", "{")
						.replace(")", "}"));
				posNode.addChild(leafNode);
				node.addChild(posNode);
			}
		}
		logger.debug("Token2chunk: %s", Arrays.asList(token2chunk).toString());

		for (int i = 0; i < chunks.length; i++) {
			Chunk ch = chunks[i];
			for (Token t : JCasUtil.selectCovered(Token.class, ch)) {
				int id = t.getId();
				int parentId = tokenParents[id];
				if (parentId < 0 || token2chunk[parentId] == null) {
					chunkParents[i] = -1;
					chunkRelationLabels[i] = "root";
					break;
				} else {
					Integer parentChunkId = token2chunk[parentId];
					if (parentChunkId != i) {
						chunkParents[i] = parentChunkId;
						chunkRelationLabels[i] = relationLabels[id];
					}
				}
			}
			logger.debug("%s: %s", ch.getChunkType(), ch.getCoveredText());
		}
		logger.debug("Chunk parent ids: %s", Arrays.asList(chunkParents).toString());
		logger.debug("Chunk Relation labels: %s", Arrays.asList(chunkRelationLabels).toString());

		Tree[] chunkRelNodes = new Tree[chunkNodes.length];
		// Construct chunk rel nodes (for alternative dep tree)
		for (int i = 0; i < chunkNodes.length; i++) {
			chunkRelNodes[i] = TreeUtil.createNode(chunkRelationLabels[i]);
		}

		// Construct a tree
		Tree chRoot = TreeUtil.createNode("ROOT");
		for (int i = 0; i < chunkNodes.length; i++) {
			Tree node = chunkNodes[i];

			Tree relNode = chunkRelNodes[i];
			relNode.addChild(node);

			int parentId = chunkParents[i];
			if (parentId == -1) {
				chRoot.addChild(relNode);
			} else {
				Tree parentNode = chunkRelNodes[parentId];
				parentNode.addChild(relNode);
			}
		}
		// TreeUtil.finalizeTreeLeaves(cas, chRoot,
		// TokenTextGetterFactory.LEMMA);
		// logger.info("Chunk Tree: %s",
		// TreeUtil.serializeTree(chRoot).replace("(", "[").replace(")", "]"));
		return chRoot;
	}

	public static List<Tree> getChunks(Tree tree) {
		List<Tree> chunks = new ArrayList<>();
		for (Tree node : tree) {
			if (node.isPrePreTerminal()) {
				chunks.add(node);
			}
		}
		return chunks;
	}
	
	public static List<Tree> getPreterminals(Tree tree) {
		List<Tree> chunks = new ArrayList<>();
		for (Tree node : tree) {
			if (node.isPreTerminal()) {
				chunks.add(node);
			}
		}
		return chunks;
	}
	
	public static List<Tree> getChunks(Tree tree, Predicate<Tree> filter) {
		List<Tree> chunks = new ArrayList<>();
		for (Tree node : tree) {
			Tree nodeCopy = node.deepCopy();
			nodeCopy = nodeCopy.prune(filter);
			if ((nodeCopy!=null) && (nodeCopy.isPrePreTerminal())) {
				chunks.add(node);
			}
		}
		return chunks;
	}

	
	
}
