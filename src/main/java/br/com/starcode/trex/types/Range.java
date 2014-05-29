package br.com.starcode.trex.types;

public class Range {

	private Integer rowCount;
	private Integer columnCount;
	
	public Range(
			Integer rowCount, 
			Integer columnCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;
	}
	
	public Integer rowCount() {
		return rowCount;
	}
	
	public Integer columnCount() {
		return columnCount;
	}
	
	
}
