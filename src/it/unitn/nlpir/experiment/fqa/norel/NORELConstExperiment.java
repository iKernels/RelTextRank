package it.unitn.nlpir.experiment.fqa.norel;

import java.util.Properties;

import it.unitn.nlpir.experiment.fqa.StanfordAETrecQAWithQCExperiment;
import it.unitn.nlpir.projectors.Projectors;
import it.unitn.nlpir.tree.ConstituencyTreeBuilder;
import it.unitn.nlpir.tree.leaffinalizers.LeafSPTKFullAllLeavesFinalizer;

/**
 * <li> CONST + FOCUS + REL
 * <li> SPTK fine-grained QC
 * <li> <b>Example:</b>
* @author IKernels group
 *
 */
public class NORELConstExperiment extends  StanfordAETrecQAWithQCExperiment {
	public NORELConstExperiment(String configFile) {
		super(configFile);
	}
	
	public NORELConstExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public NORELConstExperiment(Properties p) {
		super(p);
	}
	
	public NORELConstExperiment() {
		super();
	}
	
	protected void setupProjector() {
		this.pruningRay = -1;		
		this.projector = Projectors.getNoRELProjector(new ConstituencyTreeBuilder(), new LeafSPTKFullAllLeavesFinalizer());
	}
}
