package br.com.starcode.trex;

import java.util.HashMap;
import java.util.Map;

import br.com.starcode.trex.interpreter.Interpreter;
import br.com.starcode.trex.interpreter.MVEL2Interpreter;
import static org.junit.Assert.*;

public class InterpreterTest {

	@org.junit.Test
	public void mvel2Interpreter() throws Exception {

		Interpreter interpreter = new MVEL2Interpreter();

		Object r1 = interpreter.parseTemplate("template test", null, null, null);
		assertEquals("template test", r1);


		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("name", "Luiz Ricardo");
		Object r2 = interpreter.parseTemplate("My name is @{name}!", null, mapa, null);
		assertEquals("My name is Luiz Ricardo!", r2);

	}
	
	@org.junit.Test
	public void mvel2InterpreterExpression() throws Exception {

		Interpreter interpreter = new MVEL2Interpreter();

		Object r1 = interpreter.parseExpression("'template test'", null, null, null);
		assertEquals("template test", r1);

		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("name", "Luiz Ricardo");
		Object r2 = interpreter.parseExpression("name", null, mapa, null);
		assertEquals("Luiz Ricardo", r2);
		
		Object r3 = interpreter.parseExpression("get('name')",  mapa, null, null);
		assertEquals("Luiz Ricardo", r3);

	}

	@org.junit.Test
	public void mvel2InterpreterWithCache() throws Exception {

		Interpreter interpreter = new MVEL2Interpreter().enableCache();

		Object r1 = interpreter.parseTemplate("template test", null, null, null);
		assertEquals("template test", r1);

		Object r2 = interpreter.parseTemplate("template test", null, null, null);
		assertEquals("template test", r2);

	}
}
