package it.unitn.nlpir.experiment.fqa;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unitn.nlpir.projectors.LCTProjectors;

/**
 * <li> DT3_Q + DT2_A + FOCUS + REL
 * <li> Features read from the external file
 * <li> SPTK fine-grained QC
 * <li> <b>Example</b>: [ROOT [be::v [who::w [GR-attr] [POS-WP]] [author::n [the::d [GR-det] [POS-DT]] [of::i [book::n [the::d [GR-det] [POS-DT]] [lady::n [the::d [GR-det] [POS-DT]] [iron::n [REL-GR-nn] [REL-POS-NN]] [biography::n [a::d [GR-det] [POS-DT]] [of::i [thatcher::n [margaret::n [REL-GR-nn] [REL-POS-NNP]] [REL-GR-pobj] [REL-POS-NNP]] [GR-prep] [POS-IN]] [REL-GR-dep] [REL-POS-NN]] [REL-GR-appos] [REL-POS-NN]] [GR-pobj] [POS-NN]] [GR-prep] [POS-IN]] [REL-FOCUS-HUM-GR-nsubj] [POS-NN]] [ROOT] [POS-VBZ]]]	[ROOT [root [REL-NP [DT [the]] [REL-NNP [iron]] [REL-NN [lady]]] [dep [REL-NP [DT [a]] [REL-NN [biography]]] [prep [REL-FOCUS-HUM-NP [IN [of]] [REL-NNP [margaret]] [REL-NNP [thatcher]]] [prep [REL-FOCUS-HUM-NP [IN [by]] [NNP [hugo]] [NNP [young]]] [appos [REL-FOCUS-HUM-NP [NNP [farrar]]] [dep [REL-FOCUS-HUM-NP [NNP [straus]] [CC [&]] [NNP [giroux]]]]]]]]]]
* @author IKernels group
 * 
 *
 *
 */
public class LCTqDT2aExperiment extends StanfordAETrecQAWithQCExperiment{
	protected static final Logger logger = LoggerFactory.getLogger(LCTqDT2aExperiment.class);

	public LCTqDT2aExperiment(String configFile, Properties p) {
		super(configFile, p);
	}
	
	public LCTqDT2aExperiment(String configFile) {
		super(configFile);
	}
	
	public LCTqDT2aExperiment(Properties p) {
		super(p);
	}
	
	public LCTqDT2aExperiment() {
		super();
	}
	protected void setupProjector() {
		pruningRay = -1;
		this.projector = LCTProjectors.getMixedFocusProjectorLCTandDepPH(pruningRay,   this.doFocusMatch,  this.typeFocusMatch, this.markFocusInQuestion);
	}
}	
	
