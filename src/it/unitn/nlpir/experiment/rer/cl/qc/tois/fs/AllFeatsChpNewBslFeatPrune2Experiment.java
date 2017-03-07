package it.unitn.nlpir.experiment.rer.cl.qc.tois.fs;



import it.unitn.nlpir.experiment.rer.cl.qc.tois.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.features.EntitiesDistributionInDocument;
import it.unitn.nlpir.features.FeatureSets;
import it.unitn.nlpir.features.QuestionCategoryFeature;
import it.unitn.nlpir.features.StringKernelScore;
import it.unitn.nlpir.features.WordNet3SimilarityScore;
import it.unitn.nlpir.features.WordOverlap;
import it.unitn.nlpir.features.builder.FeaturesBuilder;
import it.unitn.nlpir.features.providers.fvs.DependencyTripletsProvider;
import it.unitn.nlpir.features.providers.similarity.CosineSimilarity;
import it.unitn.nlpir.features.providers.similarity.PTKSimilarity;
import it.unitn.nlpir.features.providers.trees.old.RelConstituencyTreeProvider;
import it.unitn.nlpir.features.providers.trees.old.RelDependencyTreeProvider;
import it.unitn.nlpir.features.providers.trees.old.RelPhraseDependencyTreeProvider;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PosChunkFullTreeBuilder;
import it.unitn.nlpir.tree.TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder;
import it.unitn.nlpir.uima.TokenTextGetterFactory;

/**
 * CH + V + FC_thres + QT, Stanford preprocessing pipeline
* @author IKernels group
 *
 */
public class AllFeatsChpNewBslFeatPrune2Experiment extends StanfordAETrecQAWithQCExperiment {
	
	
	public AllFeatsChpNewBslFeatPrune2Experiment(String configFile) {
		super(configFile);
	}
	
	
	protected void setupProjector() {
		this.pruningRay = 2;
		
		this.projector = Projectors.getFocusWithThreshFocusUntypedFocusInQProjector(new PosChunkFullTreeBuilder(), pruningRay,
                new StartsWithOrContainsTagPruningRule(), new TreeLeafFinalizerAndQCAsRootChildToQAndDocAdder());
		
		
	}
	
	protected void setupFeatures() {
		fb = new FeaturesBuilder()
		
		.extend(FeatureSets.buildBowFeatures())
		.extend(FeatureSets.buildOldKernelFeatures())
		.add(new QuestionCategoryFeature())
		.extend(FeatureSets.buildSimpleBowFeatures())
		.add(new CosineSimilarity(new DependencyTripletsProvider(
				TokenTextGetterFactory.LEMMA))) //gut
		.add(new PTKSimilarity(new RelConstituencyTreeProvider())) //gut
		.add(new PTKSimilarity(new RelDependencyTreeProvider()))//gut
		.add(new PTKSimilarity(new RelPhraseDependencyTreeProvider()))
		.extend(FeatureSets.buildDKProWordOverlapFeatures())
		.extend(FeatureSets.buildDKProESAFullTextFeatures())
		.add(new EntitiesDistributionInDocument())
		.add(new StringKernelScore())
		.add(new WordOverlap())
		.add(new WordNet3SimilarityScore())
		.add(new WordNet3SimilarityScore(true));
		
		
	
	}
	
}
