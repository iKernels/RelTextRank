package it.unitn.nlpir.features.nouima;

import java.util.Collection;
import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import it.unitn.nlpir.util.Pair;

/**
 * Returns ordered lists of features
* @author IKernels group
 *
 */
public interface NoUIMACollectionProvider {
	Pair<Collection<String>, Collection<String>> getCollections(NoUIMACandidate qaPair);
}
