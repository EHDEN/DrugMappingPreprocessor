package org.ohdsi.drugmapping.preprocessors.zindex;

import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.gui.files.DelimitedInputFileGUI;
import org.ohdsi.drugmapping.preprocessors.Preprocessor;

public class ZIndex extends Preprocessor {
	private static final long serialVersionUID = 8907817283501911409L;
	
	
	public ZIndex(DrugMappingPreprocessor drugMapping, MainFrame mainFrame) {
		super(drugMapping, mainFrame, "ZIndex", new ZIndexPreprocessorInputFiles());
	}
	
	
	public String getOutputFileName() {
		DelimitedInputFileGUI gpkIPCIFile = (DelimitedInputFileGUI) getInputFile("ZIndex GPK IPCI Compositions File");
		return "ZIndex" + (((gpkIPCIFile != null) && gpkIPCIFile.isSelected()) ? " IPCI" : "") + " - GPK" + ".csv";
	}
	
	
	public void run(String outputFileName) {
		if (getInputFile("ZIndex GPK File") != null) getInputFile("ZIndex GPK File").logFileSettings();
		if (getInputFile("ZIndex GSK File") != null) getInputFile("ZIndex GSK File").logFileSettings();
		if (getInputFile("ZIndex GNK File") != null) getInputFile("ZIndex GNK File").logFileSettings();
		if (getInputFile("ZIndex GPK Statistics File") != null) getInputFile("ZIndex GPK Statistics File").logFileSettings();
		if (getInputFile("ZIndex GPK IPCI Compositions File") != null) getInputFile("ZIndex GPK IPCI Compositions File").logFileSettings();
		new ZIndexPreprocessor(
				(DelimitedInputFileGUI) getInputFile("ZIndex GPK File"), 
				(DelimitedInputFileGUI) getInputFile("ZIndex GSK File"), 
				(DelimitedInputFileGUI) getInputFile("ZIndex GNK File"), 
				(DelimitedInputFileGUI) getInputFile("ZIndex GPK Statistics File"), 
				(DelimitedInputFileGUI) getInputFile("ZIndex GPK IPCI Compositions File"),
				outputFileName
				);
	}
}
