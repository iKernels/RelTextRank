package it.unitn.nlpir.resultsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.system.core.TextPairConversionBase;
import it.unitn.nlpir.util.StringUtil;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
/**
 * (c)UNITN
* @author IKernels group
 *
 */
public class Result {	
	// Specifies the expected format of the line containing a Result.
	private static final int numFields = 6;
	
	public final String questionId;
	public final String documentId;
	public final String rankingPosition;
	public final String rankingScore;
	public final String relevantFlag;
	public String documentText;
	public String answerPhrase;
	public final static String TRUE = "true";
	protected static final Logger logger = LoggerFactory.getLogger(TextPairConversionBase.class);
	
	/**
	 * 
	 * @param questionId
	 * @param documentId
	 * @param rankingPosition
	 * @param rankingScore
	 * @param relevantFlag
	 * @param documentText
	 */
	public Result(String questionId, String documentId, String rankingPosition,
			String rankingScore, String relevantFlag, String documentText) {
		this.questionId = questionId;
		this.documentId = documentId;
		this.rankingPosition = rankingPosition;
		this.rankingScore = rankingScore;
		this.relevantFlag = relevantFlag;
		this.documentText = documentText;
	}
	
	public Result(String line) {
		Iterable<String> tokens = Splitter.on(" ").limit(numFields).trimResults().split(line);
		String[] toks = Iterables.toArray(tokens, String.class);
		assert (toks.length >= numFields) : "Wrong number of fields in the resultset. " +
				"Check that the format of your resultset is correct.";
		
		this.questionId = toks[0];
		this.documentId = toks[1];
		this.rankingPosition = toks[2];
		this.rankingScore = toks[3];
		this.relevantFlag = toks[4];
		if (toks.length < 6)
			this.documentText = "NULL";
		else
			this.documentText = StringUtil.filterHTMLEntities(toks[5]);
	}
	
	@Override
	public String toString() {
		return questionId + " " + documentId + " " + rankingPosition
				+ " " + rankingScore + " " + relevantFlag + " " + documentText;
	}
}
