package it.unitn.nlpir.annotators;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import it.unitn.nlpir.annotators.resources.StanfordCoreNLPResource;
import it.unitn.nlpir.annotators.resources.StanfordCoreNLPResourceImpl;
import it.unitn.nlpir.features.providers.trees.old.DependencyTreeProducer.DependencyTreeProcessorException;
import it.unitn.nlpir.types.CoreferenceChain;
import it.unitn.nlpir.types.Mention;
import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.Sentence;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.UIMAUtil;
import it.unitn.nlpir.util.Coord;
import it.unitn.nlpir.util.SentenceTreeMerger;
import it.unitn.nlpir.util.StanfordUtil;
import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.syntax.dependency.type.DependencyNode;
import org.cleartk.syntax.dependency.type.DependencyRelation;
import org.cleartk.syntax.dependency.type.TopDependencyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.ArrayListMultimap;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.BeginIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.EndIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * The single annotators to run can be specified in the StanfordCoreNLPResource
 * implementation.
 */
public class StanfordCoreNLPAnnotator extends JCasAnnotator_ImplBase {
	private final Logger logger = LoggerFactory.getLogger(StanfordCoreNLPAnnotator.class);

	public final static String RESOURCE_KEY = "stanford.uima.CoreNLPPipeline";
	@ExternalResource(key = RESOURCE_KEY)
	protected StanfordCoreNLPResource stanfordCoreNLPResource;

	protected StanfordCoreNLP pipeline;

	public static final String defaultAnnotatorList = "tokenize, ssplit, pos, lemma";
	public static final String fullAnnotatorList = "tokenize, ssplit, pos, lemma, ner, parse, dcoref";

	public static final String ANNOTATOR_LIST = "annotatorList";
	@ConfigurationParameter(name = ANNOTATOR_LIST, mandatory = false, description = "list of comma separated stanford annotators")
	protected String annotatorList;
	
	public static final String ADD_DEP_PARSING = "addDepParsing";
	@ConfigurationParameter(name = ADD_DEP_PARSING, mandatory = false, description = "list of comma separated stanford annotators")
	private String addDepParsing;
	
	public static AnalysisEngineDescription getDescription() {
		return getDescription(defaultAnnotatorList);
	}

	public static AnalysisEngineDescription getDescription(String annotatorList) {
		ExternalResourceDescription extDesc = createExternalResourceDescription(
				StanfordCoreNLPResourceImpl.class, "null");

		AnalysisEngineDescription aeDesc = null;
		try {
			aeDesc = createPrimitiveDescription(StanfordCoreNLPAnnotator.class,
					StanfordCoreNLPAnnotator.RESOURCE_KEY, extDesc,
					StanfordCoreNLPAnnotator.ANNOTATOR_LIST, annotatorList,
					StanfordCoreNLPAnnotator.ADD_DEP_PARSING, "true");
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return aeDesc;
	}


	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {

		if (this.pipeline == null) {
			init();
		}
		long startTime = System.currentTimeMillis();
		
		String text = cas.getDocumentText();

		edu.stanford.nlp.pipeline.Annotation document = new edu.stanford.nlp.pipeline.Annotation(
				text);
		
		try {
			this.pipeline.annotate(document);
		} catch (OutOfMemoryError e) {
			logger.warn("Stanford Parser had an out of memory error on text: {}", text);
			throw new AnalysisEngineProcessException();
		}

		SentenceTreeMerger constituencyTreeMerger = new SentenceTreeMerger("ROOT");
		SentenceTreeMerger dependencyTreeMerger = new SentenceTreeMerger("ROOT");

		List<Token> tokens = new ArrayList<>();

		List<CoreLabel> entity = new ArrayList<>();
		String previousTag = "";

		int tokenId = 0;
		for (CoreMap sentenceAnn : document.get(SentencesAnnotation.class)) {
			List<CoreLabel> coreTokens = sentenceAnn.get(TokensAnnotation.class);

			// add the sentence annotation
			int sentBegin = sentenceAnn.get(CharacterOffsetBeginAnnotation.class);
			int sentEnd = sentenceAnn.get(CharacterOffsetEndAnnotation.class);
			Sentence sentence = new Sentence(cas, sentBegin, sentEnd);
			sentence.addToIndexes();

			for (CoreLabel token : coreTokens) {
				Token tokenAnnotation = createTokenAnnotation(cas, tokenId, token);

				tokens.add(tokenAnnotation);

				String tag = token.get(NamedEntityTagAnnotation.class);
				if (isNamedEntityTag(tag)) {
					if (tag.equals(previousTag)) {
						entity.add(token);
					} else {
						extractEntity(entity, cas);
						entity.add(token);
					}
				} else {
					extractEntity(entity, cas);
				}

				previousTag = tag;
				tokenId++;
			}

			// Extract an eventual entity at the end of the text
			extractEntity(entity, cas);

			if (sentenceAnn.has(TreeAnnotation.class)) {
				Tree tree = sentenceAnn.get(TreeAnnotation.class);

				// Convert leaf labels to their node IDs
				List<Tree> leaves = tree.getLeaves();
				int i = 0;
				for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
					Tree leaf = leaves.get(i);
					TreeUtil.setNodeLabel(leaf, String.valueOf(token.getId()));
					i++;
				}
				constituencyTreeMerger.addTree(tree.toString());
				
				if (addDepParsing.startsWith("t"))
					processDependencyGraph(cas, sentenceAnn, sentence);
				try {
				 Tree dependencyTree = StanfordUtil.getDependencyTree(tree);
				 dependencyTreeMerger.addTree(TreeUtil.serializeTree(dependencyTree));
				 } catch (DependencyTreeProcessorException e) {
				 logger.warn("Error in the DependencyTreeProcessor on text: {}", 	 sentence);
			 }
			}
		}

		if (!constituencyTreeMerger.isEmpty()) {
			UIMAUtil.createConstituencyTreeAnnotation(cas, constituencyTreeMerger.getMergedTree());
		}

		if (!dependencyTreeMerger.isEmpty()) {
			UIMAUtil.createDependencyTreeAnnotation(cas, dependencyTreeMerger.getMergedTree());
		}
		if (this.annotatorList.indexOf(StanfordCoreNLP.STANFORD_DETERMINISTIC_COREF)>0)
			addCorefInformation(cas, document);
		long endTime = System.currentTimeMillis();
		
		logger.debug("Stanford parser took {} ms to process", String.valueOf(endTime - startTime));
		
	}

