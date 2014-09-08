package br.com.starcode.trex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Builder to define which spreadsheet template will be used
 */
public class TrexTemplateBuilder {

	private TrexConfiguration config;
	private InputStream input;

	protected TrexTemplateBuilder(TrexConfiguration config) {
		if (config == null) {
			throw new IllegalArgumentException("You should provide a configuration!");
		}
		this.config = config;
	}

	/**
	 * Builder method that navigates to model builder
	 * @return Next step of DSL: model builder
	 */
	public TrexModelBuilder model() {
		return new TrexModelBuilder(config, input);
	}

	public TrexTemplateBuilder load(InputStream input) {
		this.input = input;
		return this;
	}

	public TrexTemplateBuilder load(File file) throws FileNotFoundException {
		this.input = new FileInputStream(file);
		return this;
	}

	public TrexTemplateBuilder load(String filePath) throws FileNotFoundException {
		this.input = new FileInputStream(filePath);
		return this;
	}

}
