package it.unitn.nlpir.features;

import it.unitn.nlpir.types.Token;

import java.util.Collection;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class WordOverlap implements FeatureExtractor {
	
	@Override
	public String getFeatureName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void extractFeatures(QAPair qa) {
		if(qa == null) {
			System.out.println("QAPair in extractFeatures is null");
		}
		JCas documentCas = qa.getDocumentCas();
		Multiset<Integer> counter = HashMultiset.create();
		Collection<Token> tokens = JCasUtil.select(documentCas, Token.class);
		for(Token token : tokens) {
			if(token.getIsFiltered())
				continue;
			
			int tokenLength = token.getCoveredText().length();
			counter.add(tokenLength, 1);
		}
		qa.featureVector.addFeature(0);	
	}

}
