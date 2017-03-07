package it.unitn.nlpir.util;

import it.unitn.nlpir.tree.PosChunkWithSpanMarksTreeBuilder;
import it.unitn.nlpir.tree.PosChunkWithSpanMarksTreeBuilder.Coord;
import it.unitn.nlpir.tree.TreeNode;
import it.unitn.nlpir.types.Chunk;
import it.unitn.nlpir.types.DiscourseTree;
import it.unitn.nlpir.types.Sentence;
import it.unitn.nlpir.types.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.syntax.dependency.type.DependencyNode;
import org.cleartk.syntax.dependency.type.DependencyRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;

public class PhraseDependencyTree {
	private static final Logger logger = LoggerFactory.getLogger(PhraseDependencyTree.class);

	protected JCas cas;
	protected HashMap<Integer, Chunk> token2chunk;
	public static final String GR_PREFIX = "GR-";
	public static final String POS_PREFIX = "POS-";
	public PhraseDependencyTree(JCas cas) {
		this.cas = cas;
	}

	public class DepRelation {
		public DepNode head;
		public DepNode child;
		public String relation;

		public DepNode getHead() {
			return head;
		}

		public void setHead(DepNode head) {
			this.head = head;
		}

		public DepNode getChild() {
			return child;
		}

		public void setChild(DepNode child) {
			this.child = child;
		}

		public String getRelation() {
			return relation;
		}

		public void setLabel(String label) {
			this.relation = label;
		}

		public DepRelation(String label, DepNode head, DepNode child) {
			this.relation = label;
			this.head = head;
			this.child = child;
		}

		@Override
		public String toString() {
			return String.format("%s(%s, %s)", this.relation, this.head, this.child);
		}
	}

	public class DepNode {
		public List<DepRelation> headRelations;
		public List<DepRelation> childRelations;
		public int begin;

		public int getBegin() {
			return begin;
		}

		public void setBegin(int begin) {
			this.begin = begin;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}

		public int end;
		public JCas cas;

		public DepNode(JCas cas, int begin, int end) {
			this.cas = cas;
			this.begin = begin;
			this.end = end;
			this.headRelations = new ArrayList<>();
			this.childRelations = new ArrayList<>();
		}

		public List<DepRelation> getHeadRelations() {
			return headRelations;
		}

		public void setHeadRelations(List<DepRelation> headRelations) {
			this.headRelations = headRelations;
		}

		public List<DepRelation> getChildRelations() {
			return childRelations;
		}

		public void setChildRelations(List<DepRelation> childRelations) {
			this.childRelations = childRelations;
		}

		public boolean isLeaf() {
			return this.childRelations.isEmpty();
		}

		public Token getFirstToken() {
			return JCasUtil.selectCovered(this.cas, Token.class, this.begin, this.end).get(0);
		}

		public String getCoveredText() {
			return this.toString();
		}

		@Override
		public String toString() {
			List<String> text = new ArrayList<>();
			for (Token token : JCasUtil.selectCovered(this.cas, Token.class, this.begin, this.end)) {
				text.add(token.getCoveredText());
			}
			return Joiner.on(" ").join(text);
		}
	}

	private DepNode getRootNode(Collection<DepNode> nodes) {
		DepNode root = null;
		for (DepNode node : nodes) {
			if (node.getHeadRelations().isEmpty()) {
				root = node;
				break;
			}
		}
		return root;
	}

	private void yieldNodesRec(DepNode node, Collection<DepNode> nodes) {
		if (node.isLeaf()) {
			nodes.add(node);
		} else {
			nodes.add(node);
			for (DepRelation rel : node.getChildRelations()) {
				yieldNodesRec(rel.getChild(), nodes);
			}
		}
	}

	private Collection<DepNode> yieldNodes(DepNode node) {
		List<DepNode> nodes = new ArrayList<>();
		yieldNodesRec(node, nodes);
		return nodes;
	}

	private DepNode getOrAddIfNew(HashMap<DependencyNode, DepNode> uima2pojos, DependencyNode node) {
		DepNode n = uima2pojos.get(node);
		if (n == null) {
			n = new DepNode(cas, node.getBegin(), node.getEnd());
			uima2pojos.put(node, n);
		}
		return n;
	}

	/**
	 * Converts UIMA types DependencyNode, DependencyRelation to DepNode,
	 * DepRelation that are easy to work with.
	 * 
	 * @param cas
	 * @return
	 */
	private Collection<DepNode> convertUima2pojos(Collection<DependencyNode> nodes) {
		HashMap<DependencyNode, DepNode> uima2pojos = new HashMap<>();
		for (DependencyNode node : nodes) {
			DepNode n = getOrAddIfNew(uima2pojos, node);
			for (DependencyRelation r : JCasUtil.select(node.getChildRelations(),
					DependencyRelation.class)) {
				DepNode c = getOrAddIfNew(uima2pojos, r.getChild());
				n.childRelations.add(new DepRelation(r.getRelation(), n, c));
			}
			for (DependencyRelation r : JCasUtil.select(node.getHeadRelations(),
					DependencyRelation.class)) {
				DepNode h = getOrAddIfNew(uima2pojos, r.getHead());
				n.headRelations.add(new DepRelation(r.getRelation(), h, n));
			}
		}
		Iterator<Entry<DependencyNode, DepNode>> it = uima2pojos.entrySet().iterator();

		// Remove dangling nodes
		while (it.hasNext()) {
			DepNode node = it.next().getValue();
			if (node.getChildRelations().isEmpty() && node.getHeadRelations().isEmpty()) {
				it.remove();
			}
		}
		return uima2pojos.values();
	}

