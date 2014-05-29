package br.com.starcode.trex.types;

import java.util.Date;

/**
 * Utility class to format data in cells
 * It'll be passed to the interpreter within variables map  
 */
public class FormatterHelper {
	
	private String format;
	
	public Object date(Date date, String dateFormat) {
		this.format = dateFormat;
		return date;
	}
	
	public Object number(Number date, String dateFormat) {
		this.format = dateFormat;
		return date;
	}
	
	public String format() {
		return format;
	}
	
	public void reset() {
		this.format = null;
	}

}