	private void addCorefInformation(JCas cas, edu.stanford.nlp.pipeline.Annotation document){
		Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
		Map<String,CoreLabel> tokenIdTokenMap = new HashMap<String,CoreLabel>();
		//List<CoreLabel> coreTokens = document.get(TokensAnnotation.class);
		
		int z = 1;
		for (CoreMap sentenceAnn : document.get(SentencesAnnotation.class)) {
			List<CoreLabel> coreTokens = sentenceAnn.get(TokensAnnotation.class);
			for (CoreLabel tok : coreTokens){
				tokenIdTokenMap.put(z+"-"+tok.index(), tok);//todo: rewrite
				
			};
			z++;
		}
		
		 
		for (int i : graph.keySet()){
			CorefChain chain = graph.get(i);
			
			Map<Coord,CorefMention> coords = new HashMap<Coord,CorefMention>();
			for (CorefMention mention : chain.getMentionsInTextualOrder()){
				coords.put(new Coord(mention.headIndex, mention.headIndex), mention);
			}
				
			if (coords.size()>1){
				CoreferenceChain ch = new CoreferenceChain(cas);
				FSArray mentions = new FSArray(cas, coords.size());
				int j = 0;
				for (Coord key : coords.keySet()){
					CorefMention mention = coords.get(key);
					Mention m = new Mention(cas);
					m.setBegin(tokenIdTokenMap.get(mention.sentNum+"-"+mention.headIndex).beginPosition());
					m.setEnd(tokenIdTokenMap.get(mention.sentNum+"-"+mention.headIndex).endPosition());
					m.setId("s"+String.valueOf(chain.getChainID()));
					m.setChain(ch);
					//m.set
					m.addToIndexes(cas);
					logger.debug(String.format("Coreference chain %d, mention: '%s', start:%d, end:%d", chain.getChainID(), 
							m.getCoveredText(), m.getBegin(), m.getEnd()));
					mentions.set(j, m);
					j++;
				}
				ch.setMentions(mentions);
				ch.setId("s"+String.valueOf(chain.getChainID()));
				ch.addToIndexes(cas);
			}
		}
		
	}
	
