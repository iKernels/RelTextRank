package it.unitn.nlpir.experiment.fqa;

import java.util.Properties;

import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.DependencyPosTagTreeBuilder;
import it.unitn.nlpir.tree.leaffinalizers.LeafSPTKFullAllLeavesFinalizer;

/**
 * <li> DT1 + FOCUS + REL
 * <li> Features read from the external file
 * <li> SPTK fine-grained QC
 * <li> <b>Example</b>: [ROOT [root [attr [WP [who::w]]] [VBZ [be::v]] 
 * [REL-FOCUS-HUM-nsubj [det [DT [the::d]]] [NN [author::n]] [prep [IN [of::i]]
 *  [pobj [det [DT [the::d]]] [NN [book::n]] [REL-appos [det [DT [the::d]]] 
 *  [REL-nn [REL-NN [iron::n]]] [REL-NN [lady::n]] [REL-dep [det [DT [a::d]]] [REL-NN [biography::n]] [prep [IN [of::i]] [REL-pobj [REL-nn [REL-NNP [margaret::n]]] [REL-NNP [thatcher::n]]]]]]]]]]]	[ROOT [root [det [DT [the::d]]] [REL-nn [REL-NNP [iron::n]]] [REL-NN [lady::n]] [REL-dep [det [DT [a::d]]] [REL-NN [biography::n]] [prep [IN [of::i]] [REL-FOCUS-HUM-pobj [REL-FOCUS-HUM-nn [REL-NNP [margaret::n]]] [REL-NNP [thatcher::n]] [prep [IN [by::i]] [REL-FOCUS-HUM-pobj [REL-FOCUS-HUM-nn [NNP [hugo::n]]] [NNP [young::n]] [REL-FOCUS-HUM-appos [NNP [farrar::n]] [REL-FOCUS-HUM-dep [NNP [straus::n]] [REL-FOCUS-HUM-cc [CC [&::c]]] [REL-FOCUS-HUM-conj [NNP [giroux::n]]]]]]]]]]]]
* @author IKernels group
 *
 */
public class DT1Experiment extends StanfordAETrecQAWithQCExperiment   {
	public DT1Experiment(String configFile) {
		super(configFile);
	}
	
	public DT1Experiment(Properties p) {
		super(p);
	}
	
	public DT1Experiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public DT1Experiment() {
		super();
	}
	protected void setupProjector() {
		this.pruningRay = 0;
		this.projector = Projectors.getFocusProjector(new DependencyPosTagTreeBuilder(), pruningRay, new StartsWithOrContainsTagPruningRule(), new LeafSPTKFullAllLeavesFinalizer(),
				doFocusMatch, typeFocusMatch, markFocusInQuestion);
	}
	
}
