package it.unitn.nlpir.uima;

import it.unitn.nlpir.questions.PerlPatternMatcher;
import it.unitn.nlpir.types.AnnotatorRun;
import it.unitn.nlpir.types.AnswerPattern;
import it.unitn.nlpir.types.ConstituencyTree;
import it.unitn.nlpir.types.DependencyTree;
import it.unitn.nlpir.types.DocumentId;
import it.unitn.nlpir.types.PosChunk;
import it.unitn.nlpir.types.QuestionClass;
import it.unitn.nlpir.types.RankingScore;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.Hash;
import it.unitn.nlpir.util.TreeUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.TypeOrFeature;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.Capability;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import edu.stanford.nlp.trees.Tree;

public class UIMAUtil {
	public static final String DOCUMENT_LANGUAGE = "en_us";

	public static boolean contains(JCas cas, int type) {
		return cas.getAnnotationIndex(type).iterator().hasNext();
	}

	public static Map<String, String> readQuestionCategories(String fname) throws IOException {
		Map<String, String> questionIdToClass = new HashMap<String, String>();
		for (String line : Files.readLines(new File(fname), Charsets.UTF_8)) {
			String[] data = line.trim().split(" ");
			/*if (data[1].trim().equals("ABBR"))
				data[1]="ENTY";*/
			questionIdToClass.put(data[0].trim(), data[1].trim());
			//logger.debug(data[0].trim()+"\t"+data[1].trim());
		}
		return questionIdToClass;
	}
	
	public static boolean contains(JCas cas, Type type) {
		return cas.getAnnotationIndex(type).iterator().hasNext();
	}

	public static void removeAnnotations(JCas cas, int type) {
		List<Annotation> annotations = new ArrayList<>();
		for(Annotation annotation : cas.getAnnotationIndex(type)) {
			annotations.add(annotation);
		}
		for(Annotation annotation : annotations) {
			annotation.removeFromIndexes(cas);
		}
	}

	public static void removeAnnotations(JCas cas, Type type) {
		if (type==null)
			return;
		List<Annotation> annotations = new ArrayList<>();
		for(Annotation annotation : cas.getAnnotationIndex(type)) {
			annotations.add(annotation);
		}
		for(Annotation annotation : annotations) {
			annotation.removeFromIndexes(cas);
		}
	}
	
	public static void createDependencyTreeAnnotation(JCas cas, String dependencyTree) {
		DependencyTree annotation = new DependencyTree(cas);
		annotation.setBegin(0);
		annotation.setTree(dependencyTree);
		annotation.setEnd(cas.getDocumentText().length());
		annotation.addToIndexes();
	}

	public static void createConstituencyTreeAnnotation(JCas cas, String constituencyTree) {
		ConstituencyTree annotation = new ConstituencyTree(cas);
		annotation.setBegin(0);
		annotation.setTree(constituencyTree);
		annotation.setEnd(cas.getDocumentText().length());
		annotation.addToIndexes();
	}

	public static void addRankingScoreAnnotation(JCas cas, String score) {
		RankingScore rankingScore = new RankingScore(cas);
		rankingScore.setBegin(0);
		rankingScore.setEnd(cas.getDocumentText().length());
		rankingScore.setScore(score);
		rankingScore.addToIndexes();
	}

	public static void addAnnotatorRunAnnotation(JCas cas, String annotatorName, String annotatorHash) {
		AnnotatorRun annotatorRun = new AnnotatorRun(cas);
		annotatorRun.setName(annotatorName);
		annotatorRun.setHash(annotatorHash);
		annotatorRun.addToIndexes();
	}

	public static void removeAnnotatorRunAnnotation(JCas cas, String annotatorName) {
		List<AnnotatorRun> annotations = new ArrayList<>();
		FSIterator<FeatureStructure> iterator = cas.getIndexRepository().getAllIndexedFS(
				cas.getCasType(AnnotatorRun.type));
		while(iterator.hasNext()) {
			AnnotatorRun annotatorRun = (AnnotatorRun) iterator.next();
			if(annotatorRun.getName().equals(annotatorName)) {
				annotations.add(annotatorRun);
			}
		}
		for(AnnotatorRun annotation : annotations) {
			annotation.removeFromIndexes(cas);
		}
	}

	public static void removeOutputs(JCas cas, AnalysisEngine ae) {
		/**
		 * Outputs in capabilities should carefully be described If an annotator
		 * populates a single feature, the containing type must not be specified
		 * as output.
		 */
		Capability[] capabilities = ae.getAnalysisEngineMetaData().getCapabilities();
		for(Capability capability : capabilities) {
			TypeOrFeature[] tofs = capability.getOutputs();
			for(TypeOrFeature tof : tofs) {
				if(tof.isType()) {
					Type type = cas.getTypeSystem().getType(tof.getName());
					UIMAUtil.removeAnnotations(cas, type);
				}
			}
		}
	}

	public static int numberOfAnnotations(JCas cas, int annotationType) {
		return cas.getAnnotationIndex(annotationType).size();
	}

	public static String getQuestionClass(JCas cas) throws AnnotationNotFoundException {
		if(JCasUtil.exists(cas, QuestionClass.class)) {
			return JCasUtil.selectSingle(cas, QuestionClass.class).getQuestionClass();
		} else {
			throw new AnnotationNotFoundException();
		}
	}
	
