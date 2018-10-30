package it.unitn.nlpir.util;

import it.unitn.nlpir.types.Token;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;

public class AdditionalTreeUtil {
	public static final int INITIAL_PRINT_STRINGBUILDER_SIZE = 1000;
	static final Logger logger = LoggerFactory.getLogger(AdditionalTreeUtil.class);
	
	public static String getLemmasCoveredByTokensList(List<Token> tokens){
		StringBuffer chunkSubstring = new StringBuffer();
		for (Token t: tokens){
			chunkSubstring.append(" ");
			chunkSubstring.append(t.getLemma());
		}
		
		return chunkSubstring.toString().trim();
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
	
	public static Map<Integer,Tree> buildTokenId2TreeLeafMap(Tree tree) {
		Map<Integer,Tree> idToTree = new HashMap<Integer,Tree>();
		for (Tree qLeaf : tree.getLeaves()) {
			Integer qTokenId;
			try {
				qTokenId = Integer.parseInt(qLeaf.nodeString());
				idToTree.put(qTokenId, qLeaf);
			} catch (NumberFormatException e) {
				continue;
			}
			
		}
		return idToTree;
	}
	
	
	public static void getNonRootParent(Tree node, Tree tree, List<Tree> path, String rootTag){
		Tree parent = null;
		
		if (node!=null){
			parent = node.parent(tree);
		}
		
		
		if (parent!=null){
			if (parent.label().value().toUpperCase().equals(rootTag)){
				return;
			}
			else {
				Tree nonRootNode = TreeUtil.createNode(parent.label().value());
				path.add(nonRootNode);
				getNonRootParent(parent,tree,path, rootTag);
			}
		}
		
	}

	
	
	/*public static void addSibling(Tree tree, Tree node, String label){
		if (node.parent(tree)!=null){
			Tree parent = node.parent(tree);
			int number = parent.indexOf(node);
			String mark = label;
			if ((label.contains("-"))&&(label.split("-").length==2)){
				mark = label.split("-")[1];
				label = label.split("-")[0];
			}
			if (number > -1){
				
				Tree childNode = TreeUtil.createNode(label);
				
				parent.addChild(number+1,childNode);
				TreeUtil.markNode(childNode, mark);
			}
			//.addChild(TreeUtil.createNode(label));
		}
	}*/

	public static void addNonDuplicatingSibling(Tree tree, Tree node, String label){
		if (node.parent(tree)!=null){
			//get parent of node to be marked
			Tree parent = node.parent(tree);
			
			//get number of the node
			int number = parent.objectIndexOf(node);
			
			String mark = "M";
			if ((label.contains("-"))&&(label.split("-").length==2)){
				mark = label.split("-")[1];
				label = label.split("-")[0];
			}
			
			if (number > -1){
				//check if any nodes to the right already contain this label
				boolean alreadyPresent = false;
				for (int i = number+1; i < parent.children().length; i++){
					if (parent.getChild(i).value().equals(label)){
						alreadyPresent = true;
						break;
					}
					
				}
				if (!alreadyPresent){
					Tree childNode = TreeUtil.createNode(label);
					parent.addChild(number+1,childNode);
					TreeUtil.markNode(childNode, mark);
				}
			}
			//.addChild(TreeUtil.createNode(label));
		}
	}
	
	
	public static String getRichRelTag(Tree questionTree, Tree qMatchedLeaf, String parentRelTag, String rootTag){
		return getRichRelTag(questionTree, qMatchedLeaf, parentRelTag, 1, Integer.MAX_VALUE, rootTag);
	}
	
	
	public static String getRichRelTagPlain(Tree questionTree, Tree qMatchedLeaf, String parentRelTag, String rootTag){
		return getRichRelTagPlain(questionTree, qMatchedLeaf, parentRelTag,rootTag, Integer.MAX_VALUE,2);
	}
	
	
	public static String getRichRelTagPlain(Tree questionTree, Tree qMatchedLeaf, String parentRelTag, String rootTag, int pathLength, int startIndex){
		return getRichRelTagPlain(questionTree, qMatchedLeaf, parentRelTag,rootTag, pathLength, startIndex);
	}
	
	public static String getRichRelTagPlain(Tree questionTree, Tree qMatchedLeaf, String parentRelTag, String rootTag, int pathLength, int startIndex, Map<String,String> labelmappings){
		String tag = null;
		List<Tree> path = new ArrayList<Tree>();
		getNonRootParent(qMatchedLeaf, questionTree, path,  rootTag);
		//Tree subNodeRootTree = TreeUtil.createNode(parentRelTag);
		
		boolean found = false;
		String label = parentRelTag;
		if (path.size()>=startIndex+1){
			//for (int i = Math.min(pathLength+1, path.size()-1); i>startIndex-1; i--){
			for (int i = startIndex; i < Math.min(startIndex + pathLength, path.size());i++){
				//Tree node = TreeUtil.createNode(path.get(i).label().value());
				String nodeLabel = path.get(i).label().value();
				if ((labelmappings != null) && (labelmappings.get(nodeLabel.toLowerCase())!=null)){
					nodeLabel = labelmappings.get(nodeLabel.toLowerCase()).toUpperCase();
				}
				label = label+"-"+nodeLabel;
				//root.addChild(node);
				//root = node;
			//}
			}
		}
		Tree subNodeRootTree = TreeUtil.createNode(label.toUpperCase());
		//if (found){
			tag = TreeUtil.serializeTree(subNodeRootTree).toUpperCase();
		//}
		return tag;
	}
	
	/**
	 * The dependency information about a leaf is presented as a list with the closest dependency nodes being first,
	 * moreover, in depphrase trees, the closest two nodes would be that POS and chunk-POS and then the dependency information starts.
	 * Then the items from this list are put into a new list, starting with the element at endIndex in the old list, being put first 
	 * into the new list, and so on in the reverse order length times
	 * 
	 * @param questionTree
	 * @param qMatchedLeaf
	 * @param parentRelTag
	 * @param length length of path to be turned into a tag
	 * @param endIndex index of the top node to be considered.
	 * @param rootTag
	 * @return
	 */
	public static String getRichRelTag(Tree questionTree, Tree qMatchedLeaf, String parentRelTag, int length, int endIndex, String rootTag){
		String tag = null;
		List<Tree> path = new ArrayList<Tree>();
		getNonRootParent(qMatchedLeaf, questionTree, path,  rootTag);
		Tree subNodeRootTree = TreeUtil.createNode(parentRelTag);
		Tree root = subNodeRootTree;
		boolean found = false;
		endIndex = Math.min(endIndex, path.size()-1);
		if (path.size()>=3){
			for (int i = endIndex; i>Math.max(1, endIndex-length); i--){
				Tree node = TreeUtil.createNode(path.get(i).label().value());
				found = true;
				root.addChild(node);
				//root = node;
			}
		}
		if (found){
			tag = TreeUtil.serializeTree(subNodeRootTree).toUpperCase();
		}
		return tag;
	}
	


	
	public static void addSibling(Tree tree, Tree node, String label){
		if (node.parent(tree)!=null){
			Tree parent = node.parent(tree);
			int number = parent.objectIndexOf(node);
			String mark = label;
			if ((label.contains("-"))&&(label.split("-").length==2)){
				mark = label.split("-")[1];
				label = label.split("-")[0];
			}
			if (number > -1){
				
				Tree childNode = TreeUtil.createNode(label);
				
				parent.addChild(number+1,childNode);
				TreeUtil.markNode(childNode, mark);
			}
			//.addChild(TreeUtil.createNode(label));
		}
	}
	

	public static void addHierSibling(Tree tree, Tree node, Tree sibling){
		if (node.parent(tree)!=null){
			Tree parent = node.parent(tree);
			int number = parent.objectIndexOf(node);
			
			boolean merged = false;
			if (parent.children().length>number+1){
				String oldsiblinglabel = parent.getChild(number+1).label().value();
				if ((parent.getChild(number+1).label().value().equals(sibling.label().value()))){
					if ((sibling.children()!=null)&&(sibling.children().length==1)){
						parent.getChild(number+1).addChild(sibling.getChild(0));
						//TreeUtil.markNode(sibling.getChild(0));
						merged = true;
					}
				}
			}
			
			if (!merged){
				parent.addChild(number+1,sibling);
				TreeUtil.markNode(sibling, "M");
			}
		}
	}
	
	public static void addHierChildLast(Tree tree, Tree node, Tree sibling){
		
			Tree parent = node;
			
			if (node.parent(tree)==null){
				return;
			}
			boolean merged = false;
			int lastChildId = parent.children().length-1;
			TreeUtil.markNode(sibling, "M");
			if ((parent.getChild(lastChildId).label().value().equals(sibling.label().value()))){
					//parent.addChild(sibling);
					
					//TreeUtil.markNode(sibling.getChild(0));
					merged = true;
			}
			
			
			if (!merged){
				parent.addChild(sibling);
				
			}
		
	}
	
	
	
	
	
	public static void addHierNonDupSibling(Tree tree, Tree node, Tree sibling){
		if (node.parent(tree)!=null){
			Tree parent = node.parent(tree);
			int number = parent.objectIndexOf(node);
			
			
			boolean merged = false;
			boolean alreadyPresent =false;;
			for (int i = number+1; i < parent.children().length; i++){
				if (parent.getChild(i).equals(sibling)){
					alreadyPresent = true;
					break;
				}
			}
			
			if (!alreadyPresent){
				if (parent.children().length>number+1){
					//String oldsiblinglabel = parent.getChild(number+1).label().value();
					if ((parent.getChild(number+1).label().value().equals(sibling.label().value()))){
						if ((sibling.children()!=null)&&(sibling.children().length==1)){
							parent.getChild(number+1).addChild(sibling.getChild(0));
							//TreeUtil.markNode(sibling.getChild(0));
							merged = true;
						}
					}
				}
				if (!merged){
					parent.addChild(number+1,sibling);
					TreeUtil.markNode(sibling, "M");
				}

			}
		}
	}
	
	
	/**
	 * 
	 * @param tree
	 * @param node
	 * @param sibling
	 * @param wDelimiter regular experession;
	 */
	public static void addHierSiblingSelectMaxW(Tree tree, Tree node, Tree sibling, String wDelimiter){
		
		
		String siblingLabel = (wDelimiter==null) ? sibling.value() : sibling.value().split(wDelimiter)[0];
		double siblingW = 0.0;
		if ((wDelimiter!=null)&&(sibling.value().split(wDelimiter).length==2))
			siblingW = Double.valueOf(sibling.value().split(wDelimiter)[1]);
		
		
		if (node.parent(tree)!=null){
			Tree parent = node.parent(tree);
			int number = parent.objectIndexOf(node);

			boolean alreadyPresent =false;;

			for (int i = number+1; i < parent.children().length; i++){
				
				String childFullLabel = parent.getChild(i).nodeString();
				String childLabel = (wDelimiter==null) ? childFullLabel : parent.getChild(i).nodeString().split(wDelimiter)[0];
				if ((childLabel.equals(siblingLabel))){
					alreadyPresent = true;
					
					if ((wDelimiter!=null)&&(parent.getChild(i).value().split(wDelimiter).length == 2)){
						double childW = Double.valueOf(parent.getChild(i).value().split(wDelimiter)[1]);
						parent.getChild(i).setValue(childLabel+wDelimiter+String.valueOf(Math.max(siblingW, childW)));
					}
					break;
				}
			}
			
			if (!alreadyPresent){
				parent.addChild(number+1,sibling);
				TreeUtil.markNode(sibling, "M");
			}
			
		}
		
	}
	
	
	public static void addWeightedHierChildLastNonDupSelectMax(Tree tree, Tree node, Tree sibling, String wDelimiter){
		String siblingLabel = (wDelimiter==null) ? sibling.value() : sibling.value().split(wDelimiter)[0];
		
		Tree parent = node;
		
		if (node.parent(tree)==null){
			return;
		}
		boolean merged = false;
		int lastChildId = parent.children().length-1;
		
		Tree child = parent.getChild(lastChildId);
		String childLabel = (wDelimiter==null) ? child.value() : child.value().split(wDelimiter)[0];
		
			Tree[] sibs = parent.children();
			for (int i =0; i < sibs.length; i++){
				if ((siblingLabel.equals(childLabel))) 
					break;
				childLabel = (wDelimiter==null) ? sibs[i].value() : sibs[i].value().split(wDelimiter)[0];
			}
			
		
		
		if ((siblingLabel.equals(childLabel))){
			//parent.addChild(sibling);
			
			//TreeUtil.markNode(sibling.getChild(0));
			if ((wDelimiter!=null)&&(sibling.value().split(wDelimiter).length==2)){
				double siblingW = Double.valueOf(sibling.value().split(wDelimiter)[1]);
				double childW = Double.valueOf(parent.getChild(lastChildId).nodeString().split(wDelimiter)[1]);
				parent.getChild(lastChildId).setValue(childLabel+wDelimiter+String.valueOf(Math.max(siblingW, childW)));
			}
			merged = true;
	}
		
		if (!merged){
			TreeUtil.markNode(sibling, "M");
			parent.addChild(sibling);
			
		}
	
	}
	
	
	
	public static void addChild(Tree tree, Tree node, String label){
		Tree childNode = TreeUtil.createNode(label);
		node.addChild(node.children().length,childNode);
		TreeUtil.markNode(childNode, label);
	}
	
	
	public static Tree buildTree(String treeString) {
		
		Pattern p = Pattern.compile("(\\s+\\(NN \\(NNP[S]* \\([0-9 ]+\\)\\){1,})\\s+(\\(NNP[S]* \\([0-9 ]+\\)\\)\\))");

		Matcher m = p.matcher(treeString);
		Pattern pInternal = Pattern.compile("\\(NN (\\(NNP[S]* .+?\\)\\))\\)");
		String newTreeString = "";
		int lastEnd = 0;
		while (m.find()){
			newTreeString = newTreeString + treeString.substring(lastEnd, m.start());
			String lastLeaf =m.group(2); 
			String nnps = m.group(1);
			
			newTreeString = newTreeString + " (NNP ";
					
			Matcher mi = pInternal.matcher(nnps);
			while (mi.find()){
				String leaf = mi.group(1);
				newTreeString = newTreeString + leaf+" ";
			}
			lastEnd = m.end();
			
			newTreeString = newTreeString + lastLeaf;
			newTreeString = newTreeString + ")";
		}
		if (lastEnd<treeString.length()){
			newTreeString  = newTreeString + treeString.substring(lastEnd);
		}
		treeString = newTreeString;
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
	
	
	public static Tree collapseNENodes(Tree tree, Tree newTree){
		if (newTree==null){
			newTree = TreeUtil.createNode(tree.label().value());
		}
		List<Tree> children = tree.getChildrenAsList();
		
		for (int i = children.size()-1; i >= 0 ; i--){
			Tree child = children.get(i);
			boolean found = false;
			String childLabel = child.label().value();
			if (childLabel.startsWith("NNP")){
				List<Integer> childrenToCollapse = new ArrayList<Integer>();
				Tree t = TreeUtil.createNode(childLabel);
				t.addChild(child);
				for (int j  = i-1; j >= 0; j--){
					Tree sibling = children.get(j);
					if ((sibling.label().value().equals("NN"))&&(child.isPreTerminal())){
						if (sibling.isPrePreTerminal()){
							Tree[] siblingChildren = sibling.children();
							if ((siblingChildren.length==1)&&(siblingChildren[0].label().value().startsWith("NN"))){
								found = true;
								childrenToCollapse.add(j);
								t.addChild(0,siblingChildren[0]);
								i--;
							}
						}
					}
				} //for (int j  = 0; j < i; j++){
				if (childrenToCollapse.size()>0){
					newTree.addChild(0,t);
				}
			}
		
			if (!found)
			{
				newTree.addChild(0,TreeUtil.createNode(child.label().value()));
				if (child.isPreTerminal()){
					List<Tree> terminals = child.getChildrenAsList();
					for (Tree terminal : terminals){
						newTree.getChild(0).addChild(0,terminal);
					}
				}
				else{
					collapseNENodes(child, newTree.getChild(0));
				}
			}
			
			
		}//for (int i = children.size()-1; i > 0 ; i++){
		return newTree;
	}
	
	
}
