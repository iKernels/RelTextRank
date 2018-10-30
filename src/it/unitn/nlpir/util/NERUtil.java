package it.unitn.nlpir.util;

import it.unitn.nlpir.annotators.NERTypes;
import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.Sentence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

public class NERUtil {
	
	public static List<NER> getNERsByType(String nerType, JCas cas) {
		List<NER> ners = new ArrayList<>();
		for (NER ner : JCasUtil.select(cas, NER.class)) {
			if (ner.getNERtype().equals(nerType))
				ners.add(ner);
		}
		return ners;
	}
	
	public static List<NER> getNERsByType(List<String> nerTypes, JCas cas) {
		List<NER> ners = new ArrayList<>();
		HashSet<String> nTypes = new HashSet<>(nerTypes);
		for (NER ner : JCasUtil.select(cas, NER.class)) {
			if (nTypes.contains(ner.getNERtype()))
				ners.add(ner);
		}
		return ners;
	}
	
	public static boolean containsRelatedNers(String questionClass, Sentence sent) {
		HashSet<String> types = new HashSet<>(resolveQuestionCategory2EntityClassTypes(questionClass));
		for (NER ner : JCasUtil.selectCovered(NER.class, sent)) {
			if (types.contains(ner.getNERtype()))
				return true;
		}
		return false;
	}
	
	public static List<NER> getNERsByQuestionCategory(String questionClass, JCas cas) {		
		List<String> nerTypes = resolveQuestionCategory2EntityClassTypes(questionClass);
		return getNERsByType(nerTypes, cas);
	}

	public static List<String> resolveQuestionCategory2EntityClassTypes(
			String questionClass) {
		List<String> classTypes = new ArrayList<>();
		switch (questionClass) {
		case "HUM":
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.ORGANIZATION);
			break;
		case "LOC":
			classTypes.add(NERTypes.LOCATION);
			break;
		case "NUM":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.PERCENTAGE);
			classTypes.add(NERTypes.DURATION);
			classTypes.add(NERTypes.NUMBER);
			classTypes.add(NERTypes.SET);
			break;
		case "ENTY":
			//classTypes.add(NERTypes.ORGANIZATION);
			//classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			//classTypes.add(NERTypes.LOCATION);
			break;

