package it.unitn.nlpir.tree;

import java.util.Map;

import it.unitn.nlpir.types.Chunk;
import it.unitn.nlpir.types.Token;
import it.unitn.nlpir.uima.TokenTextGetterFactory;
import it.unitn.nlpir.util.TreeUtil;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.trees.Tree;

public class TreeLeafFinalizerAndChunkSpansAsPreterminalsMarker implements ITreePostprocessor {
	public static final String defaultLeafTextType = TokenTextGetterFactory.LEMMA;
	public static final String SPAN_LABEL="SPAN";
	private String leafTextType;
	private String chunkType; 
	
	public TreeLeafFinalizerAndChunkSpansAsPreterminalsMarker(String chunkType, String leafTextType) {
		this(defaultLeafTextType);
		this.chunkType = chunkType;
	}
	
	
	public TreeLeafFinalizerAndChunkSpansAsPreterminalsMarker() {
		this(defaultLeafTextType);
		this.chunkType = null;
	}
	
	public TreeLeafFinalizerAndChunkSpansAsPreterminalsMarker(String leafTextType) {
		this.leafTextType = leafTextType;
		this.chunkType= null;
	}
	
	@Override
	public void process(Tree tree, JCas cas) {
		Map<Integer, Tree> tokenIdToTreeNodeMap = TreeUtil.getTokenIdToTreeNodeMap(tree);
		int i = 0;
		for (Chunk chunk : JCasUtil.select(cas, Chunk.class)){
			if ((this.chunkType!=null)&&(!chunk.getChunkType().toLowerCase().equals(chunkType.toLowerCase())))
				continue;
			for (Token t : JCasUtil.selectCovered(cas, Token.class, chunk)){
				int tokenID = t.getId();
				if (tokenIdToTreeNodeMap.containsKey(tokenID)){
					Tree leaf = tokenIdToTreeNodeMap.get(tokenID);
					Tree preterminal  = leaf.parent(tree);
					preterminal.setValue(String.format("%s%d-%s", SPAN_LABEL, i, preterminal.value()));
				}
			}
			i = i + 1;
		}
		TreeUtil.finalizeTreeLeaves(cas, tree, this.leafTextType);
	}

}
