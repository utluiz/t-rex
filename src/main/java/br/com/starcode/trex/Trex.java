package br.com.starcode.trex;

public abstract class Trex {

	/**
	 * Builder method that initiates the configuration builder
	 * @return instance of buider
	 */
	public static TrexConfigurationBuilder configure() {
		return new TrexConfigurationBuilder();
	}

	/**
	 * Builder method that initiates the template builder
	 * @return instance of buider
	 */
	public static TrexTemplateBuilder template() {
		return configure().template();
	}

}
