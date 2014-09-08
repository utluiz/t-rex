package br.com.starcode.trex;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import br.com.starcode.trex.util.ResourceBundleMap;

/**
 * Builder to define the "variables" available to the interpreter during spreadsheet values parsing 
 */
public class TrexModelBuilder {

	private TrexConfiguration config;
	private InputStream input;
	private Object context;
	private Map<String, Object> variables;

	protected TrexModelBuilder(TrexConfiguration config, InputStream input) {
		this.config = config;
		this.input = input;
		this.variables =  new HashMap<String, Object>();
	}

	/**
	 * Builder method that navigates to output generator "final" class
	 * @return Next step of DSL: Generator
	 */
	public TrexGenerator output() {
		return new TrexGenerator(config, input, context, variables);
	}

	protected TrexModelBuilder context(Object context) {
		this.context = context;
		return this;
	}

	protected TrexModelBuilder add(String name, Object value) {
	    if (value instanceof ResourceBundle) {
	        value = new ResourceBundleMap((ResourceBundle) value);
	    }
		variables.put(name, value);
		return this;
	}

	protected TrexModelBuilder add(Map<String, Object> map) {
		variables.putAll(map);
		return this;
	}
	
}
