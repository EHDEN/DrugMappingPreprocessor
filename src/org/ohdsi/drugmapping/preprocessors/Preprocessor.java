package org.ohdsi.drugmapping.preprocessors;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.GeneralSettings;
import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.files.InputFileDefinition;
import org.ohdsi.drugmapping.gui.Console;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.gui.ObjectExchange;
import org.ohdsi.drugmapping.gui.Setting;
import org.ohdsi.drugmapping.gui.files.Folder;
import org.ohdsi.drugmapping.gui.files.InputFileGUI;
import org.ohdsi.drugmapping.source.Source;

abstract public class Preprocessor extends JPanel implements Comparable<Preprocessor> {
	private static final long serialVersionUID = -2611669075696826114L;
	
	protected String preprocessorName = null;
	protected InputFileDefinition inputFileDefinition = null;
	protected List<InputFileGUI> inputFiles = null;
	protected MainFrame mainFrame = null;
	protected JPanel mainPanel;
	protected Console console = null;
	protected String logFileName = null;
	protected Folder outputFolder = null; 
	protected GeneralSettings settings = null;
	protected JButton startButton = null;
	protected boolean isPreprocessing = false;

	protected Source source = new Source();
	

	public Preprocessor(DrugMappingPreprocessor drugMapping, MainFrame mainFrame, String preprocessorName, InputFileDefinition inputFileDefinition) {
		super();
		this.mainFrame = mainFrame;
		this.preprocessorName = preprocessorName;
		this.inputFileDefinition = inputFileDefinition;
		
		inputFiles = new ArrayList<InputFileGUI>();
		
		setLayout(new BorderLayout());
		
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder());
		
		// File settings
		JPanel inputPanel = new JPanel(new GridLayout(0, 1));
		inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
		
		for (FileDefinition fileDefinition : getInputFiles()) {
			if (fileDefinition.isUsedInInterface()) {
				InputFileGUI inputFile = InputFileGUI.getInputFile(mainFrame.getFrame(), fileDefinition);
				inputFiles.add(inputFile);
				inputPanel.add(inputFile);
			}
		}
		
		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel outputPanel = null;
		// Output Folder
		outputPanel = new JPanel(new GridLayout(0, 1));
		outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
		outputFolder = new Folder("Output Folder", "Output Folder", DrugMappingPreprocessor.getBasePath());
		outputPanel.add(outputFolder);
		
		JPanel subSubPanel = new JPanel(new BorderLayout());
		subSubPanel.setBorder(BorderFactory.createEmptyBorder());


		// General Settings
		settings = null;
		
		
		// Buttons Panel
		JPanel buttonSectionPanel = new JPanel(new BorderLayout());

