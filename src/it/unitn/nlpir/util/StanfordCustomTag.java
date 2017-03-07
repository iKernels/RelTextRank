package it.unitn.nlpir.util;

import edu.stanford.nlp.ling.CoreAnnotations.ValueAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;

public class StanfordCustomTag {
	
	public class CustomTag extends ValueAnnotation { };
	
	public static void setTag(Tree tree, String tag) {
		CoreLabel label = (CoreLabel) tree.label();
		label.set(CustomTag.class, tag);
	}
	
	public static String getTag(Tree tree) {
		return ((CoreLabel) tree.label()).get(CustomTag.class);
	}
	
	public static void setTag(Class<? extends ValueAnnotation> annotationClass,
			Tree tree, String tag) {
		CoreLabel label = (CoreLabel) tree.label();
		label.set(annotationClass, tag);
	}
	
	public static String getTag(Class<? extends ValueAnnotation> annotationClass, Tree tree) {
		return ((CoreLabel) tree.label()).get(annotationClass);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Tree tree = TreeUtil.buildTree("(ROOT (A a) (B b) (C c (D d)))");

		String tag = StanfordCustomTag.getTag(tree);
		System.out.println("Tag " + tag);
		
		StanfordCustomTag.setTag(tree, "TAG1");
		StanfordCustomTag.setTag(tree, "TAG2");
		TreeUtil.setNodeLabel(tree, "NEWROOT");
		tag = StanfordCustomTag.getTag(tree);
		System.out.println(tag);
		System.out.println(TreeUtil.serializeTree(tree));
		
	}
}
