package it.unitn.nlpir.util;


import it.unitn.nlpir.types.NER;
import it.unitn.nlpir.types.SSense;
import it.unitn.nlpir.types.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;

import com.google.common.base.Joiner;

import edu.stanford.nlp.trees.Tree;

public class LeavesTransformer {
	private static final Logger logger = LoggerFactory.getLogger(LeavesTransformer.class);

	private static Alphabet featDict = new Alphabet();
	private Alphabet phraseDict = null;
	private WriteFile file = null;

//	private TokenTextGetter tGetter = TokenTextGetterFactory.getTokenTextGetter(tokenTextType)
	// private HashMap<Integer, FeatureVector> fvs;

	public LeavesTransformer(Alphabet dict) {
		this(dict, null);
	}

	public LeavesTransformer(WriteFile file) {
		this(new Alphabet(), file);
	}
	
	public LeavesTransformer(Alphabet dict, WriteFile file) {
		this.phraseDict = dict;
		this.file = file;
	}

	private String getCoveredText(Tree node) {
		List<String> words = new ArrayList<>();
		for (Tree leaf : node.getLeaves()) {
			words.add(leaf.label().value());
		}
		return Joiner.on(' ').join(words);
	}

	public void innerNodeToFeatureVector(Tree tree) {
		int i = 0;
		for (Tree node : tree) {
			if (i != 0 && i % 2 == 0) {
				String text = node.label().value();
				int idx = this.phraseDict.lookupIndex(text);
				logger.debug(String.format("%s: %s [%s]", node.label().value(), text, idx));
				String phraseId = String.format("%d::p", idx);
				TreeUtil.setNodeLabel(node, phraseId);
			}
			i++;
		}
	}

	

	private String featureVectorToSVMLightFormat(FeatureVector fv) {
		List<String> feats = new ArrayList<>();
		int[] indices = fv.getIndices();
		double[] values = fv.getValues();
		for (int i = 0; i < values.length; i++) {
			feats.add(String.format("%d:%s", (indices == null ? i + 1 : indices[i] + 1),
					String.valueOf(values[i])));
		}
		return Joiner.on(" ").join(feats);
	}

	public void insertFeatureVectorAfterChunk(Tree tree) {
		for (Tree node : tree) {
			if (node.isPrePreTerminal()) {
				String nodeLabel = node.label().value();
				if (nodeLabel.startsWith("N") || nodeLabel.startsWith("V")) {
					String text = getCoveredText(node);
					int idx = this.phraseDict.lookupIndex(text);
					logger.debug(String.format("%s: %s [%s]", node.label().value(), text, idx));
					String phraseId = String.format("%d::p", idx);
					Tree featNode = TreeUtil.createNode(phraseId);
					// add all children of the node to a new feature node
					for (Tree child : node.children()) {
						featNode.addChild(child);
					}
					Tree[] newKids = new Tree[1];
					newKids[0] = featNode;
					node.setChildren(newKids);
				}
			}
		}
	}

	public void leafToFeatureVector(Tree tree) {
		for (Tree node : tree) {
			if (node.isPreTerminal()) {
				Tree child = node.getChild(0);
				String text = child.label().value();
				int idx = this.phraseDict.lookupIndex(text);
				logger.debug(String.format("%s: %s [%s]", node.label().value(), text, idx));
				String phraseId = String.format("%d::p", idx);
				TreeUtil.setNodeLabel(child, phraseId);
			}
		}
	}
	
	public void leafToStanfordNERFeatureVector(Tree tree, JCas cas) {
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		Token[] tokens = x.toArray(new Token[x.size()]);
		for (Tree leaf : tree.getLeaves()) {
			Integer id;
			try {
				id = Integer.parseInt(leaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			Token token = tokens[id];
			String leafText = token.getLemma().toLowerCase().replace("(", "[").replace(")", "]");
			if (!token.getIsFiltered()) {  // Skip stop words
				StringArray feats = token.getNERFeatures();
				if (feats != null) {
					FeatureSequence fs = new FeatureSequence(featDict);
					for (String f : feats.toArray()) {
						fs.add(f);
					}					
					FeatureVector fv = new FeatureVector(fs);
					String strFv = featureVectorToSVMLightFormat(fv);
					int idx = this.phraseDict.lookupIndex(fv.toString());
					leafText = String.format("%s-%d::p", leafText, idx);
					String line = String.format("%s\t1.0\t0\t%s", leafText, strFv);
					file.writeLn(line);
				}
			}
			TreeUtil.setNodeLabel(leaf, leafText);
		}
	}

	public void leafToLsiMatrixId(Tree tree, JCas cas) {
		Collection<Token> x = JCasUtil.select(cas, Token.class);
		Token[] tokens = x.toArray(new Token[x.size()]);
		for (Tree leaf : tree.getLeaves()) {
			Integer id;
			try {
				id = Integer.parseInt(leaf.nodeString());
			} catch (NumberFormatException e) {
				continue;
			}
			Token token = tokens[id];
			String leafText = token.getCoveredText().toLowerCase().replace("(", "[").replace(")", "]");
			if (!token.getIsFiltered()) {
				leafText = String.format("%s::p", leafText);
			}
			TreeUtil.setNodeLabel(leaf, leafText);
		}
	}

	public void chunkToFeatureVector(Tree tree) {
		for (Tree node : tree) {
			if (node.isPrePreTerminal()) {
				String nodeLabel = node.label().value();
				if (nodeLabel.startsWith("N") || nodeLabel.startsWith("V")) {
					String text = getCoveredText(node);
					int idx = this.phraseDict.lookupIndex(text);
					logger.debug(String.format("%s: %s [%s]", node.label().value(), text, idx));
					Tree[] newKids = new Tree[1];
					String phraseId = String.format("%d::p", idx);
					newKids[0] = TreeUtil.createNode(phraseId);
					node.setChildren(newKids);
				}
			}
		}
	}
}
