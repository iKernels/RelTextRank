package it.unitn.nlpir.util;

import it.unitn.nlpir.features.providers.trees.old.DependencyTreeProducer;
import it.unitn.nlpir.features.providers.trees.old.DependencyTreeProducer.DependencyTreeProcessorException;
import it.unitn.nlpir.types.DependencyTree;
import it.unitn.nlpir.uima.AnalysisEngineList;
import it.unitn.nlpir.uima.Analyzer;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import edu.stanford.nlp.trees.Tree;

public class StanfordUtil {
	
	public static Tree getDependencyTree(Tree parse) throws DependencyTreeProcessorException {
		DependencyTreeProducer producer = new DependencyTreeProducer(parse);
		return producer.produceTree();
	}
	
	public static void main(String[] args) {
		AnalysisEngineList aes = new AnalysisEngineList();
		aes.addAnalysisEngine("desc/StanfordCoreNLPAnnotator.xml");
		aes.addTypeSystemForCas("desc/PipelineTypeSystem");
		Analyzer analyzer = new Analyzer(aes);
		JCas cas = analyzer.getNewJCas();
		cas.setDocumentText("He acknowledged the urgent need to get more young people" +
				" studying science and maths, and that is of vital importance to businesses.");
		analyzer.analyze(cas);
		
		DependencyTree tree = JCasUtil.selectSingle(cas, DependencyTree.class);
		System.out.println(tree.getTree().replaceAll("\\(", "[").replaceAll("\\)", "]"));
	}
}
