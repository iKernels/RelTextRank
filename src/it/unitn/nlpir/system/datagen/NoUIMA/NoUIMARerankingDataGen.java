package it.unitn.nlpir.system.datagen.NoUIMA;

import it.unitn.nlpir.features.providers.fvs.nonuima.NoUIMACandidate;
import java.util.List;

public interface NoUIMARerankingDataGen {
	
	public void handleNoUIMAData(List<NoUIMACandidate> candidates);
	
	public void cleanUp();
	
}
