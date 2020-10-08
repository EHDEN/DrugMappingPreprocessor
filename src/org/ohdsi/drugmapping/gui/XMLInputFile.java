package org.ohdsi.drugmapping.gui;

public class XMLInputFile extends GeneralInputFile {
	private static final long serialVersionUID = 4396400305276997256L;

	
	public XMLInputFile(String labelText, boolean isRequired, String defaultFileName) {
		super(labelText, ".xml", isRequired, defaultFileName);
	}
}