	private HashMap<Integer, Chunk> buildToken2ChunkMap(Sentence sent) {
		HashMap<Integer, Chunk> token2chunk = new HashMap<>();
		for (Chunk ch : JCasUtil.selectCovered(Chunk.class, sent)) {
			for (Token token : JCasUtil.selectCovered(Token.class, ch)) {
				token2chunk.put(token.getId(), ch);
			}
		}
		return token2chunk;
	}

	private void mergeNodes(DepNode node, DepNode child) {
		node.setBegin(Math.min(node.getBegin(), child.getBegin()));
		node.setEnd(Math.max(node.getEnd(), child.getEnd()));
		for (DepRelation r : child.getChildRelations()) {
			r.setHead(node);
			node.getChildRelations().add(r);
		}
	}

	private DepNode merge(DepNode node, HashMap<Integer, Chunk> token2chunk) {
		if (node.isLeaf()) {
			return node;
		}
		Chunk nodeChunkId = token2chunk.get(node.getFirstToken().getId());
		DepNode newNode = new DepNode(cas, node.getBegin(), node.getEnd());
		for (DepRelation rel : node.getChildRelations()) {
			String relLabel = rel.getRelation();
			DepNode child = merge(rel.getChild(), token2chunk);
			Chunk childChunkId = token2chunk.get(child.getFirstToken().getId());
			if (rel.getRelation().equals("pobj")){
				logger.debug(rel.getRelation());
			}
			if (nodeChunkId != null && childChunkId != null && nodeChunkId.equals(childChunkId)
			// Collapse only prep and pobj rels
					|| rel.getRelation().equals("pobj")
					|| rel.getRelation().toLowerCase().equals("pmod")
					|| rel.getRelation().toLowerCase().equals("case")
					|| rel.getRelation().endsWith(":poss")
					// || rel.getRelation().equals("prep")
					// Collapse even more dependency relations
					// || rel.getRelation().equals("cc")
					// rel.getRelation().equals("amod") ||
					// rel.getRelation().equals("conj") ||
					|| rel.getRelation().startsWith("poss")
			// rel.getRelation().startsWith("aux")
			) {
				logger.debug("{} + {}", newNode, child);
				mergeNodes(newNode, child);
				logger.debug("	-> {}", newNode);
			} else {
				newNode.getChildRelations().add(new DepRelation(relLabel, newNode, child));
			}
		}
		for (DepRelation rel : newNode.getChildRelations()) {
			DepNode child = rel.getChild();
			child.getHeadRelations().clear();
			child.getHeadRelations().add(new DepRelation(rel.getRelation(), newNode, child));
		}
		return newNode;
	}

	private String printDepTree(DepNode node) {
		if (node.isLeaf()) {
			return String.format("[%s]", node);
		} else {
			List<String> out = new ArrayList<>();
			for (DepRelation rel : node.getChildRelations()) {
				out.add(String.format("[%s %s]", rel.getRelation(), printDepTree(rel.getChild())));
			}
			return String.format("[%s %s]", node, Joiner.on(" ").join(out));
		}
	}

	private String printAltDepTree(DepNode node) {
		if (node.isLeaf()) {
			return String.format("[%s]", node);
		} else {
			List<String> out = new ArrayList<>();
			for (DepRelation rel : node.getChildRelations()) {
				out.add(String.format("[%s %s]", rel.getRelation(), printAltDepTree(rel.getChild())));
			}
			return String.format("[%s] %s", node, Joiner.on(" ").join(out));
		}
	}

	private void printDepNodes(Collection<DepNode> nodes) {
		for (DepNode node : nodes) {
			logger.info(String.format("%s | heads: %s | child: %s", node, node.headRelations, node.childRelations));
		}
	}

	private String cleanupLexical(String wordText) {
		return CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(wordText);
	}
	
	protected void addPosNodes(DepNode srcNode, Tree chNode) {
		// Add the PosTags of the covered tokens
		for (Token token : JCasUtil.selectCovered(this.cas, Token.class, srcNode.getBegin(),
				srcNode.getEnd())) {
			
			Tree posNode = TreeUtil.createNode(token.getPostag());
			Tree tNode = TreeUtil.createNode(String.valueOf(token.getId()));
			posNode.addChild(tNode);
			chNode.addChild(posNode);
		}
	}
	
	protected void addPosNodes(DepNode srcNode, Chunk chunk, Tree chNode) {
		// Add the PosTags of the covered tokens
		for (Token token : JCasUtil.selectCovered(this.cas, Token.class, chunk.getBegin(),
				chunk.getEnd())) {
			Tree posNode = TreeUtil.createNode(token.getPostag());
			Tree tNode = TreeUtil.createNode(String.valueOf(token.getId()));
			posNode.addChild(tNode);
			chNode.addChild(posNode);
		}
	}
	private void addLexNodes(DepNode srcNode, Tree chNode){
		// Add the PosTags of the covered tokens
		for (Token token : JCasUtil.selectCovered(this.cas, Token.class, srcNode.getBegin(),
				srcNode.getEnd())) {
			Tree tNode = TreeUtil.createNode(String.valueOf(token.getId()));
			chNode.addChild(tNode);
		}
	}

