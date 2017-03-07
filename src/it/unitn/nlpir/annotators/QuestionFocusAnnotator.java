package it.unitn.nlpir.annotators;

import it.unitn.nlpir.tree.ConstituencyTreeBuilder;
import it.unitn.nlpir.types.QuestionFocus;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.util.TreeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import svmlighttk.SVMTKExample;
import svmlighttk.SVMLightTK;
import edu.stanford.nlp.trees.Tree;

public class QuestionFocusAnnotator extends JCasAnnotator_ImplBase {
	static final Logger logger = LoggerFactory.getLogger(QuestionFocusAnnotator.class);

	private static final String MODEL_FILE = "data/question-focus/question-focus.model";

	private Set<String> allowedTags = new HashSet<>(Arrays.asList(new String[] { "NN", "NNS",
			"NNP", "NNPS" }));

	private SVMLightTK classifier;
	private static ConstituencyTreeBuilder treeBuilder = new ConstituencyTreeBuilder();

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		this.classifier = new SVMLightTK(MODEL_FILE);
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		Tree tree = treeBuilder.getTree(cas);

		FocusChunk focChunk = getFocus(tree, cas);
		int focus = focChunk.getId();
		if (focus != -1) {
			QuestionFocus annotation = new QuestionFocus(cas);
			List<Token> tokens = new ArrayList<>(JCasUtil.select(cas, Token.class));
			Token focusToken = tokens.get(focus);
			logger.info("{} | question focus: {}, confidence: {}", cas.getDocumentText(),
					focusToken.getCoveredText(), focChunk.getConfidence());
			annotation.setBegin(focusToken.getBegin());
			annotation.setEnd(focusToken.getEnd());
			annotation.setConfidence(focChunk.getConfidence());
			annotation.addToIndexes();
		}
	}

	private FocusChunk getFocus(Tree tree, JCas cas) {
		int currentFocus = -1;
		double maxConfidence = Double.NEGATIVE_INFINITY;
		
		// Check if there is at least one preterminal in the allowedTags
		boolean skipNotNPs = false;
		for (Tree node : tree) {
			if (node.isPreTerminal()) {
				if (this.allowedTags.contains(node.nodeString())) {
					skipNotNPs = true;
					break;
				}
			}
		}
		FocusChunk  ch = new FocusChunk(-1, 0.0);
		for (Tree leaf : tree.getLeaves()) {
			String currentLeafLabel = leaf.nodeString();
			// Tag the tree
			Tree parent = leaf.parent(tree);
			Tree parentOfParent = parent.parent(tree);
			String originalParentLabel = parent.nodeString();
			String originalParentOfParentLabel = parentOfParent.nodeString();
			TreeUtil.setNodeLabel(parent, "FOCUS-" + originalParentLabel);
			TreeUtil.setNodeLabel(parentOfParent, "FOCUS-" + originalParentOfParentLabel);

			Tree treeCopy = tree.deepCopy();
			TreeUtil.finalizeTreeLeaves(cas, treeCopy);
			String taggedConstituencyTree = TreeUtil.serializeTree(treeCopy);

			// Revert changes
			TreeUtil.setNodeLabel(parent, originalParentLabel);
			TreeUtil.setNodeLabel(parentOfParent, originalParentOfParentLabel);

			/*
			 * Heuristic to skip verbs and pronouns Skipping pronouns gives a
			 * huge improvement TODO: move the heuristic in the training model
			 */
			if (skipNotNPs && !this.allowedTags.contains(originalParentLabel))
				continue;

			SVMTKExample builder = new SVMTKExample().positive().addTree(
					taggedConstituencyTree);
			String instance = builder.toString();
			double confidence = this.classifier.classify(instance);
			if (confidence > maxConfidence) {
				currentFocus = Integer.parseInt(currentLeafLabel);
				maxConfidence = confidence;
				ch = new FocusChunk(currentFocus, confidence);
			}
		}

		return ch;
	}

	public class FocusChunk{
		private int id; 
		private double confidence;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public double getConfidence() {
			return confidence;
		}
		public void setConfidence(double confidence) {
			this.confidence = confidence;
		}
		public FocusChunk(int id, double confidence) {
			super();
			this.id = id;
			this.confidence = confidence;
		}
		
	}
}
