package it.unitn.nlpir.util.kelp.kernel;

import java.util.Map;

import org.apache.uima.jcas.JCas;

import com.fasterxml.jackson.annotation.JsonTypeName;

import it.uniroma2.sag.kelp.kernel.DirectKernel;
import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.features.presets.FeatureExtractorFactory;
import it.unitn.nlpir.resultsets.kelp.CandidatePairRepresentation;
import it.unitn.nlpir.util.Pair;
import svmlighttk.SVMVector;



@JsonTypeName("crosspair")
public class CrossPairSimilarityKernel extends DirectKernel<CandidatePairRepresentation> {
	
	protected FeaturesBuilder fb;
	
	
	public CrossPairSimilarityKernel(FeaturesBuilder fb) {
		this.fb = fb;
	}
	
	
	
	public CrossPairSimilarityKernel(String featureExtractorClassName) {
		this(featureExtractorClassName, null);
	}
	
	public CrossPairSimilarityKernel(String featureExtractorClassName, String featureCacheFileName) {
		this(FeatureExtractorFactory.getFeatureBuilder(featureExtractorClassName, featureCacheFileName));
	}
	
	public float dotProduct(SVMVector v1, SVMVector v2) {
		float p = 0.0f;
		Map<Integer, Double> v1C = v1.getFeatureTuples();
		Map<Integer, Double> v2C = v2.getFeatureTuples();
		for (Integer i : v1C.keySet()) {
			if (v2C.keySet().contains(i))
				p+=v1C.get(i)*v2C.get(i);
		}
		return p;
	}
	
	public float kernelComputation(CandidatePairRepresentation p1Rep, CandidatePairRepresentation p2Rep) {
		Pair<JCas, JCas> p1 = p1Rep.getQaCASes();
		Pair<JCas, JCas> p2 = p2Rep.getQaCASes();
		
		//Pair 1; q-q feature vector
		SVMVector v1 = fb.getFeatures(new QAPair(p1.getA(),p2.getA()));
		
		//Pair 1; a-a feature vector 
		SVMVector v2 = fb.getFeatures(new QAPair(p1.getB(),p2.getB()));
		
		return dotProduct(v1, v2)+dotProduct(p1Rep.getFeatures(), p2Rep.getFeatures());
	}

	
}
