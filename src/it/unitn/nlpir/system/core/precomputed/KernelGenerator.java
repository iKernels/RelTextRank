package it.unitn.nlpir.system.core.precomputed;

import java.io.IOException;

import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.cache.FixIndexSquaredNormCache;
import it.uniroma2.sag.kelp.kernel.cache.SquaredNormCache;
import it.uniroma2.sag.kelp.kernel.standard.NormalizationKernel;
import it.uniroma2.sag.kelp.kernel.tree.PartialTreeKernel;

public class KernelGenerator {
	public static void main(String[] args) {
		PartialTreeKernel ptk = new PartialTreeKernel(0.4f, 0.4f, 1,
			    "constTree");
			NormalizationKernel normK = new NormalizationKernel(ptk);
			int instances = 1000000;
			SquaredNormCache normCache = new FixIndexSquaredNormCache(instances);
			ptk.setSquaredNormCache(normCache);
			try {
				Kernel.save(normK, "config/kelp/ptk-gen.json");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
