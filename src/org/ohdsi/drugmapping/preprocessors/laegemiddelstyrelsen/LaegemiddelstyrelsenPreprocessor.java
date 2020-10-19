package org.ohdsi.drugmapping.preprocessors.laegemiddelstyrelsen;

import org.apache.poi.ss.usermodel.Row;
import org.ohdsi.drugmapping.gui.files.ExcelInputFileGUI;

public class LaegemiddelstyrelsenPreprocessor {

	
	public LaegemiddelstyrelsenPreprocessor(ExcelInputFileGUI drugsFile, ExcelInputFileGUI activeIngredientsFile, ExcelInputFileGUI retiredIngredientsFile, ExcelInputFileGUI scanReportFile, String outputFileName) {
		System.out.println("Preprocessing Laegemiddelstyrelsen Drugs");
		if (getData(drugsFile, activeIngredientsFile, retiredIngredientsFile, scanReportFile)) {
			writeDrugMappingFile(outputFileName);
		}
		System.out.println("Finished");
	}
	
	
	public boolean getData(ExcelInputFileGUI drugsFile, ExcelInputFileGUI activeIngredientsFile, ExcelInputFileGUI retiredIngredientsFile, ExcelInputFileGUI scanReportFile) {
		System.out.println("  Loading data");

		ExcelFile drugsFile = new ExcelFile(drugsFileName);
		if (drugsFile.open()) {
			System.out.println("    Loading drugs");
			for (String sheetName : drugsSheets) {
				if (drugsFile.getSheet(sheetName, true)) {
					while (drugsFile.hasNext(sheetName)) {
						Row row = drugsFile.getNext(sheetName);
						
						Double doubleDrugNr = drugsFile.getDoubleValue(sheetName, row, drugsDrugNrColumn);
						String stringDrugNr = drugsFile.getStringValue(sheetName, row, drugsDrugNrColumn);
						String name = drugsFile.getStringValue(sheetName, row, drugsNameColumn);
						String doseForm = drugsFile.getStringValue(sheetName, row, drugsDoseFormColumn);
						String strength = drugsFile.getStringValue(sheetName, row, drugsDrugStrengthColumn);
						String atc = drugsFile.getStringValue(sheetName, row, drugsATCColumn);
						Double doubleCompoundId = drugsFile.getDoubleValue(sheetName, row, drugsDrugIdColumn);
						String stringCompoundId = drugsFile.getStringValue(sheetName, row, drugsDrugIdColumn);
						String unit = drugsFile.getStringValue(sheetName, row, drugsUnitColumn);
						
						Long drugNr = null;
						if (doubleDrugNr != null) {
							drugNr = doubleDrugNr.longValue();
						}
						if (stringDrugNr != null) {
							drugNr = stringDrugNr.trim().equals("") ? null : Long.parseLong(stringDrugNr.trim());
						}
						
						Long compoundId = null;
						if (doubleCompoundId != null) {
							compoundId = doubleCompoundId.longValue();
						}
						if (stringCompoundId != null) {
							compoundId = stringCompoundId.trim().equals("") ? null : Long.parseLong(stringCompoundId.trim());
						}
						
						Drug drug = drugs.get(drugNr);
						if (drug == null) {
							drug = new Drug(drugNr, name, doseForm, strength, atc, unit, compoundId);
							drugs.put(drugNr, drug);
						}
					}
				}
			}
			drugsFile.close();
			
			System.out.println("      Found " + drugs.size() + " drugs");
			System.out.println("    Done");
		}
		else {
			System.out.println("ERROR: Cannot open drugs file \"" + drugsFileName + "\"!");
			result = false;
		}

		ExcelFile countsFile = new ExcelFile(countsFileName);
		if (countsFile.open()) {
			System.out.println("    Loading counts");
			for (String sheetName : countsSheets) {
				if (countsFile.getSheet(sheetName, true)) {
					Integer drugNrColumnNr = countsFile.getColumnNr(sheetName, countsDrugNrColumn);
					if (drugNrColumnNr != null) {
						Integer frequencyColumnNr = drugNrColumnNr + 1;
						
						while (countsFile.hasNext(sheetName)) {
							Row row = countsFile.getNext(sheetName);
							
							Double doubleDrugNr = countsFile.getDoubleValue(row, drugNrColumnNr);
							Double doubleFrequency = countsFile.getDoubleValue(row, frequencyColumnNr);
							
							String stringDrugNr = countsFile.getStringValue(row, drugNrColumnNr);
							String stringFrequency = countsFile.getStringValue(row, frequencyColumnNr);
							
							Long drugNr = null;
							Long frequency = null;
							
							if (doubleDrugNr != null) {
								drugNr = doubleDrugNr.longValue();
							}
							if (doubleFrequency != null) {
								frequency = doubleFrequency.longValue();
							}
							
							if ((stringDrugNr != null) && (drugNr == null)) {
								drugNr = stringDrugNr.trim().equals("") ? null : Long.parseLong(stringDrugNr.trim());
							}
							
							if ((stringFrequency != null) && (frequency == null)) {
								frequency = stringFrequency.trim().equals("") ? null : Long.parseLong(stringFrequency.trim());
							}
							
							if ((drugNr != null) && (frequency != null)) {
								counts.put(drugNr, frequency);
							}
						} 
					}
				}
			}
			
			countsFile.close();

			System.out.println("      Found " + counts.size() + " counts");
			System.out.println("    Done");
		}
		else {
			System.out.println("ERROR: Cannot open counts file \"" + compoundsFileName + "\"!");
			result = false;
		}
		
		System.out.println("  Done");
		System.out.println();
		
		return result;
	}
	
	
	private boolean getDrugs(ExcelInputFileGUI drugsFile) {
		
	}
	
	
	private boolean getIngredients(ExcelInputFileGUI ingredientsFile) {
		boolean result = true;

		if (ingredientsFile.openFileForReading(true)) {
			System.out.println("    Loading ingredients");
			while (ingredientsFile.hasNext()) {
				Row row = ingredientsFile.getNext();
				
				Long ingredientId = ingredientsFile.getAsLong(row, "DrugId");
				String name = ingredientsFile.getStringValue(row, "IngredientName");
				String nameEnglish = ingredientsFile.getStringValue(row, "IngredientNameEnglish");
				Double doubleAmount = ingredientsFile.getAsDouble(row, "Amount");
				String unit = ingredientsFile.getStringValue(row, "AmountUnit");
				
				if (ingredientId != null) {
					Double amount = null;
					if (doubleAmount != null) {
						amount = doubleAmount;
					}
					if (stringAmount != null) {
						amount = stringAmount.trim().equals("") ? null : Double.parseDouble(stringAmount.trim());
					}
					
					Ingredient ingredient = new Ingredient(name, nameEnglish, amount, unit);
					List<Ingredient> compound = compounds.get(compoundId);
					if (compound == null) {
						compound = new ArrayList<Ingredient>();
						compounds.put(compoundId, compound);
					}
					compound.add(ingredient);
				}
			}
		}
		else {
			System.out.println("ERROR: Cannot open compounds file \"" + compoundsFileName + "\"!");
			result = false;
		}

		if (ingredientsFile.open()) {
			for (String sheetName : compoundsSheets) {
				if (ingredientsFile.getSheet(sheetName, true)) {
				}
			}
			ingredientsFile.close();

			System.out.println("      Found " + compounds.keySet().size() + " compounds");
			System.out.println("    Done");
		}
		else {
			System.out.println("ERROR: Cannot open compounds file \"" + compoundsFileName + "\"!");
			result = false;
		}
		
	}
	
	
	private boolean getRetiredIngredients(ExcelInputFileGUI retiredIngredientsFile) {
		
	}
	
	
	private boolean getScanReport(ExcelInputFileGUI scanReportFile) {
		
	}
}