	public static String getPosChunkTree(JCas cas) throws AnnotationNotFoundException {
		if(JCasUtil.exists(cas, PosChunk.class)) {
			return JCasUtil.selectSingle(cas, PosChunk.class).getTree();
		} else {
			throw new AnnotationNotFoundException();
		}
	}
	
	public static Tree getTokenTree(JCas cas) throws AnnotationNotFoundException {
		Tree root = TreeUtil.createNode("ROOT");
		for (Token t : JCasUtil.select(cas, Token.class)) {
			Tree posNode = TreeUtil.createNode(String.valueOf(t.getId()));
			root.addChild(posNode);
		}
		return root;
	}
	
	public static String getConstituencyTree(JCas cas) throws AnnotationNotFoundException {
		if(JCasUtil.exists(cas, ConstituencyTree.class)) {
			return JCasUtil.selectSingle(cas, ConstituencyTree.class).getTree();
		} else {
			throw new AnnotationNotFoundException();
		}
	}
	
	public static String getDependencyTree(JCas cas) throws AnnotationNotFoundException {
		if(JCasUtil.exists(cas, DependencyTree.class)) {
			return JCasUtil.selectSingle(cas, DependencyTree.class).getTree();
		} else {
			throw new AnnotationNotFoundException();
		}
	}

	public static void setupCas(JCas cas, String documentId, String text) {
		cas.reset();
		cas.setDocumentLanguage(DOCUMENT_LANGUAGE);
		cas.setDocumentText(text);
		DocumentId docid = new DocumentId(cas);
		docid.setId(documentId);
		docid.addToIndexes();	
	}

	public static void setupCas(JCas cas, String documentId, String text, String language) {
		setupCas(cas, documentId, text);
		cas.setDocumentLanguage(language);
	}
	
	public static String replaceTokensWithIds(List<Token> tokens, String documentTree) {
		for(int i = 0; i < tokens.size(); i++) {
			documentTree = documentTree.replaceFirst("\\Q" + tokens.get(i).getCoveredText() + ")\\E",
					String.valueOf(tokens.get(i).getId()) + ")");
		}
		return documentTree;
	}
	
	public static String rebuildTree(JCas cas, String tree) {
		String result = tree;
		Map<Integer, Token> tokens = new HashMap<>();
		for(Token token : JCasUtil.select(cas, Token.class)) {
			tokens.put(token.getId(), token);
		}
		
		Pattern p = Pattern.compile(" ([0-9]+)\\)");
		Matcher m = p.matcher(tree);
		while(m.find()) {
			String id = m.group(1);
			Token token = tokens.get(Integer.parseInt(id));
			String value = token.getCoveredText();
			String replacement = " " + value + "\\)";
			result = result.replaceFirst(" " + id + "\\)", replacement);
		}
		
		return result;
	}
	
	public static StringArray buildStringArray(JCas cas, String[] array) {
		StringArray synsArray = new StringArray(cas, array.length);
		synsArray.copyFromArray(array, 0, 0, array.length);
		return synsArray;
	}
	
	public static List<String> collectTokensText(JCas cas, TokenTextGetter getter) {
		List<String> tokens = new ArrayList<>();
		for(Token token : JCasUtil.select(cas, Token.class)) {
			tokens.add(getter.getTokenText(token));
		}
		return tokens;
	}
	
	public static List<Tree> getNodesCorrespondingToTokens(Tree tree, List<String> tokens) {
		List<Tree> nodes = new ArrayList<>();
		for(Tree node : tree) {
			if(tokens.contains(node.value())) {
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	public static Map<Integer,Tree> buildTokenId2TreeLeafIndex(List<Token> tokens, Tree tree) {
		Map<Integer,Tree>leafNodes = new HashMap<Integer, Tree>();
		for (Tree qLeaf : tree.getLeaves()) {
			Integer qTokenId;
			try {
				qTokenId = Integer.parseInt(qLeaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			leafNodes.put(qTokenId, qLeaf);
		}
		return leafNodes;
	}

	public static String getAnalysisEngineDescriptionHash(AnalysisEngine ae) {
		return Hash.getHash(ae.getAnalysisEngineMetaData().toString()
				.replaceFirst("UUID = [0-9a-z:-]+\\n", ""));
	}

	
	public static void addAnswerPatternAnnotation(JCas cas, String answerPattern) {
		String documentText = cas.getDocumentText();
		if (!answerPattern.contains("\\"))
			answerPattern = String.format("\\b%s\\b", answerPattern);
		boolean isMatched = PerlPatternMatcher.search(documentText, answerPattern);
		AnswerPattern annotation = new AnswerPattern(cas);
		annotation.setIsMatched(isMatched);
		annotation.setPattern(answerPattern);
		if(isMatched) {
			int[] boundaries = PerlPatternMatcher.getMatchBoundaries(documentText, answerPattern);
			annotation.setBegin(boundaries[0]);
			annotation.setEnd(boundaries[1]);
			
		} else {
			annotation.setBegin(0);
			annotation.setEnd(documentText.length());
		}
		annotation.addToIndexes(cas);
	}
	
	public static void setBeginEnd(Annotation from, Annotation to) {
		to.setBegin(from.getBegin());
		to.setEnd(from.getEnd());
	}
	
}
