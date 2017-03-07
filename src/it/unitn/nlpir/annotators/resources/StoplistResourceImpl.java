package it.unitn.nlpir.annotators.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

public class StoplistResourceImpl implements StoplistResource, SharedResourceObject {
	
	private HashSet<String> stoplist;
	
	@Override
	public void load(DataResource aData) throws ResourceInitializationException {
		stoplist = new HashSet<String>();
	    // open input stream to data		
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(aData.getInputStream(), "UTF-8"));
			String line;
			while ((line = input.readLine()) != null) {
				String[] words = line.split ("\\s+");
				for (int i = 0; i < words.length; i++)
					stoplist.add(words[i]);
			}
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	@Override
	public boolean contains(String word) {
		return stoplist.contains(word);
	}

}