		//added the block below on 30/06/2015, should not impede the original code
		case "DATE":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.NUMBER);
			break;
		case "DURATION":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.NUMBER);
			break;
		case "CURRENCY":
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.NUMBER);
			break;
		case "QUANTITY":
			classTypes.add(NERTypes.PERCENTAGE);
			classTypes.add(NERTypes.NUMBER);
			break;
		
		/*case "ABBR":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			break;*/
		}
		return classTypes;
	}
	

	public static List<String> resolveQuestionCategory2EntityClassTypesWithLod(
			String questionClass) {
		List<String> classTypes = new ArrayList<>();
		switch (questionClass) {
		case "HUM":
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.AGENT);
			break;
		case "LOC":
			classTypes.add(NERTypes.LOCATION);
			break;
		case "NUM":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.PERCENTAGE);
			classTypes.add(NERTypes.DURATION);
			classTypes.add(NERTypes.NUMBER);
			classTypes.add(NERTypes.SET);
			break;
		case "ENTY":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			classTypes.add(NERTypes.AGENT);
			//classTypes.add(NERTypes.LOCATION);
			break;

		//added the block below on 30/06/2015, should not impede the original code
		case "DATE":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.NUMBER);
			break;
		case "CURRENCY":
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.NUMBER);
			break;
		case "QUANTITY":
			classTypes.add(NERTypes.PERCENTAGE);
			classTypes.add(NERTypes.NUMBER);
			break;
		
		/*case "ABBR":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			break;*/
		}
		return classTypes;
	}
	
	
	public static List<String> resolveQuestionCategory2EntityClassTypesEntyFixed(
			String questionClass) {
		List<String> classTypes = new ArrayList<>();
		switch (questionClass) {
		case "HUM":
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.ORGANIZATION);
			break;
		case "LOC":
			classTypes.add(NERTypes.LOCATION);
			break;
		case "NUM":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.PERCENTAGE);
			classTypes.add(NERTypes.DURATION);
			classTypes.add(NERTypes.NUMBER);
			classTypes.add(NERTypes.SET);
			break;
		case "ENTY":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			break;
		}
		return classTypes;
	}	
	
	
	
	/*
	 *	public static List<String> resolveQuestionFineCategory2EntityClassTypes(
			String questionClass) {
		List<String> classTypes = new ArrayList<>();
		switch (questionClass) {
		case "CURRENCY":
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.NUMBER);
			break;
		case "DURATION":
			classTypes.add(NERTypes.DURATION);
//			classTypes.add(NERTypes.NUMBER);
			break;
		case "ENTITY":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			break;
		case "LOCATION":
			classTypes.add(NERTypes.LOCATION);
			break;	
		case "QUANTITY":
			classTypes.add(NERTypes.NUMBER);
			break;
		case "WHEN":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.SET);
			break;
		case "WHO":
			//classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			break;
		}
		return classTypes;
	} 
	 * 
	 */
	
	public static List<String> resolveFineNumQuestionCategory2EntityClassTypesEntyFixed(
			String questionClass) {
		List<String> classTypes = new ArrayList<>();
		switch (questionClass) {
		case "HUM":
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.ORGANIZATION);
			break;
		case "LOC":
			classTypes.add(NERTypes.LOCATION);
			break;
		case "DATE":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.NUMBER);
			break;
		case "CURRENCY":
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.NUMBER);
			break;
		case "QUANTITY":
			classTypes.add(NERTypes.PERCENTAGE);
			classTypes.add(NERTypes.NUMBER);
			break;
		case "NUM":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.PERCENTAGE);
			classTypes.add(NERTypes.DURATION);
			classTypes.add(NERTypes.NUMBER);
			classTypes.add(NERTypes.SET);
			break;
		case "ENTY":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			break;
		}
		return classTypes;
	}
	
	public static List<String> resolveQuestionCategory2EntityClassTypesAsInEMNLP(
			String questionClass) {
		List<String> classTypes = new ArrayList<>();
		switch (questionClass) {
		case "HUM":
			classTypes.add(NERTypes.PERSON);
			break;
		case "LOC":
			classTypes.add(NERTypes.LOCATION);
			break;
		case "NUM":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.PERCENTAGE);
			classTypes.add(NERTypes.DURATION);
			classTypes.add(NERTypes.NUMBER);
			classTypes.add(NERTypes.SET);
			break;
		case "ENTY":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			break;
		}
		return classTypes;
	}
	
	public static List<String> resolveQuestionFineCategory2EntityClassTypes(
			String questionClass) {
		List<String> classTypes = new ArrayList<>();
		switch (questionClass) {
		case "CURRENCY":
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.NUMBER);
			break;
		case "DURATION":
			classTypes.add(NERTypes.DURATION);
//			classTypes.add(NERTypes.NUMBER);
			break;
		case "ENTITY":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			break;
		case "LOCATION":
			classTypes.add(NERTypes.LOCATION);
			break;	
		case "QUANTITY":
			classTypes.add(NERTypes.NUMBER);
			break;
		case "WHEN":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.SET);
			break;
		case "WHO":
			//classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			break;
		}
		return classTypes;
	}
	
	public static List<String> resolveFineNumQuestionCategory2EntityClassTypes(
			String questionClass) {
		List<String> classTypes = new ArrayList<>();
		switch (questionClass) {
		case "HUM":
			classTypes.add(NERTypes.PERSON);
			//classTypes.add(NERTypes.ORGANIZATION); //removed by Kateryna
			break;
		case "LOC":
			classTypes.add(NERTypes.LOCATION);
			break;
		case "DATE":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.SET);
			break;
		case "CURRENCY":
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.NUMBER);
			
			break;
		case "QUANTITY":
			//classTypes.add(NERTypes.PERCENTAGE);
			classTypes.add(NERTypes.NUMBER);//added by Kateryna on 209
			break;
		case "DURATION":
			classTypes.add(NERTypes.DURATION);
			//classTypes.add(NERTypes.SET); //removed by Kateryna on 101014
			//classTypes.add(NERTypes.NUMBER);//added by Kateryna on 209
			//classTypes.add(NERTypes.DURATION); //added by kateryna on 209
			break;
		case "ENTY":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			break;
		case "ABBR":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			break;
		case "NUM":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.MONEY);
			classTypes.add(NERTypes.PERCENTAGE);
			classTypes.add(NERTypes.DURATION);
			classTypes.add(NERTypes.NUMBER);
			classTypes.add(NERTypes.SET);
			break;
		case "ENTITY":
			classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			classTypes.add(NERTypes.MISC);
			break;
		case "LOCATION":
			classTypes.add(NERTypes.LOCATION);
			break;	
		case "WHEN":
			classTypes.add(NERTypes.DATE);
			classTypes.add(NERTypes.TIME);
			classTypes.add(NERTypes.SET);
			break;
		case "WHO":
			//classTypes.add(NERTypes.ORGANIZATION);
			classTypes.add(NERTypes.PERSON);
			break;
		}
		return classTypes;
	}
	//it.unitn.nlpir.experiment.TrecQAAnswerExtractionFineQCOnlyBowFeatsTrain40Experiment
	//it.unitn.nlpir.experiment.train40.TrecQAAnswerExtractionFineNumAutoQCExtFeatsTrain40Experiment
	
}

