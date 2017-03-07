package it.unitn.nlpir.system.classification.semeval;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Stopwatch;

import edu.stanford.nlp.trees.Tree;
import it.unitn.it.nlpir.types.Author;
import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.resultsets.Candidate;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.system.core.RERTextPairConversion;
import it.unitn.nlpir.system.datagen.RerankingDataGen;
import it.unitn.nlpir.tree.ITreePostprocessor;
import it.unitn.nlpir.types.ContextTrees;
import it.unitn.nlpir.types.DocumentId;
import it.unitn.nlpir.types.PosChunk;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.TreeUtil;


/**
 * <p>
 * Launches a pipeline to prepare the SVM Light files for classification on <a href=http://alt.qcri.org/semeval2016/>Semeval 2016 data</a>
 * </p>
 * 
 * <p>
 * Add the following information into the cases:
 * <li> Reads the users' signatures (short texts the users use to represent themselves and which they add at the end of all their messages, and adds information
 * about them directly into the CASes of the original question and answer.
 * <li> Adds the nickname of the text author directly into its respective CAS
 * <li> Enriches the answer CASes with the shallow Pos Chunk trees of the previous and the following answers in the answer thread in the Qatar Living post 
 * </p>
 * 
 * </p>
* @author IKernels group
 *
 */

public class QASemevalClassificationWithContext extends QASemevalClassificationNoSignatureCasKeepsText {
	
	@Argument(description = "name of the tree finalizer class to be used for context trees")
	protected static String finalizerClass = "it.unitn.nlpir.experiment.TrecQATestExperiment";
	public static final String NULL_PLACEHOLDER="null";
	
	
	protected ITreePostprocessor treeFinalizer;
	
	
	protected Tree addAuthorInfor(Tree tree, JCas cas, String questionAuthor){
		String documentAuthor = JCasUtil.selectSingle(cas, Author.class).getName();
		if (documentAuthor==null)
			return tree;
			
		if (documentAuthor.equals(questionAuthor)){
			for (Tree preroot : tree.getChildrenAsList()){
				preroot.setValue("SAMEAUTH-"+preroot.value()); 
			}
		}
		return tree;
	}
	
	protected void addPreviousNextTree(JCas cas, JCas nextCas, JCas prevCas, String questionAuthor){
		String prevString = NULL_PLACEHOLDER;
		String nextString = NULL_PLACEHOLDER;
		
		if (prevCas!=null){
			Tree prePosChunk = TreeUtil.buildTree(JCasUtil.selectSingle(prevCas, PosChunk.class).getTree());
			prePosChunk = addAuthorInfor(prePosChunk, prevCas, questionAuthor);
			treeFinalizer.process(prePosChunk, prevCas);
			prevString = TreeUtil.serializeTree(prePosChunk);
			
		}
		
		if (nextCas!=null){
			Tree nextPosChunk = TreeUtil.buildTree(JCasUtil.selectSingle(nextCas, PosChunk.class).getTree());
			nextPosChunk = addAuthorInfor(nextPosChunk, nextCas, questionAuthor);
			treeFinalizer.process(nextPosChunk, nextCas);
			nextString = TreeUtil.serializeTree(nextPosChunk);
		}
		
		ContextTrees prevTree = new ContextTrees(cas, 0, 1);
		StringArray s = new StringArray(cas, 2);
		s.set(0, prevString);
		s.set(1, nextString);
		prevTree.setTrees(s);
		prevTree.addToIndexes();
		
	}
	
	
	protected void addUserInfo(JCas cas){
		if (documentToUserIDMap==null)
			return;
		String id = JCasUtil.selectSingle(cas, DocumentId.class).getId().replaceAll("(document|question)\\-", "");
		
		String username = documentToUserIDMap.get(id);
		Author author = new Author(cas);
		author.setName(username);
		author.addToIndexes();
	}
	
	protected void analyzeQuestion(JCas cas){
		analyzer.analyze(cas);
		addUserInfo(cas);
	}
	
	protected void analyzeDocument(JCas cas){
		analyzer.analyze(cas);
		addUserInfo(cas);
	}
	
	public QASemevalClassificationWithContext(){
		super();
		Class<?> c = null;
		try {
			c = Class.forName(finalizerClass);
			treeFinalizer = (ITreePostprocessor) c.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void execute() {
		RerankingDataGen rerankingDataGen = instantiateRerankingDataGen(mode, outputDir);

	
		int j = 0;
		for (int i = 0, n = questions.size(); i < n; i++) {
			Question question = questions.get(i);
			String id = question.getId();
			
			logger.info(String.format("Processing question: %s (%s of %s)", id, i + 1, n));
			
			// Setup the question CAS
			UIMAUtil.setupCas(questionCas, "question-" + id, question.getText());
			logger.info(questionCas.getDocumentText());
			
			try {
				analyzeQuestion(questionCas);
			}
			catch (Exception e){
				e.printStackTrace();
				logger.error("ERROR when processing question {}, {}", id, question.getText());
				continue;
			}
			String questionClass = null;
			if (JCasUtil.select(questionCas, QuestionClass.class).size()>0)
					questionClass = JCasUtil.select(questionCas, QuestionClass.class).iterator().next()
					.getQuestionClass();
	
			List<Result> results = answers.getResults(id, candidatesToKeep);
			if (results == null) {
				logger.warn("No resultlist found for qid: {}", id);
				continue;
			}
			
			List<Candidate> candidates = new ArrayList<>();
			
			
 
			List<JCas> resultCASes = new ArrayList<JCas>();
			disableEngines();
			for (Result result : results) {
				JCas curCas = analyzer.getNewJCas();
				UIMAUtil.setupCas(curCas, "document-" + result.documentId, result.documentText);
				analyzeDocument(curCas);
				if (questionClass!=null)
					it.unitn.nlpir.util.UIMAUtil.addQuestionClassToTheCandidateDocument(questionClass, curCas);
				additionalProcessing(questionCas, curCas, j);
				j++;
				resultCASes.add(curCas);
			}
			analyzer.enableAllAnalysisEngine();
			
			processDocumentCases(question, id, results, candidates, resultCASes);
			logger.info("Writing the train/test files");
			rerankingDataGen.handleData(candidates);
			
		}
		finalize();
	

		rerankingDataGen.cleanUp();
	}

	protected void processDocumentCases(Question question, String id,
			List<Result> results, List<Candidate> candidates, 
			List<JCas> resultCASes) {
		int ir = 0;
		for (JCas documentCas : resultCASes) {
			
			logger.info(documentCas.getDocumentText());

			try {
				JCas prevCas = ir > 0 ? resultCASes.get(ir-1) : null;
				JCas nextCas = ir+1 < resultCASes.size() ? resultCASes.get(ir+1) : null;
				addPreviousNextTree(documentCas, nextCas, prevCas, documentToUserIDMap.get(id));
				candidates.add(experiment.generateCandidate(questionCas, documentCas, results.get(ir)));
			}
			catch (Exception e){
				e.printStackTrace();
				logger.error("ERROR when processing result {}, {}", results.get(ir).documentId, question.getText());
				continue;
			}
			ir++;
			
		}
		
	}
	
	public static void main(String[] args) {
		Args.parse(QASemevalClassificationWithContext.class, args);
	
		RERTextPairConversion application = new QASemevalClassificationWithContext();

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
