package it.unitn.nlpir.features.nouima;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainDocument;
import it.unitn.nlpir.features.providers.fvs.nonuima.PlainToken;
import it.unitn.nlpir.util.Pair;

public class NoUIMAOrderedLexicalUnitProvider implements NoUIMACollectionProvider {

	protected static final int defaultTokenTextType = PlainToken.LEMMA;
	private final boolean filterStopwords;
	private int tGetter;
	
	public NoUIMAOrderedLexicalUnitProvider(){
		this(false, defaultTokenTextType);
	}
	
	public NoUIMAOrderedLexicalUnitProvider(int tokenTextType) {
		this(false, tokenTextType);
	}
	
	public NoUIMAOrderedLexicalUnitProvider(boolean filterStopwords, int tokenTextType) {
		super();
		this.filterStopwords = filterStopwords;
		this.tGetter = tokenTextType;
	}

	
	@Override
	public String toString() {
		return "OrderedLexicalUnitProvider [filterStopwords=" + filterStopwords + ", tGetter=" + tGetter + "]";
	}

	protected List<PlainToken> getOriginalTokenList(PlainDocument cas){
		return cas.getTokens();
	}
	
	public Collection<String> getFeatureNGramSequenceFromCas(PlainDocument cas) {
		List<String> luList = new ArrayList<String>();
		List<PlainToken> ts = getOriginalTokenList(cas);//new ArrayList<>(JCasUtil.select(cas, Token.class));

		//remove stopwords if needed
		if (this.filterStopwords){
			List<PlainToken> tmp = new ArrayList<PlainToken>();
			for (PlainToken t : ts){
				
				if (!(t.getLemma() == null || (t.isStopword())))
					tmp.add(t);
			}
			ts = tmp;
		}

		for (int i = 0; i < ts.size(); i++) {
			String t = ts.get(i).getLemma();
			luList.add(t);
		}
		
		return luList;
	}
	


	@Override
	public Pair<Collection<String>, Collection<String>> getCollections(NoUIMACandidate qaPair) {
		Collection<String> q = getFeatureNGramSequenceFromCas(qaPair.getPair().getA());
		Collection<String> d = getFeatureNGramSequenceFromCas(qaPair.getPair().getB());
		return new Pair<Collection<String>, Collection<String>>(q,d);
	}

}
