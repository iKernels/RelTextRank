package it.unitn.nlpir.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.international.arabic.Buckwalter;

public class FileUtil {
	public static boolean createDirectoryStructure(String directoryStructure) {
		return new File(directoryStructure).mkdirs();
	}
	
	
	public static Set<String> readSetFromFile(String stopwordFile) throws IOException{
		Set<String> stopwords = new HashSet<String>();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(stopwordFile), "UTF8"));
		String line = null;
		while ((line = in.readLine()) != null) {
			stopwords.add(line.trim());
		}
		in.close();
		return stopwords;
	}
	
	public static Set<String> readBuckwalterSetFromFile(String stopwordFile) throws IOException{
		Set<String> stopwords = new HashSet<String>();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(stopwordFile), "UTF8"));
		String line = null;
		Buckwalter bw = new Buckwalter();
		while ((line = in.readLine()) != null) {
			stopwords.add(bw.unicodeToBuckwalter(line.trim()));
		}
		in.close();
		return stopwords;
	}
	
	public static Map<String,String> readFileAsMap(String mapFile, String delimiter) throws IOException{
		Map<String,String> stopwords = new HashMap<String, String>();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(mapFile), "UTF8"));
		String line = null;
		while ((line = in.readLine()) != null) {
			String[] parts = line.split(delimiter);
			stopwords.put(parts[0], parts[1]);
		}
		in.close();
		return stopwords;
	}
	
	public static Map<String,String[]> readFileAsMapOfStrings(String mapFile, String delimiter, String itemDelimiter) throws IOException{
		Map<String,String[]> stopwords = new HashMap<String, String[]>();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(mapFile), "UTF8"));
		String line = null;
		while ((line = in.readLine()) != null) {
			String[] parts = line.split(delimiter);
			if (parts.length==2)
				stopwords.put(parts[0], parts[1].split(itemDelimiter));
		}
		in.close();
		return stopwords;
	}
}
