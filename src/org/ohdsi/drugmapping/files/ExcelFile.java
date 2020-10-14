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
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



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
	
	
	
	//====================================================================
	//
	// ExcelFileSuperType
	//
	//====================================================================
	
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
	
	
	
	//====================================================================
	//
	// ExcelFileXLS
	//
	//====================================================================
	
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
	
	
	
	//====================================================================
	//
	// ExcelFileXLSX
	//
	//====================================================================
	
	public class ExcelFileXLSX extends ExcelFileSuperType {
		private XSSFWorkbook workBook = null;
		private Map<String, XSSFSheet> sheetMap = new HashMap<String, XSSFSheet>();
		private int currentRowNr = 0;
		

		public ExcelFileXLSX(String fileName) {
			super(fileName);
		}

		@Override
		boolean open() {
			boolean result = false;
			
			File file = new File(fileName);
			if (file.canRead()) {
				try {
					fileStream = new FileInputStream(file);
					workBook = new XSSFWorkbook(fileStream);
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
			
			XSSFSheet sheet = workBook.getSheet(sheetName);
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
			XSSFSheet sheet = sheetMap.get(sheetName);
			if (sheet != null) {
				result = (sheet.getRow(currentRowNr) != null);
			}
			return result;
		}

		@Override
		Row getNext(String sheetName) {
			Row result = null;
			XSSFSheet sheet = sheetMap.get(sheetName);
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
			if ((cell != null) && ((cell.getCellType() == CellType.STRING) || (cell.getCellType() == CellType.BLANK) || (cell.getCellType() == CellType._NONE))) {
				value = cell.getStringCellValue();
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
			if ((cell != null) && (cell.getCellType() == CellType.NUMERIC)) {
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
			if ((cell != null) && (cell.getCellType() == CellType.BOOLEAN)) {
				value = cell.getBooleanCellValue();
			}
			return value;
		}
	}
}
