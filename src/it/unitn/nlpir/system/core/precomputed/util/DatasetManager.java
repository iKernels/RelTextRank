package it.unitn.nlpir.system.core.precomputed.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.system.core.precomputed.GramTypeConstants;
import it.unitn.nlpir.util.Pair;

public class DatasetManager {
	public static final String DEFAULT_TRAIN_PREFIX = "R";
	public static final String DEFAULT_DEV_PREFIX = "D";
	public static final String DEFAULT_TEST_PREFIX = "T";
	
	public static final int DEFAULT_TRAIN_THRESHOLD = 10;
	
	
	protected SubsetManager[] data;
	public static final Integer TRAIN=0;
	public static final Integer DEV=1;
	public static final Integer TEST=2;
	public static final Integer[] modes = {TRAIN, DEV, TEST};
	
	protected String trainPrefix = DEFAULT_TRAIN_PREFIX;
	protected String devPrefix = DEFAULT_DEV_PREFIX;
	protected String testPrefix = DEFAULT_TEST_PREFIX;
	
	protected Map<String,Integer> prefixToDatasetTypeMapping;
	
	protected int maxTrainExamples = DEFAULT_TRAIN_THRESHOLD; 
	protected static final Logger logger = LoggerFactory.getLogger(DatasetManager.class);
	
	public DatasetManager(String answerFile) throws IOException {
		this(answerFile,true,true,false, DEFAULT_TRAIN_THRESHOLD);
	}
	
	public DatasetManager(String answerFile, int maxTrainExamples) throws IOException {
		this(answerFile,true,true,false, maxTrainExamples);
	}
	
	public DatasetManager(String answerFile,boolean filterAllPositive, boolean filterAllNegative, 
			boolean filterAllPositiveInTrain, int maxTrainExamples) throws IOException {
		this.maxTrainExamples = maxTrainExamples;
		data = new SubsetManager[modes.length];
		
		for (Integer mode : modes)
			if (mode==TRAIN)
				data[mode]= new SubsetManager(maxTrainExamples);
			else
				data[mode]= new SubsetManager();
		
		prefixToDatasetTypeMapping= new HashMap<String,Integer>();
		
		prefixToDatasetTypeMapping.put(trainPrefix, TRAIN);
		prefixToDatasetTypeMapping.put(devPrefix, DEV);
		prefixToDatasetTypeMapping.put(testPrefix, TEST);
		
		populateLists(answerFile, filterAllPositive, filterAllNegative, filterAllPositiveInTrain);
	}

	public  Pair<List<String>, List<String>> getOrderedIdsList(int mode, String labelType) {
		if (!labelType.equals(GramTypeConstants.QA_MODE_LABEL))
			return getOrderedIdsList(TRAIN, mode, labelType, labelType);
		else
			return getOrderedIdsList(mode, mode, GramTypeConstants.QQ_MODE_LABEL, GramTypeConstants.AA_MODE_LABEL);
	}
	
	public  Pair<List<String>, List<String>> getOrderedIdsList(int mode1, int mode2, String labelType1, String labelType2) {
		//first always train
		List<String> a = data[mode1].getIdsByTypeList(labelType1);
		List<String> b = data[mode2].getIdsByTypeList(labelType2);
		return new Pair<List<String>,List<String>>(a,b);
	}
	
	public  Map<String, Integer>  getLabelMappings(int mode, String labelType) {
		return null;
	}
	 
	
	protected void populateLists(String answerFile, boolean filterAllPositive, boolean filterAllNegative, boolean filterAllPositiveInTrain)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(answerFile), "UTF8"));
		String line = null;

		
		while ((line = in.readLine()) != null) {
			String [] parts = line.split(" ", 6);
			boolean label=parts[4].equals("true");
			
			Integer mode = -1;
			for (String prefix : prefixToDatasetTypeMapping.keySet())
				if (parts[0].startsWith(prefix)) {
					mode = prefixToDatasetTypeMapping.get(prefix);
					break;
				}
			
			if (mode<0){
				in.close();
				throw new java.lang.Error(String.format("Format error in line %s, unknown id prefix", line));
			}
			

			data[mode].addPair(parts[0], parts[1], label);
			
			
		}
		in.close();
		
		filterData(filterAllPositive, filterAllNegative, filterAllPositiveInTrain);
		
		logger.info(String.format("Read %d train pairs, %d dev pairs, %d test pairs", data[TRAIN].size(), data[DEV].size(), data[TEST].size()));
	}


	protected void filterData(boolean filterAllPositive, boolean filterAllNegative, boolean filterAllPositiveInTrain) {
		if (filterAllPositive) {
			data[TEST].filterAllPositive();
			data[DEV].filterAllPositive();
		}
		if (filterAllPositiveInTrain) {
			data[TRAIN].filterAllPositive();
		}
		if (filterAllNegative) {
			for (int mode : modes)
				data[mode].filterAllNegative();
		}
	}
	
}
