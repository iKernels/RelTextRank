package svmlighttk;

import java.util.ArrayList;
import java.util.List;

public class SVMTKExample {
	protected List<String> trees;
	protected List<SVMVector> vectors;
	protected boolean positive;
	protected String label;

	public SVMTKExample() {
		this.trees = new ArrayList<>();
		this.vectors = new ArrayList<>();
		this.positive = true;
		this.label = null;
	}

	public static SVMTKExample parseTreeAndVectorStr(String example){
		String[] parts = example.split("(\\|(?=(BT|ET|BV|EV))|((?<=(BT|ET|BV|EV))\\|))");
		SVMTKExample svmex = new SVMTKExample();
		svmex.addLabel(parts[0].trim());
		int i = 1;
		
		while (i < parts.length){
			if (parts[i].trim().equals("BT")){
				i = i + 1;
				//if (parts[i].trim().length()>0)
				svmex.addTree(parts[i].trim());
			}
			else if ((parts[i].trim().equals("ET"))||(parts[i].trim().equals("BV"))){
				i = i + 1;
				if (parts[i].trim().length()>0)
					svmex.addVector(new SVMVector(parts[i].trim()));
				else
					svmex.addVector(new SVMVector());
			}
			i=i+1;
		}
		return svmex;
	}
	
	public List<String> getTrees() {
		return trees;
	}

	public List<SVMVector> getVectors() {
		return vectors;
	}

	public boolean isPositive() {
		return positive;
	}

	public String getLabel() {
		return label;
	}

	public SVMTKExample addLabel(String label) {
		this.label = label;
		return this;
	}

	public SVMTKExample addTree(String tree) {
		this.trees.add(tree);
		return this;
	}

	public SVMTKExample addVector(SVMVector vector) {
		this.vectors.add(vector);
		return this;
	}

	public SVMTKExample positive() {
		this.positive = true;
		return this;
	}

	public SVMTKExample negative() {
		this.positive = false;
		return this;
	}
	
	public SVMTKExample setLabel(boolean label) {
		this.positive = label;
		return this;
	}

	public String build() {
		return this.toString();
	}
	
	@Override
	public String toString() {
		String example;

		if (this.label != null)
			example = this.label;
		else if (this.positive) {
			example = "+1";
		} else {
			example = "-1";
		}

		for (String tree : this.trees) {
			example += " |BT|";
			if (!tree.equals("")) {
				example += " " + tree;
			}
		}
		if (this.trees.isEmpty()){
			if (this.vectors.size()>1)
			example += " |BV|";}
		else
			example += " |ET|";

		for (int i = 0; i < this.vectors.size(); i++) {
			if (i != 0) {
				if ((this.trees.size()>0)||(this.vectors.size()>1))
					example += " |BV|";
			}
			String features = (!this.trees.isEmpty()) ? this.vectors.get(i).toString() :this.vectors.get(i).toString(true);
			if (!features.equals("")) {
				example += " " + features;
			}
		}

		if (this.vectors.size() > 0) {
			if ((this.trees.size()>0)||(this.vectors.size()>1))
				example += " |EV|";
		}

		return example;
	}
}
