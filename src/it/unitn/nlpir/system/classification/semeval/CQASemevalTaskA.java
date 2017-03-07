package it.unitn.nlpir.system.classification.semeval;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Stopwatch;

import it.unitn.it.nlpir.types.Author;
import it.unitn.it.nlpir.types.UserMention;
import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.questions.QuestionsFileReader;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.core.TextPairConversionBase;
import it.unitn.nlpir.system.core.RERTextPairConversion;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.types.DocumentId;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.types.semeval.PostSubject;
import it.unitn.nlpir.uima.AnalysisEngineList;
import it.unitn.nlpir.uima.UIMAUtil;

/**
 * <p>
 * Launches a pipeline to prepare the SVM Light files for classification on <a href=http://alt.qcri.org/semeval2016/>Semeval 2016 data</a>
 * </p>
 * 
 * <p>
 * Add the following information into the cases:
 * <ul>
 * <li> Reads the users' signatures (short texts the users use to represent themselves and which they add at the end of all their messages, and adds information
 * about them directly into the CASes of the original question and answer.
 * <li> Adds the nickname of the text author directly into its respective CAS
 * <li> Enriches the answer CASes with the shallow Pos Chunk trees of the previous and the following answers in the answer thread in the Qatar Living post
 * <li> Marks mentions of the names of the author of the question and the mentions of the names of the users who replied to the question before the answer in consideration
 * </ul>
 * </p>
 * 
 * 
 * <p>
 * This class in the entry point to the following publication:
 * <br> 
 * <b>Tymoshenko, Kateryna, Daniele Bonadiman, and Alessandro Moschitti. 
 * <i>"Learning to Rank Non-Factoid Answers: Comment Selection in Web Forums."</i> Proceedings of the 25th ACM International on Conference on Information and Knowledge Management. ACM, 2016.</b>
 * 
 * </p>
* @author IKernels group
 *
 */

public class CQASemevalTaskA extends QASemevalClassificationWithContext{
	protected static final Logger logger = LoggerFactory.getLogger(TextPairConversionBase.class);
	
	@Argument(description = "name of the tree finalizer class to be used for context trees")
	protected static String subjectsFile;
	
	
	protected ITreePostprocessor treeFinalizer;
	
	protected Map<String, Question> subjects;
	protected AnalysisEngineList additionalAes;
	
	
	
	protected void analyzeQuestion(JCas cas){
		
		analyzer.analyze(cas);
		addUserInfo(cas);
		String qid = JCasUtil.selectSingle(cas, DocumentId.class).getId().replace("question-", "");
		
		PostSubject ps = new PostSubject(cas);
		ps.setBegin(0);
		ps.setEnd(subjects.get(qid).getText().length());
		ps.addToIndexes();
		if (!ps.getCoveredText().equals(subjects.get(qid).getText()))
			logger.debug(ps.getCoveredText()+" <-> " );
		if (this.additionalAes!=null){
			for (AnalysisEngine ae : this.additionalAes) {
				String aeName = ae.getMetaData().getName();
				UIMAUtil.removeOutputs(cas, ae);
				UIMAUtil.removeAnnotatorRunAnnotation(cas, aeName);
				try {
					SimplePipeline.runPipeline(cas, ae);
				} catch (UIMAException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String docId = JCasUtil.selectSingle(cas, DocumentId.class).getId().replace("question-", "");
		addSignatureAnnotation(cas, docId);
	}
	
	protected void analyzeDocument(JCas cas){
		analyzer.analyze(cas);

		String docId = JCasUtil.selectSingle(cas, DocumentId.class).getId().replace("document-", "");
		addSignatureAnnotation(cas, docId);
		addUserInfo(cas);
	}
	
	public CQASemevalTaskA(){
		super();
		subjects = new HashMap<String,Question>();
		for (Question q : QuestionsFileReader.getQuestions(subjectsFile)){
			subjects.put(q.getId(), q);
		}
	}
	
	protected void processDocumentCases(Question question, String id,
			List<Result> results, List<Candidate> candidates, 
			List<JCas> resultCASes) {
		
		String topicStarter = documentToUserIDMap.get(question.getId());
		Set<String> userNames = new HashSet<String>();
		for (JCas documentCas : resultCASes) {
			try {
				userNames.add(JCasUtil.selectSingle(documentCas,Author.class).getName().replace("document-", ""));
				for (Token t : JCasUtil.select(documentCas, Token.class)){
					checkForUserMention(documentCas, t, topicStarter,"AUTHOR_MENTION");
					for (String u: userNames){
						checkForUserMention(documentCas, t, u,"USER_MENTION");
					}
			}
			} catch (Exception e){
				logger.error("Author annotation missing for:"+JCasUtil.selectSingle(documentCas,DocumentId.class).getId());
			}
		}	
		super.processDocumentCases(question, id, results, candidates, resultCASes);
	}

	protected void checkForUserMention(JCas documentCas, Token t, String u,String name) {
		if (u==null)
			return;
		if (t.getCoveredText().toLowerCase().equals(u.toLowerCase())){
			UserMention um = new UserMention(documentCas);
			um.setBegin(t.getBegin());
			um.setEnd(t.getEnd());
			um.setName(name);
			um.addToIndexes(documentCas);
			
		}
	}
	
	
	
	public static void main(String[] args) {

		Args.parse(CQASemevalTaskA.class, args);
	
		RERTextPairConversion application = new CQASemevalTaskA();

		try {
			Stopwatch watch = new Stopwatch();
			watch.start();	
			application.execute();
			logger.info("Run-time ({}): {} (ms)", RERTextPairConversion.mode, watch.elapsedMillis());
		} catch (IllegalArgumentException e) {
			Args.usage(application);
			e.printStackTrace();
		}
	}

	
}
