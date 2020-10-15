package org.ohdsi.drugmapping.preprocessors.medaman;

import org.apache.poi.ss.usermodel.Row;
import org.ohdsi.drugmapping.gui.files.ExcelInputFileGUI;
import org.ohdsi.drugmapping.source.Source;
import org.ohdsi.drugmapping.source.SourceDrug;
import org.ohdsi.drugmapping.utilities.DrugMappingStringUtilities;

public class MEDAMANPreprocessor {
	private Source source = new Source();

	
	public MEDAMANPreprocessor(ExcelInputFileGUI drugsFile, ExcelInputFileGUI atcFile, ExcelInputFileGUI countsFile, String outputFileName) {
		System.out.println("Preprocessing MEDAMAN Drugs");
		
		if (getData(drugsFile, atcFile, countsFile)) {
			writeInputFile(outputFileName);
		}
		
		System.out.println("Finished");
		System.out.println();
	}
	
	
	private boolean getData(ExcelInputFileGUI drugsFile, ExcelInputFileGUI atcFile, ExcelInputFileGUI countsFile) {
		boolean result = true;
		
		System.out.println("  Loading data");
		
		result = result && getDrugData(drugsFile);
		result = result && getATCData(atcFile);
		result = result && getCountData(countsFile);

		System.out.println("  Done");

		return result;
	}
	
	
	private boolean getDrugData(ExcelInputFileGUI drugsFile) {
		boolean result = true;
		
		if (drugsFile.openFileForReading(true)) {
			System.out.println("    Loading drugs");

			while (drugsFile.hasNext()) {
				Row row = drugsFile.getNext();
				Integer drugCode                = drugsFile.getAsInteger(row, "DrugCode");
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


				String drugCodeString = drugCode.toString();
				drugCodeString = ("0000000" + drugCodeString).substring(drugCodeString.length());
				//drugName = DrugMappingStringUtilities.convertToStandardCharacters(drugName);
				ingredientCode = DrugMappingStringUtilities.safeToUpperCase(DrugMappingStringUtilities.convertToStandardCharacters(ingredientName == null ? "" : ingredientName));
				//ingredientName = DrugMappingStringUtilities.convertToStandardCharacters(ingredientName);
				
				
				if (drugCode != null) {
					Double dosage = null;
					String dosageUnit = null;
					Double factor = 1.0;
							
					if (amountNumeratorString != null) {
						dosage = Double.parseDouble(amountNumeratorString);
						
						Double amountNumeratorTop = null;
						if (amountNumeratorTopString != null) {
							amountNumeratorTop = Double.parseDouble(amountNumeratorTopString);
						}
						if ((amountNumeratorUnit != null) && (amountNumeratorTopUnit != null) && (amountNumeratorTopUnit.endsWith(amountNumeratorUnit))) {
							amountNumeratorTopUnit = amountNumeratorTopUnit.substring(0, amountNumeratorUnit.length()).trim();
							if (DrugMappingStringUtilities.safeToUpperCase(amountNumeratorTopUnit).startsWith("E")) {
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
						
						if ((amountNumeratorTop != null) && (amountNumeratorTop > 0.0)) {
							dosage = (dosage + amountNumeratorTop) / 2.0;
						}
						
						if (amountDenominatorString != null) {
							Double amountDenominator = Double.parseDouble(amountDenominatorString);
							if (amountDenominator > 0) {
								dosage = (dosage * factor) / amountDenominator;
							}
						}
					}
					
					dosageUnit = amountNumeratorUnit == null ? "" : amountNumeratorUnit;
					dosageUnit += amountDenominatorUnit == null ? "" : ("/" + amountDenominatorUnit);

					SourceDrug sourceDrug = source.addSourceDrug(drugCodeString, drugName, null);
					if (sourceDrug != null) {
						sourceDrug.addFormulation(doseForm);
						sourceDrug.addIngredient(ingredientCode, ingredientName, "", dosage, dosageUnit, "");
					}
				}
			}

			System.out.println("      Found " + source.getDrugCount() + " drugs");
			System.out.println("    Done");
		}
		else {
			System.out.println("ERROR: Cannot open drugs file \"" + drugsFile.getFileName() + "\"!");
		}

		return result;
	}
	
	
	private boolean getATCData(ExcelInputFileGUI atcFile) {
		boolean result = true;

		Integer atcCount = 0;
		if (atcFile.openFileForReading(true)) {
			System.out.println("    Loading ATC codes");
			
			while (atcFile.hasNext()) {
				Row row = atcFile.getNext();
				Integer drugCode = atcFile.getAsInteger(row, "DrugCode");
				String atcCode  = atcFile.get(row, "ATC").trim();

				String drugCodeString = drugCode.toString();
				drugCodeString = ("0000000" + drugCodeString).substring(drugCodeString.length());
				
				if ((drugCode != null) && (!drugCode.equals("")) && (atcCode != null) && (!atcCode.equals(""))) {
					if ((!atcCode.equals("X00XX00")) && (!atcCode.equals("Z99ZZ99"))) {
						SourceDrug sourceDrug = source.getSourceDrug(drugCodeString);
						if (sourceDrug != null) {
							sourceDrug.addATC(atcCode);
							atcCount++;
						}
					}
				}
			}

			System.out.println("      Found " + atcCount + " atc codes");
			System.out.println("    Done");
		}
		else {
			System.out.println("ERROR: Cannot open atc file \"" + atcFile.getFileName() + "\"!");
			result = false;
		}

		return result;
	}
	
	
	private boolean getCountData(ExcelInputFileGUI countsFile) {
		boolean result = true;

		Integer atcCount = 0;
		if (countsFile.openFileForReading(true)) {
			Integer drugCodeColumnNr = countsFile.getColumnNr("DrugCode");
			if (drugCodeColumnNr != null) {
				System.out.println("    Loading drug use counts");
				
				Integer countColumnNr = drugCodeColumnNr + 1;
				
				while (countsFile.hasNext()) {
					Row row = countsFile.getNext();
					Integer drugCode = countsFile.getAsInteger(row, "DrugCode");
					Long count = countsFile.getAsLong(row, countColumnNr);
					
					if ((drugCode != null) && (!drugCode.equals("")) && (count != null)) {
						String drugCodeString = drugCode.toString();
						drugCodeString = ("0000000" + drugCodeString).substring(drugCodeString.length());
						
						SourceDrug sourceDrug = source.getSourceDrug(drugCodeString);
						if (sourceDrug != null) {
							sourceDrug.setCount(count);
							atcCount++;
						}
					}
				}

				System.out.println("      Found " + atcCount + " atc codes");
				System.out.println("    Done");
			}
			else {
				System.out.println("ERROR: Cannot get counts column in file \"" + countsFile.getFileName() + "\"!");
			}
		}
		else {
			System.out.println("ERROR: Cannot open counts file \"" + countsFile.getFileName() + "\"!");
			result = false;
		}

		return result;
	}
	
	
	public boolean writeInputFile(String outputFileName) {
		boolean result = true;
		
		System.out.println("  Writing drug mapping input file \"" + outputFileName + "\"");
		
		if (!source.save(outputFileName, true, true)) {
			System.out.println("ERROR: Cannot write input file \"" + outputFileName + "\"!");
		}
		
		System.out.println("  Done");
		
		return result;
	}
}
