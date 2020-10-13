package org.ohdsi.drugmapping.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

public class ExcelFileXLS extends ExcelFileSuperType {
	private HSSFWorkbook workBook = null;
	private FormulaEvaluator formulaEvaluator = null;
	private Map<String, HSSFSheet> sheetMap = new HashMap<String, HSSFSheet>();
	private int currentRowNr = 0;
	

	public ExcelFileXLS(String fileName) {
		super(fileName);
	}

	@Override
	boolean open() {
		boolean result = false;
		
		File file = new File(fileName);
		if (file.canRead()) {
			try {
				fileStream = new FileInputStream(file);
				workBook = new HSSFWorkbook(fileStream);
				formulaEvaluator = workBook.getCreationHelper().createFormulaEvaluator();
				sheetColumnNrs = new HashMap<String, Map<String, Integer>>();
				result = true;
			} 
			catch (FileNotFoundException exception) {
			} 
			catch (IOException exception) {
			}
			if (!result) {
				JOptionPane.showMessageDialog(null, "Cannot open Excel file \"" + fileName + "\"!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			JOptionPane.showMessageDialog(null, "Cannot read Excel file \"" + fileName + "\"!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return result;
	}

	@Override
	Set<String> getSheetNames() {
		return sheetMap.keySet();
	}

	@Override
	boolean getSheet(String sheetName, boolean hasHeader) {
		boolean result = false;
		
		HSSFSheet sheet = workBook.getSheet(sheetName);
		if (sheet != null) {
			sheetMap.put(sheetName, sheet);
			if (hasHeader) {
				Row header = sheet.getRow(0);
				if (header != null) {
					Map<String, Integer> columnNrs = sheetColumnNrs.get(sheetName);
					if (columnNrs == null) {
						columnNrs = new HashMap<String, Integer>();
						sheetColumnNrs.put(sheetName, columnNrs);
					}
					int columnNr = 0;
					for (Iterator<Cell> cellIterator = header.cellIterator(); cellIterator.hasNext();) {
						Cell cell = cellIterator.next();
						columnNrs.put(cell.getStringCellValue(), columnNr);
						columnNr++;
					}
					currentRowNr = 1;
					result = true;
				}
			}
			else {
				currentRowNr = 0;
				result = true;
			}
		}
		
		return result;
	}

	@Override
	boolean hasNext(String sheetName) {
		boolean result = false;
		HSSFSheet sheet = sheetMap.get(sheetName);
		if (sheet != null) {
			result = (sheet.getRow(currentRowNr) != null);
		}
		return result;
	}

	@Override
	Row getNext(String sheetName) {
		Row result = null;
		HSSFSheet sheet = sheetMap.get(sheetName);
		if (sheet != null) {
			result = sheet.getRow(currentRowNr);
			if (result != null) {
				currentRowNr++;
			}
		}
		return result;
	}

	@Override
	String getStringValue(String sheetName, Row row, String columnName) {
		String value = null;
		if ((sheetMap.get(sheetName) != null) && (row != null) && (columnName != null) && (!columnName.equals(""))) {
			Map<String, Integer> columnNrs = sheetColumnNrs.get(sheetName);
			if (columnNrs != null) {
				Integer columnNr = columnNrs.get(columnName);
				if (columnNr != null) {
					value = getStringValue(row, columnNr);
				}
			}
		}
		return value;
	}

	@Override
	String getStringValue(Row row, Integer columnNr) {
		String value = null;
		Cell cell = getCell(row, columnNr);
		if (cell != null) {
			CellType cellType = formulaEvaluator.evaluateInCell(cell).getCellType();
			if (((cellType == CellType.STRING) || (cellType == CellType.BLANK) || (cellType == CellType._NONE))) {
				value = cell.getStringCellValue();
			}
		}
		return value;
	}

	@Override
	Double getDoubleValue(String sheetName, Row row, String columnName) {
		Double value = null;
		if ((sheetMap.get(sheetName) != null) && (row != null) && (columnName != null) && (!columnName.equals(""))) {
			Map<String, Integer> columnNrs = sheetColumnNrs.get(sheetName);
			if (columnNrs != null) {
				Integer columnNr = columnNrs.get(columnName);
				if (columnNr != null) {
					value = getDoubleValue(row, columnNr);
				}
			}
		}
		return value;
	}

	@Override
	Double getDoubleValue(Row row, Integer columnNr) {
		Double value = null;
		Cell cell = getCell(row, columnNr);
		if ((cell != null) && (formulaEvaluator.evaluateInCell(cell).getCellType() == CellType.NUMERIC)) {
			value = cell.getNumericCellValue();
		}
		return value;
	}

	@Override
	Boolean getBooleanValue(String sheetName, Row row, String columnName) {
		Boolean value = null;
		if ((sheetMap.get(sheetName) != null) && (row != null) && (columnName != null) && (!columnName.equals(""))) {
			Map<String, Integer> columnNrs = sheetColumnNrs.get(sheetName);
			if (columnNrs != null) {
				Integer columnNr = columnNrs.get(columnName);
				if (columnNr != null) {
					value = getBooleanValue(row, columnNr);
				}
			}
		}
		return value;
	}

	@Override
	Boolean getBooleanValue(Row row, Integer columnNr) {
		Boolean value = null;
		Cell cell = getCell(row, columnNr);
		if ((cell != null) && (formulaEvaluator.evaluateInCell(cell).getCellType() == CellType.BOOLEAN)) {
			value = cell.getBooleanCellValue();
		}
		return value;
	}
}