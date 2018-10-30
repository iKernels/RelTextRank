package it.unitn.nlpir.features.providers.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.Pair;

public class OrderedLexicalUnitProvider implements CollectionProvider {

	protected static final String defaultTokenTextType = TokenTextGetterFactory.LEMMA;
	private final boolean filterStopwords;
	protected TokenTextGetter tGetter;
	
	public OrderedLexicalUnitProvider(){
		this(false, defaultTokenTextType);
	}
	
	public OrderedLexicalUnitProvider(String tokenTextType) {
		this(false, tokenTextType);
	}
	
	public OrderedLexicalUnitProvider(boolean filterStopwords, String tokenTextType) {
		super();
		this.filterStopwords = filterStopwords;
		this.tGetter = TokenTextGetterFactory.getTokenTextGetter(tokenTextType);
	}

	
	@Override
	public String toString() {
		return "OrderedLexicalUnitProvider [filterStopwords=" + filterStopwords + ", tGetter=" + tGetter + "]";
	}

	protected List<Token> getOriginalTokenList(JCas cas){
		List<Token> ts = new ArrayList<>(JCasUtil.select(cas, Token.class));
		return ts;
	}
	public Collection<String> getFeatureNGramSequenceFromCas(JCas cas) {
		List<String> luList = new ArrayList<String>();
		List<Token> ts = getOriginalTokenList(cas);//new ArrayList<>(JCasUtil.select(cas, Token.class));

		//remove stopwords if needed
		if (this.filterStopwords){
			List<Token> tmp = new ArrayList<Token>();
			for (Token t : ts){
				String text = tGetter.getTokenText(t);
				if (!(text == null || (t.getIsFiltered())))
					tmp.add(t);
			}
			ts = tmp;
		}
		/*		Iterator<Token> iter = ts.iterator();
		while (iter.hasNext()) {
			Token t = iter.next();
			String text = tGetter.getTokenText(t);
			if (text == null || (this.filterStopwords && t.getIsFiltered()))
				iter.remove();
		}*/

		for (int i = 0; i < ts.size(); i++) {
			String t = tGetter.getTokenText(ts.get(i));
			luList.add(t);
		}
		
		return luList;
	}
	
	@Override
	public Pair<Collection<String>, Collection<String>> getCollections(
			QAPair qaPair) {
		Collection<String> q = getFeatureNGramSequenceFromCas(qaPair.getQuestionCas());
		Collection<String> d = getFeatureNGramSequenceFromCas(qaPair.getDocumentCas());
		return new Pair<Collection<String>, Collection<String>>(q,d);
	}

}
