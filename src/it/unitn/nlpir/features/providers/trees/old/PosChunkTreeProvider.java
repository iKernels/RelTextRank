package it.unitn.nlpir.features.providers.trees.old;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.features.providers.trees.ITreeProvider;
import it.unitn.nlpir.uima.AnnotationNotFoundException;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.trees.Tree;

public class PosChunkTreeProvider implements ITreeProvider {
	static final Logger logger = LoggerFactory.getLogger(PosChunkTreeProvider.class);
	
	private final static String defaultLeafTextType = TokenTextGetterFactory.LEMMA;
	private String leafTextType;
	
	public PosChunkTreeProvider() {
		this(defaultLeafTextType);
	}
	
	public PosChunkTreeProvider(String leafTextType) {
		this.leafTextType = leafTextType;
	}
	
	@Override
	public Pair<String, String> getTrees(QAPair qaPair) {
		String qTree = null;
		String dTree = null;
		try {
			JCas questionCas = qaPair.getQuestionCas();
			JCas documentCas = qaPair.getDocumentCas();
			
			Tree q = TreeUtil.buildTree(UIMAUtil.getPosChunkTree(questionCas));
			Tree d = TreeUtil.buildTree(UIMAUtil.getPosChunkTree(documentCas));
			TreeUtil.finalizeTreeLeaves(questionCas, q, this.leafTextType);
			TreeUtil.finalizeTreeLeaves(documentCas, d, this.leafTextType);
			qTree = TreeUtil.serializeTree(q);
			dTree = TreeUtil.serializeTree(d);		
		} catch (AnnotationNotFoundException e) {
			logger.warn("PosChunk tree annotation not found: {}", e);
		}

		return new Pair<String, String>(qTree, dTree);
	}
}
