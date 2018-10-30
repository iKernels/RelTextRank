package it.unitn.nlpir.system.core.precomputed;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.experiment.kernmat.NoUIMAExperiment;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.core.TextPairConversionBase;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.tree.TreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizer;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.TreeUtil;
import it.unitn.nlpir.util.WriteFile;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import edu.stanford.nlp.trees.Tree;



/**
 * 
 *
 */
public class TreeRespresentationListExtractor  extends TextPairConversionBase{

	
	protected static final Logger logger = LoggerFactory.getLogger(SimilarityListGenerator.class);
	protected NoUIMAExperiment noUIMAExperiment;
	
	public static  String TAB_DELIMITER = "\t"; 
	public static  String SPACE_DELIMITER = " ";
	
	
	protected List<NoUIMAExperiment> noUIMAExperiments;
	
	@Argument(description = "parallelize", required=false)
	protected static Integer threads = -1;
	
	@Argument(description = "logstep", required=false)
	protected static Integer logstep = 10000;
	
	
	
	@Argument(description = "Output Matrix file", required=true)
	protected static String outputTreesFile;
	
	
	@Argument(description = "Tree builder class", required=true)
	protected static String treeBuilderClass;
	
	protected TreeBuilder treeBuilder;
	protected ITreePostprocessor treePostprocessor;
	protected Kernel kernel;

	protected JCas cas;

	public TreeRespresentationListExtractor(){
		super();
		initTreeBuilder();
		cas = analyzer.getNewJCas();
		this.treePostprocessor = new TreeLeafFinalizer();
	}

	
	public Map<String,String> getIdToTreeMap() {
		Map<String,String> m = new HashMap<String,String>();
		for (int i = 0, n = questions.size(); i < n; i++) {
			Question q = questions.get(i);
			m.put(q.getId(), getTree(q.getId(), q.getText()));
			List<Result> results = answers.getResults(q.getId(), 100000);
			if (results == null) {
				logger.warn("No resultlist found for qid: {}", q.getId());
				continue;
			}
			
			for (Result result : results) {
				m.put(result.documentId, getTree(result.documentId, result.documentText));
			}
			
		}
		return m;
	}

	protected String getTree(String id, String text) {
		UIMAUtil.setupCas(cas,  id, text);
		analyzer.analyze(cas);
		Tree questionTree = treeBuilder.getTree(cas);
		treePostprocessor.process(questionTree, cas);
		return TreeUtil.serializeTree(questionTree);
	}
	
	
		
	public void execute() {
		Map<String, String> idToTreeMap = getIdToTreeMap();
		logger.info("Finished processing the texts");
		WriteFile writer =  null;
		writer = new WriteFile(outputDir, outputTreesFile);
		for (String id: idToTreeMap.keySet()){
			writer.writeLn(String.format("%s\t%s", id, idToTreeMap.get(id)));
		}
		writer.close();
		logger.info(String.format("Wrote the trees to %s/%s", outputDir, outputTreesFile));
	}
	
	protected Example getExample(String tree) {
		Example example = new SimpleExample();
		
		Representation r = new TreeRepresentation();
		try {
			r.setDataFromText(tree);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		example.addRepresentation("T", r);
		
		return example;
	}

	

	public void initTreeBuilder() {
		Constructor<?> c;
		try {
			c = Class.forName(treeBuilderClass).getConstructor();
			treeBuilder = (TreeBuilder) c.newInstance();
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public List<Pair<String,String>> readPairsOfInterestFromFile(String inputFileName) throws IOException{
		List<Pair<String,String>> idsList = new ArrayList<Pair<String,String>>();
		for (String line : FileUtils.readLines(new File(inputFileName), "UTF-8")) {
			String [] parts = line.trim().split("\t");
			idsList.add(new Pair<String,String>(parts[0], parts[1]));
		}
		return idsList;
	}
	
	public Set<String> getUniqueIdsSet(List<Pair<String,String>> pairs){
		Set<String> result = new HashSet<String>();
		for (Pair<String,String> p : pairs) {
			result.add(p.getA());
			result.add(p.getB());
		}
		return result;
	}

	public static void main(String[] args) {
		try{
			Args.parse(TreeRespresentationListExtractor.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(TreeRespresentationListExtractor.class);
			
			System.exit(0);
		}
		
		TreeRespresentationListExtractor application = new TreeRespresentationListExtractor();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time: {} (ms)", watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}
	}
}
