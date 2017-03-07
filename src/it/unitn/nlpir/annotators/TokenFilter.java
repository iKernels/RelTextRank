package it.unitn.nlpir.annotators;

import it.unitn.nlpir.annotators.resources.StoplistResource;
import it.unitn.nlpir.types.Token;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.descriptor.TypeCapability;
import org.uimafit.util.JCasUtil;

import com.google.common.base.CharMatcher;

@TypeCapability(inputs = { "it.unitn.nlpir.types.Token:begin", "it.unitn.nlpir.types.Token:end" }, outputs = { "it.unitn.nlpir.types.Token:filtered" })
public class TokenFilter extends JCasAnnotator_ImplBase {

	public static final String MODEL_KEY = "uima.stoplist";
	@ExternalResource(key = MODEL_KEY)
	private StoplistResource stoplist;

	public static final String PARAM_LOWERCASE = "lowercase";
	@ConfigurationParameter(name = PARAM_LOWERCASE, mandatory = false)
	private boolean lowercase = true;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	protected boolean filter(String word) {
		String clean = CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(word);
		return clean.isEmpty() || this.stoplist.contains(word.toLowerCase());
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		
		// Lazy initialization
		if(this.stoplist == null) {
			init();
		}

		for(Token token : JCasUtil.select(cas, Token.class)) {
			if(filter(token.getCoveredText())) {
				token.setIsFiltered(true);
			}
		}
	}

	public void init() {
		try {
			this.stoplist = (StoplistResource) getContext().getResourceObject(MODEL_KEY);
		} catch(ResourceAccessException e) {
			e.printStackTrace();
		}
	}
}
