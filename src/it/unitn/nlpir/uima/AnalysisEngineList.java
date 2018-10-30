package it.unitn.nlpir.uima;

import it.unitn.nlpir.util.Hash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;


public class AnalysisEngineList implements Iterable<AnalysisEngine> {

	List<AnalysisEngine> aes;
	List<String> aesNames;
	List<String> aesDescriptors;

	Map<String, TypeSystemDescription> typeSystems;
	Map<String, TypeSystemDescription> analysisEngineTypeSystems;
	Set<String> forceExecution;
	List<String> typeSystemsForCAS;

	public AnalysisEngineList() {
		this.aes = new ArrayList<>();
		this.aesNames = new ArrayList<>();
		this.aesDescriptors = new ArrayList<>();
		this.typeSystems = new HashMap<>();
		this.typeSystemsForCAS = new ArrayList<>();
		this.forceExecution = new HashSet<>();
	}
	
	public AnalysisEngineList addAnalysisEngine(AnalysisEngine ae) {
		return addAnalysisEngine(ae, false);
	}
	public AnalysisEngineList addAnalysisEngine(AnalysisEngine ae, boolean forceExecution) {
		this.aes.add(ae);
		String aeName=ae.getClass().getSimpleName();
		this.aesNames.add(aeName);
		if (forceExecution) {
			this.forceExecution.add(aeName);
		}
		return this;
	}

	public AnalysisEngineList addAnalysisEngine(AnalysisEngineDescription aeDesc, boolean forceExecution) {
		AnalysisEngine ae;
		try {
			ae = AnalysisEngineFactory.createPrimitive(aeDesc);
			this.aes.add(ae);
			String aeName = ae.getMetaData().getName();
			this.aesNames.add(aeName);
			if (forceExecution) {
				this.forceExecution.add(aeName);
			}
		} catch (ResourceInitializationException e1) {
			e1.printStackTrace();
		}
		return this;
	}
	
	public AnalysisEngineList addAnalysisEngine(AnalysisEngineDescription aeDesc) {
		return addAnalysisEngine(aeDesc, false);
	}
	
	public AnalysisEngineList addAnalysisEngine(String xmlDescriptor, Object... configParams) {
		try {
			AnalysisEngine ae = AnalysisEngineFactory.createAnalysisEngineFromPath(
					xmlDescriptor, configParams);
			// Store data about the analysis engine
			this.aes.add(ae);
			this.aesNames.add(ae.getMetaData().getName());
			this.aesDescriptors.add(xmlDescriptor);
		} catch (UIMAException | IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public AnalysisEngineList addAnalysisEngineWithTypeSystem(String xmlDescriptor, String... xmlTypeSystems) {
		String key = this.hashTypeSystems(xmlTypeSystems);

		TypeSystemDescription typeSystemDescription = produceTypeSystemDescription(key,
				xmlTypeSystems);
		try {
			AnalysisEngine ae = AnalysisEngineFactory.createAnalysisEngineFromPath(xmlDescriptor);

			// Store data about the analysis engine
			this.aes.add(ae);
			this.aesNames.add(ae.getMetaData().getName());
			this.aesDescriptors.add(xmlDescriptor);
			this.typeSystems.put(ae.getMetaData().getName(), typeSystemDescription);
		} catch (UIMAException | IOException e) {
			e.printStackTrace();
		}
		
		return this;
	}

	public Iterator<AnalysisEngine> iterator() {
		return this.aes.iterator();
	}

	public AnalysisEngineList addTypeSystemForCas(String typeSystem) {
		String typeSystemName = typeSystem;
		
		if (typeSystem.endsWith(".xml")) {
			typeSystemName = typeSystem.substring(0, typeSystem.lastIndexOf(".xml"));
		}
		
		this.typeSystemsForCAS.add(typeSystemName);
		
		return this;
	}

	public String[] getTypeSystemsForCas() {
		return this.typeSystemsForCAS.toArray(new String[this.typeSystemsForCAS.size()]);
	}

	private TypeSystemDescription produceTypeSystemDescription(String key, String... xmlTypeSystems) {
		TypeSystemDescription typeSystemDescription;
		if (this.typeSystems.containsKey(key)) {
			typeSystemDescription = this.typeSystems.get(key);
		} else {
			typeSystemDescription = TypeSystemDescriptionFactory
					.createTypeSystemDescriptionFromPath(xmlTypeSystems);
			this.typeSystems.put(key, typeSystemDescription);
		}
		return typeSystemDescription;
	}

	private String hashTypeSystems(String... xmlTypeSystems) {
		List<String> xmlTypeSystemList = new ArrayList<>();
		for (String xmlTypeSystem : xmlTypeSystems) {
			xmlTypeSystemList.add(xmlTypeSystem);
		}
		Collections.sort(xmlTypeSystemList);
		String hash = "";
		for (String xmlTypeSystem : xmlTypeSystemList) {
			hash += xmlTypeSystem;
		}
		return Hash.getHash(hash);
	}
}
