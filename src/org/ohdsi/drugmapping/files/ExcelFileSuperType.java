package org.ohdsi.drugmapping.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public abstract class ExcelFileSuperType {
	protected String fileName;;
	
	protected FileInputStream fileStream;;
	protected Map<String, Map<String, Integer>> sheetColumnNrs;;

	
	public ExcelFileSuperType(String fileName) {
		this.fileName = fileName;
	}
	
	
	abstract boolean open();
	
	public boolean close() {
		boolean result = false;
		try {
			fileStream.close();
			result = true;
		}
		catch (IOException e) {
			result = false;
		}
		return result;
	}
	
	abstract Set<String> getSheetNames();
	
	abstract boolean getSheet(String sheetName, boolean hasHeader);
	
	abstract boolean hasNext(String sheetName);
	
	abstract Row getNext(String sheetName);
	
	abstract String getStringValue(String sheetName, Row row, String columnName);
	
	abstract String getStringValue(Row row, Integer columnNr);
	
	abstract Double getDoubleValue(String sheetName, Row row, String columnName);
	
	abstract Double getDoubleValue(Row row, Integer columnNr);
	
	abstract Boolean getBooleanValue(String sheetName, Row row, String columnName);
	
	abstract Boolean getBooleanValue(Row row, Integer columnNr);
	
	public List<String> getColumnNames(String sheetName) {
		List<String> columNames = null;
		Map<String, Integer> columnNrs = sheetColumnNrs.get(sheetName);
		if ((columnNrs != null) && (columnNrs.keySet().size() > 0)) {
			columNames = new ArrayList<String>();
			columNames.addAll(columnNrs.keySet());
			for (String columnName : columnNrs.keySet()) {
				columNames.set(columnNrs.get(columnName), columnName);
			}
		}
		return columNames;
	}
	
	
	public Integer getColumnNr(String sheetName, String columnName) {
		Integer columnNr = null;

		Map<String, Integer> columnNrs = sheetColumnNrs.get(sheetName);
		if (columnNrs != null) {
			columnNr = columnNrs.get(columnName);
		}
		
		return columnNr;
	}
	

	public Cell getCell(Row row, Integer columnNr) {
		Cell cell = null;
		if ((row != null) && (columnNr != null) && (columnNr >= 0)) {
			cell = row.getCell(columnNr);
		}
		return cell;
	}

}
