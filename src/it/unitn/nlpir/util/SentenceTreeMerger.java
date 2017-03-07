package it.unitn.nlpir.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class SentenceTreeMerger {
	
	private List<String> trees;
	private String rootLabel = "ROOT";
	
	public SentenceTreeMerger(String rootLabel) {
		this.trees = new ArrayList<>();
		this.rootLabel = rootLabel;
	}
	
	public SentenceTreeMerger addTree(String tree) {
		this.trees.add(tree);
		return this;
	}
	
	public String getMergedTree() {
		String mergedTree = Joiner.on(" ").join(this.trees);	
		return String.format("(%s %s)", this.rootLabel, mergedTree);
	}

	public boolean isEmpty() {
		return this.trees.isEmpty();
	}
}
