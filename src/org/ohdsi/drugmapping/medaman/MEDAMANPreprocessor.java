package org.ohdsi.drugmapping.medaman;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.gui.files.ExcelInputFileGUI;
import org.ohdsi.drugmapping.gui.files.Folder;
import org.ohdsi.drugmapping.utilities.DrugMappingFileUtilities;
import org.ohdsi.drugmapping.utilities.DrugMappingStringUtilities;

public class MEDAMANPreprocessor {
	
	private Map<String, String> atcMap;

	
	public MEDAMANPreprocessor(ExcelInputFileGUI drugsFile, ExcelInputFileGUI atcFile, Folder outputFolder) {
		getData(atcFile);
		writeCSVFile(drugsFile, outputFolder.getFolderName());
	}
	
	
	private boolean getData(ExcelInputFileGUI atcFile) {
		System.out.println("  Loading data");
		boolean result = true;
		
		if (atcFile.openFileForReading(true)) {
			atcMap = new HashMap<String, String>();
			
			while (atcFile.hasNext()) {
				Row row = atcFile.getNext();
				String drugCode = atcFile.get(row, "DrugCode").trim();
				String atcCode  = atcFile.get(row, "ATC").trim();
				
				if ((drugCode != null) && (!drugCode.equals("")) && (atcCode != null) && (!atcCode.equals(""))) {
					if ((!atcCode.equals("X00XX00")) && (!atcCode.equals("Z99ZZ99"))) {
						atcMap.put(drugCode, atcCode);
					}
				}
			}
		}
		else {
			System.out.println("ERROR: Cannot open atc file \"" + atcFile.getFileName() + "\"!");
			result = false;
		}

		return result;
	}
	
	
	public void writeCSVFile(ExcelInputFileGUI drugsFile, String outputPath) {
		System.out.println("  Writing drug mapping input file");
		
		String header = "SourceCode";
		header += "," + "SourceName";
		header += "," + "SourceATCCode";
		header += "," + "SourceFormulation";
		header += "," + "SourceCount";
		header += "," + "IngredientCode";
		header += "," + "IngredientName";
		header += "," + "IngredientNameEnglish";
		header += "," + "Dosage";
		header += "," + "DosageUnit";
		header += "," + "CASNumber";
		
		String fileName = DrugMappingPreprocessor.debug ? DrugMappingFileUtilities.getNextFileName(outputPath, "MEDAMAN.csv") : "MEDAMAN.csv";
		String logFileName = ((fileName.lastIndexOf(".") != -1) ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName) + " Log.txt";
		
		fileName = outputPath + (outputPath.contains("\\") ? "\\" : "/") + fileName;
		logFileName = outputPath + (outputPath.contains("\\") ? "\\" : "/") + logFileName;

		System.out.println("    Output file: " + fileName);
		System.out.println("    Log file   : " + logFileName);
		
		PrintWriter file = DrugMappingFileUtilities.openOutputFile(fileName, header);
		PrintWriter logFile = DrugMappingFileUtilities.openOutputFile(logFileName, null);
		
		if (drugsFile.openFileForReading(true)) {
			while (drugsFile.hasNext()) {
				Row row = drugsFile.getNext();
				String drugCode                 = drugsFile.get(row, "DrugCode");
				String drugName                 = drugsFile.get(row, "DrugName");
				String doseForm                 = drugsFile.get(row, "DoseForm");
				String ingredientCode           = drugsFile.get(row, "IngredientCode");
				String ingredientName           = drugsFile.get(row, "IngredientName");
				String amountNumeratorString    = drugsFile.get(row, "AmountNumerator");
				String amountNumeratorUnit      = drugsFile.get(row, "AmountNumeratorUnit");
				String amountDenominatorString  = drugsFile.get(row, "AmountDenominator");
				String amountDenominatorUnit    = drugsFile.get(row, "AmountDenominatorUnit");
				String amountNumeratorTopString = drugsFile.get(row, "AmountNumeratorTop");
				String amountNumeratorTopUnit   = drugsFile.get(row, "AmountNumeratorTopUnit");
				
				if (drugCode != null) {
					String atcCode = atcMap.get(drugCode);

					String drugRecord = drugCode;
					drugRecord += "," + DrugMappingStringUtilities.escapeFieldValue(drugName == null ? "" : drugName);
					drugRecord += "," + DrugMappingStringUtilities.escapeFieldValue(atcCode == null ? "" : atcCode);
					drugRecord += "," + DrugMappingStringUtilities.escapeFieldValue(doseForm == null ? "" : doseForm);
					drugRecord += "," + "-1"; // Count
					drugRecord += "," + DrugMappingStringUtilities.escapeFieldValue(ingredientCode == null ? "" : ingredientCode);
					drugRecord += "," + DrugMappingStringUtilities.escapeFieldValue(ingredientName == null ? "" : ingredientName);
					drugRecord += "," + "";   // IngredientNameEnglish
					
					Double dosage = null;
					String dosageUnit = null;
					Double factor = 1.0;
							
					if (amountNumeratorString != null) {
						Double amountNumerator = Double.parseDouble(amountNumeratorString);
						
						Double amountNumeratorTop = null;
						if (amountNumeratorTopString != null) {
							amountNumeratorTop = Double.parseDouble(amountNumeratorTopString);
						}
						if ((amountNumeratorUnit != null) && (amountNumeratorTopUnit != null) && (amountNumeratorTopUnit.endsWith(amountNumeratorUnit))) {
							amountNumeratorTopUnit = amountNumeratorTopUnit.substring(0, amountNumeratorUnit.length()).trim();
							if (amountNumeratorTopUnit.toUpperCase().startsWith("E")) {
								String factorString = amountNumeratorTopUnit.substring(1).trim();
								try {
									int power = Integer.parseInt(factorString);
									for (int pow = 0; pow < power; pow++) {
										factor = factor * 10.0;
									}
								} catch (NumberFormatException e) {
									factor = 1.0;
								}
							}
						}
						
						if (amountNumeratorTop != null) {
							amountNumerator = (amountNumerator + amountNumeratorTop) / 2.0;
						}
						
						if (amountDenominatorString != null) {
							Double amountDenominator = Double.parseDouble(amountDenominatorString);
							if (amountDenominator > 0) {
								dosage = (amountNumerator * factor) / amountDenominator;
							}
						}
						
						dosageUnit = amountNumeratorUnit == null ? "" : amountNumeratorUnit;
						dosageUnit += amountDenominatorUnit == null ? "" : ("/" + amountDenominatorUnit);
					}

					drugRecord += "," + dosage == null ? "" : dosage;
					drugRecord += "," + dosageUnit == null ? "" : dosageUnit;
					drugRecord += "," + "";   // CASNumber
					
					file.println(drugRecord);
				}
			}
		}
		else {
			System.out.println("ERROR: Cannot open drugs file \"" + drugsFile.getFileName() + "\"!");
		}
		
		DrugMappingFileUtilities.closeOutputFile(file);
		DrugMappingFileUtilities.closeOutputFile(logFile);
		
		System.out.println("  Done");
		System.out.println();
	}
}
