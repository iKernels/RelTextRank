package it.unitn.nlpir.system.reranker;

import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.PairCompareOnB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RankedList<T, V> implements Iterable<Pair<T, V>> {
	
	private List<Pair<T, V>> list;
	private final PairCompareOnB<T, V> comparator;
	
	public RankedList() {
		this.list = new ArrayList<>();
		this.comparator = new PairCompareOnB<T, V>();
	}
	
	public void add(T example, V value) {
		Pair<T, V> element = new Pair<T, V>(
				example, value);
		this.list.add(element);
	}

	@Override
	public Iterator<Pair<T, V>> iterator() {
		Collections.sort(this.list, this.comparator);
		return this.list.iterator();
	}
	
	public void clear() {
		this.list.clear();
	}
}
