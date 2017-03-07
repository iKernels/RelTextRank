package it.unitn.nlpir.features.providers.trees;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.util.Pair;

public interface ITreeProvider {
	Pair<String, String> getTrees(QAPair qaPair);
}
