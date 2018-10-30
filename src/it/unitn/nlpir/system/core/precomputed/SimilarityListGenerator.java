package it.unitn.nlpir.system.core.precomputed;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.utils.JacksonSerializerWrapper;
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
import it.unitn.nlpir.util.ZipWriteFile;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Stopwatch;
import edu.stanford.nlp.trees.Tree;



/**
 * 
 *
 */
public class SimilarityListGenerator  extends TextPairConversionBase{

	protected static final Logger logger = LoggerFactory.getLogger(SimilarityListGenerator.class);
	protected NoUIMAExperiment noUIMAExperiment;
	
	public static  String TAB_DELIMITER = "\t"; 
	public static  String SPACE_DELIMITER = " ";
	
	
	protected List<NoUIMAExperiment> noUIMAExperiments;
	
	@Argument(description = "parallelize", required=false)
	protected static Integer threads = -1;
	
	@Argument(description = "logstep", required=false)
	protected static Integer logstep = 10000;
	
	@Argument(description = "Kelp kernel configuration file", required=true)
	protected static String kernelConfigurationFile;
	
	@Argument(description = "Input ids file", required=true)
	protected static String idsFile;
	
	@Argument(description = "Output Matrix file", required=true)
	protected static String outputSimilaritiesFile;
	
	
	@Argument(description = "Tree builder class", required=true)
	protected static String treeBuilderClass;
	
	protected TreeBuilder treeBuilder;
	protected ITreePostprocessor treePostprocessor;
	protected Kernel kernel;

	protected JCas cas;

	public SimilarityListGenerator(){
		super();
		initKernel();
		initTreeBuilder();
		cas = analyzer.getNewJCas();
		this.treePostprocessor = new TreeLeafFinalizer();
	}
	
	public static Pair<String,String> getIdPair(String line) {
		String [] parts = line.split(SPACE_DELIMITER);
		return new Pair<String,String>(parts[0],parts[1]);
	}
	
	
	public Map<String,String> getIdToTreeMap(Set<String> ids) {
		Map<String,String> m = new HashMap<String,String>();
		for (int i = 0, n = questions.size(); i < n; i++) {
			Question q = questions.get(i);
			
			if (ids.contains(q.getId())) {
				m.put(q.getId(), getTree(q.getId(), q.getText()));
			}
			
			List<Result> results = answers.getResults(q.getId(), 100000);
			if (results == null) {
				logger.warn("No resultlist found for qid: {}", q.getId());
				continue;
			}
			
			for (Result result : results) {
				if (ids.contains(result.documentId)) {
					m.put(result.documentId, getTree(result.documentId, result.documentText));
				}
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
		long globalStart = System.currentTimeMillis();
		
		
		//reading the ids
		
		List<Pair<String, String>> idsPairs = null;
		try {
			idsPairs = readPairsOfInterestFromFile(idsFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(String.format("Read %d pairs to process", idsPairs.size()));
		Set<String> ids = getUniqueIdsSet(idsPairs);
		logger.info(String.format("%d unique texts to annotate", ids.size()));
		logger.info("Started processing the texts");
		Map<String, String> idToTreeMap = getIdToTreeMap(ids);
		logger.info("Finished processing the texts");
		
		
		globalStart = System.currentTimeMillis();
		logger.info(String.format("Started computing the kernels"));
		WriteFile writer =  null;
		writer = new ZipWriteFile(outputDir, outputSimilaritiesFile);
		int i = 0;
		int logStep = 1000;
		for (Pair<String,String> p : idsPairs) {
			if (!idToTreeMap.containsKey(p.getA())) {
				logger.warn(String.format("Key %s is missing in the tree map", p.getA()));
				continue;
			}
			if (!idToTreeMap.containsKey(p.getB())) {
				logger.warn(String.format("Key %s is missing in the tree map", p.getB()));
				continue;
			}
			String treeString1 = idToTreeMap.get(p.getA());
			String treeString2 = idToTreeMap.get(p.getB());
			
			Example e1 = getExample(treeString1);
			Example e2 = getExample(treeString2);
			float result = kernel.innerProduct(e1, e2);
			logger.debug(String.format("%s\t%s\t%s\t%s\t%.5f", p.getA(), p.getB(), treeString1, treeString2, result));
			writer.writeLn(String.format("%s\t%s\t%.5f", p.getA(), p.getB(), result));
			if (i % logStep == 0)
				logger.info(String.format("Processed %d pairs", i));
			i++;
		}
		logger.info(String.format("Computed kernels for %d pairs in %d", idsPairs.size(), System.currentTimeMillis()-globalStart));
		writer.close();

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
	
	public void initKernel() {
		JacksonSerializerWrapper serializer = new JacksonSerializerWrapper();
		try {
			this.kernel = serializer.readValue(new File(kernelConfigurationFile), Kernel.class);
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
			Args.parse(SimilarityListGenerator.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(SimilarityListGenerator.class);
			
			System.exit(0);
		}
		
		SimilarityListGenerator application = new SimilarityListGenerator();

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