	private Tree buildTree(Collection<DepNode> depNodes) {
		HashMap<Integer, DepNode> nodes = new HashMap<>();
		HashMap<Integer, Tree> treeNodes = new HashMap<>();
		HashMap<Integer, Tree> treeRelNodes = new HashMap<>();
		List<Integer> nodeIds = new ArrayList<>();
		for (DepNode node : depNodes) {
			int idNode = node.getBegin();
			nodeIds.add(idNode);
			nodes.put(idNode, node);

			treeNodes.put(idNode, TreeUtil.createNode(String.valueOf(node).replace(" ", "-")));
			List<DepRelation> headRels = node.getHeadRelations();
			if (headRels.isEmpty()) {
				treeRelNodes.put(idNode, TreeUtil.createNode("root"));
			} else {
				for (DepRelation rel : headRels) {
					treeRelNodes.put(idNode, TreeUtil.createNode(rel.getRelation()));
				}
			}
		}

		// Process tokens in their natural order
		Collections.sort(nodeIds);
		Tree root = TreeUtil.createNode("ROOT");
		for (Integer id : nodeIds) {
			DepNode node = nodes.get(id);
			Tree treeNode = treeNodes.get(id);
			Tree treeRelNode = treeRelNodes.get(id);

			treeRelNode.addChild(treeNode);

			List<DepRelation> rels = node.getHeadRelations();
			if (rels.isEmpty()) {
				root.addChild(treeRelNode);
			} else {
				for (DepRelation rel : rels) {
					DepNode head = rel.getHead();
					Tree parent = treeNodes.get(head.getBegin());
					parent.addChild(treeRelNode);
				}
			}
		}
		return root;
	}

	
	private Tree buildAltTree(Collection<DepNode> depNodes, boolean addPosTags) {
		HashMap<Integer, DepNode> nodes = new HashMap<>();
		HashMap<Integer, Tree> treeRelNodes = new HashMap<>();
		List<Integer> nodeIds = new ArrayList<>();
		for (DepNode node : depNodes) {

			int idNode = node.getBegin();
			nodeIds.add(idNode);
			nodes.put(idNode, node);

			List<DepRelation> headRels = node.getHeadRelations();
			if (headRels.isEmpty()) {
				treeRelNodes.put(idNode, TreeUtil.createNode("root"));
			} else {
				for (DepRelation rel : headRels) {
					treeRelNodes.put(idNode, TreeUtil.createNode(rel.getRelation()));
				}
			}
		}

		// Process tokens in their natural order
		Collections.sort(nodeIds);
		Tree root = null;
		for (Integer id : nodeIds) {
			DepNode node = nodes.get(id);
			Tree treeRelNode = treeRelNodes.get(id);
			if (addPosTags)
				addPosNodes(node, treeRelNode);
			else
				addLexNodes(node, treeRelNode);

			List<DepRelation> rels = node.getHeadRelations();
			if (rels.isEmpty()) {
				root = treeRelNode;
			} else {
				for (DepRelation rel : rels) {
					DepNode head = rel.getHead();
					Tree parent = treeRelNodes.get(head.getBegin());
					parent.addChild(treeRelNode);
				}
			}
		}
		return root;
	}
	
	protected Tree buildAltTreeWithOnlyChunks(Collection<DepNode> depNodes) {
		HashMap<Integer, DepNode> nodes = new HashMap<>();
		// HashMap<Integer, Tree> treeNodes = new HashMap<>();
		HashMap<Integer, Tree> treeRelNodes = new HashMap<>();
		List<Integer> nodeIds = new ArrayList<>();
		for (DepNode node : depNodes) {
			int idNode = node.getBegin();
			nodeIds.add(idNode);
			nodes.put(idNode, node);

			List<DepRelation> headRels = node.getHeadRelations();
			if (headRels.isEmpty()) {
				treeRelNodes.put(idNode, TreeUtil.createNode("root"));
			} else {
				for (DepRelation rel : headRels) {
					treeRelNodes.put(idNode, TreeUtil.createNode(rel.getRelation()));
				}
			}
		}

		// Process tokens in their natural order
		Collections.sort(nodeIds);
		Tree root = TreeUtil.createNode("ROOT");
		for (Integer id : nodeIds) {
			DepNode node = nodes.get(id);
			// Tree treeNode = treeNodes.get(id);
			Tree treeRelNode = treeRelNodes.get(id);

			String chunkLabel = null;

			// Select chunks spanned by the node and force the chunk label to be
			// NP
			// if there is at least one NP chunk
			
			for (Chunk ch : JCasUtil.selectCovered(this.cas, Chunk.class, node.getBegin(),
					node.getEnd())) {
				chunkLabel = ch.getChunkType();
				// Tree chNode = TreeUtil.createNode(chunkLabel);
				Tree chNode = new TreeNode(CoreLabel.factory().newLabel(chunkLabel), node.getBegin(),
						node.getEnd());
				addPosNodes(node, ch, chNode);
				treeRelNode.addChild(chNode);

				
			}
			if (chunkLabel==null) continue;
			List<DepRelation> rels = node.getHeadRelations();
			if (rels.isEmpty()) {
				root.addChild(treeRelNode);
			} else {
				for (DepRelation rel : rels) {
					DepNode head = rel.getHead();
					Tree parent = treeRelNodes.get(head.getBegin());
					parent.addChild(treeRelNode);
				}
			}
			// If no chunk found that completely covers the considered span of
			// tokens,
			// try to resolve using the token2chunk map
			/*if (chunkLabel == null) {
				for (Token t : JCasUtil.selectCovered(this.cas, Token.class, node.getBegin(),
						node.getEnd())) {
					Chunk ch = this.token2chunk.get(t.getId());
					if (ch != null) {
						chunkLabel = ch.getChunkType();
					}
				}
			}
			if (chunkLabel == null) {
				chunkLabel = "O";
			}*/

			
		}
		return root;
	}

