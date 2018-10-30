package it.unitn.nlpir.system.core.precomputed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.uniroma2.sag.kelp.data.example.Example;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.kelp.KelpUtilities;

public class ExamplesContainer {
	
	private Map<String,Map<String,Example>> examples;
	private static int size = 0;
	
	public ExamplesContainer() {
		this.examples = new HashMap<String,Map<String,Example>>();
		size = 0;
	}
	
	public List<Pair<String,String>> getAllIds(){
		List<Pair<String,String>> allIds = new ArrayList<Pair<String,String>>();
		for (String key : examples.keySet()) {
			for (String key_a : examples.get(key).keySet()) {
				allIds.add(new Pair<String, String>(key, key_a));
			}
		}
		return allIds;
	}
	
	public Set<String> getQuestionIds(){
		return this.examples.keySet();
	}
	
	public Set<String> getAnswerIds(String qid){
		return this.examples.get(qid).keySet();
	}
	
	public int size() {
		return size;
	}
	
	public void add(String qid, String aid, String exampleString) {
		add(qid,aid,KelpUtilities.generateKelpExample("+1", exampleString));
	}
	
	public void add(Pair<String,String> id, Example example) {
		add(id.getA(),id.getB(), example);
	}
	
	public void add(Pair<String,String> id, String example) {
		add(id.getA(),id.getB(), example);
	}
	
	
	public void add(String qid, String aid, Example example) {
		if (!this.examples.containsKey(qid)) {
			this.examples.put(qid, new HashMap<String,Example>());
		}
		this.examples.get(qid).put(aid,example);
		size++;
	}
	
	public Example get(String qid, String aid) {
		return this.examples.get(qid).get(aid);
	}
	
	public Example get(Pair<String,String> id) {
		return get(id.getA(), id.getB());
	}
	
}
