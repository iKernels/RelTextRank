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
import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.WriteFile;
import it.unitn.nlpir.util.ZipWriteFile;
import svmlighttk.SVMTKExample;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Stopwatch;



/**
 * 
 * <p>
 * The class generates the kernel matrix of pairwise similarities for a given corpus.
 * Takes question/answer files as input where the first letter of and ID should be R|D|T (tRain|Dev|Test)
 * </p>
 * <p>
 * 
 * To avoid disc IO overhead, keeps everything in memory. JCases are extremely constly to be held in memory, so I am reading everything into a pseudo-cas struture,
 * and I have created version of feature extractors which work for these data.
 * 
 *  * It measures all-to-all similarity reading the input data from two input files: question file and answer file
 * 
 * Here the input file format is as follows:
 * 
 * <code>qid TAB aid TAB tree</code>
 * 
 * We tree in the quesiton/answer file, correspondingly, is a tree
 * 
 * It outputs three files:
 * <li> q-q similarity (all-to-all)
 * <li> a-a similarity (all-to-all)
 * <li> q-a (relevant a to a given q)
 * 
 * We assume that all input files come pre-filtered.
 * 
 * 
 * </p>
* @author IKernels group
 *
 */
public class KelpGramMatrixGenerator  {

	protected static final Logger logger = LoggerFactory.getLogger(KelpGramMatrixGenerator.class);
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
	
	@Argument(description = "Mode (train|test|dev). Output files in the output folder will be marked with their respective mode", required=true)
	protected static String mode;
	
	@Argument(description = "SVMLight data file train", required=true)
	protected static String svmLightFileTrain;
	
	@Argument(description = "IDs data for train", required=true)
	protected static String idsFileTrain;
	
	@Argument(description = "Output Matrix file", required=true)
	protected static String outputMatrixFolder;
	
	
	@Argument(description = "SVMLight data file test  (if not set then train gram matrix will be computed)", required=false)
	protected static String svmLightFileTest;
	
	@Argument(description = "IDs data for test (if not set then train gram matrix will be computed)", required=false)
	protected static String idsFileTest;
	
	protected ExamplesContainer questions;

	protected ExamplesContainer candidateAnswers;

	protected Kernel kernel;
	
	@Argument(description = "thread number (i.e. process only n-th pair); if -1 then do not parallelize;", required=false)
	protected static Integer thread = -1;
	
	@Argument(description = "total number of threads;", required=false)
	protected static Integer totalThreadNumber = -1;
	
	//allow overwriting new annotations in the dynamic CASes in RAM (serialization in XMIs is controlled by doNotStoreNew)
	@Argument(description = "Skip qq comparsion", required = false)
	protected static boolean skipQQ = false;
	
	@Argument(description = "Skip aa comparsion", required = false)
	protected static boolean skipAA = false;
	
	@Argument(description = "Skip qa comparsion", required = false)
	protected static boolean skipQA = false;
		
	public static Pair<String,String> getIdPair(String line) {
		String [] parts = line.split(SPACE_DELIMITER);
		return new Pair<String,String>(parts[0],parts[1]);
	}
	
	public Pair<ExamplesContainer,ExamplesContainer> readExamplesFromFile(String examplesFile, String relevancyFile) throws IOException{
		
		List<SVMTKExample> examples = FileUtils.readLines(new File(examplesFile)).stream().
				map(SVMTKExample::parseTreeAndVectorStr).collect(Collectors.toList());
		

		List<Pair<String,String>> ids = FileUtils.readLines(new File(relevancyFile)).stream().
				map(KelpGramMatrixGenerator::getIdPair).collect(Collectors.toList());
		
		ExamplesContainer questionDataset = new ExamplesContainer();
		ExamplesContainer answerDataset = new ExamplesContainer();
		
		for (int i = 0; i < examples.size(); i++) {
			questionDataset.add(ids.get(i), examples.get(i).getTrees().get(0));
			answerDataset.add(ids.get(i), examples.get(i).getTrees().get(1));
		}
		
		return new Pair<ExamplesContainer, ExamplesContainer>(questionDataset,answerDataset);
		
	}

