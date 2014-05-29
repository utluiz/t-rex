package br.com.starcode.trex;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;

import br.com.starcode.trex.interpreter.Interpreter;
import br.com.starcode.trex.interpreter.MVEL2Interpreter;
import br.com.starcode.trex.types.FormatterHelper;

public class ExpressionTest {

	@org.junit.Test
	@Ignore
	public void mvel1() throws Exception {

		Interpreter interpreter = new MVEL2Interpreter();

		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("name", "Luiz Ricardo");
		
		interpreter.parseExpression("for (x : 9) System.out.println(x)", null, mapa, null);

	}
	
	//função especial
	public static class Special {
		public void loop(int i) {
			System.out.println("Passed here! " + i);
		}
	};
	
	@org.junit.Test
	@Ignore
	public void mvel2() throws Exception {

		Interpreter interpreter = new MVEL2Interpreter();

		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("name", "Luiz Ricardo");
		
		mapa.put("__specialVariable$", new Special());
		
		interpreter.parseExpression("for (x : 9) __specialVariable$.loop(x) ", null, mapa, null);

	}
	
	@org.junit.Test
	public void formatter() throws Exception {

		Interpreter interpreter = new MVEL2Interpreter();
		FormatterHelper f = new FormatterHelper();
		
		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("age", 1);
		Date d = new Date();
		mapa.put("date", d);
		
		Object ret = interpreter.parseExpression("format.date(date, \"dd/mm/yyyy\")", null, mapa, f);
		Assert.assertEquals(d, ret);
		Assert.assertEquals("dd/mm/yyyy", f.format());
		
		f.reset();
		Object ret2 = interpreter.parseExpression("format.number(age, \"#.##\")", null, mapa, f);
		Assert.assertEquals(1, ret2);
		Assert.assertEquals("#.##", f.format());
		
		f.reset();
		Object ret3 = interpreter.parseExpression("date", null, mapa, f);
		Assert.assertEquals(d, ret3);
		Assert.assertNull(f.format());

	}
	
}
