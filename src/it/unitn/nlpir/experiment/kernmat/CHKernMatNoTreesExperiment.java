package it.unitn.nlpir.experiment.kernmat;

import java.util.Properties;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.experiment.AnalyzerConfig;
import it.unitn.nlpir.experiment.fqa.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.features.QAPair;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.tree.PosChunkTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;
import it.unitn.nlpir.uima.AnalysisEngineList;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;
import svmlighttk.SVMVector;

/**
 
* @author IKernels group
 *
 */
public class CHKernMatNoTreesExperiment extends StanfordAETrecQAWithQCExperiment {
	protected static final Logger logger = LoggerFactory.getLogger(CHKernMatNoTreesExperiment.class);
	protected static final Pair<String,String> dummyQaPair = new Pair<String,String>(TreeUtil.serializeTree(TreeUtil.createNode("ROOT")),TreeUtil.serializeTree(TreeUtil.createNode("ROOT")));
	public CHKernMatNoTreesExperiment(String configFile) {
		super(configFile);
	}
	
	public CHKernMatNoTreesExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public CHKernMatNoTreesExperiment(Properties p) {
		super(p);
	}
	
	public CHKernMatNoTreesExperiment() {
		super();
	}
	
	protected void setupProjector() {
		logger.info(String.format("pruningRay=%d", pruningRay));
		this.projector = Projectors.getRelTreeProjector(new PosChunkTreeBuilder(), pruningRay,  new StartsWithOrContainsTagPruningRule(), 
				new TreeLeafFinalizer());
	}

	@Override
	public AnalysisEngineList getAnalysisEngineList() {
		return AnalyzerConfig.getStanfordGenericAnalysisEngineList();
	}

	@Override
	public Candidate generateCandidate(JCas questionCas, JCas documentCas, Result result) {
		
		SVMVector featureVector = fb.getFeatures(new QAPair(questionCas, documentCas, result,
				new SVMVector(), dummyQaPair));

		return new Candidate(result, dummyQaPair, featureVector);
	}
}