	protected Tree buildAltTreeWithChunks(Collection<DepNode> depNodes) {
		HashMap<Integer, DepNode> nodes = new HashMap<>();
		// HashMap<Integer, Tree> treeNodes = new HashMap<>();
		HashMap<Integer, Tree> treeRelNodes = new HashMap<>();
		List<Integer> nodeIds = new ArrayList<>();
		for (DepNode node : depNodes) {
			int idNode = node.getBegin();
			nodeIds.add(idNode);
			nodes.put(idNode, node);

			List<DepRelation> headRels = node.getHeadRelations();
			if (headRels.isEmpty()) {
				treeRelNodes.put(idNode, TreeUtil.createNode("root"));
			} else {
				for (DepRelation rel : headRels) {
					treeRelNodes.put(idNode, TreeUtil.createNode(rel.getRelation()));
				}
			}
		}

		// Process tokens in their natural order
		Collections.sort(nodeIds);
		Tree root = TreeUtil.createNode("ROOT");
		for (Integer id : nodeIds) {
			DepNode node = nodes.get(id);
			// Tree treeNode = treeNodes.get(id);
			Tree treeRelNode = treeRelNodes.get(id);

			String chunkLabel = null;

			// Select chunks spanned by the node and force the chunk label to be
			// NP
			// if there is at least one NP chunk
			for (Chunk ch : JCasUtil.selectCovered(this.cas, Chunk.class, node.getBegin(),
					node.getEnd())) {
				chunkLabel = ch.getChunkType();
				if (chunkLabel.startsWith("N"))
					break;
			}

			// If no chunk found that completely covers the considered span of
			// tokens,
			// try to resolve using the token2chunk map
			if (chunkLabel == null) {
				for (Token t : JCasUtil.selectCovered(this.cas, Token.class, node.getBegin(),
						node.getEnd())) {
					Chunk ch = this.token2chunk.get(t.getId());
					if (ch != null) {
						chunkLabel = ch.getChunkType();
					}
				}
			}
			if (chunkLabel == null) {
				chunkLabel = "O";
			}

			// Tree chNode = TreeUtil.createNode(chunkLabel);
			Tree chNode = new TreeNode(CoreLabel.factory().newLabel(chunkLabel), node.getBegin(),
					node.getEnd());
			addPosNodes(node, chNode);
			treeRelNode.addChild(chNode);

			List<DepRelation> rels = node.getHeadRelations();
			if (rels.isEmpty()) {
				root.addChild(treeRelNode);
			} else {
				for (DepRelation rel : rels) {
					DepNode head = rel.getHead();
					Tree parent = treeRelNodes.get(head.getBegin());
					parent.addChild(treeRelNode);
				}
			}
		}
		return root;
	}

