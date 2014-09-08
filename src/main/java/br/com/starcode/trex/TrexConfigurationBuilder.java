package br.com.starcode.trex;

import br.com.starcode.trex.interpreter.Interpreter;
import br.com.starcode.trex.interpreter.MVEL2Interpreter;
import br.com.starcode.trex.spreadsheet.AdditionalModifications;
import br.com.starcode.trex.spreadsheet.PoiSheetParser;
import br.com.starcode.trex.spreadsheet.SheetParser;

/**
 * Builder to build the configuration
 */
public class TrexConfigurationBuilder {

	private Interpreter interpreter;
	private SheetParser parser;
	private AdditionalModifications additionalModifications;

	protected TrexConfigurationBuilder() {
		interpreter = new MVEL2Interpreter();
		parser = new PoiSheetParser();
	}

	/**
	 * Builder method that navigates to template builder
	 * @return Next builder of DSL (template builder)
	 */
	public TrexTemplateBuilder template() {
		return new TrexTemplateBuilder(new TrexConfiguration(interpreter, parser, additionalModifications));
	}

	public TrexConfigurationBuilder interpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
		return this;
	}

	public TrexConfigurationBuilder noInterpreter() {
		this.interpreter = null;
		return this;
	}

	public Interpreter interpreter() {
		return interpreter;
	}

	public TrexConfigurationBuilder parser(SheetParser parser) {
		this.parser = parser;
		return this;
	}

	public SheetParser parser() {
		return parser;
	}

	public TrexConfigurationBuilder additionalModifications(AdditionalModifications additionalModifications) {
		this.additionalModifications = additionalModifications;
		return this;
	}

	public AdditionalModifications additionalModifications() {
		return additionalModifications;
	}


}
