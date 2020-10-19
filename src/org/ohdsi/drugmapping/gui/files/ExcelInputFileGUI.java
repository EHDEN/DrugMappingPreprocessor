package org.ohdsi.drugmapping.gui.files;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.poi.ss.usermodel.Row;
import org.ohdsi.drugmapping.files.ExcelFile;
import org.ohdsi.drugmapping.files.FileColumnDefinition;
import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.utilities.DrugMappingStringUtilities;

public class ExcelInputFileGUI extends InputFileGUI {
	private static final long serialVersionUID = -8908651240263793215L;
	
	private static int LABEL_SIZE = 50;
	
	private List<JComboBox<String>> comboBoxList;

	private String sheetName = null;
	private Map<String, String> columnMapping = new HashMap<String, String>();
	
	private ExcelFile excelFile = null;
	
	
	public ExcelInputFileGUI(Component parent, FileDefinition fileDefinition) {
		super(parent, fileDefinition);
		
		for (FileColumnDefinition column : fileDefinition.getColumns()) {
			columnMapping.put(column.getColumnName(), null);
		}
	}
	
	
	public String getSheetName() {
		return sheetName;
	}
	
	
	public void setSheetName(String name) {
		sheetName = name;
	}
	
	
	public List<String> getColumns() {
		List<String> columns = new ArrayList<String>();
		for (FileColumnDefinition column : getFileDefinition().getColumns()) {
			columns.add(column.getColumnName());
		}
		return columns;
	}
	
	
	public Map<String, String> getColumnMapping() {
		return columnMapping;
	}
	
	
	public void setColumnMapping(Map<String, String> columnMapping) {
		this.columnMapping = columnMapping;
	}
	
	
	public void addColumnMapping(String column, String inputColumn) {
		columnMapping.put(column, inputColumn);
	}
	
	
	public Integer getColumnNr(String fieldName) {
		Integer columnNr = null;
		String mappedFieldName = columnMapping.get(fieldName);	
		if (mappedFieldName == null) {
			throw new RuntimeException("Field \"" + fieldName + "\" not found");
		}
		else {
			columnNr = excelFile.getColumnNr(sheetName, mappedFieldName);
		}
		return columnNr;
	}
	
	
	public boolean fileExists() {
		boolean exists = false;
		if (getFileName() != null) {
			File inputFile = new File(getFileName());
			if (inputFile.exists()) {
				exists = true;
			}
		}
		return exists;
	}
	
	
	public boolean hasNext() {
		return excelFile.hasNext(getSheetName());
	}
	
	
	public Row getNext() {
		return excelFile.getNext(getSheetName());
	}
	
	
	public String getStringValue(Row row, String fieldName) {
		String value = null;
		String mappedFieldName = columnMapping.get(fieldName);	
		if (mappedFieldName == null) {
			throw new RuntimeException("Field \"" + fieldName + "\" not found");
		}
		else {
			value = getMappedStringValue(row, mappedFieldName);
		}
		return value;
	}
	
	
	private String getMappedStringValue(Row row, String mappedFieldName) {
		String value = excelFile.getStringValue(getSheetName(), row, mappedFieldName);
		return value == null ? null : DrugMappingStringUtilities.convertToANSI(value);
	}
	
	
	public String getStringValue(Row row, Integer fieldNr) {
		String value = excelFile.getStringValue(row, fieldNr);
		return value == null ? null : DrugMappingStringUtilities.convertToANSI(value);
	}
	
	
	public Double getDoubleValue(Row row, String fieldName) {
		Double value = null;
		String mappedFieldName = columnMapping.get(fieldName);	
		if (mappedFieldName == null) {
			throw new RuntimeException("Field \"" + fieldName + "\" not found");
		}
		else {
			value = getMappedDoubleValue(row, mappedFieldName);
		}
		return value;
	}
	
	
	private Double getMappedDoubleValue(Row row, String mappedFieldName) {
		return excelFile.getDoubleValue(getSheetName(), row, mappedFieldName);
	}
	
	
	public Double getDoubleValue(Row row, Integer fieldNr) {
		return excelFile.getDoubleValue(row, fieldNr);
	}
	
	
	public Boolean getBooleanValue(Row row, String fieldName) {
		Boolean value = null;
		String mappedFieldName = columnMapping.get(fieldName);	
		if (mappedFieldName == null) {
			throw new RuntimeException("Field \"" + fieldName + "\" not found");
		}
		else {
			value = getMappedBooleanValue(row, mappedFieldName);
		}
		return value;
	}
	
	
	public Boolean getBooleanValue(Row row, Integer fieldNr) {
		return excelFile.getBooleanValue(row, fieldNr);
	}
	
	
	private Boolean getMappedBooleanValue(Row row, String mappedFieldName) {
		return excelFile.getBooleanValue(getSheetName(), row, mappedFieldName);
	}
	
	
	public boolean hasField(String fieldName) {
		return (columnMapping.get(fieldName) != null);
	}
	
	
	public String get(Row row, String fieldName) {
		String value = null;
		String mappedFieldName = columnMapping.get(fieldName);	
		if (mappedFieldName == null) {
			throw new RuntimeException("Field \"" + fieldName + "\" not found");
		}
		else {
			String cellStringValue = getMappedStringValue(row, mappedFieldName);
			if (cellStringValue == null) {
				Double cellDoubleValue = getMappedDoubleValue(row, mappedFieldName);
				if (cellDoubleValue == null) {
					Boolean cellBooleanValue = getMappedBooleanValue(row, mappedFieldName);
					if (cellBooleanValue != null) {
						value = cellBooleanValue ? "True" : "False";
					}
				}
				else {
					value = cellDoubleValue.toString();
				}
			}
			else {
				value = cellStringValue;
			}
		}
		return value;
	}
	
	
	public String get(Row row, Integer fieldNr) {
		String value = null;
		String cellStringValue = getStringValue(row, fieldNr);
		if (cellStringValue == null) {
			Double cellDoubleValue = getDoubleValue(row, fieldNr);
			if (cellDoubleValue == null) {
				Boolean cellBooleanValue = getBooleanValue(row, fieldNr);
				if (cellBooleanValue != null) {
					value = cellBooleanValue ? "True" : "False";
				}
			}
			else {
				value = cellDoubleValue.toString();
			}
		}
		else {
			value = cellStringValue;
		}
		return value;
	}
	
	
	public Integer getAsInteger(Row row, String fieldName) {
		Integer result = null;
		
		String resultString = get(row, fieldName);
		try {
			result = (int) Math.round(Double.parseDouble(resultString));
		} catch (NumberFormatException e) {
			result = null;
		}
		
		return result;
	}
	
	
	public Integer getAsInteger(Row row, Integer fieldNr) {
		Integer result = null;
		
		String resultString = get(row, fieldNr);
		try {
			result = (int) Math.round(Double.parseDouble(resultString));
		} catch (NumberFormatException e) {
			result = null;
		}
		
		return result;
	}
	
	
	public Long getAsLong(Row row, String fieldName) {
		Long result = null;
		
		String resultString = get(row, fieldName);
		try {
			result = (long) Math.round(Double.parseDouble(resultString));
		} catch (NumberFormatException e) {
			result = null;
		}
		
		return result;
	}
	
	
	public Long getAsLong(Row row, Integer fieldNr) {
		Long result = null;
		
		String resultString = get(row, fieldNr);
		try {
			result = (long) Math.round(Double.parseDouble(resultString));
		} catch (NumberFormatException e) {
			result = null;
		}
		
		return result;
	}
	
	
	public Double getAsDouble(Row row, String fieldName) {
		Double result = null;
		
		String resultString = get(row, fieldName);
		try {
			result = Double.parseDouble(resultString);
		} catch (NumberFormatException e) {
			result = null;
		}
		
		return result;
	}
	
	
	public Double getAsDouble(Row row, Integer fieldNr) {
		Double result = null;
		
		String resultString = get(row, fieldNr);
		try {
			result = Double.parseDouble(resultString);
		} catch (NumberFormatException e) {
			result = null;
		}
		
		return result;
	}
	
	
	void defineFile(InputFileGUI inputFile) {
		ExcelInputFileGUI excelInputFile = (ExcelInputFileGUI) inputFile;
		JDialog fileDialog = new JDialog();
		fileDialog.setLayout(new BorderLayout());
		fileDialog.setModal(true);
		fileDialog.setSize(500, 400);
		fileDialog.setMinimumSize(new Dimension(500,  400));
		MainFrame.setIcon(fileDialog);
		fileDialog.setLocationRelativeTo(null);
		fileDialog.setTitle("Input Sheet Definition");
		fileDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// File section
		JPanel fileSectionPanel = new JPanel(new BorderLayout());
		fileSectionPanel.setBorder(BorderFactory.createTitledBorder(getLabelText()));
		
		// File description
		JPanel fileDescriptionPanel = new JPanel(new BorderLayout());
		fileDescriptionPanel.setBorder(BorderFactory.createEmptyBorder());
		JTextArea fileDescriptionField = new JTextArea();
		fileDescriptionField.setEditable(false);
		fileDescriptionField.setBackground(fileDescriptionPanel.getBackground());
		String description = "";
		for (String line : getFileDefinition().getDescription()) {
			if (!description.equals("")) {
				description += "\n";
			}
			description += line;
		}
		fileDescriptionField.setText(description);
		fileDescriptionPanel.add(fileDescriptionField, BorderLayout.NORTH);
		
		// File selection
		JPanel fileSelectionPanel = new JPanel();
		fileSelectionPanel.setLayout(new GridLayout(0, 1));
		
		// File chooser
		JPanel fileChooserPanel = new JPanel();
		fileChooserPanel.setLayout(new BoxLayout(fileChooserPanel, BoxLayout.X_AXIS));
		fileChooserPanel.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel fileChooserLabelPanel = new JPanel(new BorderLayout());
		fileChooserLabelPanel.setMinimumSize(new Dimension(LABEL_SIZE, fileChooserLabelPanel.getHeight()));
		fileChooserLabelPanel.setPreferredSize(new Dimension(LABEL_SIZE, fileChooserLabelPanel.getHeight()));
		fileChooserLabelPanel.add(new JLabel("File:"), BorderLayout.CENTER);
		
		JTextField fileField = new JTextField(excelInputFile.getFileName());
		fileField.setEditable(false);
		
		JButton fileButton = new JButton("Browse");

		fileChooserPanel.add(fileChooserLabelPanel);
		fileChooserPanel.add(fileField);
		fileChooserPanel.add(new JLabel("  "));
		fileChooserPanel.add(fileButton);
		
		// Sheet chooser
		JPanel sheetChooserPanel = new JPanel();
		sheetChooserPanel.setLayout(new BoxLayout(sheetChooserPanel, BoxLayout.X_AXIS));
		sheetChooserPanel.setBorder(BorderFactory.createEmptyBorder());
		JPanel sheetChooserLabelPanel = new JPanel(new BorderLayout());
		sheetChooserLabelPanel.setMinimumSize(new Dimension(LABEL_SIZE, sheetChooserLabelPanel.getHeight()));
		sheetChooserLabelPanel.setPreferredSize(new Dimension(LABEL_SIZE, sheetChooserLabelPanel.getHeight()));
		sheetChooserLabelPanel.add(new JLabel("Sheet:"), BorderLayout.CENTER);
		
		JComboBox<String> sheetChooserComboBox = new JComboBox<String>(new String[]{});
		sheetChooserComboBox.setMinimumSize(new Dimension(450 - LABEL_SIZE, sheetChooserComboBox.getHeight()));
		sheetChooserComboBox.setPreferredSize(new Dimension(450 - LABEL_SIZE, sheetChooserComboBox.getHeight()));
		
		sheetChooserPanel.add(sheetChooserLabelPanel);
		sheetChooserPanel.add(sheetChooserComboBox);
				
		fileSelectionPanel.add(fileChooserPanel);
		fileSelectionPanel.add(sheetChooserPanel);
		
		fileSectionPanel.add(fileDescriptionPanel, BorderLayout.NORTH);
		fileSectionPanel.add(fileSelectionPanel, BorderLayout.CENTER);
				
		// Mapping section
		JPanel mappingSectionPanel = new JPanel(new BorderLayout());
		
		// Column mapping section
		mappingSectionPanel.setBorder(BorderFactory.createTitledBorder("Column Mapping"));
		
		JPanel mappingScrollPanel = new JPanel(new BorderLayout());
		mappingScrollPanel.setBorder(BorderFactory.createEmptyBorder());
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().add(mappingScrollPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		mappingSectionPanel.add(scrollPane, BorderLayout.CENTER);
		
		boolean first = true;
		JPanel lastPanel = mappingScrollPanel; 
		comboBoxList = new ArrayList<JComboBox<String>>();
		for (FileColumnDefinition column : getFileDefinition().getColumns()) {
			if (!first) {
				JPanel newPanel = new JPanel(new BorderLayout());
				lastPanel.add(newPanel, BorderLayout.CENTER);
				lastPanel = newPanel;
			}
			JPanel columnPanel = new JPanel();
			columnPanel.setLayout(new GridLayout(0, 2));
			JComboBox<String> comboBox = new JComboBox<String>(new String[]{});
			comboBoxList.add(comboBox);
			JLabel columnLabel = new JLabel(column.getColumnName());
			String columnDescription = "";
			for (String line : column.getDescription()) {
				if (columnDescription.equals("")) {
					columnDescription += "<html>";
				}
				else {
					columnDescription += "<br>";
				}
				columnDescription += line;
			}
			columnDescription += "</html>";
			columnLabel.setToolTipText(columnDescription);
			columnPanel.add(columnLabel);
			columnPanel.add(comboBox);
			
			lastPanel.add(columnPanel, BorderLayout.NORTH);
			
			first = false;
		}
		
		// Button section
		JPanel buttonSectionPanel = new JPanel(new BorderLayout());
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton okButton = new JButton("    OK    ");
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		buttonSectionPanel.add(buttonPanel, BorderLayout.WEST); 
		
		// Browse action
		fileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectFile(fileDialog, fileField)) {
					updateSheetsAndColumns(fileField.getText(), sheetChooserComboBox, null);
				}
			}
		});
		
		sheetChooserComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!sheetChooserComboBox.getSelectedItem().toString().equals("")) {
					updateColumns(sheetChooserComboBox.getSelectedItem().toString());
				}
			}
		});

		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (saveFileSettings(excelInputFile, fileField.getText(), sheetChooserComboBox.getSelectedItem().toString(), comboBoxList)) {
					fileDialog.dispose();
				}				
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fileDialog.dispose();				
			}
		});

		fileDialog.add(fileSectionPanel, BorderLayout.NORTH);
		fileDialog.add(mappingSectionPanel, BorderLayout.CENTER);
		fileDialog.add(buttonSectionPanel, BorderLayout.SOUTH);
		
		updateSheetsAndColumns(getFileName(), sheetChooserComboBox, getSheetName());
		
		fileDialog.setVisible(true);
	}
	
	
	private boolean saveFileSettings(ExcelInputFileGUI inputFile, String fileName, String sheetName, List<JComboBox<String>> comboBoxList) {
		boolean saveOK = false;
		boolean columnMappingComplete = true;
		for (int columnNr = 0; columnNr < inputFile.getColumns().size(); columnNr++) {
			if (((String) comboBoxList.get(columnNr).getSelectedItem()).equals("")) {
				columnMappingComplete = false;
				break;
			}
		}
		if (
				(!fileName.equals("")) &&
				(!sheetName.equals("")) &&
				columnMappingComplete
			) {
			inputFile.setFileName(fileName);
			inputFile.setSheetName(sheetName);
			Map<String, String> columnMapping = new HashMap<String, String>();
			int columnNr = 0;
			for (String column : inputFile.getColumns()) {
				String mapping = (String) comboBoxList.get(columnNr).getSelectedItem();
				if (mapping.equals("")) {
					mapping = null;
				}
				columnMapping.put(column, mapping);
				columnNr++;
			}
			inputFile.setColumnMapping(columnMapping);
			saveOK = true;
		}
		else {
			JOptionPane.showMessageDialog(null, "Settings are not complete!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return saveOK;
	}
	
	
	private void updateSheetsAndColumns(String fileName, JComboBox<String> sheetCombobox, String currentSheetName) {
		if (fileName != null) {
			excelFile = new ExcelFile(fileName);
			if (excelFile.open()) {
				Set<String> sheetNames = excelFile.getSheetNames();
				if (sheetNames != null) {
					List<String> sortedSheetNames = new ArrayList<String>();
					sortedSheetNames.addAll(sheetNames);
					Collections.sort(sortedSheetNames);
					String[] sortedSheetNamesArray = new String[sortedSheetNames.size()];
					for (int sheetNr = 0; sheetNr < sortedSheetNames.size(); sheetNr++) {
						sortedSheetNamesArray[sheetNr] = sortedSheetNames.get(sheetNr);
					}
					sheetCombobox.setModel(new DefaultComboBoxModel<String>(sortedSheetNamesArray));
					sheetCombobox.setSelectedIndex(currentSheetName == null ? 0 : sortedSheetNames.indexOf(currentSheetName));
					updateColumns(sheetCombobox.getSelectedItem().toString());
				}
				else {
					sheetCombobox.setModel(new DefaultComboBoxModel<String>(new String[] {}));
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "Could not open the selected file", "Getting file information", JOptionPane.ERROR_MESSAGE);
				excelFile = null;
			}
		}
	}
	
	
	private void updateColumns(String sheetName) {
		if (excelFile != null) {
			List<String> columnList = excelFile.getColumnNames(sheetName);
			String[] columns = new String[columnList.size()];
			for (int columnNr = 0; columnNr < columnList.size(); columnNr++) {
				columns[columnNr] = columnList.get(columnNr);
			}
			int columnNr = 0;
			for (JComboBox<String> comboBox : comboBoxList) {
				String columnName = getFileDefinition().getColumns()[columnNr].getColumnName();
				String mappedColumn = columnMapping.get(columnName);
				int itemNrToSelect = 0;
				
				comboBox.removeAllItems();
				comboBox.addItem("");
				int itemNr = 1;
				for (String column : columns) {
					comboBox.addItem(column);
					if ((mappedColumn != null) && mappedColumn.equals(column)) {
						itemNrToSelect = itemNr;
					}
					itemNr++;
				}
				comboBox.setSelectedIndex(itemNrToSelect);
				if (itemNrToSelect == 0) {
					columnMapping.remove(columnName);
				}
				columnNr++;
			}
		}
	}
	

	@Override
	public List<String> getSettings() {
		List<String> settings = new ArrayList<String>();

		settings.add("#");
		settings.add("# " + getLabelText());
		settings.add("#");
		settings.add("");
		settings.add(getLabelText() + ".filename=" + getFileName());
		settings.add(getLabelText() + ".selected=" + (isSelected() ? "Yes" : "No"));
		settings.add(getLabelText() + ".sheetName=" + getSheetName());
		for (String column : getColumns()) {
			settings.add(getLabelText() + ".column." + column + "=" + (getColumnMapping().get(column) == null ? "" : getColumnMapping().get(column)));
		}
		
		return settings;
	}
	

	@Override
	public void putSettings(List<String> settings) {
		for (String setting : settings) {
			if ((!setting.trim().equals("")) && (!setting.substring(0, 1).equals("#"))) {
				int equalSignIndex = setting.indexOf("=");
				String settingPath = setting.substring(0, equalSignIndex);
				String value = setting.substring(equalSignIndex + 1).trim();
				String[] settingPathSplit = settingPath.split("\\.");
				if ((settingPathSplit.length > 0) && (settingPathSplit[0].equals(getLabelText()))) {
					if ((settingPathSplit.length == 3) && (settingPathSplit[1].equals("column"))) { // Column mapping
						if (getColumns().contains(settingPathSplit[2])) {
							getColumnMapping().put(settingPathSplit[2], value);
						}
					}
					else if (settingPathSplit.length == 2) {
						if (settingPathSplit[1].equals("filename")) setFileName(value);
						else if (settingPathSplit[1].equals("selected")) setSelected(value.toUpperCase().equals("YES"));
						else if (settingPathSplit[1].equals("sheetName")) setSheetName(value);
						else {
							// Unknown setting
						}
					}
				}
			}
		}
	}


	@Override
	List<FileFilter> getFileFilters() {
		List<FileFilter> fileFilters = new ArrayList<FileFilter>();
		fileFilters.add(new FileFilter() {

	        @Override
	        public boolean accept(File f) {
	            return f.getName().endsWith(".xlsx");
	        }

	        @Override
	        public String getDescription() {
	            return "Excel Workbook";
	        }

	    });
		fileFilters.add(new FileFilter() {

	        @Override
	        public boolean accept(File f) {
	            return f.getName().endsWith(".xls");
	        }

	        @Override
	        public String getDescription() {
	            return "Excel 97-2003 Workbook";
	        }

	    });
		return fileFilters;
	}
	
	
	@Override
	public boolean openFileForReading() {
		return openFileForReading(false);
	}
	
	
	@Override
	public boolean openFileForReading(boolean suppressError) {
		boolean result = false;
		
		if (getFileName() != null) {
			File inputFile = new File(getFileName());
			if (inputFile.exists() && inputFile.canRead()) {
				excelFile = new ExcelFile(getFileName());
				if (excelFile.open()) {
					if (excelFile.getSheet(getSheetName(), true)) {
						result = true;
					}
					else {
						if (!suppressError) {
							JOptionPane.showMessageDialog(null, "Cannot find sheet '" + getSheetName() + "' in file '" + getFileName() + "'!", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				else {
					if (!suppressError) {
						JOptionPane.showMessageDialog(null, "Cannot open workbook in file '" + getFileName() + "'!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else {
				if (!suppressError) {
					JOptionPane.showMessageDialog(null, "Cannot read file '" + getFileName() + "'!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		return result;
	}


	@Override
	public void logFileSettings() {
		if (getFileName() != null) {
			System.out.println("Input File: " + getFileDefinition().getFileName());
			System.out.println("  Filename: " + getFileName());
			System.out.println("  File type: " + FileDefinition.getFileTypeName(getFileType()));
			System.out.println("  Sheet name: '" + getSheetName() + "'");
			System.out.println("  Fields:");
			List<String> columns = getColumns();
			Map<String, String> columnMapping = getColumnMapping();
			for (String column : columns) {
				System.out.println("    " + column + " -> " + columnMapping.get(column));
			}
			System.out.println();
		}
	}
	
	
	

}
