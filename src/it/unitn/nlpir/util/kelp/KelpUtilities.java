package it.unitn.nlpir.util.kelp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.LabelFactory;
import it.uniroma2.sag.kelp.data.representation.Representation;
import it.uniroma2.sag.kelp.data.representation.tree.TreeRepresentation;

public class KelpUtilities {
	protected static final Logger logger = LoggerFactory.getLogger(KelpUtilities.class);
	public static Example generateKelpExample(String labelString, String treeRepresenation) {
		Example example = new SimpleExample();
		Representation r = new TreeRepresentation();
		try {
//			r.setDataFromText("(CD (##::c))");
			r.setDataFromText(treeRepresenation.replace("##", "DSHARP"));
		} catch (Exception e) {
			logger.warn(treeRepresenation);
			try {
				r.setDataFromText(treeRepresenation);
			}
			catch (Exception e1)
			{}
			e.printStackTrace();
		}
		example.addRepresentation("T", r);
		example.setLabels(new Label[]{LabelFactory.parseLabel((labelString))});
		return example;
	}
}
