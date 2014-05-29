package br.com.starcode.trex.interpreter;

import java.util.List;
import java.util.Map;

import br.com.starcode.trex.types.ForStatementData;
import br.com.starcode.trex.types.FormatterHelper;
import br.com.starcode.trex.types.IfStatementData;

/**
 * A class responsible for interpreting templates.
 * It'll be invoked for each text cell.
 */
public interface Interpreter {

	Object parseTemplate(
			String template, 
			Object context, 
			Map<String, Object> variables,
			FormatterHelper formatter);
	
	Object parseExpression(
			String expression, 
			Object context, 
			Map<String, Object> variables,
			FormatterHelper formatter);
	
	ForStatementData parseForStatement(String expression);
	
	List<Object> executeForStatement(
			ForStatementData forStatementData, 
			Object context, 
			Map<String, Object> variables);

	IfStatementData parseIfStatement(String expression);
	
	boolean executeIfStatement(
			IfStatementData ifStatementData, 
			Object context, 
			Map<String, Object> variables);

}
