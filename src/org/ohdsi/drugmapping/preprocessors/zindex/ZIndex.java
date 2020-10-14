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
		logFileSettings("ZIndex GPK File", getInputFile("ZIndex GPK File"));
		logFileSettings("ZIndex GSK File", getInputFile("ZIndex GSK File"));
		logFileSettings("ZIndex GNK File", getInputFile("ZIndex GNK File"));
		logFileSettings("ZIndex GPK Statistics File", getInputFile("ZIndex GPK Statistics File"));
		logFileSettings("ZIndex GPK IPCI Compositions File", getInputFile("ZIndex GPK IPCI Compositions File"));
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
