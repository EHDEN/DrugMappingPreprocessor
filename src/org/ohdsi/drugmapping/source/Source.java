package org.ohdsi.drugmapping.source;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ohdsi.drugmapping.utilities.DrugMappingFileUtilities;

public class Source {
	private List<SourceDrug> sourceDrugs = new ArrayList<SourceDrug>();
	private Map<String, SourceDrug> sourceDrugMap = new HashMap<String, SourceDrug>();
	
	public static int dosagePrecision = -1; 
	
	
	public SourceDrug addSourceDrug(String sourceDrugCode, String sourceDrugName, Long sourceDrugCount) {
		SourceDrug sourceDrug = getSourceDrug(sourceDrugCode);
		if (sourceDrug == null) {
			sourceDrug = new SourceDrug(sourceDrugCode, sourceDrugName, sourceDrugCount);
			sourceDrugs.add(sourceDrug);
			sourceDrugMap.put(sourceDrugCode, sourceDrug);
		}
		return sourceDrug;
	}
	
	
	public SourceDrug getSourceDrug(String sourceDrugCode) {
		return sourceDrugMap.get(sourceDrugCode);
	}
	
	
	public Integer getDrugCount() {
		return sourceDrugs.size();
	}
	
	
	public boolean save(String outputFileName, boolean sortATCCodes, boolean sortFormulations) {
		boolean result = false;
		
		PrintWriter outputFile = DrugMappingFileUtilities.openOutputFile(outputFileName, SourceDrug.getHeader());
		if (outputFile != null) {
			for (SourceDrug sourceDrug : sourceDrugs) {
				for (String record : sourceDrug.getDescription(sortATCCodes, sortFormulations)) {
					outputFile.println(record);
				}
			}
			outputFile.close();
			result = true;
		}
		
		return result;
	}
}
