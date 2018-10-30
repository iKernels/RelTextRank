package svmlighttk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import cc.mallet.types.FeatureVector;

import com.google.common.base.Joiner;

public class SVMVector {
	public static final double TOLERANCE = 1e-8;

	private TreeMap<Integer, Double> features;
	private int fid;

	public SVMVector() {
		this.fid = 1;
		this.features = new TreeMap<>();
	}
	
	public SVMVector(String featuresString) {
		this();
		for (String fpair : featuresString.trim().split("\\s+")) {
			String[] sp = fpair.split(":");
			int fid = Integer.parseInt(sp[0]);
			double fval = Double.parseDouble(sp[1]);
			addFeature(fid, fval);
		}
	}
	
	public SVMVector(TreeMap<Integer, Double> features) {
		this.features = features;
		this.fid = features.lastKey();
	}

	public SVMVector(FeatureVector fv) {
		this();
		addFeatures(fv);
	}

	public SVMVector addFeature(double feature) {
		features.put(fid++, feature);
		return this;
	}

	public SVMVector addFeature(int fid, double value) {
		features.put(fid, value);
		return this;
	}

	public SVMVector addFeatures(FeatureVector fv) {
		int[] fids = fv.getIndices();
		double[] values = fv.getValues();
		for (int i = 0; i < fv.numLocations(); i++) {
			features.put(fids[i] + 1, values[i]);
		}
		return this;
	}

	public SVMVector addFeatures(List<Double> features) {
		for (double feature : features)
			addFeature(feature);
		return this;
	}

	public List<Double> getFeatures() {
		return new ArrayList<Double>(features.values());
	}
	
	public Map<Integer, Double> getFeatureTuples() {
		return this.features;
	}
	
	public void normalize() {
		double norm = 0.0;
		for (double v : features.values()) {
			norm += v*v;
		}
		norm = Math.sqrt(norm);
		for (Entry<Integer, Double> entry : features.entrySet()) {
			double value = entry.getValue();
			entry.setValue(value/norm);
		}
	}

	public String build() {
		return this.toString();
	}
	
	@Override
	public String toString() {
		List<String> feats = new ArrayList<>();
		for (Entry<Integer, Double> entry : features.entrySet()) {
			int fid = entry.getKey();
			Double score = entry.getValue();
			if (score.isInfinite() || score.isNaN() || Math.abs(score) < TOLERANCE)
				continue;
			feats.add(fid + ":" + score);
		}
		
		return Joiner.on(" ").join(feats);
	}
	
	
	public String toString(boolean doNotAllowEmptyVectors) {
		List<String> feats = new ArrayList<>();
		for (Entry<Integer, Double> entry : features.entrySet()) {
			int fid = entry.getKey();
			Double score = entry.getValue();
			if (score.isInfinite() || score.isNaN() || Math.abs(score) < TOLERANCE)
				continue;
			feats.add(fid + ":" + score);
		}
		if  (doNotAllowEmptyVectors) {
			if (feats.size()==0)
				feats.add("1:0.0");
		}
		return Joiner.on(" ").join(feats);
	}
}
