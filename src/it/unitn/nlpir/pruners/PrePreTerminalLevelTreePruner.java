package it.unitn.nlpir.pruners;

import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

import java.util.function.Predicate;

/**
 * Prunes out all preterminals which are not REL-labeled, have not REL-labeled children or preterminal neighbours matching these criteria withing the scope of pruning ray
* @author IKernels group
 *
 */
public class PrePreTerminalLevelTreePruner implements Pruner {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(PrePreTerminalLevelTreePruner.class);
	
	private PruningRule pruningRule;
	private int pruningRay;
	private boolean keepNoRelSentence;
	
	public PrePreTerminalLevelTreePruner(PruningRule rule, int pruningRay) {
		this(rule, pruningRay, false);
	}
	
	public PrePreTerminalLevelTreePruner(PruningRule rule, int pruningRay, boolean keepNoRelSentence) {
		this.pruningRule = rule;
		this.pruningRay = pruningRay;
		this.keepNoRelSentence = keepNoRelSentence;
	}
	
	public Tree pruneSentence(Tree tree) {
		//get all the preterminals
		List<Tree> leaves = tree.getLeaves();
		
		List<Tree> chunks = new ArrayList<Tree>();
		
		
		for (Tree leaf : leaves){
			if (leaf.parent(tree)==null) continue;
			Tree prepreterminal = leaf.parent(tree).parent(tree);
			if (prepreterminal==null){
				System.err.println("why?");
				continue;
			}
			if (!chunks.contains(prepreterminal)){
				chunks.add(prepreterminal);
			}
		}
		
		
		//get all the preterminals matching the rule
		//List<Tree> chunks = tree.///tree.getChildrenAsList();
		List<Integer> chunksToKeep = new ArrayList<>();
		for(int i = 0; i < chunks.size(); i++) {
			if (chunks.get(i)==null){
				System.err.println("why?");
				continue;
			}
			if (chunks.get(i).label()==null){
				System.err.println("why?");
			}
			if(pruningRule.isSatisfiedOn(chunks.get(i))) {
				int beginIndex = Math.max(i - this.pruningRay, 0);
				int endIndex = Math.min(i + this.pruningRay, chunks.size() - 1);
				for(int j = beginIndex; j <= endIndex; j++) {
					chunksToKeep.add(j);
				}
			}
		}
		
		// If there is no relational information return the root node only
		if(this.keepNoRelSentence && chunksToKeep.size() == 0) {
			return TreeUtil.createNode(tree.value());
		}
		//Tree prunedTree = TreeUtil.createNode(tree.nodeString());
		Set<Tree> chunkTreesToKeep = new HashSet<Tree>();
		//List<Integer> indexes = new ArrayList<>(chunksToKeep);
		//Collections.sort(indexes);
		for(Integer index : chunksToKeep) {
			chunkTreesToKeep.add(chunks.get(index));
		}
		Tree prunedTree = tree.prune(new PruneTreeFilter(chunkTreesToKeep));
		if (prunedTree==null)
			prunedTree = TreeUtil.createNode(tree.value());
		/*List<Integer> indexes = new ArrayList<>(chunksToKeep);
		Collections.sort(indexes);*/
		
		//doing the actual pruning
		/*Tree prunedTree = TreeUtil.createNode(tree.nodeString());
		Set<Tree> chunkTreesToKeep = new HashSet<Tree>();
		for(Integer index : indexes) {
			chunkTreesToKeep.add(chunks.get(index));
		}*/
		
		/*Set<Tree> unprunedNodes = new HashSet<Tree>();
		getUnprunedTreeSet(tree , tree,  chunkTreesToKeep, unprunedNodes);
		
		for (Tree child : tree.getChildrenAsList()){
			constructPrunedTree(tree, prunedTree, unprunedNodes, child);
		}*/
		return prunedTree;
	}
	
	protected void constructPrunedTree(Tree originalTree, Tree prunedTree, Set<Tree> nodesToKeep, Tree tree){	
		if (nodesToKeep.contains(tree)){
			Tree copy = tree.deepCopy();
			while (copy.getChildrenAsList().size()>0){
				copy.removeChild(0);
			}
			prunedTree.addChild(copy);
			for (Tree child: tree.getChildrenAsList()){
				constructPrunedTree(originalTree, prunedTree.lastChild(), nodesToKeep, child);
			}
		}
	}
	
	/**
	 * Populated unpruned with the list of prepreterminal nodes which should not be pruned
	 * @param originalTree
	 * @param tree
	 * @param chunksToKeep
	 * @param unpruned
	 * @return
	 */
	protected boolean getUnprunedTreeSet(Tree originalTree, Tree tree,  List<Tree> chunksToKeep, Set<Tree> unpruned){
		if (tree.isPrePreTerminal()){
			if (chunksToKeep.contains(tree)){
				unpruned.add(tree);
				unpruned.addAll(tree.getChildrenAsList());
				for (Tree leaf : tree.getChildrenAsList()){
					unpruned.addAll(leaf.getChildrenAsList());
				}
				return true;
			}			
			else {
				return false;
			}
		}
		else {
			//children = tree.getChildrenAsList()
			boolean unprunedLeaf = false;
			for (Tree child: tree.getChildrenAsList()){
				if (getUnprunedTreeSet(originalTree, child, chunksToKeep, unpruned)){
					unprunedLeaf = true;
					unpruned.add(child);
				}
			}
			return unprunedLeaf;
		}
	}
	
	public Tree prune(Tree tree) {
		List<Tree> sentences = tree.getChildrenAsList();
		Tree prunedTree = TreeUtil.createNode(tree.nodeString());
		for (Tree sent : sentences) {		
			sent = pruneSentence(sent);
			if (sent.numChildren() > 0)  // Remove dangling sentence root nodes where all chunks were pruned away. 
				prunedTree.addChild(sent);
		}
		return prunedTree;
	}
	
	public class PruneTreeFilter implements Predicate<Tree> {

		
		private Set<Tree> treesToKeep;
		public PruneTreeFilter(Set<Tree> treesToKeep) {
			this.treesToKeep = treesToKeep;
		}


		@Override
		public boolean test(Tree tree) {
			// TODO Auto-generated method stub
			if (tree.isPrePreTerminal())
				return treesToKeep.contains(tree);
			else 
				return true;
		}
	}
}
