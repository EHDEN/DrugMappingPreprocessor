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
		boolean result = false;
		
		if (getFileName() != null) {
			File inputFile = new File(getFileName());
			if (inputFile.exists() && inputFile.canRead()) {
				xmlFile = new XMLFile(getFileName());
				if (!xmlFile.openFile()) {
					if (!suppressError) {
						JOptionPane.showMessageDialog(null, "Couldn't open file for reading!", "Error", JOptionPane.ERROR_MESSAGE);
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
}
