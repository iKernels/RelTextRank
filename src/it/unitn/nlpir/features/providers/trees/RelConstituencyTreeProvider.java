package it.unitn.nlpir.features.providers.trees;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.features.providers.trees.ITreeProvider;
import it.unitn.nlpir.projectors.Projector;
import it.unitn.nlpir.projectors.old.ConstituencyTreeProjector;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.util.Pair;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelConstituencyTreeProvider implements ITreeProvider {
	static final Logger logger = LoggerFactory.getLogger(RelConstituencyTreeProvider.class);

	private Projector projector;	
	
	public RelConstituencyTreeProvider() {
		this.projector = new ConstituencyTreeProjector();
	}
	
	public RelConstituencyTreeProvider(Projector projector) {
		this.projector = projector;
	}

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
