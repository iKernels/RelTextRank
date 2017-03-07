package it.unitn.nlpir.annotators;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import it.unitn.nlpir.annotators.resources.StanfordCoreNLPResourceWithProps;
import it.unitn.nlpir.annotators.resources.StanfordCoreNLPResourceWithPropsImpl;
import java.util.Properties;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * The single annotators to run can be specified in the StanfordCoreNLPResource
 * implementation.
 */
public class StanfordCoreNLPWithPropsAnnotator extends StanfordCoreNLPAnnotator {



	
	public static AnalysisEngineDescription getDescription() {
		return getDescription(defaultAnnotatorList);
	}

	public static AnalysisEngineDescription getDescription(String annotatorList) {
		ExternalResourceDescription extDesc = createExternalResourceDescription(
				StanfordCoreNLPResourceWithPropsImpl.class, "null");


		AnalysisEngineDescription aeDesc = null;
		try {
			aeDesc = createPrimitiveDescription(StanfordCoreNLPWithPropsAnnotator.class,
					StanfordCoreNLPWithPropsAnnotator.RESOURCE_KEY, extDesc,
					StanfordCoreNLPWithPropsAnnotator.ANNOTATOR_LIST, annotatorList,
					StanfordCoreNLPWithPropsAnnotator.ADD_DEP_PARSING, "true");
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return aeDesc;
	}

	
	protected void init() {
		try {
			this.stanfordCoreNLPResource = (StanfordCoreNLPResourceWithProps) getContext()
					.getResourceObject(RESOURCE_KEY);
			Properties p = new Properties();
			p.put("annotators", this.annotatorList);
			p.put("parse.originalDependencies", "true");
			
			
			if (this.stanfordCoreNLPResource instanceof StanfordCoreNLPResourceWithProps)
				this.pipeline =  ((StanfordCoreNLPResourceWithProps) this.stanfordCoreNLPResource).getPipeline(p);
			Properties props = this.pipeline.getProperties();
			System.out.println(props);
		} catch (ResourceAccessException e) {
			e.printStackTrace();
		}
	}


}