	protected Example getExample(SVMTKExample svmExample) {
		Example example = new SimpleExample();
		
		Representation r1 = new TreeRepresentation();
		try {
			r1.setDataFromText(svmExample.getTrees().get(0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Representation r2 = new TreeRepresentation();
		try {
			r2.setDataFromText(svmExample.getTrees().get(1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		example.addRepresentation("TQ", r1);
		example.addRepresentation("TA", r2);
		return example;
	}

	public KelpGramMatrixGenerator(){
		initKernel();
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
	
	
	public boolean isTrainQuestion(Question q) {
		return (q.getId().startsWith("R"));
	}
	
	

	public void execute() throws IOException {
		long globalStart = System.currentTimeMillis();

		//reducing the time for reading from the hard drive
		logger.info("Started reading the data");
		Pair<ExamplesContainer, ExamplesContainer> dataset1 = readExamplesFromFile(svmLightFileTrain, idsFileTrain);
		
		boolean triangular = svmLightFileTest == null;
		Pair<ExamplesContainer, ExamplesContainer> dataset2 = triangular ? dataset1 :  readExamplesFromFile(svmLightFileTest, idsFileTest);
		
		
		logger.info("Finished reading the cached data");
		logger.info(String.format("Read the data in %d ms", System.currentTimeMillis()-globalStart));
		
		globalStart = System.currentTimeMillis();
		WriteFile writer =  null;
		if (skipQQ)
			logger.info("Skipping comparing QQ pairs");
		else {
			logger.info("Processing question-question pairs");
			writer = new ZipWriteFile(outputMatrixFolder,String.format("qq-%s", mode));
			execute(writer, dataset1.getA(), dataset2.getA(), triangular);
			writer.close();
			logger.info(String.format("Processed question-question pairs in %d ms", System.currentTimeMillis()-globalStart));
			globalStart = System.currentTimeMillis();
		}
		
		if (skipAA) {
			logger.info("Skipping comparing AA pairs");
		}
		else {
			logger.info("Processing answer-answer pairs");
			writer = new ZipWriteFile(outputMatrixFolder,String.format("aa-%s", mode));
			execute(writer, dataset1.getB(), dataset2.getB(), triangular);
			writer.close();
			logger.info(String.format("Processed answer-answer pairs in %d ms", System.currentTimeMillis()-globalStart));
			globalStart = System.currentTimeMillis();
		}
		
		if (skipQA) {
			logger.info("Processing question-answer pairs");
		}
		else {
			logger.info("Processing question-answer pairs for one of the datasets");
			writer = new ZipWriteFile(outputMatrixFolder,String.format("qa-%s", mode));
			executeIntraPair(writer, dataset2.getA(), dataset2.getB());
			writer.close();
			logger.info(String.format("Processed question-answer pairs in %d ms", System.currentTimeMillis()-globalStart));
			globalStart = System.currentTimeMillis();
		}
		
	
		
	}


	protected void execute(WriteFile writer, ExamplesContainer container) {
		execute(writer, container, container, true);
	}
	
	protected void serializeSimilarity(WriteFile writer, String id1, String id2, double similarity) {
		writer.writeLn(String.format("%s\t%s\t%.10f", id1, id2, similarity));
	}
	

	protected void serializeSimilarity(WriteFile writer, Pair<String, String> id1, Pair<String,String> id2, double similarity) {
		writer.writeLn(String.format("%s\t%s\t%s\t%s\t%.10f", id1.getA(), id1.getB(), id2.getA(), id2.getB(), similarity));
	}
	
	

	protected void executeIntraPair(WriteFile writer, ExamplesContainer containerQ, ExamplesContainer containerA) {
		long globalStart = System.currentTimeMillis();
		int count_time = 0;
		int z = -1;
		

		
		int total = containerQ.size();
		
		logger.info(String.format("Total of %d pairs to be processed: ", total));
		int prev_count_time=0;
		
		
			for (String qid : containerQ.getQuestionIds()) {
			for (String aid : containerQ.getAnswerIds(qid)) {
				Example ex1 = containerQ.get(qid, aid);
				Example ex2 = containerA.get(qid, aid);
				float similarity = kernel.innerProduct(ex1, ex2);
				serializeSimilarity(writer, qid, aid, similarity);
				count_time++;
				if (!(((totalThreadNumber>1) && (z%totalThreadNumber==thread))||(totalThreadNumber<=1))) {
					continue;
				}
				if (count_time-prev_count_time > 1000 ) {
					prev_count_time = count_time;
					double speed = Double.valueOf(System.currentTimeMillis()-globalStart)/Double.valueOf(count_time);
					logger.info(String.format("Taking %.5f ms average per pair", speed));
					logger.info(String.format("Total of %d pairs processed, %d remaning, %.2f hours to go with the current speed ", count_time, total-count_time, Double.valueOf(total-count_time)*speed/1000.0/60.0/60.0));
					
				}
			}
		}
		
		long globalEnd = System.currentTimeMillis();
		logger.info(String.format("Took %.5f ms average per pair", Double.valueOf(globalEnd-globalStart)/Double.valueOf(count_time)));	
	}
	
	protected void execute(WriteFile writer, ExamplesContainer container1, ExamplesContainer container2, boolean triangular) {
		long globalStart = System.currentTimeMillis();
		int count_time = 0;
		int z = -1;
		

		
		int total = triangular ? container1.size()*container1.size()/2 : container1.size()*container2.size();
		
		logger.info(String.format("Total of %s pairs to be processed: ", NumberFormat.getNumberInstance(Locale.US).format(total)));
		int prev_count_time=0;
		
		List<Pair<String, String>> ids1 = container1.getAllIds();
		List<Pair<String, String>> ids2 = triangular ? ids1 : container2.getAllIds();
		
		
		for (int i  = 0;  i<ids1.size(); i++) {
			int start_j = triangular ? i : 0;
			Example ex1 = container1.get(ids1.get(i));
			
			for (int j = start_j; j < ids2.size(); j++) {
				count_time++;
				if (!(((totalThreadNumber>1) && (z%totalThreadNumber==thread))||(totalThreadNumber<=1))) {
					continue;
				}
				//do the computation here
				
				Example ex2 = container2.get(ids2.get(j));
				//compute kernel here
				float similarity = kernel.innerProduct(ex1, ex2);
				serializeSimilarity(writer, ids1.get(i), ids2.get(j), similarity);
				
				if (count_time-prev_count_time > logstep ) {
					prev_count_time = count_time;
					double speed = Double.valueOf(System.currentTimeMillis()-globalStart)/Double.valueOf(count_time);
					logger.info(String.format("Taking %.5f ms average per pair", speed));
					logger.info(String.format("Kernel invoked %d times", kernel.getKernelComputations()));
//					System.out.println("Kernel cache:");
//					System.out.println(kernel.getKernelCache());
//					logger.info(String.format("Cache hits: %d; Cache misses: %d", kernel.getKernelCache().getCacheHits(), kernel.getKernelCache().getCacheMisses()));
					
					logger.info(String.format("Total of %s pairs processed, %s remaning, %.2f hours to go with the current speed ", 
							 NumberFormat.getNumberInstance(Locale.US).format(count_time), 
							 NumberFormat.getNumberInstance(Locale.US).format(total-count_time), Double.valueOf(total-count_time)*speed/1000.0/60.0/60.0));
					
				}
			}
		}
		long globalEnd = System.currentTimeMillis();
		logger.info(String.format("Took %.5f ms average per pair", Double.valueOf(globalEnd-globalStart)/Double.valueOf(count_time)));	
	}
	
	

	public static void main(String[] args) {
		try{
			Args.parse(KelpGramMatrixGenerator.class, args);
			
		}
		catch (IllegalArgumentException e) {
			System.err.println(String.format("CANNOT RUN THE SYSTEM: %s", e.getLocalizedMessage()));
			Args.usage(KelpGramMatrixGenerator.class);
			
			System.exit(0);
		}
		
		KelpGramMatrixGenerator application = new KelpGramMatrixGenerator();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time: {} (ms)", watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
