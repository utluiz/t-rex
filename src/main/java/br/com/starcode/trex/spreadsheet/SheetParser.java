package br.com.starcode.trex.spreadsheet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import br.com.starcode.trex.interpreter.Interpreter;

public interface SheetParser {

	SheetParser load(InputStream input) throws IOException;

	SheetParser process(
			Interpreter interpreter, 
			Object context,
			Map<String, Object> variables,
			AdditionalModifications additionalModifications);

	void writeTo(OutputStream output) throws IOException;

}
