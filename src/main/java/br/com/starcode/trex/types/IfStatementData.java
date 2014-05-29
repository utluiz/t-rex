package br.com.starcode.trex.types;

public class IfStatementData {

	private String expression;
	private Range range;
	
	public IfStatementData(
			String expression, 
			Range range) {
		this.expression = expression;
		this.range = range;
	}
	
	public String expression() {
		return expression;
	}
	
	public Range range() {
		return range;
	}
	
}
