package org.ohdsi.drugmapping.preprocessors.medaman;

import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.gui.files.ExcelInputFileGUI;
import org.ohdsi.drugmapping.preprocessors.Preprocessor;

public class MEDAMAN extends Preprocessor {
	private static final long serialVersionUID = 3889366258399726001L;

	
	public MEDAMAN(DrugMappingPreprocessor drugMapping, MainFrame mainFrame) {
		super(drugMapping, mainFrame, "MEDAMAN", new MEDAMANPreprocessorInputFiles());
	}
	
	
	public String getOutputFileName() {
		return "MEDAMAN.csv";
	}
	
	
	public void run(String outputFileName) {
		logFileSettings("MEDAMAN Drug File", getInputFile("MEDAMAN Drug File"));
		logFileSettings("MEDAMAN ATC File", getInputFile("MEDAMAN ATC File"));
		new MEDAMANPreprocessor(
				(ExcelInputFileGUI) getInputFile("MEDAMAN Drug File"),
				(ExcelInputFileGUI) getInputFile("MEDAMAN ATC File"), 
				(ExcelInputFileGUI) getInputFile("MEDAMAN ScanReport File"),
				outputFileName);
	}
}