	private void processDependencyGraph(JCas cas, CoreMap sentenceAnn, Sentence sentence) {
		// get the dependencies
		// SemanticGraph dependencies =
		// sentenceAnn.get(CollapsedCCProcessedDependenciesAnnotation.class);
		SemanticGraph dependencies = sentenceAnn.get(BasicDependenciesAnnotation.class);

		// convert Stanford nodes to UIMA annotations
		List<Token> tokens = JCasUtil.selectCovered(cas, Token.class, sentence);
		Map<IndexedWord, DependencyNode> stanfordToUima = new HashMap<IndexedWord, DependencyNode>();
		for (IndexedWord stanfordNode : dependencies.vertexSet()) {
			int indexBegin = stanfordNode.get(BeginIndexAnnotation.class);
			int indexEnd = stanfordNode.get(EndIndexAnnotation.class);
			int tokenBegin = tokens.get(indexBegin).getBegin();
			int tokenEnd = tokens.get(indexEnd - 1).getEnd();
			DependencyNode node;
			if (dependencies.getRoots().contains(stanfordNode)) {
				node = new TopDependencyNode(cas, tokenBegin, tokenEnd);
			} else {
				node = new DependencyNode(cas, tokenBegin, tokenEnd);
			}
			stanfordToUima.put(stanfordNode, node);
			
		}

		// create relation annotations for each Stanford dependency
		ArrayListMultimap<DependencyNode, DependencyRelation> headRelations = ArrayListMultimap
				.create();
		ArrayListMultimap<DependencyNode, DependencyRelation> childRelations = ArrayListMultimap
				.create();
		for (SemanticGraphEdge stanfordEdge : dependencies.edgeIterable()) {
			DependencyRelation relation = new DependencyRelation(cas);
			DependencyNode head = stanfordToUima.get(stanfordEdge.getGovernor());
			DependencyNode child = stanfordToUima.get(stanfordEdge.getDependent());
			String relationType = stanfordEdge.getRelation().toString();
			if (head == null || child == null || relationType == null) {
				throw new RuntimeException(String.format(
						"null elements not allowed in relation:\nrelation=%s\nchild=%s\nhead=%s\n",
						relation, child, head));
			}
			relation.setHead(head);
			relation.setChild(child);
			relation.setRelation(relationType);
			relation.addToIndexes();
			headRelations.put(child, relation);
			childRelations.put(head, relation);
		}
		//TreePrint tp = new TreePrint(addDepParsing);
		// set the relations for each node annotation
		for (DependencyNode node : stanfordToUima.values()) {
			node.setHeadRelations(org.cleartk.util.UIMAUtil.toFSArray(cas, headRelations.get(node)));
			node.setChildRelations(org.cleartk.util.UIMAUtil.toFSArray(cas,
					childRelations.get(node)));
			node.addToIndexes();
		}
	}

	protected Token createTokenAnnotation(JCas cas, int tokenId, CoreLabel token) {
		Token tokenAnnotation = new Token(cas);
		tokenAnnotation.setBegin(token.beginPosition());
		tokenAnnotation.setEnd(token.endPosition());
		tokenAnnotation.setId(tokenId);
		tokenAnnotation.setPostag(token.get(PartOfSpeechAnnotation.class));
		tokenAnnotation.setLemma(token.lemma());
		tokenAnnotation.addToIndexes();
		return tokenAnnotation;
	}

	protected void init() {
		try {
			this.stanfordCoreNLPResource = (StanfordCoreNLPResource) getContext()
					.getResourceObject(RESOURCE_KEY);
			//Properties props = this.pipeline.getProperties();
			//props.put("threads", "2");
			
			this.pipeline = this.stanfordCoreNLPResource.getPipeline(annotatorList);
		} catch (ResourceAccessException e) {
			e.printStackTrace();
		}
	}

	protected boolean isNamedEntityTag(String ne) {
		return ne != null && !ne.equals("O");
	}


	private void extractEntity(List<CoreLabel> entity, JCas cas) {
		if (!entity.isEmpty()) {
			int begin = entity.get(0).beginPosition();
			int end = entity.get(entity.size() - 1).endPosition();
			logger.debug(String.format("NER: %s [%s, %s] %s", entity.get(0).ner(), begin, end, entity.get(0).originalText()));
			Annotation annotation = getNamedEntityAnnotation(entity.get(0).ner(), cas);
			if (annotation != null) {
				annotation.setBegin(begin);
				annotation.setEnd(end);
				annotation.addToIndexes();
			}
			entity.clear();
		}
	}

	private Annotation getNamedEntityAnnotation(String type, JCas cas) {
		NER ner = new NER(cas);
		switch (type) {
		case "LOCATION":
			ner.setNERtype(NERTypes.LOCATION);
			break;
		case "ORGANIZATION":
			ner.setNERtype(NERTypes.ORGANIZATION);
			break;
		case "PERSON":
			ner.setNERtype(NERTypes.PERSON);
			break;
		case "DATE":
			ner.setNERtype(NERTypes.DATE);
			break;
		case "TIME":
			ner.setNERtype(NERTypes.TIME);
			break;
		case "MONEY":
			ner.setNERtype(NERTypes.MONEY);
			break;
		case "NUMBER":
			ner.setNERtype(NERTypes.NUMBER);
			break;
		default:
			ner.setNERtype(type);
		}
		return ner;
//		return null;
	}

}
