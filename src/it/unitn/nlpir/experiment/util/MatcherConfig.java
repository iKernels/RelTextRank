package it.unitn.nlpir.experiment.util;

import java.util.Map;

public class MatcherConfig {
	
	public static final String REL_FOCUS_MATCHER_TYPE="relfocus";
	public static final String REL_MATCHER_TYPE="rel";
	public static final String TM_MATCHER_TYPE="tm";
	
	private String matchingStrategyClassName;
	private String matcherType;
	private String matcherClass;
	public String getMatcherClass() {
		return matcherClass;
	}

	
	public void setMatcherClass(String matcherClass) {
		this.matcherClass = matcherClass;
	}
	
	
	private Map<String,String> parameters;
	
	
	public MatcherConfig() {
		
	}
	
	public MatcherConfig(String matchingStrategyClassName, String matcherType, String matcherClass, Map<String, String> parameters) {
		super();
		this.matchingStrategyClassName = matchingStrategyClassName;
		this.matcherType = matcherType;
		this.parameters = parameters;
		this.matcherClass = matcherClass;
	}
	
	public String getParameterValue(String key) {
		return this.parameters.get(key);
	}
	public String getMatchingStrategyClassName() {
		return matchingStrategyClassName;
	}
	public void setMatchingStrategyClassName(String matchingStrategyClassName) {
		this.matchingStrategyClassName = matchingStrategyClassName;
	}
	public String getMatcherType() {
		return matcherType;
	}
	public void setMatcherType(String matcherType) {
		this.matcherType = matcherType;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	
	
}
