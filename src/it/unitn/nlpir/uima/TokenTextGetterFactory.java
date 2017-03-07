package it.unitn.nlpir.uima;

import it.unitn.nlpir.types.Token;


class RawTextAsTokenText implements TokenTextGetter {

	@Override
	public String getTokenText(Token token) {
		return token.getCoveredText();
	}
}

class StemAsTokenText implements TokenTextGetter {

	@Override
	public String getTokenText(Token token) {
		return token.getStem();
	}
}

class PostagAsTokenText implements TokenTextGetter {

	@Override
	public String getTokenText(Token token) {
		return token.getPostag();
	}
}

class SubstitutionAsTokenText implements TokenTextGetter {
	@Override
	public String getTokenText(Token token) {
		return token.getSubstitution();
	}
}

class LemmaAsTokenText implements TokenTextGetter {

	@Override
	public String getTokenText(Token token) {
		return token.getLemma();
	}
}

class TopicAsTokenText implements TokenTextGetter {
	
	@Override
	public String getTokenText(Token token) {
		return token.getTopic();
	}
}

public class TokenTextGetterFactory {

	public static final String TEXT = "TEXT";
	public static final String STEM = "STEM";
	public static final String LEMMA = "LEMMA";
	public static final String POSTAG = "POSTAG";
	public static final String TOPIC = "TOPIC";
	public static final String SUBSTITUTION = "SUBSTITUTION";
	
	public static TokenTextGetter getTokenTextGetter(String tokenTextType) {
		TokenTextGetter tokenTextGetter = null;
		switch (tokenTextType) {
		case TEXT:
			tokenTextGetter = new RawTextAsTokenText();
			break;
		case STEM:
			tokenTextGetter = new StemAsTokenText();
			break;
		case LEMMA:
			tokenTextGetter = new LemmaAsTokenText();
			break;
		case POSTAG:
			tokenTextGetter = new PostagAsTokenText();
			break;
		case TOPIC:
			tokenTextGetter = new TopicAsTokenText();
			break;
		case SUBSTITUTION:
			tokenTextGetter = new SubstitutionAsTokenText();
			break;
		default:
			System.out.println("No such tokenTextGetter found");
			System.exit(0);
			break;
		}
		return tokenTextGetter;
	}
	
}