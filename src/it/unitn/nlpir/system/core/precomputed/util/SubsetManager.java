package it.unitn.nlpir.system.core.precomputed.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.unitn.nlpir.system.core.precomputed.GramTypeConstants;
import it.unitn.nlpir.util.Pair;

public class SubsetManager {
	protected List<Pair<String,String>> ids;
	
	protected Map<String,Integer> aidExampleCounter = null;
	protected int maxTrainExamples;
	protected Set<String> hasPositiveExamples;
	protected Set<String> hasNegativeExamples;
	
	public SubsetManager() {
		this(-1);
	}
	public SubsetManager(int maxCandidatesPerQ) {
		this.ids = new ArrayList<Pair<String,String>>();
		this.hasNegativeExamples = new HashSet<String>();
		this.hasPositiveExamples = new HashSet<String>();
		
		if (maxCandidatesPerQ>0) {
			aidExampleCounter = new HashMap<String,Integer>();
			this.maxTrainExamples = maxCandidatesPerQ;
		}
	}
	
	public List<String> getIdsByTypeList(String type){
		if (type.equals(GramTypeConstants.QQ_MODE_LABEL)) {
			return this.ids.stream().map( e -> e.getA()).collect(Collectors.toList());
		}
		else if (type.equals(GramTypeConstants.AA_MODE_LABEL)) {
			return this.ids.stream().map( e -> e.getB()).collect(Collectors.toList());
		}
		
		return null;
	}
	
	public int addPair(String qid, String aid, boolean label) {
		
		if (aidExampleCounter!=null) {
			
			
			if (!aidExampleCounter.containsKey(qid)){
				aidExampleCounter.put(qid, 0);
			}
			if (aidExampleCounter.get(qid)>maxTrainExamples)
				return -1;
			aidExampleCounter.put(qid, aidExampleCounter.get(qid)+1);
		}
		
		this.ids.add(new Pair<String,String>(qid,aid));
		
		if (label)
			hasPositiveExamples.add(qid);
		else
			hasNegativeExamples.add(qid);
		
		return this.ids.size()-1;
	}
	
	public void filterAllPositive() {
		this.ids.removeIf(i->!hasNegativeExamples.contains(i.getA()));
	}
	
	public void filterAllNegative() {
		this.ids.removeIf(i->!hasPositiveExamples.contains(i.getA()));
	}
	
	public int size() {
		return this.ids.size();
	}
	
}
