package it.unitn.nlpir.resultsets;

import it.unitn.nlpir.questions.QuestionsFileReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Reader for the files with the candidate answers in the Question Answering context or for the files with the text2 in the (text1,text2) pairs for the case of text pair classification
 * </p>
 * <p>
 * The file is space delimited with the following columns for the (text1,text2)  pair (note that the actual text of text1 is contained in a file to be read by {@link QuestionsFileReader}). :
 * <ol>
 * <li> text1 id
 * <li> text2 id
 * <li> (optional, should be set to any integer number if unavailable) if text1 is a search query, and text2 comes from a search engine, the text2 rank here. There ranking starts with 0.
 * <li> (optional, should be set to any flaot number if unavailable) if text1 is a search query, and text2 comes from a search engine, the text2 rank here. There ranking starts with 0.
 * <li> <b>true</b> if (text1,text2) should be classified as positive instance, <b>false</b> otherwise
 * <li> raw text of text2
 * <ol>
 * In the question answering setting the text1 and text2 are the question and the candidate answer respectively. The text1-to-text2 mapping is one-to-many mapping.
 * </p>
* @author IKernels group
 *
 */
public class ResultSetFileReader {
	private final Logger logger = LoggerFactory.getLogger(ResultSetFileReader.class);
	// Keep all candidates in the resultSet by default
	private static int candidatesToKeep = -1;

	private HashMap<String, List<Result>> qid2resultSet;

	public ResultSetFileReader(String path) {
		constructResultSet(path, candidatesToKeep, new QAResultSetParser());
	}
	
	public ResultSetFileReader(String path, ResultSetParser parser) {
		constructResultSet(path, candidatesToKeep, parser);
	}
	
	public ResultSetFileReader(String path, int candidatesToKeep) {
		constructResultSet(path, candidatesToKeep, new QAResultSetParser());
	}

	public ResultSetFileReader(String path, int candidatesToKeep, ResultSetParser parser) {	
		constructResultSet(path, candidatesToKeep, parser);
	}
	
	public void constructResultSet(String path, int candidatesToKeep, ResultSetParser parser) {
		qid2resultSet = new HashMap<>();
		String line="";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
			
			while ((line = br.readLine()) != null) {
				try{
					Result result = parser.parse(line.trim());
					
					// Add result to the map
					List<Result> resultSet = qid2resultSet.get(result.questionId);
					if (resultSet == null) {
						resultSet = new ArrayList<>();
						qid2resultSet.put(result.questionId, resultSet);
					}
					// Don't store more candidates than candidatesToKeep
					if (candidatesToKeep >= 0
							&& resultSet.size() >= candidatesToKeep)
						continue;
					resultSet.add(result);
				}
				catch (ArrayIndexOutOfBoundsException e) {
					logger.error("Skipping (out of bounds): "+line);
			 	}
			}
		} catch (IOException|ArrayIndexOutOfBoundsException e) {
			logger.debug(line);
			e.printStackTrace();
		}
		
		logger.info("Questions read: {}", qid2resultSet.size());
	}

	public List<Result> getResults(String qid) {
		return qid2resultSet.get(qid);
	}
	
	public List<Result> getResults(String qid, int candidatesToKeep) {
		List<Result> ret = qid2resultSet.get(qid);
		if (ret == null)
			return null;
		return ret.subList(0, Math.min(ret.size(), candidatesToKeep));
	}
}
