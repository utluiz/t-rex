package br.com.starcode.trex.spreadsheet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

import br.com.starcode.trex.interpreter.Interpreter;
import br.com.starcode.trex.types.ForStatementData;
import br.com.starcode.trex.types.FormatterHelper;
import br.com.starcode.trex.types.IfStatementData;

public class PoiSheetParser implements SheetParser {

	private Workbook workbook;
	private Interpreter interpreter;
	private Object context;
	private Map<String, Object> variables;
	private FormatterHelper formatter;

	public PoiSheetParser() {
		//stores formatting options
		formatter = new FormatterHelper();
	}

	public SheetParser load(InputStream input) throws IOException {
		try {
			workbook = WorkbookFactory.create(input);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return this;
	}

	public SheetParser process(
			Interpreter interpreter, 
			Object context,
			Map<String, Object> variables,
			AdditionalModifications additionalModifications) {
		
		//validation
		if (workbook == null) {
			throw new RuntimeException("No spreadsheet loaded!");
		}
		
		//store parameters
		this.interpreter = interpreter;
		this.context = context;
		
		//clone map
		this.variables = new HashMap<String, Object>();
		if (variables != null) {
			this.variables.putAll(variables);
		}
		
		//user pre processing
		if (additionalModifications != null) {
			additionalModifications.beforeProcess(workbook);
		}
		
		//process sheets
		int sheetCount = workbook.getNumberOfSheets();
		for (int i = 0; i < sheetCount; i++) {
			processSheet(workbook.getSheetAt(i));
		}
		
		//user post processing
		if (additionalModifications != null) {
			additionalModifications.afterProcess(workbook);
		}
		return this;

	}
	
	private void processSheet(Sheet sheet) {

		int i = 0;
		while (i <= sheet.getLastRowNum()) {
			Row row = sheet.getRow(i);
			if (row != null) {
				//row.setRowNum(i);
				i += processRow(row);
			} else {
				i++;
			}
		}
		
	}
	
	private int processRow(Row row) {
		
		//loop for special expression statements (@for and @if for now)
		ForStatementData forData = null;
		IfStatementData ifData = null;
		Cell forCell = null;
		Cell ifCell = null;
		for (Cell cell : row) {
			
			if (cell.getCellType() == Cell.CELL_TYPE_STRING
					|| (cell.getCellType() == Cell.CELL_TYPE_FORMULA
					&& cell.getCachedFormulaResultType() == Cell.CELL_TYPE_STRING)) {
				
				String value = cell.getStringCellValue();
				if (value != null) {
					
					forData = interpreter.parseForStatement(value);
					if (forData != null) {
						forCell  = cell;
						break;
					} else {
						ifData = interpreter.parseIfStatement(value);
						if (ifData != null) {
							ifCell  = cell;
							break;
						}
					}
					
				} 
				
			}
			
		}
		
		if (forData != null) {
			
			return processForStatement(forData, forCell);
			
		} else if (ifData != null) {
				
			return processIfStatement(ifData, ifCell);
				
		} else {
		
			for (Cell cell : row) {
				if (cell.getCellType() == Cell.CELL_TYPE_STRING
						|| (cell.getCellType() == Cell.CELL_TYPE_FORMULA
						&& cell.getCachedFormulaResultType() == Cell.CELL_TYPE_STRING)) {
					
					String value = cell.getStringCellValue();
					if (value != null) {
						
						changeValueIfNeeded(cell);
						
					}
	
				}
			}
			return 1;
			
		}
		
	}
	
	private int processIfStatement(
			IfStatementData ifData,
			Cell ifCell) {

		//remove IF cell
		ifCell.getRow().removeCell(ifCell);
		
		//get model values in for loop
		boolean result = false;
		try {
			result = interpreter.executeIfStatement(ifData, context, variables);
		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage() + " (row " + ifCell.getRow().getRowNum() + ")", e);
		}
		
		if (!result) {
			
			//count lines
			Integer interval = ifData.range().rowCount();
			if (interval == null || interval <= 0) interval = 1;
			
			//removes lines
			removeRows(ifCell.getRow().getSheet(), ifCell.getRowIndex(), interval);
			
		}
		
		//process all lines
		return 0;
		
	}
	
	private void removeRows(Sheet sheet, int position, int count) {
		for (int i = 0; i < count; i++) {
			Row row = sheet.getRow(position + i);
			if (row != null) {
				sheet.removeRow(row);
			}
			
		}
		sheet.shiftRows(position + count, sheet.getLastRowNum(), -count);
	}
	
	private int processForStatement(
			ForStatementData forData,
			Cell forCell) {
		
		Row row = forCell.getRow();
		Sheet sheet = row.getSheet();
		
		//remove FOR cell
		row.removeCell(forCell);
		
		//get model values in for loop
		List<Object> modelList = null;
		try {
			modelList = interpreter.executeForStatement(forData, context, variables);
		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage() + " (row " + row.getRowNum() + ")", e);
		}
		
