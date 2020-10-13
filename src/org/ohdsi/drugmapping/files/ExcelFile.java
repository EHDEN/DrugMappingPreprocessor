package org.ohdsi.drugmapping.files;

import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Row;

public class ExcelFile {
	ExcelFileSuperType excelFile = null;
	
	
	public ExcelFile(String fileName) {
		if (fileName.endsWith(".xls")) {
			excelFile = new ExcelFileXLS(fileName);
		}
		else if (fileName.endsWith(".xlsx")) {
			excelFile = new ExcelFileXLSX(fileName);
		}
		else {
			JOptionPane.showMessageDialog(null, "Unknown file extension \"" + fileName.substring(fileName.lastIndexOf(".")) + "\"!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	public boolean open() {
		return excelFile != null ? excelFile.open() : false;
	}
	
	
	public boolean close() {
		return excelFile != null ? excelFile.close() : false;
	}
	
	
	public boolean getSheet(String sheetName, boolean hasHeader) {
		return excelFile != null ? excelFile.getSheet(sheetName, hasHeader) : false;
	}
	

	public boolean hasNext(String sheetName) {
		return excelFile != null ? excelFile.hasNext(sheetName) : false;
	}
	
	
	public Row getNext(String sheetName) {
		return excelFile != null ? excelFile.getNext(sheetName) : null;
	}
	
	
	public String getStringValue(String sheetName, Row row, String columnName) {
		return excelFile != null ? excelFile.getStringValue(sheetName, row, columnName) : null;
	}
	
	
	public String getStringValue(Row row, Integer columnNr) {
		return excelFile != null ? excelFile.getStringValue(row, columnNr) : null;
	}
	
	
	public Double getDoubleValue(String sheetName, Row row, String columnName) {
		return excelFile != null ? excelFile.getDoubleValue(sheetName, row, columnName) : null;
	}
	
	
	public Double getDoubleValue(Row row, Integer columnNr) {
		return excelFile != null ? excelFile.getDoubleValue(row, columnNr) : null;
	}
	
	
	public Boolean getBooleanValue(String sheetName, Row row, String columnName) {
		return excelFile != null ? excelFile.getBooleanValue(sheetName, row, columnName) : null;
	}
	
	
	public Boolean getBooleanValue(Row row, Integer columnNr) {
		return excelFile != null ? excelFile.getBooleanValue(row, columnNr) : null;
	}
	
	
	public List<String> getColumnNames(String sheetName) {
		return excelFile != null ? excelFile.getColumnNames(sheetName) : null;
	}
	
	
	public Integer getColumnNr(String sheetName, String columnName) {
		return excelFile != null ? excelFile.getColumnNr(sheetName, columnName) : null;
	}
}
