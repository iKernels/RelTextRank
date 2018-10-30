package it.unitn.nlpir.features.providers.fvs.nonuima;

public class PlainToken {
	private String lemma;
	private String postag;
	private boolean isStopword;
	private int begin;
	private int end;
	
	public static final int LEMMA = 0;
	
	public static final int POSTAG = 1;
	
	public PlainToken(String lemma, String postag, boolean isStopword, int begin, int end) {
		super();
		this.lemma = lemma;
		this.postag = postag;
		this.isStopword = isStopword;
		this.begin = begin;
		this.end = end;
	}


	@Override
	public String toString() {
		return "PlainToken [lemma=" + lemma + ", postag=" + postag + ", isStopword=" + isStopword + ", begin=" + begin
				+ ", end=" + end + "]";
	}


	public int getBegin() {
		return begin;
	}


	public void setBegin(int begin) {
		this.begin = begin;
	}


	public int getEnd() {
		return end;
	}


	public void setEnd(int end) {
		this.end = end;
	}

	public String getProperty(int property) {
		if (property==0)
			return this.lemma;
		else if (property==1)
			return this.postag;
		else
			return this.lemma;
	}
	
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	public String getPostag() {
		return postag;
	}
	public void setPostag(String postag) {
		this.postag = postag;
	}
	public boolean isStopword() {
		return isStopword;
	}
	public void setStopword(boolean isStopword) {
		this.isStopword = isStopword;
	}
	
}
