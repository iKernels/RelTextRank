package it.unitn.nlpir.features.providers.trees.old;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.features.providers.trees.ITreeProvider;
import it.unitn.nlpir.nodematchers.HardNodeMatcher;
import it.unitn.nlpir.nodematchers.MatchingStrategy;
import it.unitn.nlpir.nodematchers.NodeMatcher;
import it.unitn.nlpir.nodematchers.TwoParentsMatchingStrategy;
import it.unitn.nlpir.projectors.Projector;
import it.unitn.nlpir.projectors.old.DependencyTreeRelProjector;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.Pair;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelDependencyTreeProvider implements ITreeProvider {
	static final Logger logger = LoggerFactory.getLogger(RelDependencyTreeProvider.class);
	private static final String matchingTokenTextType = TokenTextGetterFactory.LEMMA;
	private static final String leafTextType = TokenTextGetterFactory.LEMMA;
	private static final String relTag = "REL";

	private static MatchingStrategy strategy = new TwoParentsMatchingStrategy();
	private static NodeMatcher matcher = new HardNodeMatcher(matchingTokenTextType, relTag,
			strategy);
	private static Projector projector = new DependencyTreeRelProjector(matcher, leafTextType, relTag);

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
