package it.unitn.nlpir.system.core.precomputed;

import it.unitn.nlpir.util.Pair;
import it.unitn.nlpir.util.WriteFile;

public class SimilarityUtils {
	public static void serializeSimilarity(WriteFile writer, String id1, String id2, double similarity) {
		writer.writeLn(String.format("%s\t%s\t%.10f", id1, id2, similarity));
	}
	

	public static void serializeSimilarity(WriteFile writer, Pair<String, String> id1, Pair<String,String> id2, double similarity) {
		writer.writeLn(String.format("%s\t%s\t%s\t%s\t%.10f", id1.getA(), id1.getB(), id2.getA(), id2.getB(), similarity));
	}
}
