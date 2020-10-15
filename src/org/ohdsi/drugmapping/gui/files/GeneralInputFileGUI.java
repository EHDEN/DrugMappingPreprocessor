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
	void defineFile(InputFileGUI inputFile) {
		selectFile(getInterfaceParent(), getFileNameField());
	}


	@Override
	List<FileFilter> getFileFilters() {
		List<FileFilter> fileFilters = new ArrayList<FileFilter>();
		return fileFilters;
	}


	@Override
	boolean openFileForReading() {
		boolean result = false;
		
		if (getFileName() != null) {
			file = new GeneralFile(getFileName());
			result = file.openForReading();
		}
		
		return result;
	}


	@Override
	boolean openFileForReading(boolean suppressError) {
		// TODO Auto-generated method stub
		return false;
	}
}
