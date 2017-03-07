package it.unitn.nlpir.tree;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.LabelFactory;
import edu.stanford.nlp.trees.SimpleTreeFactory;
import edu.stanford.nlp.trees.Tree;

public class TreeNodeFactory extends SimpleTreeFactory {

	  private LabelFactory lf;

	  /**
	   * Make a TreeFactory that produces LabeledScoredTree trees.
	   * The labels are of class <code>StringLabel</code>.
	   */
	  public TreeNodeFactory() {
	    this(CoreLabel.factory());
	  }

	  /**
	   * Make a TreeFactory that uses LabeledScoredTree trees, where the
	   * labels are as specified by the user.
	   *
	   * @param lf the <code>LabelFactory</code> to be used to create labels
	   */
	  public TreeNodeFactory(LabelFactory lf) {
	    this.lf = lf;
	  }

	  @Override
	  public Tree newLeaf(final String word) {
	    return new TreeNode(lf.newLabel(word));
	  }

	  /**
	   * Create a new leaf node with the given label
	   *
	   * @param label the label for the leaf node
	   * @return A new tree leaf
	   */
	  @Override
	  public Tree newLeaf(Label label) {
	    return new TreeNode(lf.newLabel(label));
	  }

	  @Override
	  public Tree newTreeNode(final String parent, final List<Tree> children) {
	    return new TreeNode(lf.newLabel(parent), children);
	  }

	  /**
	   * Create a new non-leaf tree node with the given label
	   *
	   * @param parentLabel The label for the node
	   * @param children    A <code>List</code> of the children of this node,
	   *                    each of which should itself be a <code>LabeledScoredTree</code>
	   * @return A new internal tree node
	   */
	  @Override
	  public Tree newTreeNode(Label parentLabel, List<Tree> children) {
	    return new TreeNode(lf.newLabel(parentLabel), children);
	  }
}
