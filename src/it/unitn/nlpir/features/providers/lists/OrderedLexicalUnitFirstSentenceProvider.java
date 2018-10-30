package it.unitn.nlpir.features.providers.lists;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import it.unitn.nlpir.types.Sentence;
import it.unitn.nlpir.types.Token;

public class OrderedLexicalUnitFirstSentenceProvider extends OrderedLexicalUnitProvider implements CollectionProvider {
	
	public OrderedLexicalUnitFirstSentenceProvider(){
		this(false, defaultTokenTextType);
	}
	
	public OrderedLexicalUnitFirstSentenceProvider(String tokenTextType) {
		this(false, tokenTextType);
	}
	
	public OrderedLexicalUnitFirstSentenceProvider(boolean filterStopwords, String tokenTextType) {
		super(filterStopwords, tokenTextType);
	}

	protected List<Token> getOriginalTokenList(JCas cas){
		List<Token> ts = new ArrayList<>();
		List<Sentence> sents = new ArrayList<>(JCasUtil.select(cas, Sentence.class));
		if (sents.size()<1)
			return ts;
		ts = JCasUtil.selectCovered(Token.class, sents.get(0));
		return ts;
	}
	
	


}
