package it.unitn.nlpir.util;

import java.util.Comparator;

public class PairCompareOnB<K, V> implements Comparator<Pair<K, V>> {

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Pair<K, V> o1, Pair<K, V> o2) {
		
		Comparable<K> v1 = (Comparable<K>) o1.getB();
		Comparable<K> v2 = (Comparable<K>) o2.getB();
		
		return v2.compareTo((K) v1);
	}
}
