package org.ohdsi.drugmapping.gui.files;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import org.ohdsi.drugmapping.files.FileDefinition;

public class GeneralInputFileGUI extends InputFileGUI {
	private static final long serialVersionUID = 7241670501557895511L;
	
	public GeneralInputFileGUI(Component parent, FileDefinition fileDefinition) {
		super(parent, fileDefinition);
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
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	boolean openFileForReading(boolean suppressError) {
		// TODO Auto-generated method stub
		return false;
	}
}
