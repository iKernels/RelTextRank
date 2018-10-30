package it.unitn.nlpir.questions;

/**
 * 
 * Entity class containing information about a question
 *
 */
public class Question {
	
	private String id;
	private String text;
	private String perlAnswerPattern;
	private String type;
	
	public Question() {
		this.id = "";
		this.text = "";
		this.perlAnswerPattern = "";
		this.type = "";
	}
	
	@Override
	public String toString() {
		return "Question [id=" + id + ", text=" + text + ", perlAnswerPattern=" + perlAnswerPattern + ", type=" + type
				+ "]";
	}

	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getPerlAnswerPattern() {
		return this.perlAnswerPattern;
	}
	
	public void setPerlAnswerPattern(String perlAnswerPattern) {
		this.perlAnswerPattern = perlAnswerPattern;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
