package it.unitn.nlpir.experiment.fqa;

import java.util.Properties;

import it.unitn.nlpir.experiment.rer.cl.qc.tois.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.pruners.StartsWithOrContainsTagPruningRule;
import it.unitn.nlpir.tree.ConstituencyTreeBuilder;
import it.unitn.nlpir.tree.leaffinalizers.LeafSPTKFullAllLeavesFinalizer;

/**
 * <li> CONST + FOCUS + REL
 * <li> SPTK fine-grained QC
 * <li> <b>Example:</b>
* @author IKernels group
 *
 */
public class ConstExperiment extends  StanfordAETrecQAWithQCExperiment {
	public ConstExperiment(String configFile) {
		super(configFile);
	}
	
	public ConstExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public ConstExperiment(Properties p) {
		super(p);
	}
	
	public ConstExperiment() {
		super();
	}
	
	protected void setupProjector() {
		this.pruningRay = 0;		
		this.projector = Projectors.getFocusProjector(new ConstituencyTreeBuilder(), pruningRay, new StartsWithOrContainsTagPruningRule(), new LeafSPTKFullAllLeavesFinalizer());
	}
	



}
