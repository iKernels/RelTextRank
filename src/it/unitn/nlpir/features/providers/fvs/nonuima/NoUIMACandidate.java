package it.unitn.nlpir.features.providers.fvs.nonuima;

import it.unitn.nlpir.util.Pair;
import svmlighttk.SVMVector;

public class NoUIMACandidate {
	public SVMVector featureVector;
	private Pair<PlainDocument,PlainDocument> pair;
	private int row;
	private int col;
	
	public NoUIMACandidate(Pair<PlainDocument, PlainDocument> pair) {
		this(pair,0,0);
	}

	public NoUIMACandidate(Pair<PlainDocument, PlainDocument> pair, int row_id, int col_id) {
		super();
		this.pair = pair;
		this.featureVector = new SVMVector();
		this.row = row_id;
		this.col = col_id;
	}
	
	public NoUIMACandidate clone() {
		NoUIMACandidate result =  new NoUIMACandidate(this.pair, this.row, this.col);
		result.featureVector = featureVector;
		return result;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row_id) {
		this.row = row_id;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col_id) {
		this.col = col_id;
	}

	public SVMVector getFeatureVector() {
		return featureVector;
	}

	public Pair<PlainDocument, PlainDocument> getPair() {
		return pair;
	}
	
	
}
