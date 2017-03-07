package it.unitn.nlpir.uima;

import org.apache.uima.jcas.JCas;

public class UIMANoPersistence implements UIMAPersistence {

	@Override
	public void serialize(JCas cas, String casId) {
		
	}

	@Override
	public void deserialize(JCas cas, String casId) {

	}

	@Override
	public boolean isAlreadySerialized(String casId) {
		return false;
	}

}
