package it.unitn.nlpir.system.classification.semeval;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Stopwatch;

import it.unitn.nlpir.cli.Args;
import it.unitn.nlpir.cli.Argument;
import it.unitn.nlpir.system.core.RERTextPairConversion;
import it.unitn.nlpir.types.CQAUserSignature;
import it.unitn.nlpir.types.DocumentId;
import it.unitn.nlpir.util.FileUtil;
import it.unitn.nlpir.util.semeval.UserSignature;


/**
 * <p>
 * Launches a pipeline to prepare the SVM Light files for classification on <a href=http://alt.qcri.org/semeval2016/>Semeval 2016 data</a>
 * </p>
 * 
 * <p>
 * Reads the users' signatures (short texts the users use to represent themselves and which they add at the end of all their messages, and adds information
 * about them directly into the CASes of question and answer. 
 * </p>
* @author IKernels group
 *
 */
public class QASemevalClassificationNoSignatureCasKeepsText extends QASemevalClassification {

	@Argument(description = "location of the question->userid map")
	protected static String userIDFile;
	Map<String, UserSignature> signatures;
	protected Map<String, String> documentToUserIDMap;
	public QASemevalClassificationNoSignatureCasKeepsText(){
		super();
		
		try {
			documentToUserIDMap = FileUtil.readFileAsMap(userIDFile, "\t");
			signatures = UserSignature.retrieveUserSignatures(questions, answers, documentToUserIDMap);
			int i = 0;
			
			for (String key : signatures.keySet()){
				if (signatures.get(key).getSignatures().size()>0){
					logger.debug(String.format("%s\t%s", key, StringUtils.join(signatures.get(key).getSignatures(),"\t")));
					i++;
				}
				
			}
			logger.debug(String.format("Found %d signatures", i));

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	protected void analyzeQuestion(JCas cas){
		analyzer.analyze(cas);
		String docId = JCasUtil.selectSingle(cas, DocumentId.class).getId().replace("question-", "");
		addSignatureAnnotation(cas, docId);
	}
	
	protected void analyzeDocument(JCas cas){
		analyzer.analyze(cas);
		String docId = JCasUtil.selectSingle(cas, DocumentId.class).getId().replace("document-", "");
		addSignatureAnnotation(cas, docId);
		
	}

	public void addSignatureAnnotation(JCas cas, String docId) {
		String userName = documentToUserIDMap.get(docId);
		for (String signature : signatures.get(userName).getSignatures()){
			if (cas.getDocumentText().endsWith(signature)){
				int docLength = cas.getDocumentText().length();
				CQAUserSignature signAnnotation = new CQAUserSignature(cas);
				signAnnotation.setEnd(docLength);
				signAnnotation.setBegin(docLength-signature.length());

				logger.debug(String.format("Removing signature: %s", signAnnotation.getCoveredText()));
				signAnnotation.addToIndexes();
				
			}
		}
	}
	
	
	

	public static void main(String[] args) {
		try{
			Args.parse(QASemevalClassificationNoSignatureCasKeepsText.class, args);
		}
		catch (Exception e){
			Args.usage(QASemevalClassificationNoSignatureCasKeepsText.class);
			e.printStackTrace();
			return;
		}

	
		RERTextPairConversion application = new QASemevalClassificationNoSignatureCasKeepsText();

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
