package it.unitn.nlpir.experiment.fqa;

import java.util.Properties;

import it.unitn.nlpir.experiment.rer.cl.qc.tois.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.PhraseDependencyTreeBuilder;
import it.unitn.nlpir.tree.leaffinalizers.LeafSPTKFullAllLeavesFinalizer;

/**
 * <li> DT2 + FOCUS + REL
 * <li> Features read from the external file
 * <li> SPTK fine-grained QC
 * <li> <b>Example:</b> [ROOT [root [attr [NP [WP [who::w]]]] [VP [VBZ [be::v]]] [nsubj [REL-FOCUS-HUM-NP [DT [the::d]] [NN [author::n]]] [prep [NP [IN [of::i]] [DT [the::d]] [NN [book::n]]] [appos [REL-NP [DT [the::d]] [REL-NN [iron::n]] [REL-NN [lady::n]]] [dep [REL-NP [DT [a::d]] [REL-NN [biography::n]]] [prep [REL-NP [IN [of::i]] [REL-NNP [margaret::n]] [REL-NNP [thatcher::n]]]]]]]]]]	[ROOT [root [REL-NP [DT [the::d]] [REL-NNP [iron::n]] [REL-NN [lady::n]]] [dep [REL-NP [DT [a::d]] [REL-NN [biography::n]]] [prep [REL-FOCUS-HUM-NP [IN [of::i]] [REL-NNP [margaret::n]] [REL-NNP [thatcher::n]]] [prep [REL-FOCUS-HUM-NP [IN [by::i]] [NNP [hugo::n]] [NNP [young::n]]] [appos [REL-FOCUS-HUM-NP [NNP [farrar::n]]] [dep [REL-FOCUS-HUM-NP [NNP [straus::n]] [CC [&::c]] [NNP [giroux::n]]]]]]]]]]
* @author IKernels group
 *
 */
public class DT2Experiment  extends StanfordAETrecQAWithQCExperiment {
	public DT2Experiment(String configFile) {
		super(configFile);
	}
	
	public DT2Experiment(Properties p) {
		super(p);
	}
	
	public DT2Experiment() {
		super();
	}
	
	public DT2Experiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	protected void setupProjector() {
		this.projector = Projectors.getFocusProjector(new PhraseDependencyTreeBuilder(), pruningRay, new StartsWithOrContainsTagPruningRule(), new LeafSPTKFullAllLeavesFinalizer());
	}
	

}
