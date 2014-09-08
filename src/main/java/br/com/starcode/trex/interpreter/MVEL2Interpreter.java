package br.com.starcode.trex.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import br.com.starcode.trex.types.ForStatementData;
import br.com.starcode.trex.types.FormatterHelper;
import br.com.starcode.trex.types.IfStatementData;
import br.com.starcode.trex.types.Range;

/**
 * Uses MVEL expression language interpreter
 * http://mvel.codehaus.org/Language+Guide+for+2.0
 */
public class MVEL2Interpreter implements Interpreter {

	protected Map<String, CompiledTemplate> templateCompiledCache;
	protected Map<String, CompiledExpression> expressionCompiledCache;
	
	public MVEL2Interpreter() {
	}

	public Object parseTemplate(
			String template, 
			Object context, 
			Map<String, Object> variables,
			FormatterHelper formatter) {
		
		defineFormatter(variables, formatter);
		if (templateCompiledCache != null) {
			
			CompiledTemplate compiledTemplate = templateCompiledCache.get(template);
			if (compiledTemplate == null) {
				compiledTemplate = TemplateCompiler.compileTemplate(template);
				templateCompiledCache.put(template, compiledTemplate);
			}
			return TemplateRuntime.execute(compiledTemplate, context, variables); 
			
		} else {
			return TemplateRuntime.eval(template, context, variables);
		}
		
	}
	
	public Object parseExpression(
			String expression, 
			Object context,
			Map<String, Object> variables,
			FormatterHelper formatter) {
		
		defineFormatter(variables, formatter);
		if (expressionCompiledCache != null) {
			
			CompiledExpression compiledExpression = expressionCompiledCache.get(expression);
			if (compiledExpression == null) {
				compiledExpression = (CompiledExpression) MVEL.compileExpression(expression);
				expressionCompiledCache.put(expression, compiledExpression);
			}
			if (variables != null && formatter != null) {
				formatter.reset();
				variables.put("format", formatter);
			}
			return MVEL.executeExpression(compiledExpression, context, variables);
			
		} else {
			return MVEL.eval(expression, context, variables);
		}
		
	}

	public MVEL2Interpreter enableCache() {
		templateCompiledCache = new HashMap<String, CompiledTemplate>();
		expressionCompiledCache = new HashMap<String, CompiledExpression>();
		return this;
	}

	public MVEL2Interpreter disableCache() {
		templateCompiledCache = null;
		expressionCompiledCache = null;
		return this;
	}

	/**
	 * Parses a for statement like:
	 * \@for (item : list) 3 rows 2 columns
	 * \@for (item : list) 2 col 3 row 
	 */
	public ForStatementData parseForStatement(String expression) {
		
		if (expression != null && expression.trim().startsWith("@for")) {
			
			//gets the name of the variable
			Pattern p1 = Pattern.compile("\\(\\s*([^:]+)\\s:*");
			Matcher m1 = p1.matcher(expression);
			if (!m1.find()) {
				throw new RuntimeException("Invalid 'for' statement!");
			}
			String varName = m1.group(1);
			
			//position of range information
			int pos = expression.lastIndexOf(')');
			
			return new ForStatementData(
					expression.substring(1, pos + 1), 
					varName, 
					parseRange(expression.substring(pos + 1)));
			
		}
		return null; 
		
	}
	
	public List<Object> executeForStatement(
			ForStatementData forStatementData, 
			Object context, 
			Map<String, Object> variables) {

		//adds a special list of objects
		List<Object> resultList = new ArrayList<Object>();
		variables.put("__specialVariable$", resultList);
		
		//execute for loop adding the items to the list
		String forStatement = forStatementData.expression() + 
				" { __specialVariable$.add(" + forStatementData.variableName() + ") }";
		parseExpression(forStatement, context, variables, null);
		
		//returns
		return resultList;
		
	}

	public IfStatementData parseIfStatement(String expression) {
		
		if (expression != null && expression.trim().startsWith("@if")) {
			
			//position of range information
			int pos = expression.lastIndexOf(')');
			
			return new IfStatementData(
					expression.substring(1, pos + 1), 
					parseRange(expression.substring(pos + 1)));
			
		}
		return null; 
		
	}
	
	public boolean executeIfStatement(IfStatementData ifStatementData, Object context, Map<String, Object> variables) {
		
		//adds a special list of objects
		List<Object> resultList = new ArrayList<Object>();
		variables.put("__specialVariable$", resultList);
		
		//execute if and add ans item to the list when true
		String ifStatement = ifStatementData.expression() + 
				" { __specialVariable$.add(\"\") }";
		parseExpression(ifStatement, context, variables, null);
		
		//returns
		return !resultList.isEmpty();
		
	}

	protected Range parseRange(String rangeText) {
		
		//gets the name of the variable
		Pattern p1 = Pattern.compile("([\\d])+\\s+ro");
		Matcher m1 = p1.matcher(rangeText);
		Integer rows = null;
		if (m1.find()) {
			rows = new Integer(m1.group(1));
		}
		
		//gets the name of the variable
		Pattern p2 = Pattern.compile("([\\d])+\\s+co");
		Matcher m2 = p2.matcher(rangeText);
		Integer cols = null;
		if (m2.find()) {
			cols = new Integer(m2.group(1));
		}
		
		return new Range(rows, cols);
		
	}
	
	public void defineFormatter(
			Map<String, Object> variables,
			FormatterHelper formatter) {
		
			if (variables != null && formatter != null) {
				
				//clear previous commands (to avoid creating the object for every cell)
				formatter.reset();
				variables.put("format", formatter);
				
			}
		
	}
	
}