		Integer interval = forData.range().rowCount();
		if (interval == null || interval <= 0) interval = 1;

		if (modelList != null && !modelList.isEmpty()) {
			
			int rowNum = row.getRowNum();
			int itemCount = modelList.size();
			int rowCount = itemCount * interval;
			
			//shifts the rows after this (less the existing one)
			sheet.shiftRows(rowNum, sheet.getLastRowNum(), rowCount - interval, true, false);
			
			//for each interval
			for (int i = 0; i < itemCount; i++) {
				
				Object item = modelList.get(i);
				
				//define "local variable" values in loop
				Object oldItem = variables.put(forData.variableName(), item);
				Object oldItemIndex = variables.put("index", i + 1);
				
				for (int j = 0; j < interval; j++) {
					
					//base row (last one)
					Row baseRow = sheet.getRow(rowNum + (itemCount  - 1) * interval + j);
					
					if (baseRow == null) {
						continue;
					}
					
					//creates the row if it isn't the last
					if (i < itemCount - 1) {
						
						int currentRow = rowNum + i * interval + j;
						Row newRow = sheet.createRow(currentRow);
						
						//clone attributes, including values
						cloneRow(baseRow, newRow);
						
						//interprets row
						processRow(newRow);
						
					} else {
						
						//interprets row
						processRow(baseRow);
						
					}
					
				}
				
				//restores old values if it did exist
				variables.remove(forData.variableName());
				if (oldItem != null) {
					variables.put(forData.variableName(), oldItem);
				}
				variables.remove("index");
				if (oldItemIndex != null) {
					variables.put("index", oldItemIndex);
				}
				
			}
			return rowCount;
			
		} else {
			
			//removes lines
			removeRows(forCell.getRow().getSheet(), forCell.getRowIndex(), interval);
			
			//do not advance
			return 0;
			
		}
		
	}
	
	private void changeValueIfNeeded(Cell cell) {

		//validate
		String originalValue = cell.getStringCellValue();
		if (originalValue == null || originalValue.isEmpty()) return;
		
		//interprets
		Object result = interpreter.parseTemplate(originalValue, context, variables, formatter);
		
		//converts
		if (result != null) {
			
			if (formatter.format() != null) {
				
				//create data format
				DataFormat df = workbook.createDataFormat();
				short sdf = df.getFormat(formatter.format());
				
				//set format
				CellStyle style = cell.getCellStyle();
				if (style == null) {
					style = workbook.createCellStyle();
				}
				style.setDataFormat(sdf);
				
			}
			
			if (result instanceof Date) {
				
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue((Date) result);
				
			} else if (result instanceof Calendar) {
				
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue((Calendar) result);
				
			} else if (result instanceof Number) {
				
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Number numValue = (Number) result;
				cell.setCellValue(numValue.doubleValue());
				
			} else if (result instanceof Boolean) {
				
				cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
				Boolean boolValue = (Boolean) result;
				cell.setCellValue(boolValue);
				
			} else  {

				String strValue = result.toString();
				if (!originalValue.equals(strValue)) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(strValue);
				}
			
			}
			
		} else {
			cell.setCellValue((String) null);
		}
		
	}
	
	private void cloneRow(Row sourceRow, Row newRow) {

        // Loop through source columns to add to new row
		for (Cell oldCell : sourceRow) {
			
            // Grab a copy of the old/new cell
            Cell newCell = newRow.createCell(oldCell.getColumnIndex());

            // Copy style from old cell and apply to new cell
            CellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);

            // If there is a cell comment, copy
            if (oldCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());
            
            // Set the cell data value
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
            }
        }

        // If there are are any merged regions in the source row, copy to new row
		Sheet sheet = sourceRow.getSheet();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(
                		newRow.getRowNum(),
                        (newRow.getRowNum() +
                                (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
                        cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn());
                sheet.addMergedRegion(newCellRangeAddress);
            }
        }

	}
	
	/*private void cloneColumn(Row sourceRow, Row newRow) {

        // Loop through source columns to add to new row
        for (Cell oldCell : sourceRow) {
            
            // Grab a copy of the old/new cell
            Cell newCell = newRow.createCell(oldCell.getColumnIndex());

            // Copy style from old cell and apply to new cell
            CellStyle newCellStyle = workbook.createCellStyle();
            newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            newCell.setCellStyle(newCellStyle);

            // If there is a cell comment, copy
            if (oldCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());
            
            // Set the cell data value
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
            }
        }

        // If there are are any merged regions in the source row, copy to new row
        Sheet sheet = sourceRow.getSheet();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(
                        newRow.getRowNum(),
                        (newRow.getRowNum() +
                                (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
                        cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn());
                sheet.addMergedRegion(newCellRangeAddress);
            }
        }

    }*/

	public void writeTo(OutputStream output) throws IOException {
		if (workbook == null) {
			throw new RuntimeException("No spreadsheet loaded!");
		}
		if (output == null) {
			throw new RuntimeException("Null output provided!");
		}
		workbook.write(output);
	}

}
