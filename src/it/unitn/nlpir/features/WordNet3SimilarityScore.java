package it.unitn.nlpir.features;

import it.unitn.nlpir.types.Token;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity.PoS;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.core.ResourceFactory;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.ResourceLoaderException;

public class WordNet3SimilarityScore implements FeatureExtractor {
	private final Logger logger = LoggerFactory.getLogger(WordNet3SimilarityScore.class);

	private LexicalSemanticResource rn;
	private boolean useHypernyms;
	
	public WordNet3SimilarityScore() {
		this(false);
	}
	
	public WordNet3SimilarityScore(boolean useHypernyms) {
		this.useHypernyms = useHypernyms;
		try {
			rn = ResourceFactory.getInstance().get("wordnet3", "en");
		} catch (ResourceLoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rn.setIsCaseSensitive(false);
	}

	
	protected PoS getPos(String posTag){
		if (posTag.substring(0, 1).toLowerCase().equals("n")){
			return PoS.n;
		}
		if (posTag.substring(0, 1).toLowerCase().equals("v")){
			return PoS.v;
		}
		return null;
	}
	
	
	protected Set<String> getAllSynonymsOfAllSenses(Set<Entity> en){
		Set<String> syns1 = new HashSet<String>();
		for (Entity e1:en){
			
			syns1.addAll(e1.getLexemes());
		}
		return syns1;
	}
	
	protected Set<String> getAllHypernymsOfAllSenses(Set<Entity> en) throws LexicalSemanticResourceException{
		Set<String> syns1 = new HashSet<String>();
		for (Entity e1:en){
			
		
				Set<Entity> hyper1 = rn.getParents(e1);
				for (Entity h1 : hyper1){
					syns1.addAll(h1.getLexemes());
				}
			
			
		}
		return syns1;
	}
	
	private boolean matchWN(Token t1, Token t2) {
		Set<String> syns1 = new HashSet<String>();
		Set<String> syns2 = new HashSet<String>();
		PoS pos1 = getPos(t1.getPostag());
		PoS pos2 = getPos(t2.getPostag());
		if ((pos1==null)||(pos2==null)||(!pos1.equals(pos2)))
			return false;
		
		
		try {
			Set<Entity> es1 = rn.getEntity(t1.getLemma(), pos1);
			Set<Entity> es2 = rn.getEntity(t2.getLemma(), pos2);
			
			if (!this.useHypernyms){
				syns1.addAll(getAllSynonymsOfAllSenses(es1));
				syns2.addAll(getAllSynonymsOfAllSenses(es2));
			}
			else{
				syns1.addAll(getAllHypernymsOfAllSenses(es1));
				syns2.addAll(getAllHypernymsOfAllSenses(es2));
			}

		} catch (LexicalSemanticResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		if (syns1 != null && syns2 != null) {
			for (String s1 : syns1) {
				for (String s2 : syns2) {
					if (s1.equals(s2)) {
						logger.debug("Matched pair: {} {}", s1, s2);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private double computeWNOverlap(Collection<Token> tokens1, Collection<Token> tokens2) {
		double score = 0.0;
		for (Token t1 : tokens1) {
			if (t1.getIsFiltered())
				continue;
			String lemma1 = t1.getLemma().toLowerCase();
			for (Token t2 : tokens2) {
				if (t2.getIsFiltered())
					continue;
				String lemma2 = t2.getLemma().toLowerCase();
				logger.debug("Comparing: {} {}", lemma1, lemma2);
				if (lemma1.equals(lemma2) || matchWN(t1, t2)) {
					score += 1.0;
					break;
				}	
			}
		}
		int numTokens1 = tokens1.size();
		return score / numTokens1;
	}
	
	public double computeScore(QAPair qa) {
		JCas cas1 = qa.getQuestionCas();
		JCas cas2 = qa.getDocumentCas();
		Collection<Token> tokens1 = JCasUtil.select(cas1, Token.class);
		Collection<Token> tokens2 = JCasUtil.select(cas2, Token.class);

		double score = 0.5 * (computeWNOverlap(tokens1, tokens2) + computeWNOverlap(tokens2, tokens1));
		if (score == -1){
			score = 0.0;
		}
		return score;
	}


	@Override
	public String getFeatureName() {
		return this.getClass().getSimpleName() + (this.useHypernyms ? ".hypernyms" : ".synonyms");
	}

	@Override
	public void extractFeatures(QAPair qa) {
		qa.getFeatureVector().addFeature(computeScore(qa));
	}
}