	protected Tree buildAltTreeWithChunksAndPunkt(Collection<DepNode> depNodes) {
		HashMap<Integer, DepNode> nodes = new HashMap<>();
		// HashMap<Integer, Tree> treeNodes = new HashMap<>();
		HashMap<Integer, Tree> treeRelNodes = new HashMap<>();
		List<Integer> nodeIds = new ArrayList<>();
		for (DepNode node : depNodes) {
			int idNode = node.getBegin();
			nodeIds.add(idNode);
			nodes.put(idNode, node);

			List<DepRelation> headRels = node.getHeadRelations();
			if (headRels.isEmpty()) {
				treeRelNodes.put(idNode, TreeUtil.createNode("root"));
			} else {
				for (DepRelation rel : headRels) {
					treeRelNodes.put(idNode, TreeUtil.createNode(rel.getRelation()));
				}
			}
		}

		// Process tokens in their natural order
		Collections.sort(nodeIds);
		Tree root = TreeUtil.createNode("ROOT");
		for (Integer id : nodeIds) {
			DepNode node = nodes.get(id);
			// Tree treeNode = treeNodes.get(id);
			Tree treeRelNode = treeRelNodes.get(id);

			String chunkLabel = null;

			// Select chunks spanned by the node and force the chunk label to be
			// NP
			// if there is at least one NP chunk
			for (Chunk ch : JCasUtil.selectCovered(this.cas, Chunk.class, node.getBegin(),
					node.getEnd())) {
				chunkLabel = ch.getChunkType();
				if (chunkLabel.startsWith("N"))
					break;
			}

			// If no chunk found that completely covers the considered span of
			// tokens,
			// try to resolve using the token2chunk map
			if (chunkLabel == null) {
				for (Token t : JCasUtil.selectCovered(this.cas, Token.class, node.getBegin(),
						node.getEnd())) {
					Chunk ch = this.token2chunk.get(t.getId());
					if (ch != null) {
						chunkLabel = ch.getChunkType();
					}
				}
			}
			if (chunkLabel == null) {
				chunkLabel = "O";
			}

			// Tree chNode = TreeUtil.createNode(chunkLabel);
			Tree chNode = new TreeNode(CoreLabel.factory().newLabel(chunkLabel), node.getBegin(),
					node.getEnd());
			addPosNodes(node, chNode);
			treeRelNode.addChild(chNode);

			List<DepRelation> rels = node.getHeadRelations();
			if (rels.isEmpty()) {
				root.addChild(treeRelNode);
			} else {
				for (DepRelation rel : rels) {
					DepNode head = rel.getHead();
					Tree parent = treeRelNodes.get(head.getBegin());
					parent.addChild(treeRelNode);
				}
			}
		}
		return root;
	}
	
	
	protected Tree buildAltTreeWithChunksSpanMark(Collection<DepNode> depNodes, List<Coord> spans) {
		HashMap<Integer, DepNode> nodes = new HashMap<>();
		// HashMap<Integer, Tree> treeNodes = new HashMap<>();
		HashMap<Integer, Tree> treeRelNodes = new HashMap<>();
		List<Integer> nodeIds = new ArrayList<>();
		for (DepNode node : depNodes) {
			int idNode = node.getBegin();
			nodeIds.add(idNode);
			nodes.put(idNode, node);

			List<DepRelation> headRels = node.getHeadRelations();
			if (headRels.isEmpty()) {
				treeRelNodes.put(idNode, TreeUtil.createNode("root"));
			} else {
				for (DepRelation rel : headRels) {
					treeRelNodes.put(idNode, TreeUtil.createNode(rel.getRelation()));
				}
			}
		}

		// Process tokens in their natural order
		Collections.sort(nodeIds);
		Tree root = TreeUtil.createNode("ROOT");
		for (Integer id : nodeIds) {
			DepNode node = nodes.get(id);
			// Tree treeNode = treeNodes.get(id);
			Tree treeRelNode = treeRelNodes.get(id);

			String chunkLabel = null;
			String spanLabel = "SPAN";
			//int nodeIND=-1;
			for (int i = 0; i < spans.size(); i++){
				if ((spans.get(i).getBegin()<=node.getBegin())&&(spans.get(i).getEnd()>node.getBegin())){
					//nodeIND = i;
					spanLabel = spanLabel+String.valueOf(i);
				}
			}
			
			// Select chunks spanned by the node and force the chunk label to be
			// NP
			// if there is at least one NP chunk
			for (Chunk ch : JCasUtil.selectCovered(this.cas, Chunk.class, node.getBegin(),
					node.getEnd())) {
				chunkLabel = ch.getChunkType();
				if (chunkLabel.startsWith("N"))
					break;
			}

			// If no chunk found that completely covers the considered span of
			// tokens,
			// try to resolve using the token2chunk map
			if (chunkLabel == null) {
				for (Token t : JCasUtil.selectCovered(this.cas, Token.class, node.getBegin(),
						node.getEnd())) {
					Chunk ch = this.token2chunk.get(t.getId());
					if (ch != null) {
						chunkLabel = ch.getChunkType();
					}
				}
			}
			if (chunkLabel == null) {
				chunkLabel = "O";
			}

			
			chunkLabel = chunkLabel+spanLabel;
			// Tree chNode = TreeUtil.createNode(chunkLabel);
			Tree chNode = new TreeNode(CoreLabel.factory().newLabel(chunkLabel), node.getBegin(),
					node.getEnd());
			addPosNodes(node, chNode);
			treeRelNode.addChild(chNode);

			List<DepRelation> rels = node.getHeadRelations();
			if (rels.isEmpty()) {
				root.addChild(treeRelNode);
			} else {
				for (DepRelation rel : rels) {
					DepNode head = rel.getHead();
					Tree parent = treeRelNodes.get(head.getBegin());
					parent.addChild(treeRelNode);
				}
			}
		}
		return root;
	}
	
	
	public Tree buildDepTree() {
		Tree root = TreeUtil.createNode("ROOT");
		for (Sentence sent : JCasUtil.select(cas, Sentence.class)) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			Tree tree = buildAltTree(nodes, false);
			if (tree != null)
				root.addChild(tree);
		}
		return root;
	}
	
	public Tree buildPosTagDepTree() {
		Tree root = TreeUtil.createNode("ROOT");
		for (Sentence sent : JCasUtil.select(cas, Sentence.class)) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			Tree tree = buildAltTree(nodes, true);
			if (tree != null)
				root.addChild(tree);
		}
		return root;
	}

	
	protected void removeLeaveParents(Tree leaf, Tree root){
		Tree parent  = leaf.parent(root);
		parent.removeChild(parent.objectIndexOf(leaf));
		if (parent.isLeaf()){
			removeLeaveParents(parent,root);
		}
	}
	public Tree buildPosTagDepTreeWithChunksIfNeeded() {
		Tree root = TreeUtil.createNode("ROOT");
		
		
		
		for (Sentence sent : JCasUtil.select(cas, Sentence.class)) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			Tree tree = buildAltTree(nodes, true);
			if (tree != null)
				root.addChild(tree);
			else {
				for (Chunk chunk: JCasUtil.selectCovered(cas, Chunk.class,sent)){
					for (Token token : JCasUtil.selectCovered(cas, Token.class,chunk)){
						tree = TreeUtil.createNode("root");
						Tree pos = TreeUtil.createNode(token.getPostag());
						tree.addChild(pos);
						pos.addChild(TreeUtil.createNode(String.valueOf(token.getId())));
						root.addChild(tree);
					}
				}
			}
		}
		//clean empty leaves
		Map<Integer,String> tokenLemmasMap = new HashMap<Integer, String>();
		for (Token t: JCasUtil.select(cas, Token.class)){
			tokenLemmasMap.put(t.getId(), t.getLemma());
		}
		for (Tree leaf : root.getLeaves()){
			int lemmaId = Integer.valueOf(leaf.value());
			if (cleanupLexical(tokenLemmasMap.get(lemmaId)).isEmpty()){
				removeLeaveParents(leaf, root);
			}
		}		
		return root;
	}
	
	public Tree buildPhraseDepTreeWithChunksMarked() {
		Tree root = buildPhraseDepTreeWithChunksIfNeeded();
		//TODO: create an external procedure
		Map<Integer,Tree> tokenIDToPosMap = new HashMap<Integer,Tree>();
		for (Tree leaf : root.getLeaves()){
			if (!leaf.value().matches("[0-9]+")){
				logger.debug("Something wrong with "+TreeUtil.serializeTree(root));
			}
			tokenIDToPosMap.put(Integer.valueOf(leaf.value()),leaf.parent(root));
		}
		
		int i = 0;
		for (Chunk chunk: JCasUtil.select(cas, Chunk.class)) {
			//Select covered tokens
			if (!chunk.getChunkType().contains("NP")) continue;
			boolean updated =false;
			for (Token token : JCasUtil.selectCovered(cas, Token.class, chunk)){
				if (tokenIDToPosMap.containsKey(token.getId())){
					String curLabel = tokenIDToPosMap.get(token.getId()).value()+"-CHUNK"+String.valueOf(i);
					tokenIDToPosMap.get(token.getId()).setValue(curLabel);
					updated = true;
				}
			}
			if (updated) i = i +1;
		}
		return root;
	}
	
	public Tree buildPosTagDepTreeWithChunksMarked() {

		Tree root = buildPosTagDepTreeWithChunksIfNeeded();
		
		
		Map<Integer,Tree> tokenIDToPosMap = new HashMap<Integer,Tree>();
		for (Tree leaf : root.getLeaves()){
			tokenIDToPosMap.put(Integer.valueOf(leaf.value()),leaf.parent(root));
		}
		
		int i = 0;
		for (Chunk chunk: JCasUtil.select(cas, Chunk.class)) {
			//Select covered tokens
			if (!chunk.getChunkType().contains("NP")) continue;
			boolean updated =false;
			for (Token token : JCasUtil.selectCovered(cas, Token.class, chunk)){
				if (tokenIDToPosMap.containsKey(token.getId())){
					String curLabel = tokenIDToPosMap.get(token.getId()).value()+"-CHUNK"+String.valueOf(i);
					tokenIDToPosMap.get(token.getId()).setValue(curLabel);
					updated = true;
				}
			}
			if (updated) i = i +1;
		}
		
		/*Tree root = TreeUtil.createNode("ROOT");
		for (Sentence sent : JCasUtil.select(cas, Sentence.class)) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			Tree tree = buildAltTree(nodes, true);
			if (tree != null)
				root.addChild(tree);
			

		}*/
		return root;
	}
	
	
	public Tree buildPhraseDepTreeWithSpans() {
		Tree root = TreeUtil.createNode("ROOT");
		DiscourseTree discTree = JCasUtil.selectSingle(cas, DiscourseTree.class);
		Tree dTree = TreeUtil.buildTree(discTree.getTree());
		Pattern p = Pattern.compile("\\{([0-9]+)\\;([0-9]+)\\}");//Pattern.compile("SPAN([0-9]+)");
		List<Coord> spans = new ArrayList<Coord>();
		for (Tree spanLeaf : dTree.getLeaves()){
			Matcher m = p.matcher(spanLeaf.value());
			m.find();
			spans.add(new Coord(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2))));
		}
		for (Sentence sent : JCasUtil.select(cas, Sentence.class)) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			this.token2chunk = buildToken2ChunkMap(sent);
			DepNode rootDepNode = getRootNode(nodes);
			try {
				DepNode newRoot = merge(rootDepNode, token2chunk);
				logger.debug("Normal tree: {}", printDepTree(newRoot));
				Collection<DepNode> mergedList = yieldNodes(newRoot);
				// Tree tree = buildTree(mergedList);
				Tree tree = buildAltTreeWithChunksSpanMark(mergedList,spans);
				root.addChild(tree.getChild(0));
				logger.debug("Resulting tree: {}", TreeUtil.serializeTree(root).replace("(", "[").replace(")", "]"));
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("Skipping sentence: {}", sent.getCoveredText());
			}
		}
		
		
		
		
		
		
		return root;
	}
	
	public class Coord{
		private int begin;
		private int end;
		
		
		
		public Coord(int begin, int end) {
			super();
			this.begin = begin;
			this.end = end;
		}
		
		
		public int getBegin() {
			return begin;
		}


		public void setBegin(int begin) {
			this.begin = begin;
		}


		public int getEnd() {
			return end;
		}


		public void setEnd(int end) {
			this.end = end;
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + begin;
			result = prime * result + end;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Coord other = (Coord) obj;
			
			if (begin != other.begin)
				return false;
			if (end != other.end)
				return false;
			return true;
		}
	
	}
	
	public Tree buildPhraseDepTree() {
		Tree root = TreeUtil.createNode("ROOT");
		for (Sentence sent : JCasUtil.select(cas, Sentence.class)) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			this.token2chunk = buildToken2ChunkMap(sent);
			DepNode rootDepNode = getRootNode(nodes);
			try {
				DepNode newRoot = merge(rootDepNode, token2chunk);
				logger.debug("Normal tree: {}", printDepTree(newRoot));
				Collection<DepNode> mergedList = yieldNodes(newRoot);
				// Tree tree = buildTree(mergedList);
				Tree tree = buildAltTreeWithChunks(mergedList);
				root.addChild(tree.getChild(0));
				logger.debug("Resulting tree: {}", TreeUtil.serializeTree(root).replace("(", "[").replace(")", "]"));
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("Skipping sentence: {}", sent.getCoveredText());
			}
		}
		return root;
	}

	
	public Tree buildPhraseDepTreeWithPunct() {
		
		//get an ordinary tree
		Tree t = this.buildPhraseDepTree();
		
		//get token id, token map
		Map<Integer,Tree> tokensToDependencyNodesMap = new HashMap<Integer,Tree>();
		for (Tree leaf : t.getLeaves()){
			Tree grandparent = leaf.ancestor(3, t);
			if (leaf.ancestor(3, t)!=null){
				tokensToDependencyNodesMap.put(Integer.valueOf(leaf.value()),grandparent);
			}
		}

		
		//get all tokens
		/*for (Token token : JCasUtil.select(cas, Token.class)){
			int val = Integer.valueOf(token.getId());
			if (!tokensToDependencyNodesMap.containsKey(val)){
				for (int i = val-1; i>=0; i-- ){
					
					//locating the dependency node to which the punctuation is to be added
					if (tokensToDependencyNodesMap.containsKey(i)){
						Tree node = TreeUtil.createNode(token.getPostag());
						Tree posNode = TreeUtil.createNode(token.getPostag());
						node.addChild(posNode);
						posNode.addChild(TreeUtil.createNode(String.valueOf(val)));
						
						//figuring out where to add the punctuation node under the dependency node
						Tree grandpar = tokensToDependencyNodesMap.get(i);
						int j = 0; //index of a chunk child of a grammar node to which 
						boolean found = false;
						for (Tree l : grandpar.getChildrenAsList()){ //getting the chunk nodes
							for (Tree leaf : l.getLeaves()){ //see to which chunk the previous word belongs
								if (leaf.value().equals(String.valueOf(i))){
									grandpar.addChild(j+1,node);
									found=true;
									break;
								}
							}
							if (found) break;
							j++;
						}
						
					
						
						tokensToDependencyNodesMap.put(val, tokensToDependencyNodesMap.get(i));
						break;
					}
				}
			}
		}*/
		Collection<Token> tokens = JCasUtil.select(cas, Token.class);
		for (Token token : tokens ){
			int val = Integer.valueOf(token.getId());
			Tree leftNode = null;
			Tree rightNode = null;
			int leftInd = -1;
			if (!tokensToDependencyNodesMap.containsKey(val)){
				for (int i = val-1; i>=0; i-- ){
					//locating the dependency node to which the punctuation is to be added
					if (tokensToDependencyNodesMap.containsKey(i)){
						leftNode = tokensToDependencyNodesMap.get(i);
						leftInd = i;
						break;
					}
				}
				for (int i = val+1; i<tokens.size(); i++ ){
					//locating the dependency node to which the punctuation is to be added
					if (tokensToDependencyNodesMap.containsKey(i)){
						rightNode = tokensToDependencyNodesMap.get(i);
						break;
					}
				}
				Tree topNode = null;
				int dist =100;

				Tree node = TreeUtil.createNode(token.getPostag());
				Tree posNode = TreeUtil.createNode(token.getPostag());
				node.addChild(posNode);
				posNode.addChild(TreeUtil.createNode(String.valueOf(val)));
				
				if (leftNode==null){
					t.addChild(0,node);
					tokensToDependencyNodesMap.put(val, t);
					continue;
				}
				
				if ((rightNode!=null)&&(leftNode!=null)){
					for (Tree path :t.pathNodeToNode(leftNode, rightNode)){
						int distToRoot = t.pathNodeToNode(path, t).size();
						if (distToRoot<dist){
							dist = distToRoot;
							topNode= path;
						}
					}
				}
				else if (rightNode==null){
					topNode =t.children()[0];
				}
				
				
				int j = 0; //index of a chunk child of a grammar node to which 
				boolean found = false;
				for (Tree l : topNode.getChildrenAsList()){ //getting the chunk nodes
					for (Tree leaf : l.getLeaves()){ //see to which chunk the previous word belongs
						if (leaf.value().equals(String.valueOf(leftInd))){
							topNode.addChild(j+1,node);
							tokensToDependencyNodesMap.put(val, topNode);
							found=true;
							break;
						}
					}
					if (found) break;
					j++;
				}
			}
		
			
		}
		
		
		
		return t;
	}
	
	
	public Tree buildPhraseDepTreeWithChunksIfNeeded() {
		Tree root = TreeUtil.createNode("ROOT");
		for (Sentence sent : JCasUtil.select(cas, Sentence.class)) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			this.token2chunk = buildToken2ChunkMap(sent);
			
			DepNode rootDepNode = getRootNode(nodes);
			if (rootDepNode==null){
				logger.debug("null root");
				for (Chunk chunk: JCasUtil.selectCovered(cas, Chunk.class,sent)){
					Tree tree = TreeUtil.createNode(chunk.getChunkType());
					for (Token token : JCasUtil.selectCovered(cas, Token.class,chunk)){
						
						Tree pos = TreeUtil.createNode(token.getPostag());
						tree.addChild(pos);
						pos.addChild(TreeUtil.createNode(String.valueOf(token.getId())));
						root.addChild(tree);
					}
					root.addChild(tree);
				}
			}
			else{
				try {
					
					DepNode newRoot = merge(rootDepNode, token2chunk);
					
					logger.debug("Normal tree: {}", printDepTree(newRoot));
					Collection<DepNode> mergedList = yieldNodes(newRoot);
					// Tree tree = buildTree(mergedList);
					
					Tree tree = buildAltTreeWithChunks(mergedList);
					
					if (tree == null)
						root.addChild(tree);
					else {
						root.addChild(tree.getChild(0));
						logger.debug("Resulting tree: {}", TreeUtil.serializeTree(root).replace("(", "[").replace(")", "]"));
					}
				} catch (Exception e) {
					//e.printStackTrace();
					logger.error("Skipping sentence: {}", sent.getCoveredText());
				}
			}
		}
		return root;
	}
	
	
	
	
	private List<String> getPosTags(DepNode srcNode){
		
		List<String> postags = new ArrayList<String>();
		for (Token token : JCasUtil.selectCovered(this.cas, Token.class, srcNode.getBegin(),
				srcNode.getEnd())) {
			postags.add(token.getPostag());
		}
		return postags;
		
	}
	

	private List<Integer> getTokenIds(DepNode srcNode){
		
		List<Integer> tokenIds = new ArrayList<Integer>();
		for (Token token : JCasUtil.selectCovered(this.cas, Token.class, srcNode.getBegin(),
				srcNode.getEnd())) {
			tokenIds.add(token.getId());
		}
		return tokenIds;
		
	}
	
	private Tree buildLCTTree(Collection<DepNode> depNodes) {
		return buildLCTTree(depNodes, false);
	}
	private Tree buildLCTTree(Collection<DepNode> depNodes, boolean addGrammarPosDistinction) {
		HashMap<Integer, DepNode> nodes = new HashMap<>();
		HashMap<Integer, Tree> treeLexNodes = new HashMap<>();
		List<Integer> nodeIds = new ArrayList<>();
		for (DepNode node : depNodes) {
			int idNode = node.getBegin();
			nodeIds.add(idNode);
			nodes.put(idNode, node);
			List<String> posTags = getPosTags(node);
			if (posTags.size()!=1)
				logger.error("Smth wrong with the postags");
					
			//Tree treeLexNode = TreeUtil.createNode(node.getCoveredText()+"::"+posTags.get(0).substring(0,1).toLowerCase()); 
			List<Integer> tokenIds =  getTokenIds(node);
			Tree treeLexNode = TreeUtil.createNode(String.valueOf(tokenIds.get(0)));
			
			List<DepRelation> rels = node.getHeadRelations();
			
			
			if (rels.isEmpty()) {
				treeLexNode.addChild(TreeUtil.createNode("ROOT"));	
			}
			else {
				for (DepRelation rel : rels){
					String relStr = addGrammarPosDistinction ? GR_PREFIX+ rel.getRelation() : rel.getRelation();
					treeLexNode.addChild(TreeUtil.createNode(relStr));
				}
			}
			for (String posTag : posTags){
				String tagStr = addGrammarPosDistinction ? POS_PREFIX + posTag : posTag;
				treeLexNode.addChild(TreeUtil.createNode(tagStr));
			}
			
			
			treeLexNodes.put(idNode, treeLexNode);
		}

		// Process tokens in their natural order
		Collections.sort(nodeIds);
		Collections.reverse(nodeIds);
		Tree root = null;
		/*if (cas.getDocumentText().contains("Believe")){
			logger.error("gotcha");
		}*/
		for (Integer id : nodeIds) {
			DepNode node = nodes.get(id);
			Tree treeLexNode = treeLexNodes.get(id);
			List<DepRelation> rels = node.getHeadRelations();
			if (rels.isEmpty()) {
				root = treeLexNode;
			} else {
				for (DepRelation rel : rels) {
					DepNode head = rel.getHead();
					Tree parent = treeLexNodes.get(head.getBegin());
					parent.addChild(0, treeLexNode);
				}
			}
			
		}
		return root;
	}
	
	/**
	 *  Lexical Centered Tree (LCT),
		e.g. see Figure 4, in which both GR and PoS-Tag are
		added as the rightmost children.
	 * @return
	 */
	public Tree buildLCTTree(){
		Tree root = TreeUtil.createNode("ROOT");
		for (Sentence sent : JCasUtil.select(cas, Sentence.class)) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			Tree tree = buildLCTTree(nodes, false);
			if (tree != null)
				root.addChild(tree);
			else
				logger.error("Skipping sentence: {}", sent.getCoveredText());
		}
		return root;
		
	}
	
	
	/**
	 *  Lexical Centered Tree (LCT),
		e.g. see Figure 4, in which both GR and PoS-Tag are
		added as the rightmost children.
	 * @return
	 */
	public Tree buildLCTTreeWithGrammarPosDistinction(){
		Tree root = TreeUtil.createNode("ROOT");
		for (Sentence sent : JCasUtil.select(cas, Sentence.class)) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			Tree tree = buildLCTTree(nodes, true);
			if (tree != null)
				root.addChild(tree);
			else
				logger.error("Skipping sentence: {}", sent.getCoveredText());
		}
		return root;
		
	}
	
	public Tree buildChunkPhraseDepTree() {
		Tree root = TreeUtil.createNode("ROOT");
		for (Sentence sent : JCasUtil.select(cas, Sentence.class)) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			Set<String> relStrings = new HashSet<String>();
			for (DepNode n: nodes){
				
				//logger.debug("Node: "+n.getCoveredText());
				String nodeString = n.getCoveredText();
				for (DepRelation rel : n.getChildRelations()){
					relStrings.add(String.format("%s(%s,%s)",rel.getRelation(), nodeString, rel.getChild().getCoveredText()));
					
				}
				for (DepRelation rel : n.getHeadRelations()){
					relStrings.add(String.format("%s(%s,%s)",rel.getRelation(),rel.getHead().getCoveredText(), nodeString));
					//					logger.debug();
				}
			}
			for (String relString : relStrings){
				logger.debug(relString);	
			}
			
			
			this.token2chunk = buildToken2ChunkMap(sent);
			DepNode rootDepNode = getRootNode(nodes);
			try {
				DepNode newRoot = merge(rootDepNode, token2chunk);
				logger.debug("Normal tree: {}", printDepTree(newRoot));
				Collection<DepNode> mergedList = yieldNodes(newRoot);
				// Tree tree = buildTree(mergedList);
				Tree tree = buildAltTreeWithOnlyChunks(mergedList);
				root.addChild(tree.getChild(0));
				logger.debug("Resulting tree: {}", TreeUtil.serializeTree(root).replace("(", "[").replace(")", "]"));
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Skipping sentence: {}", sent.getCoveredText());
			}
		}
		return root;
	}
	
	
	
	public Tree buildPhraseDepTreeKeepNERRelatedSentence(String questionCategory) {
		Tree root = TreeUtil.createNode("ROOT");
		List<Sentence> nerSentences = new ArrayList<>();
		Collection<Sentence> sentences = JCasUtil.select(cas, Sentence.class);
		for (Sentence sent : sentences) {
			// Skip sentences that don't contain the correct entities
			if (NERUtil.containsRelatedNers(questionCategory, sent))
				nerSentences.add(sent);
		}
		// Add first sentence if no NER-related sentences found.
		if (nerSentences.isEmpty())
			nerSentences.add(sentences.iterator().next());

		for (Sentence sent : nerSentences) {
			Collection<DepNode> nodes = convertUima2pojos(JCasUtil.selectCovered(
					DependencyNode.class, sent));
			this.token2chunk = buildToken2ChunkMap(sent);
			DepNode rootDepNode = getRootNode(nodes);
			try {
				DepNode newRoot = merge(rootDepNode, token2chunk);
				logger.debug("Normal tree: {}", printDepTree(newRoot));
				Collection<DepNode> mergedList = yieldNodes(newRoot);
				// Tree tree = buildTree(mergedList);
				Tree tree = buildAltTreeWithChunks(mergedList);
				root.addChild(tree.getChild(0));
			} catch (Exception e) {
				logger.error("Skipping sentence: {}", sent.getCoveredText());
			}
		}
		return root;
	}
}
