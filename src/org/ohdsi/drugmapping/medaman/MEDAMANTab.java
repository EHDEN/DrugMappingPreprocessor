package org.ohdsi.drugmapping.medaman;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.files.InputFileDefinition;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.gui.MainFrameTab;
import org.ohdsi.drugmapping.gui.files.ExcelInputFileGUI;
import org.ohdsi.drugmapping.gui.files.Folder;
import org.ohdsi.drugmapping.gui.files.InputFileGUI;

public class MEDAMANTab extends MainFrameTab {
	private static final long serialVersionUID = 3889366258399726001L;

	private static InputFileDefinition inputFileDefinition = new MEDAMANPreprocessorInputFiles();
	
	private List<InputFileGUI> inputFiles = new ArrayList<InputFileGUI>();

	
	public MEDAMANTab(DrugMappingPreprocessor drugMapping, MainFrame mainFrame) {
		super();
		
		this.mainFrame = mainFrame;
		
		setLayout(new BorderLayout());
		
		JPanel level1Panel = new JPanel(new BorderLayout());
		level1Panel.setBorder(BorderFactory.createEmptyBorder());

		
		// File settings
		JPanel filePanel = new JPanel(new GridLayout(0, 1));
		filePanel.setBorder(BorderFactory.createTitledBorder("Input"));
		
		for (FileDefinition fileDefinition : getInputFiles()) {
			if (fileDefinition.isUsedInInterface()) {
				InputFileGUI inputFile = InputFileGUI.getInputFile(mainFrame.getFrame(), fileDefinition);
				inputFiles.add(inputFile);
				filePanel.add(inputFile);
			}
		}
		
		JPanel level2Panel = new JPanel(new BorderLayout());
		level2Panel.setBorder(BorderFactory.createEmptyBorder());
		
		JPanel outputPanel = null;
		// Output Folder
		outputPanel = new JPanel(new GridLayout(0, 1));
		outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
		outputFolder = new Folder("Output Folder", "Output Folder", DrugMappingPreprocessor.getBasePath());
		outputPanel.add(outputFolder);
		
		JPanel level3Panel = new JPanel(new BorderLayout());
		level3Panel.setBorder(BorderFactory.createEmptyBorder());


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
		add(level1Panel, BorderLayout.CENTER);
		level1Panel.add(filePanel, BorderLayout.NORTH);
		level1Panel.add(level2Panel, BorderLayout.CENTER);
		level2Panel.add(outputPanel, BorderLayout.NORTH);
		level2Panel.add(level3Panel, BorderLayout.CENTER);
		if (settings != null) {
			level3Panel.add(settings, BorderLayout.NORTH);
		}
		level3Panel.add(createConsolePanel(), BorderLayout.CENTER);
		add(buttonSectionPanel, BorderLayout.SOUTH);
	}
	
	
	public boolean hasFileSettings() {
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
	
	
	public String getOutputFileName() {
		return "MEDAMAN.csv";
	}
	
	
	public List<FileDefinition> getInputFiles() {
		return inputFileDefinition.getInputFiles();
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
	
	
	public void run(String outputFileName) {
		logFileSettings("MEDAMAN Drug File", getInputFile("MEDAMAN Drug File"));
		logFileSettings("MEDAMAN ATC File", getInputFile("MEDAMAN ATC File"));
		new MEDAMANPreprocessor(
				(ExcelInputFileGUI) getInputFile("MEDAMAN Drug File"), 
				(ExcelInputFileGUI) getInputFile("MEDAMAN ATC File"), 
				getOutputFolder());
	}
}
