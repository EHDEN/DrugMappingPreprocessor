package org.ohdsi.drugmapping;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.gui.MainFrameTab;
import org.ohdsi.drugmapping.utilities.DrugMappingFileUtilities;

public class DrugMappingPreprocessor { 
	public static boolean debug = false;
	public static Boolean autoStart = false;
	
	public static String baseName = "";
	
	public static Set<JComponent> componentsToDisableWhenRunning = new HashSet<JComponent>();
	
	private static String currentPath = null;	
	private static String basePath = new File(".").getAbsolutePath();
	
	private MainFrame mainFrame;
	
	
	public static String getCurrentPath() {
		return currentPath;
	}
	
	
	public static void setCurrentPath(String path) {
		currentPath = path;
	}
	
	
	public static String getBasePath() {
		return basePath;
	}
	
	
	public static void setBasePath(String path) {
		basePath = path;
	}
	
	
	public static void disableWhenRunning(JComponent component) {
		componentsToDisableWhenRunning.add(component);
	}
	
	
	public DrugMappingPreprocessor(Map<String, String> parameters) {
		List<String> fileSettings = null;
		List<String> generalSettings = null;
		debug = (parameters.get("debug") != null);
		
		mainFrame = new MainFrame(this);
		MainFrameTab tab = null;
		if (parameters.containsKey("preprocessor")) {
			String preprocessor = parameters.get("preprocessor").toLowerCase();
			tab = mainFrame.getTab(preprocessor);
			
			if (tab != null) {
				if (parameters.containsKey("filesettings")) {
					fileSettings = mainFrame.readSettingsFromFile(parameters.get("filesettings"), true);
				}
				if (parameters.containsKey("generalsettings")) {
					generalSettings = mainFrame.readSettingsFromFile(parameters.get("generalsettings"), false);
				}
				if (fileSettings != null) {
					tab.loadFileSettingsFile(fileSettings);
				}
				if (generalSettings != null) {
					tab.loadGeneralSettingsFile(generalSettings);
				}
				mainFrame.selectTab(preprocessor);
				
				if (parameters.containsKey("autostart")) {
					autoStart = parameters.get("autostart").toLowerCase().equals("yes");
				}
			}
		}
	}
	
	
	private void Show() {
		mainFrame.show();
		if (autoStart) {
			StartMapping();
		}
	}
	
	
	public void StartMapping() {
		// Create log file and set basePath
		String outputFileName = mainFrame.getOutputFileName();
		String outputFolder = mainFrame.getOutputFolder().getFolderName();
		outputFileName = DrugMappingFileUtilities.getNextFileName(outputFolder, outputFileName);
		outputFileName = outputFolder + (outputFolder.contains("\\") ? "\\" : "/") + outputFileName;
		String logFileName = ((outputFileName.lastIndexOf(".") != -1) ? outputFileName.substring(0, outputFileName.lastIndexOf(".")) : outputFileName) + " Log.txt";
		mainFrame.setLogFile(logFileName);
		MappingThread mappingThread = new MappingThread(outputFileName);
		mappingThread.start();
	}
	
	
	private class MappingThread extends Thread {
		private String outputFileName = null;
		
		public MappingThread(String outputFileName) {
			this.outputFileName = outputFileName;
		}
		
		public void run() {
			for (JComponent component : componentsToDisableWhenRunning)
				component.setEnabled(false);
			
			mainFrame.getCurrentTab().run(outputFileName);

			mainFrame.closeLogFile();
			
			for (JComponent component : componentsToDisableWhenRunning)
				component.setEnabled(true);
		}
		
	}
	
	
	public static void main(String[] args) {
		Map<String, String> parameters = new HashMap<String, String>();

		for (int i = 0; i < args.length; i++) {
			int equalSignIndex = args[i].indexOf("=");
			String argVariable = args[i].toLowerCase();
			String value = "";
			if (equalSignIndex != -1) {
				argVariable = args[i].substring(0, equalSignIndex).toLowerCase();
				value = args[i].substring(equalSignIndex + 1);
			}
			parameters.put(argVariable, value);
		}
		
		DrugMappingPreprocessor drugMappingPreprocessor = new DrugMappingPreprocessor(parameters);
		drugMappingPreprocessor.Show();
		/* */
	}

}
