package org.ohdsi.drugmapping.gui.files;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.files.XMLFile;
import org.ohdsi.drugmapping.files.XMLFile.XMLRoot;

public class XMLInputFileGUI extends InputFileGUI {
	private static final long serialVersionUID = 4396400305276997256L;
	
	private XMLFile xmlFile = null;
	
	public XMLInputFileGUI(Component parent, FileDefinition fileDefinition) {
		super(parent, fileDefinition);
	}
	
	
	public XMLRoot getXMLRoot() {
		return xmlFile.getXMLRoot();
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
					if (settingPathSplit.length == 2) {
						if (settingPathSplit[1].equals("filename")) setFileName(value);
						else if (settingPathSplit[1].equals("selected")) setSelected(value.toUpperCase().equals("YES"));
						else {
							// Unknown setting
						}
					}
				}
			}
		}
	}


	@Override
	void defineFile(InputFileGUI inputFile) {
		selectFile(getInterfaceParent(), getFileNameField());
	}


	@Override
	List<FileFilter> getFileFilters() {
		List<FileFilter> fileFilters = new ArrayList<FileFilter>();
		fileFilters.add(new FileFilter() {

	        @Override
	        public boolean accept(File f) {
	            return f.getName().endsWith(".xml");
	        }

	        @Override
	        public String getDescription() {
	            return "XML File";
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
		boolean result = true;
		
		if (getFileName() != null) {
			File inputFile = new File(getFileName());
			if (inputFile.exists() && inputFile.canRead()) {
				xmlFile = new XMLFile(getFileName());
				if (!xmlFile.openFile()) {
					if (!suppressError) {
						JOptionPane.showMessageDialog(null, "Couldn't open file for reading!", "Error", JOptionPane.ERROR_MESSAGE);
						result = false;
					}
				}
			}
			else {
				if (!suppressError) {
					JOptionPane.showMessageDialog(null, "Cannot read file '" + getFileName() + "'!", "Error", JOptionPane.ERROR_MESSAGE);
					result = false;
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
			System.out.println();
		}
	}
}
