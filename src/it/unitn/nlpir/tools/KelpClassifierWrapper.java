package it.unitn.nlpir.tools;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.ExampleFactory;
import it.uniroma2.sag.kelp.data.example.ParsingExampleException;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;
import it.uniroma2.sag.kelp.utils.JacksonSerializerWrapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class KelpClassifierWrapper implements IOneVsAllClassifier{
	protected it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier classifier;
	
	
	public KelpClassifierWrapper(String modelPath){
		JacksonSerializerWrapper serializer = new JacksonSerializerWrapper();
		try {
			classifier= serializer.readValue(new File(modelPath), it.uniroma2.sag.kelp.predictionfunction.classifier.Classifier.class);
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
	}
	
	
	@Override
	public String getMostConfidentModel(String instance) {
		Example example;
		try {
			example = ExampleFactory.parseExample(instance);
			ClassificationOutput output = classifier.predict(example);
			List<Label> predictedClasses = output.getPredictedClasses();
			//System.out.println("predicted classes "+predictedClasses);
			return predictedClasses.get(0).toString();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingExampleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
