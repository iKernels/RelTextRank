package it.unitn.nlpir.features.providers.fvs.nonuima;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;


import it.unitn.nlpir.features.providers.fvs.DependencyTripletsProvider;
import it.unitn.nlpir.types.Sentence;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetter;
import it.unitn.nlpir.uima.TokenTextGetterFactory;


public class PlainDocument {
	private List<PlainToken> tokens;
	private List<String> dependencies;
	private String text;
	private String id;
	protected static final Logger logger = LoggerFactory.getLogger(PlainDocument.class);
	
	public PlainDocument(String text, List<PlainToken> tokens, List<String> dependencies, String id) {
		this.tokens = tokens;
		this.dependencies = dependencies;
		this.text = text;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "PlainDocument [tokens=" + tokens + ", dependencies=" + dependencies + ", text=" + text + ", id=" + id
				+ "]";
	}

	public PlainDocument(String text, String id) {
		this (text, new ArrayList<PlainToken>(), new ArrayList<String>(), id);
	}
	
	
	public PlainDocument(JCas cas, String id, int maxsent, int maxtok) {
		this.tokens = new ArrayList<PlainToken>();
		TokenTextGetter tGetter = TokenTextGetterFactory.getTokenTextGetter(TokenTextGetterFactory.LEMMA);
		if ((maxsent<0) && (maxtok<0)) {
			for (Token t: JCasUtil.select(cas, Token.class)) {
				addToken(new PlainToken(t.getLemma(), t.getPostag(), t.getIsFiltered(), t.getBegin(), t.getEnd()));
			}
		
			
			this.dependencies = DependencyTripletsProvider.getDepTriplets(cas, tGetter);
			this.text = cas.getDocumentText();
		}
		else {
			int sc = 0;
			this.dependencies = new ArrayList<String>();
			StringBuffer textToConsider = new StringBuffer();
			for (Sentence s: JCasUtil.select(cas, Sentence.class)) {
				
				if (sc>maxsent) {
					break;
				}
				sc+=1;
				int tc = 0;
				
				int sbegin = s.getBegin();
				int send = 0;
				for (Token t : JCasUtil.selectCovered(Token.class, s)) {
					//note that if any sentence before was truncated the begin and pos locations of tokens will be useless
					
					addToken(new PlainToken(t.getLemma(), t.getPostag(), t.getIsFiltered(), t.getBegin(), t.getEnd()));
					tc++;
					send = t.getEnd();
					if (tc == maxtok) {
						break;
					}
					
				}
				if ((sbegin<0) || (send<0) || (send-sbegin<0) || (send>cas.getDocumentText().length()) ) {
					logger.error(String.format("Boundaries: [%d; %d]; doc length=%d; Sentence=%s ([%d;%d])", sbegin, send, cas.getDocumentText().length(), s.getCoveredText(),s.getBegin(), s.getEnd()));
//					System.out.println(String.format("Boundaries: [%d; %d]; Sentence=%s ([%d;%d])", sbegin, send,s.getCoveredText(),s.getBegin(), s.getEnd()));
				}
				
				textToConsider.append(cas.getDocumentText().substring(sbegin, send));
				this.dependencies.addAll(DependencyTripletsProvider.getDepTriplets(cas, tGetter,sbegin,send));
				
			}
			this.text = textToConsider.toString();
			
			
		}
		this.id = id;
		logger.debug(String.format("Original text: %s\n Resulting plain doc: %s", cas.getDocumentText(), this.toString()));
		 
	}
	
	
	
	
	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void addToken(PlainToken t) {
		this.tokens.add(t);
	}
	
	public void addDependency(String p) {
		this.dependencies.add(p);
	}

	public List<PlainToken> getTokens() {
		return tokens;
	}

	public List<String> getDependencies() {
		return dependencies;
	}
	
	
	
}

