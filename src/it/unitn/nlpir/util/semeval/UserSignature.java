package it.unitn.nlpir.util.semeval;

import it.unitn.nlpir.questions.Question;
import it.unitn.nlpir.resultsets.Result;
import it.unitn.nlpir.resultsets.ResultSetFileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



/**
 * Simone's class adapted to our pipeline. Removes signatures in the semeval messages.
* @author IKernels group
 *
 */
public class UserSignature {

	private static final int MINIMUM_SIGNATURE_LENGTH = 10;
	private static int removedSignatures = 0;

	private List<String> signatures;

	private List<String> messageBodies;

	public UserSignature(){
		this.signatures = new ArrayList<String>();
		this.messageBodies = new LinkedList<String>();
	}

	public void addMessageBody(String body){

		String messageBody = body.trim();
		for(String signature : signatures){
			if(messageBody.endsWith(signature)){
				return;
			}
		}

		int index = 0;
		for(String previousBody : messageBodies){

			if(previousBody.equals(messageBody)){//if two messages are identical it is probably due to a double posting
				return;
			}

			String commonSuffix = getCommonSuffix(previousBody, messageBody).trim();

			if(commonSuffix.length()>=MINIMUM_SIGNATURE_LENGTH && !commonSuffix.toLowerCase().contains("thank")){

				if(messageBody.length() != commonSuffix.length() ){//To avoid empty messages due to double posting of the final part of a comment (like in Q510_C3)
					this.signatures.add(commonSuffix);
					this.messageBodies.remove(index);
					return;
				}

			}

			index++;
		}
		this.messageBodies.add(messageBody);
	}

	public List<String> getSignatures(){
		return this.signatures;
	}

	public static String getCommonSuffix(String stringA, String stringB){

		


		String signature = "";
		if (stringA.length()==stringB.length()){
			return signature;
		}
		int i = stringA.length() - 1;
		int j = stringB.length() -1;
		while ((i>=0)&&(j>=0)){
			if (stringA.charAt(i)!=stringB.charAt(j)){
				break;
			}
			signature = stringA.charAt(i) + signature ;
			i--; j--;
		}
		/*for(int i=0; i<shortestLenght; i++){
			if(!partsA[partsA.length-i-1].equals(partsB[partsB.length-i-1])){
				break;
			}
			signature = partsA[partsA.length-i-1] + "\n" + signature;
		}*/
		
		return signature;
	}



	public static String removeSignature(String body, UserSignature userProfile){
		String messageBody = body.trim();
		int index = messageBody.indexOf("-----------");

		if(index>0){
			removedSignatures++;
			messageBody = messageBody.substring(0, index).trim();
		}
		for(String signature : userProfile.getSignatures()){
			if(messageBody.endsWith(signature)){
				removedSignatures++;
				return messageBody.substring(0, messageBody.length()-signature.length()).trim();
			}
		}

		return messageBody;
	}

	/**
	 * @return the removedSignatures
	 */
	public static int getRemovedSignatures() {
		return removedSignatures;
	}

	public static Map<String, UserSignature> retrieveUserSignatures(List<Question> questions, ResultSetFileReader dataset, Map<String,String> aidToUserNameMap) throws IOException {
		HashMap<String, UserSignature> profiles = new HashMap<String, UserSignature>();
		HashSet<String> qIds = new HashSet<String>();
		HashSet<String> cIds = new HashSet<String>();
		//for(CQAquestionThreadList originalQuestion : dataset){
		retrieveUserSignatures(questions, dataset, aidToUserNameMap, profiles, qIds, cIds);
		//}

		return profiles;
	}

	



	private static void retrieveUserSignatures(List<Question> questions, ResultSetFileReader dataset, Map<String,String> aidToUserNameMap, 
			HashMap<String, UserSignature> profiles, HashSet<String> qIds, HashSet<String> cIds) throws IOException {
		for (Question question : questions){
			String qid = question.getId();
			if(qIds.add(qid)){
				String quserid = aidToUserNameMap.get(qid);

				String qbody = question.getText();
				if (qbody.length()>2000){
					System.out.println(qbody);
				}
				UserSignature questionUserProfile = profiles.get(quserid);
				if(questionUserProfile==null){
					questionUserProfile = new UserSignature();
					profiles.put(quserid, questionUserProfile);
				}
				questionUserProfile.addMessageBody(qbody);

			}


			for(Result answer : dataset.getResults(qid)){
				if (answer.documentText.contains("Always look on the bright side of life")){
					System.out.println(answer.documentText);
				}
				if(cIds.add(answer.documentId)){
					String cuserid = aidToUserNameMap.get(answer.documentId);
					String cbody = answer.documentText;
					if (cbody.length()>2000){
						System.out.println(cbody);
					}

					UserSignature commentUserProfile = profiles.get(cuserid);
					if(commentUserProfile==null){
						commentUserProfile = new UserSignature();
						profiles.put(cuserid, commentUserProfile);
					}

					commentUserProfile.addMessageBody(cbody);
				}


			}

		}
	}


	public static void removeSignatures(Map<String, UserSignature> userSignatures, List<Question> questions, ResultSetFileReader dataset, Map<String,String> aidToUserNameMap){
		
			for(Question question : questions){
				//CQAquestion2016 question = thread.getQuestion();

				UserSignature qUserProfile = userSignatures.get(aidToUserNameMap.get(question.getId()));
				if(qUserProfile !=null){
					if (qUserProfile.signatures.size()>0){
						System.out.println(qUserProfile.signatures);
					}
					String newBody = UserSignature.removeSignature(question.getText(), qUserProfile);
					question.setText(newBody);
				}

				for(Result answer : dataset.getResults(question.getId())){
					UserSignature cUserProfile = userSignatures.get(aidToUserNameMap.get(answer.documentId));
					if(cUserProfile !=null){
						
						String newBody = UserSignature.removeSignature(answer.documentText, cUserProfile);
						/*if (cUserProfile.signatures.size()>0){
							System.out.println(answer.documentText+"\t->\t"+newBody);
						}*/
						answer.documentText = newBody;
					}
				}

			}
		
	}

	

}
