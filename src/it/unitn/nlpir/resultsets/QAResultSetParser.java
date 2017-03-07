package it.unitn.nlpir.resultsets;

public class QAResultSetParser implements ResultSetParser {

	@Override
	public Result parse(String line) {
		return new Result(line);
	}

}
