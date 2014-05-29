package br.com.starcode.trex;

public abstract class Trex {

	/**
	 * Builder method that initiates the configuration builder
	 */
	public static TrexConfigurationBuilder configure() {
		return new TrexConfigurationBuilder();
	}

	/**
	 * Builder method that initiates the template builder
	 */
	public static TrexTemplateBuilder template() {
		return configure().template();
	}

}
