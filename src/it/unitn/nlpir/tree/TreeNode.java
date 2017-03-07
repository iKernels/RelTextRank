package it.unitn.nlpir.tree;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.LabelFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeFactory;

public class TreeNode extends Tree {

	
	private static final long serialVersionUID = -8992385140984593817L;

	/**
	 * Label of the parse tree.
	 */
	private Label label = null;

	/**
	 * Text spanned by <code>TreeNode</code>
	 */
	private int begin;
	private int end;
	
	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}
	

	/**
	 * Daughters of the parse tree.
	 */
	private Tree[] daughterTrees = null;

	/**
	 * Create an empty parse tree.
	 */
	public TreeNode() {
		setChildren(EMPTY_TREE_ARRAY);
	}

	/**
	 * Create a leaf parse tree with given word.
	 *
	 * @param label the <code>Label</code> representing the <i>word</i> for
	 *              this new tree leaf.
	 */
	public TreeNode(Label label) {
		this(label, 0, 0);
	}

	/**
	 * Create a leaf parse tree with given word and score.
	 *
	 * @param label The <code>Label</code> representing the <i>word</i> for
	 * @param score The score for the node
	 *              this new tree leaf.
	 */
	public TreeNode(Label label, int begin, int end) {
		this();
		this.label = label;
		this.begin = begin;
		this.end = end;
	}

	/**
	 * Create parse tree with given root and array of daughter trees.
	 *
	 * @param label             root label of tree to construct.
	 * @param daughterTreesList List of daughter trees to construct.
	 */
	public TreeNode(Label label, List<Tree> daughterTreesList) {
		this.label = label;
		setChildren(daughterTreesList);
	}

	/**
	 * Returns an array of children for the current node, or null
	 * if it is a leaf.
	 */
	@Override
	public Tree[] children() {
		return daughterTrees;
	}

	/**
	 * Sets the children of this <code>Tree</code>.  If given
	 * <code>null</code>, this method prints a warning and sets the
	 * Tree's children to the canonical zero-length Tree[] array.
	 * Constructing a LabeledScoredTreeLeaf is preferable in this
	 * case.
	 *
	 * @param children An array of child trees
	 */
	@Override
	public void setChildren(Tree[] children) {
		if (children == null) {
			daughterTrees = EMPTY_TREE_ARRAY;
		} else {
			daughterTrees = children;
		}
	}

	/**
	 * Returns the label associated with the current node, or null
	 * if there is no label
	 */
	@Override
	public Label label() {
		return label;
	}

	/**
	 * Sets the label associated with the current node, if there is one.
	 */
	@Override
	public void setLabel(final Label label) {
		this.label = label;
	}


	/**
	 * Return a <code>TreeFactory</code> that produces trees of the
	 * same type as the current <code>Tree</code>.  That is, this
	 * implementation, will produce trees of type
	 * <code>LabeledScoredTree(Node|Leaf)</code>.
	 * The <code>Label</code> of <code>this</code>
	 * is examined, and providing it is not <code>null</code>, a
	 * <code>LabelFactory</code> which will produce that kind of
	 * <code>Label</code> is supplied to the <code>TreeFactory</code>.
	 * If the <code>Label</code> is <code>null</code>, a
	 * <code>StringLabelFactory</code> will be used.
	 * The factories returned on different calls a different: a new one is
	 * allocated each time.
	 *
	 * @return a factory to produce labeled, scored trees
	 */
	@Override
	public TreeFactory treeFactory() {
		LabelFactory lf = (label() == null) ? CoreLabel.factory() : label().labelFactory();
		return new TreeNodeFactory(lf);
	}

	// extra class guarantees correct lazy loading (Bloch p.194)
	private static class TreeFactoryHolder {
		static final TreeFactory tf = new TreeNodeFactory();
	}

	/**
	 * Return a <code>TreeFactory</code> that produces trees of the
	 * <code>LabeledScoredTree{Node|Leaf}</code> type.
	 * The factory returned is always the same one (a singleton).
	 *
	 * @return a factory to produce labeled, scored trees
	 */
	public static TreeFactory factory() {
		return TreeFactoryHolder.tf;
	}

	/**
	 * Return a <code>TreeFactory</code> that produces trees of the
	 * <code>LabeledScoredTree{Node|Leaf}</code> type, with
	 * the <code>Label</code> made with the supplied
	 * <code>LabelFactory</code>.
	 * The factory returned is a different one each time
	 *
	 * @param lf The LabelFactory to use
	 * @return a factory to produce labeled, scored trees
	 */
	public static TreeFactory factory(LabelFactory lf) {
		return new TreeNodeFactory(lf);
	}

	@Override
	public String nodeString() {
		StringBuilder buff = new StringBuilder();
		buff.append(super.nodeString());
		return buff.toString();
	}

}
