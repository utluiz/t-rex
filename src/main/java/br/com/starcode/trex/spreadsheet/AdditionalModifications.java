package br.com.starcode.trex.spreadsheet;

import org.apache.poi.ss.usermodel.Workbook;

public interface AdditionalModifications {

	void beforeProcess(Workbook wb);

	void afterProcess(Workbook wb);

}
