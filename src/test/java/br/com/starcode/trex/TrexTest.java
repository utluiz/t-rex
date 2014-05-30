package br.com.starcode.trex;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import br.com.starcode.trex.spreadsheet.AdditionalModifications;

public class TrexTest {

	@Test
	public void i18NPort() throws IOException, InvalidFormatException {
	    
	    //resource
	    ResourceBundle r = ResourceBundle.getBundle("br.com.starcode.trex.msg", new Locale("pt", "BR"));
	    
	    //model
	    List<String> list = new ArrayList<String>();
	    list.add("0.#");
	    list.add("0.0");
	    list.add("00.00");
	    Map<String, Object> model = new HashMap<String, Object>();
	    model.put("name", "Luiz");
	    model.put("age", 30);
	    
	    //generates
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    Trex
	        .template().load(getClass().getResourceAsStream("i18n.xlsx"))
	        .model().add("i18n", r).add("items", list).context(model)
	        .output().to(bos);
	    
	    //checks
	    Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
	    wb.write(new FileOutputStream("target/i18n-output.xlsx"));
	    
        assertEquals("Nome:", wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
        assertEquals("Luiz", wb.getSheetAt(0).getRow(0).getCell(1).getStringCellValue());
        assertEquals("Idade:", wb.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
        assertEquals(30, (int) wb.getSheetAt(0).getRow(1).getCell(1).getNumericCellValue());
        assertEquals("Qualquer Valor", wb.getSheetAt(0).getRow(0).getCell(2).getStringCellValue());
        
        assertEquals(1, (int) wb.getSheetAt(0).getRow(3).getCell(0).getNumericCellValue());
        assertEquals(2, (int) wb.getSheetAt(0).getRow(4).getCell(0).getNumericCellValue());
        assertEquals(3, (int) wb.getSheetAt(0).getRow(5).getCell(0).getNumericCellValue());
        assertEquals(1, (int) wb.getSheetAt(0).getRow(3).getCell(1).getNumericCellValue());
        assertEquals(2, (int) wb.getSheetAt(0).getRow(4).getCell(1).getNumericCellValue());
        assertEquals(3, (int) wb.getSheetAt(0).getRow(5).getCell(1).getNumericCellValue());
        
//        DataFormatter f = new DataFormatter();
//        assertEquals("1", f.formatCellValue(wb.getSheetAt(0).getRow(3).getCell(0)));
//        assertEquals("2.0", f.formatCellValue(wb.getSheetAt(0).getRow(3).getCell(0)));
//        assertEquals("03.00", f.formatCellValue(wb.getSheetAt(0).getRow(3).getCell(0)));
        assertEquals("0.#", wb.getSheetAt(0).getRow(3).getCell(1).getCellStyle().getDataFormatString());
        assertEquals("0.0", wb.getSheetAt(0).getRow(4).getCell(1).getCellStyle().getDataFormatString());
        assertEquals("00.00", wb.getSheetAt(0).getRow(5).getCell(1).getCellStyle().getDataFormatString());

	}
	
	@Test
    public void i18NEng() throws IOException, InvalidFormatException {
        
        //resource
        ResourceBundle r = ResourceBundle.getBundle("br.com.starcode.trex.msg", new Locale("en", "US"));
        
        //model
        List<String> list = new ArrayList<String>();
        list.add("0.#");
        list.add("0.0");
        list.add("00.00");
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Luiz");
        model.put("age", 30);
        
        //generates
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Trex
            .template().load(getClass().getResourceAsStream("i18n.xlsx"))
            .model().add("i18n", r).add("items", list).context(model)
            .output().to(bos);
        
        //checks
        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
        wb.write(new FileOutputStream("target/i18n-output.xlsx"));
        
        assertEquals("Name:", wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
        assertEquals("Luiz", wb.getSheetAt(0).getRow(0).getCell(1).getStringCellValue());
        assertEquals("Age:", wb.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
        assertEquals(30, (int) wb.getSheetAt(0).getRow(1).getCell(1).getNumericCellValue());
        
        assertEquals("Any Value", wb.getSheetAt(0).getRow(0).getCell(2).getStringCellValue());
        
        assertEquals(1, (int) wb.getSheetAt(0).getRow(3).getCell(0).getNumericCellValue());
        assertEquals(2, (int) wb.getSheetAt(0).getRow(4).getCell(0).getNumericCellValue());
        assertEquals(3, (int) wb.getSheetAt(0).getRow(5).getCell(0).getNumericCellValue());
        assertEquals(1, (int) wb.getSheetAt(0).getRow(3).getCell(1).getNumericCellValue());
        assertEquals(2, (int) wb.getSheetAt(0).getRow(4).getCell(1).getNumericCellValue());
        assertEquals(3, (int) wb.getSheetAt(0).getRow(5).getCell(1).getNumericCellValue());
        
        assertEquals("0.#", wb.getSheetAt(0).getRow(3).getCell(1).getCellStyle().getDataFormatString());
        assertEquals("0.0", wb.getSheetAt(0).getRow(4).getCell(1).getCellStyle().getDataFormatString());
        assertEquals("00.00", wb.getSheetAt(0).getRow(5).getCell(1).getCellStyle().getDataFormatString());

    }

	@Test
	public void additionalModifications() throws IOException, InvalidFormatException {
	    //resource
        ResourceBundle r = ResourceBundle.getBundle("br.com.starcode.trex.msg", new Locale("en", "US"));
        
        //model
        List<String> list = new ArrayList<String>();
        list.add("0.#");
        list.add("0.0");
        list.add("00.00");
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", "Luiz");
        model.put("age", 30);
        
        final List<String> checkList = new ArrayList<String>();
        
        //generates
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Trex
            .configure().additionalModifications(new AdditionalModifications() {
                
                public void beforeProcess(Workbook wb) {
                    checkList.add(wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
                }
                
                public void afterProcess(Workbook wb) {
                    checkList.add(wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
                }
            })
            .template().load(getClass().getResourceAsStream("i18n.xlsx"))
            .model().add("i18n", r).add("items", list).context(model)
            .output().to(bos);
        
        //checks
        Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(bos.toByteArray()));
        //wb.write(new FileOutputStream("target/i18n-output.xlsx"));
        
        assertEquals("@{i18n.name}", checkList.get(0));
        assertEquals("Name:", checkList.get(1));
        
	}

}
