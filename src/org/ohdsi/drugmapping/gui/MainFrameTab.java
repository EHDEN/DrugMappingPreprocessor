package org.ohdsi.drugmapping.gui;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.GeneralSettings;
import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.gui.files.DelimitedInputFileGUI;
import org.ohdsi.drugmapping.gui.files.Folder;
import org.ohdsi.drugmapping.gui.files.InputFileGUI;

public class MainFrameTab extends JPanel {
	private static final long serialVersionUID = -2611669075696826114L;
	
	protected MainFrame mainFrame;
	protected Console console = null;
	protected String logFileName = null;
	protected Folder outputFolder = null; 
	protected GeneralSettings settings = null;
	protected JButton startButton = null;

	public MainFrameTab() {
		super();
	}
	
	
	public JScrollPane createConsolePanel() {
		JTextArea consoleArea = new JTextArea();
		consoleArea.setToolTipText("General progress information");
		consoleArea.setEditable(false);
		console = new Console();
		console.setTextArea(consoleArea);
		if (!(System.getProperty("runInEclipse") == null ? false : System.getProperty("runInEclipse").equalsIgnoreCase("true"))) {
			System.setOut(new PrintStream(console));
			System.setErr(new PrintStream(console));
		}
		JScrollPane consoleScrollPane = new JScrollPane(consoleArea);
		consoleScrollPane.setBorder(BorderFactory.createTitledBorder("Console"));
		consoleScrollPane.setAutoscrolls(true);
		ObjectExchange.console = console;
		return consoleScrollPane;
	}
	
	
	public void checkReadyToStart() {
		if (startButton != null) {
			boolean readyToStart = true;
			if (settings != null) {
				for (Setting setting : settings.getSettings()) {
					readyToStart = readyToStart && setting.isSetCorrectly();
				}
			}
			startButton.setEnabled(readyToStart);
		}
	}

	
	public void initialize() {
		if (settings != null) {
			for (Setting setting : settings.getSettings()) {
				setting.initialize();
			}
		}
	}
	
	
	public boolean hasFileSettings() {
		// To be overruled at the subclass
		return false;
	}

	
	public void loadFileSettingsFile(List<String> fileSettings) {
		// To be overruled at the subclass
	}

	
	public void saveFileSettingsFile() {
		// To be overruled at the subclass
	}
	
	
	public boolean hasGeneralSettings() {
		// To be overruled at the subclass
		return false;
	}

	
	public void loadGeneralSettingsFile(List<String> fileSettings) {
		// To be overruled at the subclass
	}

	
	public void saveGeneralSettingsFile() {
		// To be overruled at the subclass
	}
	
	
	public List<String> readSettingsFromFile(String settingsFileName, boolean mandatory) {
		// To be overruled at the subclass
		return null;
	}
	
	
	public String getOutputFileName() {
		// To be overruled at the subclass
		return null;
	}
	

	public void run(String outputFileName) {
		// To be overruled at the subclass
	}
	
	
	public String getFile(FileNameExtensionFilter extensionsFilter, boolean fileShouldExist) {
		String fileName = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(DrugMappingPreprocessor.getCurrentPath() == null ? (DrugMappingPreprocessor.getBasePath() == null ? System.getProperty("user.dir") : DrugMappingPreprocessor.getBasePath()) : DrugMappingPreprocessor.getCurrentPath()));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (extensionsFilter != null) {
			fileChooser.setFileFilter(extensionsFilter);
		}
		int returnVal = fileShouldExist ? fileChooser.showOpenDialog(mainFrame.getFrame()) : fileChooser.showDialog(mainFrame.getFrame(), "Save");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fileName = fileChooser.getSelectedFile().getAbsolutePath();
			DrugMappingPreprocessor.setCurrentPath(fileChooser.getSelectedFile().getAbsolutePath().substring(0, fileChooser.getSelectedFile().getAbsolutePath().lastIndexOf(File.separator)));
		}
		return fileName;
	}
	
	
	public Folder getOutputFolder() {
		return outputFolder;
	}
	
	
	public void clearConsole() {
		console.clear();
	}
	
	
	public void setLogFile(String logFile) {
		clearConsole();
		if (logFile != null) {
			console.setDebugFile(logFile);
		}
	}
	
	
	public void closeLogFile() {
		console.closeDebugFile();
	}
	
	
	public void logFileSettings(String fileId, InputFileGUI file) {
		if (file.getFileName() != null) {
			System.out.println("Input File: " + fileId);
			System.out.println("  Filename: " + file.getFileName());
			System.out.println("  File type: " + FileDefinition.getFileTypeName(file.getFileType()));
			if (file.getFileType() == FileDefinition.DELIMITED_FILE) {
				System.out.println("  Field delimiter: '" + file.getFieldDelimiter() + "'");
				System.out.println("  Text qualifier: '" + file.getTextQualifier() + "'");
			}
			if ((file.getFileType() == FileDefinition.DELIMITED_FILE) || (file.getFileType() == FileDefinition.EXCEL_FILE)) {
				System.out.println("  Fields:");
				List<String> columns = file.getColumns();
				Map<String, String> columnMapping = file.getColumnMapping();
				for (String column : columns) {
					System.out.println("    " + column + " -> " + columnMapping.get(column));
				}
			}
			System.out.println();
		}
	}
	
	
	public void logFolderSettings(Folder folder) {
		if (folder.getFolderName() != null) {
			System.out.println(folder.getName() + ": " + folder.getFolderName());
			System.out.println();
		}
	}
	
	
	public void logGeneralSettings() {
		System.out.println("General Settings:");
		for (Setting setting : settings.getSettings()) {
			System.out.println("  " + setting.getLabel() + " " + setting.getValueAsString());
		}
		System.out.println();
	}
}
