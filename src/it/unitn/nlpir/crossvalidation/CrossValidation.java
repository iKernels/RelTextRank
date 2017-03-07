package it.unitn.nlpir.crossvalidation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CrossValidation<T> {
	final List<T> data;
	int seed = 123;

	public CrossValidation(List<T> data) {
		this.data = data;
	}

	public CrossValidation(List<T> data, int seed) {
		this.data = data;
		this.seed = seed;
	}

	public List<Fold<T>> getFolds(int numFolds) {
		List<T> cpy = new ArrayList<>(this.data);
		Random rand = new Random(seed);
		Collections.shuffle(cpy, rand);
		int N = data.size();
		int partSize = N / numFolds;
		List<Fold<T>> folds = new ArrayList<>();
		for (int i = 0; i < numFolds; i++) {
			List<T> test = cpy.subList(i * partSize, (i + 1) * partSize);
			List<T> train = new ArrayList<>();
			for (int j = 0; j < numFolds; j++) {
				if (i == j)
					continue;
				train.addAll(cpy.subList(j * partSize, (j + 1) * partSize));
			}
			Fold<T> fold = new Fold<>(train, test);
			folds.add(fold);
		}
		return folds;
	}
}
