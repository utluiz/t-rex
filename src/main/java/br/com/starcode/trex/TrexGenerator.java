package br.com.starcode.trex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import br.com.starcode.trex.util.StringBuilderOutputStream;

/**
 * Final step of builder pattern. 
 * It actually outputs the content to the destination calling the proper spreadsheet parser.
 */
public class TrexGenerator {

	private TrexConfiguration config;
	private InputStream input;
	private Map<String, Object> variables;
	private Object context;

	protected TrexGenerator(
			TrexConfiguration config, 
			InputStream input,
			Object context,
			Map<String, Object> variables) {
		this.config = config;
		this.input = input;
		this.context =  context;
		this.variables =  variables;
	}

	public void to(OutputStream output) throws IOException {
		config.parser()
		.load(input)
		.process(config.interpreter(), context, variables, config.additionalModifications())
		.writeTo(output);
	}

	public void to(File file) throws IOException {
		to(new FileOutputStream(file));
	}

	public void to(String filePath) throws IOException {
		to(new FileOutputStream(filePath));
	}

	public void to(StringBuilder sb) throws IOException {
		to(new StringBuilderOutputStream(sb));
	}

	public byte[] get() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		to(bos);
		return bos.toByteArray();
	}

}
