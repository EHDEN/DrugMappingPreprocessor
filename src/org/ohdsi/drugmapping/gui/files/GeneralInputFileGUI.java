package org.ohdsi.drugmapping.gui.files;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileFilter;

import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.files.GeneralFile;
import org.ohdsi.drugmapping.utilities.DrugMappingStringUtilities;

public class GeneralInputFileGUI extends InputFileGUI {
	private static final long serialVersionUID = 7241670501557895511L;
	
	private GeneralFile file = null;
	
	public GeneralInputFileGUI(Component parent, FileDefinition fileDefinition) {
		super(parent, fileDefinition);
	}
	
	
	public boolean close() {
		boolean result = false;
		
		if (file != null) {
			result = file.close();
		}
		
		return result;
	}
	
	
	public String readLine() {
		String line = null;
		
		if (file != null) {
			line = DrugMappingStringUtilities.convertToANSI(file.readLine());
		}
		
		return line;
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
		return fileFilters;
	}


	@Override
	public boolean openFileForReading() {
		boolean result = false;
		
		if (getFileName() != null) {
			file = new GeneralFile(getFileName());
			result = file.openForReading();
		}
		
		return result;
	}


	@Override
	public boolean openFileForReading(boolean suppressError) {
		// TODO Auto-generated method stub
		return false;
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
