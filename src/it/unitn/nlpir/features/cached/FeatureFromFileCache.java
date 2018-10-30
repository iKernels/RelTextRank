package it.unitn.nlpir.features.cached;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.fit.util.JCasUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import svmlighttk.SVMTKExample;
import svmlighttk.SVMVector;
import it.unitn.nlpir.features.FeatureExtractor;
import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.types.DocumentId;
import it.unitn.nlpir.util.Pair;

public class FeatureFromFileCache implements FeatureExtractor {
	protected Map<Pair<String,String>, SVMVector> features;
	protected static final Logger logger = LoggerFactory.getLogger(FeatureFromFileCache.class);
	
	
	public FeatureFromFileCache(String featureFile) throws IOException {
		features = new HashMap<Pair<String,String>, SVMVector>();
		
		BufferedReader inValues = new BufferedReader(new InputStreamReader(
				new FileInputStream(featureFile), "UTF8"));
		String line = null;


		while ((line = inValues.readLine()) != null) {
			
			String [] parts =line.split("\t");
			if (parts.length!=3){
				logger.info(String.format("Skipping empty line: '%s'", line));
			}
			Pair<String, String> key = new Pair<String,String>(parts[0].trim(), parts[1].trim());
			if (features.containsKey(key))
				logger.error(String.format("key %s is already in the map", key.toString()));
			features.put(key, new SVMVector(parts[2].trim()));

		}
		inValues.close();
		logger.info(String.format("%d feature vectors read from %s", features.size(), featureFile));
	}
	
	
	
	public FeatureFromFileCache(String featureIdFile, String featureFile) throws IOException {
		features = new HashMap<Pair<String,String>, SVMVector>();
		
		BufferedReader inIds = new BufferedReader(new InputStreamReader(
				new FileInputStream(featureIdFile), "UTF8"));
		BufferedReader inValues = new BufferedReader(new InputStreamReader(
				new FileInputStream(featureFile), "UTF8"));
		String lineIds = null;
		String lineValues = null;
		boolean isTrainingFile = featureIdFile.endsWith(".res");
		while ((lineIds = inIds.readLine()) != null) {
			lineValues = inValues.readLine();			
			String [] ids = lineIds.split(" ");
			//if we have a non-reranking file TODO: change this later
			if (!isTrainingFile){
				Pair<String, String> p1 = new Pair<String,String>(ids[0], ids[1]);
				SVMTKExample ex = SVMTKExample.parseTreeAndVectorStr(lineValues);
				features.put(p1, ex.getVectors().get(0));
			}
			else{ //if we have a reranking file with two pairs per line
				Pair<String, String> p1 = new Pair<String,String>(ids[0], ids[1]);
				Pair<String, String> p2 = new Pair<String,String>(ids[0], ids[2]);
				SVMTKExample ex = SVMTKExample.parseTreeAndVectorStr(lineValues);
				features.put(p1, ex.getVectors().get(0));
				features.put(p2, ex.getVectors().get(1));
			}
			
		}
		inIds.close();
		inValues.close();
		logger.info(String.format("%d feature vectors read from %s", features.size(), featureFile));
	}
	
	
	
	@Override
	public void extractFeatures(QAPair qa) {
		String qid = JCasUtil.selectSingle(qa.getQuestionCas(), DocumentId.class).getId().replace("question-", "");
		String aid = JCasUtil.selectSingle(qa.getDocumentCas(), DocumentId.class).getId().replace("document-", "");
		SVMVector v = this.features.get(new Pair<String,String>(qid,aid));
		if (v!=null){
			qa.setFeatureVector(v);}
		else{
			logger.error(String.format("Feature vector not found for question %s and document %s",qid,aid));
			System.out.println(String.format("Feature vector not found for question %s and document %s",qid,aid));
		}
	}

	@Override
	public String getFeatureName() {
		return "FeatureFromFileCache";
	}

	

}
