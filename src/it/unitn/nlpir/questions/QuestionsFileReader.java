package it.unitn.nlpir.questions;

import it.unitn.nlpir.resultsets.ResultSetFileReader;
import it.unitn.nlpir.util.ReadFile;
import it.unitn.nlpir.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import edu.berkeley.nlp.lm.util.Logger;

/**
 * <p>
 * Reader for the files with the questions in the Question Answering context or for the files with the text1 in the (text1,text2) pairs for the case of text pair classification.
 * </p>
 * 
 * <p>
 * The file is space-delimited and contains the following columns (note that the actual text of text1 is contained in a file to be read by {@link ResultSetFileReader}).:
 * <ol>
 * <li> Question (text1) id
 * <li> Question (text1) text
 * </ol>
 * </p>
 * 
* @author IKernels group
 *
 */
public class QuestionsFileReader {
	
	public static List<Question> getQuestions(String questionsPath) {
		List<Question> questions = new ArrayList<>();
		ReadFile in = new ReadFile(questionsPath);
		while(in.hasNextLine()) {
			String line = in.nextLine().replace("\t", " ");
			String[] data = new String[2];
			data[0] = line.substring(0, line.indexOf(" "));
			data[1] = line.substring(line.indexOf(" ") + 1, line.length());
			Question question = new Question();
			question.setId(data[0]);
			question.setText(StringUtil.filterHTMLEntities(data[1]));
			questions.add(question);
		}
		return questions;
	}

	public static void assignAnswerPatterns(List<Question> questions, String answersPath) {
		Map<String, String> qidToPattern = new HashMap<>();
		ReadFile in = new ReadFile(answersPath);
		while(in.hasNextLine()) {
			String line = in.nextLine().trim();
			List<String> data = Lists.newArrayList(Splitter.on(" ").limit(2).split(line));
			if (data.size()<2){
				Logger.warn(String.format("Format error in %s: '%s'", answersPath, line));
				continue;
			}
			String qid = data.get(0);
			String pattern = data.get(1);
			
			String storedPattern = qidToPattern.get(qid);
			if(storedPattern != null) {
				pattern = storedPattern + "|" + pattern;
			}
			qidToPattern.put(qid, pattern);
		}
		in.close();
		
		for(Question question : questions) {
			String qid = question.getId();
			if(qidToPattern.containsKey(qid)) {
				question.setPerlAnswerPattern(qidToPattern.get(qid));
			}
		}
	}

}
