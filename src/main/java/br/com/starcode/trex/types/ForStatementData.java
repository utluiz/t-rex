package br.com.starcode.trex.types;

public class ForStatementData {

	private String expression;
	private String variableName;
	private Range range;
	
	public ForStatementData(
			String expression, 
			String variableName, 
			Range range) {
		this.expression = expression;
		this.variableName = variableName;
		this.range = range;
	}
	
	public String expression() {
		return expression;
	}
	
	public String variableName() {
		return variableName;
	}
	
	public Range range() {
		return range;
	}
	
}