		// Start Button
		JPanel buttonPanel = new JPanel(new FlowLayout());
		startButton = new JButton("  Start  ");
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				drugMapping.StartMapping();
			}
		});
		buttonPanel.add(startButton);
		DrugMappingPreprocessor.disableWhenRunning(startButton);
		
		buttonSectionPanel.add(buttonPanel, BorderLayout.WEST);
	
		
		// Build panel
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.add(inputPanel, BorderLayout.NORTH);
		mainPanel.add(subPanel, BorderLayout.CENTER);
		subPanel.add(outputPanel, BorderLayout.NORTH);
		subPanel.add(subSubPanel, BorderLayout.CENTER);
		if (settings != null) {
			subSubPanel.add(settings, BorderLayout.NORTH);
		}
		subSubPanel.add(createConsolePanel(), BorderLayout.CENTER);
		add(buttonSectionPanel, BorderLayout.SOUTH);
	}
	
	
	public JScrollPane createConsolePanel() {
		JTextArea consoleArea = new JTextArea();
		consoleArea.setToolTipText("General progress information");
		consoleArea.setEditable(false);
		console = new Console();
		console.setTextArea(consoleArea);
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
		isPreprocessing = false;
	}
	
	
	public boolean isPreprocessing() {
		return isPreprocessing;
	}
	
	
	public void setIsPreprocessing(boolean isPreprocessing) {
		this.isPreprocessing = isPreprocessing;
	}
	
	
	public List<FileDefinition> getInputFiles() {
		return inputFileDefinition.getInputFiles();
	}
	
	
	public boolean hasFileSettings() {
		// To be overruled at the subclass
		return true;
	}

	
	public void loadFileSettingsFile() {
		loadFileSettingsFile(readSettingsFromFile());
	}

	
	public void loadFileSettingsFile(List<String> fileSettings) {
		if (fileSettings != null) {
			if (outputFolder != null) {
				outputFolder.putSettings(fileSettings);
			}
			for (InputFileGUI inputFile : inputFiles) {
				inputFile.putSettings(fileSettings);
			}
		}
	}

	
	public void saveFileSettingsFile() {
		List<String> settings = new ArrayList<String>();
		if (outputFolder != null) {
			settings.addAll(outputFolder.getSettings());
			settings.add("");
			settings.add("");
		}
		for (InputFileGUI inputFile : inputFiles) {
			settings.addAll(inputFile.getSettings());
			settings.add("");
			settings.add("");
		}
		saveSettingsToFile(settings);
	}

	
	public void loadGeneralSettingsFile() {
		List<String> generalSettings = readSettingsFromFile();
		loadGeneralSettingsFile(generalSettings);
	}
	
	
	private void saveSettingsToFile(List<String> settings) {
		String settingsFileName = getFile(new FileNameExtensionFilter("Settings Files", "ini"), false);
		if (settingsFileName != null) {
			try {
				PrintWriter settingsFile = new PrintWriter(settingsFileName);
				for (String line : settings) {
					settingsFile.println(line);
				}
				settingsFile.close();
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(mainFrame.getFrame(), "Unable to write settings to file!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	
	private List<String> readSettingsFromFile() {
		return readSettingsFromFile(getFile(new FileNameExtensionFilter("Settings Files", "ini"), true), true);
	}
	
	
	public List<String> readSettingsFromFile(String settingsFileName, boolean mandatory) {
		List<String> settings = new ArrayList<String>();
		if (settingsFileName != null) {
			try {
				BufferedReader settingsFileBufferedReader = new BufferedReader(new FileReader(settingsFileName));
				String line = settingsFileBufferedReader.readLine();
				while (line != null) {
					settings.add(line);
					line = settingsFileBufferedReader.readLine();
				}
				settingsFileBufferedReader.close();
			}
			catch (IOException e) {
				if (mandatory) {
					JOptionPane.showMessageDialog(mainFrame.getFrame(), "Unable to read settings from file '" + settingsFileName + "'!", "Error", JOptionPane.ERROR_MESSAGE);
				}
				settings = null;
			}
		}
		return settings;
	}
	
	
	public InputFileGUI getInputFile(String fileName) {
		InputFileGUI file = null;
		
		for (InputFileGUI inputFile : inputFiles) {
			if (inputFile.getLabelText().equals(fileName)) {
				file = inputFile;
				break;
			}
		}
		
		return file;
	}
	
	
	public void redirectSystemOutput() {
		if (!(System.getProperty("runInEclipse") == null ? false : System.getProperty("runInEclipse").equalsIgnoreCase("true"))) {
			System.setOut(new PrintStream(console));
			System.setErr(new PrintStream(console));
		}
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
	
	
	public String getPreprocessorName() {
		return preprocessorName;
	}
	

	public void run(String outputFileName) {
		for (InputFileGUI inputFile : inputFiles) {
			inputFile.logFileSettings();
			System.out.println("Output file: " + outputFileName);
			System.out.println();
		}
		
		System.out.println("Preprocessing " + getPreprocessorName() + " Drugs");

		System.out.println("  Loading data");
		boolean getDataResult = getData();
		System.out.println("  Done");
		
		if (getDataResult) {
			writeInputFile(outputFileName);
		}
		
		System.out.println("Finished");
		System.out.println();
	}
	
	
	protected boolean writeInputFile(String outputFileName) {
		boolean result = true;
		
		System.out.println("  Writing drug mapping input file \"" + outputFileName + "\"");
		
		if (!source.save(outputFileName, true, true)) {
			System.out.println("ERROR: Cannot write input file \"" + outputFileName + "\"!");
		}
		
		System.out.println("  Done");
		
		return result;
	}
	
	
	abstract public boolean hasGeneralSettings();

	
	abstract public void loadGeneralSettingsFile(List<String> fileSettings);

	
	abstract public void saveGeneralSettingsFile();
	
	
	abstract public String getOutputFileName();
	
	
	abstract public boolean getData();


	@Override
	public int compareTo(Preprocessor otherPreprocessor) {
		return this.getPreprocessorName().compareTo(otherPreprocessor.getPreprocessorName());
	}
}
