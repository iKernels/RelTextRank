package it.unitn.nlpir.crossvalidation;

import java.util.List;

public class Fold<T> {
	List<T> train;
	List<T> test;
	
	public Fold(List<T> train, List<T> test) {
		this.train = train;
		this.test = test;
	}

	public List<T> getTrain() {
		return train;
	}

	public void setTrain(List<T> train) {
		this.train = train;
	}

	public List<T> getTest() {
		return test;
	}

	public void setTest(List<T> test) {
		this.test = test;
	}
}