package it.unitn.nlpir.features.providers.lists;

import java.util.Collection;
import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.util.Pair;

/**
 * Returns ordered lists of features
* @author IKernels group
 *
 */
public interface CollectionProvider {
	Pair<Collection<String>, Collection<String>> getCollections(QAPair qaPair);
}
