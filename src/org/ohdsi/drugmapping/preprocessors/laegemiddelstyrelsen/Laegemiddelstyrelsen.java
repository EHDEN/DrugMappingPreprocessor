package org.ohdsi.drugmapping.preprocessors.laegemiddelstyrelsen;

import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.gui.files.ExcelInputFileGUI;
import org.ohdsi.drugmapping.preprocessors.Preprocessor;

public class Laegemiddelstyrelsen extends Preprocessor {
	private static final long serialVersionUID = 6180410934671941821L;
	

	public Laegemiddelstyrelsen(DrugMappingPreprocessor drugMapping, MainFrame mainFrame) {
		super(drugMapping, mainFrame, "Laegemiddelstyrelsen", new LaegemiddelstyrelsenPreprocessorInputFiles());
	}
	
	
	public String getOutputFileName() {
		return "MEDAMAN.csv";
	}
	
	
	public void run(String outputFileName) {
		if (getInputFile("Laegemiddelstyrelsen Drugs File")               != null) getInputFile("Laegemiddelstyrelsen Drugs File").logFileSettings();
		if (getInputFile("Laegemiddelstyrelsen Active Ingredients File")  != null) getInputFile("Laegemiddelstyrelsen Active Ingredients File").logFileSettings();
		if (getInputFile("Laegemiddelstyrelsen Retired Ingredients File") != null) getInputFile("Laegemiddelstyrelsen Retired Ingredients File").logFileSettings();
		if (getInputFile("Laegemiddelstyrelsen ScanReport File")          != null) getInputFile("Laegemiddelstyrelsen ScanReport File").logFileSettings();
		new LaegemiddelstyrelsenPreprocessor(
				(ExcelInputFileGUI) getInputFile("Laegemiddelstyrelsen Drugs File"),
				(ExcelInputFileGUI) getInputFile("Laegemiddelstyrelsen Active Ingredients File"),
				(ExcelInputFileGUI) getInputFile("Laegemiddelstyrelsen Retired Ingredients File"),
				(ExcelInputFileGUI) getInputFile("Laegemiddelstyrelsen ScanReport File"),
				outputFileName);
	}

}
