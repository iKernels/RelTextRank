package it.unitn.nlpir.features.providers.trees.old;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.features.providers.trees.ITreeProvider;
import it.unitn.nlpir.nodematchers.HardNodeMatcher;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.strategies.MatchingStrategy;
import it.unitn.nlpir.nodematchers.strategies.ThreeParentsMatchingStrategy;
import it.unitn.nlpir.projectors.Projector;
import it.unitn.nlpir.projectors.old.PhraseDepTreeRelProjector;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.Pair;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelPhraseDependencyTreeProvider implements ITreeProvider {
	static final Logger logger = LoggerFactory.getLogger(RelPhraseDependencyTreeProvider.class);
	private static final String matchingTokenTextType = TokenTextGetterFactory.LEMMA;
	private static final String leafTextType = TokenTextGetterFactory.LEMMA;
	private static final String relTag = "REL";

	private static MatchingStrategy strategy = new ThreeParentsMatchingStrategy();
	private static NodeMatcher matcher = new HardNodeMatcher(matchingTokenTextType, relTag,
			strategy);
	private static Projector projector = new PhraseDepTreeRelProjector(matcher, leafTextType, relTag);

	@Override
	public Pair<String, String> getTrees(QAPair qaPair) {
		JCas questionCas = qaPair.getQuestionCas();
		JCas documentCas = qaPair.getDocumentCas();

		Pair<String, String> treePair = null;
		try {
			treePair = projector.project(questionCas, documentCas);

			logger.debug("qTree: {}", treePair.getA());
			logger.debug("dTree: {}", treePair.getB());
		} catch (AnnotationNotFoundException e) {
			logger.warn("Dependcy tree annotation not found: {}", e);
			logger.warn("No projection carried for this example. Required annotation not found.");
		}

		return treePair;
	}
}
