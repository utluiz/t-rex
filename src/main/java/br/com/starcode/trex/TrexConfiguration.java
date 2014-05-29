package br.com.starcode.trex;

import br.com.starcode.trex.interpreter.Interpreter;
import br.com.starcode.trex.spreadsheet.AdditionalModifications;
import br.com.starcode.trex.spreadsheet.SheetParser;

public class TrexConfiguration {

	private Interpreter interpreter;
	private SheetParser parser;
	private AdditionalModifications additionalModifications;

	protected TrexConfiguration(
			Interpreter interpreter,
			SheetParser parser,
			AdditionalModifications additionalModifications) {
		if (parser == null) {
			throw new IllegalArgumentException("You should provide a spreadsheet parser!");
		}
		this.interpreter = interpreter;
		this.parser = parser;
	}

	public Interpreter interpreter() {
		return interpreter;
	}

	public SheetParser parser() {
		return parser;
	}

	public AdditionalModifications additionalModifications() {
		return additionalModifications;
	}
	
}
