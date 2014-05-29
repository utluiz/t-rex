package br.com.starcode.trex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import br.com.starcode.trex.interpreter.MVEL2Interpreter;
import br.com.starcode.trex.spreadsheet.PoiSheetParser;
import br.com.starcode.trex.spreadsheet.SheetParser;

public class SheetParserTest {

	@org.junit.Test
	public void poiParserSimple() throws Exception {

		//generate
		SheetParser parser = new PoiSheetParser();
		parser.load(getClass().getResourceAsStream("simple.xlsx"));
		parser.process(new MVEL2Interpreter(), null, null, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parser.writeTo(bos);

		//check
		Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
		assertEquals("Simple Text!", wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());

	}

	public static class User {
		private String name;
		public User(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public String getAddress() {
			return "Endereço";
		}
	}

	@org.junit.Test
	public void poiParserExpressions() throws Exception {

		//model
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("otherUser", new User("Ricardo"));
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "your name");
		variables.put("map", map);
		String[] strArray = {"Item 1", "Item 2"};
		variables.put("array", strArray);
		List<String> list = new ArrayList<String>();
		list.add("Item 3");
		list.add("Item 4");
		variables.put("list", list);

		//generate
		SheetParser parser = new PoiSheetParser();
		parser.load(getClass().getResourceAsStream("expressions.xlsx"));
		parser.process(new MVEL2Interpreter(), new User("Luiz"), variables, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parser.writeTo(bos);

		//check
		Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
		assertEquals("Put your name here!", wb.getSheetAt(0).getRow(2).getCell(1).getStringCellValue());
		assertEquals("My name is Luiz...", wb.getSheetAt(0).getRow(4).getCell(1).getStringCellValue());
		assertEquals("Item 4", wb.getSheetAt(0).getRow(6).getCell(2).getStringCellValue());
		assertEquals("Item 1", wb.getSheetAt(0).getRow(6).getCell(3).getStringCellValue());
		assertEquals("Ricardo is here", wb.getSheetAt(0).getRow(8).getCell(1).getStringCellValue());

	}
	
	@org.junit.Test
	public void poiParserMultipleSheets() throws Exception {

		//generate
		SheetParser parser = new PoiSheetParser();
		parser.load(getClass().getResourceAsStream("expressions-multiple-sheets.xlsx"));
		parser.process(new MVEL2Interpreter(), new User("Silva"), null, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parser.writeTo(bos);

		//check
		Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
		assertEquals("Name: Silva", wb.getSheetAt(0).getRow(2).getCell(1).getStringCellValue());
		assertEquals("Address: Endereço", wb.getSheetAt(1).getRow(2).getCell(1).getStringCellValue());

	}
	
	public static class Parcel {
		private Integer number;
		private Date dueDate;
		public Parcel(Integer number, Date dueDate) {
			this.number = number;
			this.dueDate = dueDate;
		}
		public Integer getNumber() {
			return number;
		}
		public Date getDueDate() {
			return dueDate;
		}
	}
	
	@org.junit.Test
	public void poiParserLoop() throws Exception {
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		//model
		Map<String, Object> variables = new HashMap<String, Object>();
		List<Parcel> parcels = new ArrayList<Parcel>();
		parcels.add(new Parcel(1, df.parse("10/05/2014")));
		parcels.add(new Parcel(2, df.parse("20/06/2014")));
		parcels.add(new Parcel(3, df.parse("30/07/2014")));
		variables.put("parcels", parcels);
		variables.put("client", new User("Silva"));
		
		//generate
		SheetParser parser = new PoiSheetParser();
		parser.load(getClass().getResourceAsStream("loop-rows.xlsx"));
		parser.process(new MVEL2Interpreter(), null, variables, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parser.writeTo(bos);

		//check
		Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
		//wb.write(new FileOutputStream("target/loop-rows-output.xlsx"));
		
		assertEquals("Silva", wb.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());
		assertNull(wb.getSheetAt(0).getRow(5).getCell(1));
		assertEquals(1, (int) wb.getSheetAt(0).getRow(5).getCell(2).getNumericCellValue());
		assertEquals(2, (int) wb.getSheetAt(0).getRow(6).getCell(2).getNumericCellValue());
		assertEquals(3, (int) wb.getSheetAt(0).getRow(7).getCell(2).getNumericCellValue());
		assertEquals(df.parse("10/05/2014"), wb.getSheetAt(0).getRow(5).getCell(3).getDateCellValue());
		assertEquals(df.parse("20/06/2014"), wb.getSheetAt(0).getRow(6).getCell(3).getDateCellValue());
		assertEquals(df.parse("30/07/2014"), wb.getSheetAt(0).getRow(7).getCell(3).getDateCellValue());

		assertEquals(1, (int) wb.getSheetAt(0).getRow(17).getCell(2).getNumericCellValue());
		assertEquals("FIXED 1", wb.getSheetAt(0).getRow(17).getCell(3).getStringCellValue());
		assertEquals("TEXT", wb.getSheetAt(0).getRow(18).getCell(3).getStringCellValue());
		assertEquals("FIXED 2", wb.getSheetAt(0).getRow(19).getCell(3).getStringCellValue());
		assertEquals(2, (int) wb.getSheetAt(0).getRow(20).getCell(2).getNumericCellValue());
		assertEquals("FIXED 1", wb.getSheetAt(0).getRow(20).getCell(3).getStringCellValue());
		assertEquals("TEXT", wb.getSheetAt(0).getRow(21).getCell(3).getStringCellValue());
		assertEquals("FIXED 2", wb.getSheetAt(0).getRow(22).getCell(3).getStringCellValue());
		assertEquals(3, (int) wb.getSheetAt(0).getRow(23).getCell(2).getNumericCellValue());
		assertEquals("FIXED 1", wb.getSheetAt(0).getRow(23).getCell(3).getStringCellValue());
		assertEquals("TEXT", wb.getSheetAt(0).getRow(24).getCell(3).getStringCellValue());
		assertEquals("FIXED 2", wb.getSheetAt(0).getRow(25).getCell(3).getStringCellValue());
		
	}
	
	@org.junit.Test
	public void poiParserFormat() throws Exception {
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		//model
		Map<String, Object> variables = new HashMap<String, Object>();
		List<Parcel> parcels = new ArrayList<Parcel>();
		parcels.add(new Parcel(1, df.parse("10/05/2014")));
		parcels.add(new Parcel(2, df.parse("20/06/2014")));
		parcels.add(new Parcel(3, df.parse("30/07/2014")));
		variables.put("parcels", parcels);
		variables.put("client", new User("Silva"));
		
		//generate
		SheetParser parser = new PoiSheetParser();
		parser.load(getClass().getResourceAsStream("format.xlsx"));
		parser.process(new MVEL2Interpreter(), null, variables, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parser.writeTo(bos);

		//check
		Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
		//wb.write(new FileOutputStream("target/format-output.xlsx"));
		
		assertEquals("Silva", wb.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());
		assertNull(wb.getSheetAt(0).getRow(5).getCell(1));
		
		assertEquals(1, (int) wb.getSheetAt(0).getRow(5).getCell(2).getNumericCellValue());
		assertEquals(2, (int) wb.getSheetAt(0).getRow(6).getCell(2).getNumericCellValue());
		assertEquals(3, (int) wb.getSheetAt(0).getRow(7).getCell(2).getNumericCellValue());
		
		assertEquals("0.00", wb.getSheetAt(0).getRow(5).getCell(2).getCellStyle().getDataFormatString());
		assertEquals("0.00", wb.getSheetAt(0).getRow(6).getCell(2).getCellStyle().getDataFormatString());
		assertEquals("0.00", wb.getSheetAt(0).getRow(7).getCell(2).getCellStyle().getDataFormatString());
		
		assertEquals(df.parse("10/05/2014"), wb.getSheetAt(0).getRow(5).getCell(3).getDateCellValue());
		assertEquals(df.parse("20/06/2014"), wb.getSheetAt(0).getRow(6).getCell(3).getDateCellValue());
		assertEquals(df.parse("30/07/2014"), wb.getSheetAt(0).getRow(7).getCell(3).getDateCellValue());
		
		assertEquals("dd/mm/yy", wb.getSheetAt(0).getRow(5).getCell(3).getCellStyle().getDataFormatString());
		assertEquals("dd/mm/yy", wb.getSheetAt(0).getRow(6).getCell(3).getCellStyle().getDataFormatString());
		assertEquals("dd/mm/yy", wb.getSheetAt(0).getRow(7).getCell(3).getCellStyle().getDataFormatString());
		
	}

	@org.junit.Test
	public void poiParserLoopEmpty() throws Exception {
		
		//model
		Map<String, Object> variables = new HashMap<String, Object>();
		List<Parcel> parcels = new ArrayList<Parcel>();
		variables.put("parcels", parcels);
		variables.put("client", new User("Silva"));
		
		//generate
		SheetParser parser = new PoiSheetParser();
		parser.load(getClass().getResourceAsStream("loop-rows-empty.xlsx"));
		parser.process(new MVEL2Interpreter(), null, variables, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parser.writeTo(bos);

		//check
		Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
		//wb.write(new FileOutputStream("target/loop-rows-empty-output.xlsx"));
		
		assertEquals("Silva", wb.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());
		assertEquals(6, wb.getSheetAt(0).getLastRowNum());
		assertEquals("A", wb.getSheetAt(0).getRow(5).getCell(0).getStringCellValue());
		assertEquals("B", wb.getSheetAt(0).getRow(6).getCell(0).getStringCellValue());
		
	}
	
	@org.junit.Test
	public void poiParserIfTrue() throws Exception {
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		//model
		Map<String, Object> variables = new HashMap<String, Object>();
		List<Parcel> parcels = new ArrayList<Parcel>();
		parcels.add(new Parcel(1, df.parse("10/05/2014")));
		parcels.add(new Parcel(2, df.parse("20/06/2014")));
		parcels.add(new Parcel(3, df.parse("30/07/2014")));
		variables.put("parcels", parcels);
		variables.put("mostrar", true);
		variables.put("mostrar_outra", true);
		variables.put("client", new User("Silva"));
		
		//generate
		SheetParser parser = new PoiSheetParser();
		parser.load(getClass().getResourceAsStream("if-rows.xlsx"));
		parser.process(new MVEL2Interpreter(), null, variables, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parser.writeTo(bos);

		//check
		Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
		//wb.write(new FileOutputStream("target/if-rows-output.xlsx"));
		
		assertEquals("Silva", wb.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());
		assertEquals(1, (int) wb.getSheetAt(0).getRow(5).getCell(2).getNumericCellValue());
		assertEquals(2, (int) wb.getSheetAt(0).getRow(6).getCell(2).getNumericCellValue());
		assertEquals(3, (int) wb.getSheetAt(0).getRow(7).getCell(2).getNumericCellValue());
		
		assertEquals("FIXED", wb.getSheetAt(0).getRow(8).getCell(1).getStringCellValue());
		assertEquals("VALOR", wb.getSheetAt(0).getRow(9).getCell(1).getStringCellValue());
		assertEquals("FIXED 2", wb.getSheetAt(0).getRow(10).getCell(1).getStringCellValue());
		
	}
	
	@org.junit.Test
	public void poiParserIfTrueFalse() throws Exception {
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		//model
		Map<String, Object> variables = new HashMap<String, Object>();
		List<Parcel> parcels = new ArrayList<Parcel>();
		parcels.add(new Parcel(1, df.parse("10/05/2014")));
		parcels.add(new Parcel(2, df.parse("20/06/2014")));
		parcels.add(new Parcel(3, df.parse("30/07/2014")));
		variables.put("parcels", parcels);
		variables.put("mostrar", true);
		variables.put("mostrar_outra", false);
		variables.put("client", new User("Silva"));
		
		//generate
		SheetParser parser = new PoiSheetParser();
		parser.load(getClass().getResourceAsStream("if-rows.xlsx"));
		parser.process(new MVEL2Interpreter(), null, variables, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parser.writeTo(bos);

		//check
		Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
		//wb.write(new FileOutputStream("target/if-rows-output.xlsx"));
		
		assertEquals("Silva", wb.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());
		assertEquals(1, (int) wb.getSheetAt(0).getRow(5).getCell(2).getNumericCellValue());
		assertEquals(2, (int) wb.getSheetAt(0).getRow(6).getCell(2).getNumericCellValue());
		assertEquals(3, (int) wb.getSheetAt(0).getRow(7).getCell(2).getNumericCellValue());
		
		assertEquals(9, wb.getSheetAt(0).getLastRowNum());
		
	}
	
	@org.junit.Test
	public void poiParserIfFalse() throws Exception {
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		//model
		Map<String, Object> variables = new HashMap<String, Object>();
		List<Parcel> parcels = new ArrayList<Parcel>();
		parcels.add(new Parcel(1, df.parse("10/05/2014")));
		parcels.add(new Parcel(2, df.parse("20/06/2014")));
		parcels.add(new Parcel(3, df.parse("30/07/2014")));
		variables.put("parcels", parcels);
		variables.put("mostrar", false);
		variables.put("mostrar_outra", false);
		variables.put("client", new User("Silva"));
		
		//generate
		SheetParser parser = new PoiSheetParser();
		parser.load(getClass().getResourceAsStream("if-rows.xlsx"));
		parser.process(new MVEL2Interpreter(), null, variables, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parser.writeTo(bos);

		//check
		Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
		//wb.write(new FileOutputStream("target/if-rows-output.xlsx"));
		
		assertEquals("Silva", wb.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());
		assertEquals(5, wb.getSheetAt(0).getLastRowNum());
		assertEquals("FIXED", wb.getSheetAt(0).getRow(4).getCell(1).getStringCellValue());
		
	}
	
	@org.junit.Test
	public void poiParserIfFalseTrue() throws Exception {
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		//model
		Map<String, Object> variables = new HashMap<String, Object>();
		List<Parcel> parcels = new ArrayList<Parcel>();
		parcels.add(new Parcel(1, df.parse("10/05/2014")));
		parcels.add(new Parcel(2, df.parse("20/06/2014")));
		parcels.add(new Parcel(3, df.parse("30/07/2014")));
		variables.put("parcels", parcels);
		variables.put("mostrar", false);
		variables.put("mostrar_outra", true);
		variables.put("client", new User("Silva"));
		
		//generate
		SheetParser parser = new PoiSheetParser();
		parser.load(getClass().getResourceAsStream("if-rows.xlsx"));
		parser.process(new MVEL2Interpreter(), null, variables, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		parser.writeTo(bos);

		//check
		Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
		//wb.write(new FileOutputStream("target/if-rows-output.xlsx"));
		
		assertEquals("Silva", wb.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());
		assertEquals("FIXED", wb.getSheetAt(0).getRow(4).getCell(1).getStringCellValue());
		assertEquals("VALOR", wb.getSheetAt(0).getRow(5).getCell(1).getStringCellValue());
		assertEquals("FIXED 2", wb.getSheetAt(0).getRow(6).getCell(1).getStringCellValue());
		
	}
	
}
