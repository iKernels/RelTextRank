package it.unitn.nlpir.features.providers.trees;

import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class ProjectedTreeProvider implements ITreeProvider {
	static final Logger logger = LoggerFactory.getLogger(ProjectedTreeProvider.class);
	
	@Override
	public Pair<String, String> getTrees(QAPair qaPair) {
		Preconditions.checkNotNull(qaPair.getQaProj());
		return qaPair.getQaProj();
	}
}
