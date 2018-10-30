package it.unitn.nlpir.util.kelp;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ExampleFactory;
import it.uniroma2.sag.kelp.data.example.ParsingExampleException;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.LabelFactory;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;
import it.uniroma2.sag.kelp.utils.JacksonSerializerWrapper;
import it.unitn.nlpir.classifiers.Classifier;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


public class KelpWrapper implements Classifier {
	protected it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier classifier;
	protected static final String POSITIVE_EXAMPLE_LABEL = "+1";
	protected Label positiveLabel;
	/**
	 * Load a model from file
	 * @param modelFile
	 */
	public KelpWrapper(String modelFile) {
		JacksonSerializerWrapper serializer = new JacksonSerializerWrapper();
		try {
			classifier= serializer.readValue(new File(modelFile), it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.positiveLabel = LabelFactory.parseLabel((POSITIVE_EXAMPLE_LABEL));
	}

	/**
	 * Classify an instance (dependency tree string)
	 * @param instance
	 * @return the classification confidence
	 */
	@Override
	public double classify(String instance) {
		Example example;
		try {
			example = ExampleFactory.parseExample(instance);
			ClassificationOutput output = classifier.predict(example);
			return output.getScore(this.positiveLabel);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingExampleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -1.0;
	}


	
	public static Classifier newInstance(String path) {
		return new KelpWrapper(path);
	}
	
	
	
	
}
