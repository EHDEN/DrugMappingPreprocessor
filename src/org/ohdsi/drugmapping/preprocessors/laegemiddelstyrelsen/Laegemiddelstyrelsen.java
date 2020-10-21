package org.ohdsi.drugmapping.preprocessors.laegemiddelstyrelsen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.gui.files.ExcelInputFileGUI;
import org.ohdsi.drugmapping.preprocessors.Preprocessor;
import org.ohdsi.drugmapping.source.SourceDrug;
import org.ohdsi.drugmapping.utilities.DrugMappingDateUtilities;
import org.ohdsi.drugmapping.utilities.DrugMappingStringUtilities;

public class Laegemiddelstyrelsen extends Preprocessor {
	private static final long serialVersionUID = 6180410934671941821L;
	
	
	private Map<String, String> drugUnitMap;
	private Map<Long, Set<String>> compoundDrugsMap;
	

	public Laegemiddelstyrelsen(DrugMappingPreprocessor drugMapping, MainFrame mainFrame) {
		super(drugMapping, mainFrame, "Laegemiddelstyrelsen", new LaegemiddelstyrelsenInputFiles());
	}
	
	
	public String getOutputFileName() {
		return "Laegemiddelstyrelsen.csv";
	}


	@Override
	public boolean hasGeneralSettings() {
		return false;
	}


	@Override
	public void loadGeneralSettingsFile(List<String> fileSettings) {
	}


	@Override
	public void saveGeneralSettingsFile() {
	}


	@Override
	public boolean getData() {
		drugUnitMap = new HashMap<String, String>();
		compoundDrugsMap = new HashMap<Long, Set<String>>();
		
		boolean result = getDrugs((ExcelInputFileGUI) getInputFile("Laegemiddelstyrelsen Drugs File"));
		result = result && getDrugCompounds((ExcelInputFileGUI) getInputFile("Laegemiddelstyrelsen Active Compounds File"), (ExcelInputFileGUI) getInputFile("Laegemiddelstyrelsen Retired Compounds File"));
		result = result && getCountData((ExcelInputFileGUI) getInputFile("Laegemiddelstyrelsen ScanReport File"));
		return result;
	}
	
	
	private boolean getDrugs(ExcelInputFileGUI drugsFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading Laegemiddelstyrelsen Drugs File ...");
		if (drugsFile.openFileForReading(true)) {
			while (drugsFile.hasNext()) {
				Row row = drugsFile.getNext();
				
				Long drugNr = drugsFile.getAsLong(row, "DrugCode");
				String name = drugsFile.getStringValue(row, "DrugName");
				String doseForm = drugsFile.getStringValue(row, "DoseForm");
				String strength = drugsFile.getStringValue(row, "Strength");
				String atc = drugsFile.getStringValue(row, "ATC");
				Long compoundId = drugsFile.getAsLong(row, "DrugId");
				String unit = drugsFile.getStringValue(row, "DrugUnit");
				
				name = (name == null ? "" : name) + (((strength == null) || strength.equals("")) ? "" : (" (" + strength + ")"));
				doseForm = doseForm == null ? "" : doseForm.toLowerCase();
				atc = atc.startsWith("Q") ? "" : atc;
				
				if (drugNr != null) {
					String drugNrString = drugNr.toString();
					
					SourceDrug sourceDrug = source.addSourceDrug(drugNrString, name, null);
					sourceDrug.addFormulation(doseForm);
					sourceDrug.addATC(atc);
					
					drugUnitMap.put(drugNrString, unit == null ? "" : unit.trim());
					if (compoundId != null) {
						Set<String> drugsSet = compoundDrugsMap.get(compoundId);
						if (drugsSet == null) {
							drugsSet = new HashSet<String>();
							compoundDrugsMap.put(compoundId, drugsSet);
						}
						drugsSet.add(drugNrString);
					}
				}
			}
			System.out.println(DrugMappingDateUtilities.getCurrentTime() + "      Found " + source.getDrugCount() + " drugs.");
		}
		else {
			System.out.println("ERROR: Cannot open drugs file \"" + drugsFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}
	
	
	private boolean getDrugCompounds(ExcelInputFileGUI activeCompoundsFile, ExcelInputFileGUI retiredCompoundsFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading Laegemiddelstyrelsen Compounds ...");
		result = result && getCompounds(activeCompoundsFile);
		result = result && getCompounds(retiredCompoundsFile);
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "      Found " + compoundDrugsMap.keySet().size() + " drug compounds.");
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}
	
	
	private boolean getCompounds(ExcelInputFileGUI compoundsFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "      Loading " + compoundsFile.getFileDefinition().getFileName() + " ...");
		if (compoundsFile.openFileForReading(true)) {
			while (compoundsFile.hasNext()) {
				Row row = compoundsFile.getNext();
				
				Long compoundId = compoundsFile.getAsLong(row, "DrugId");
				String ingredientName = compoundsFile.getStringValue(row, "IngredientName");
				String ingredientNameEnglish = compoundsFile.getStringValue(row, "IngredientNameEnglish");
				Double amount = compoundsFile.getAsDouble(row, "Amount");
				String unit = compoundsFile.getStringValue(row, "AmountUnit");
				
				String ingredientCode = ((ingredientName == null) || ingredientName.equals("")) ? "" : DrugMappingStringUtilities.safeToUpperCase(DrugMappingStringUtilities.convertToStandardCharacters(ingredientName));
				
				if (compoundId != null) {
					Set<String> drugsSet = compoundDrugsMap.get(compoundId);
					if (drugsSet != null) {
						for (String drugNrString : drugsSet) {
							String drugUnit = drugUnitMap.get(drugNrString);
							if (drugUnit == null) {
								drugUnit = "";
							}
							String ingredientUnit = (((amount == null) || (amount == 0) || (unit == null)) ? "" : (unit.trim() + (unit.contains("/") ? "" : ("/" + drugUnit)))).trim();
							//if ((!unit.equals("")) && (unit.startsWith("/"))) {
							//	unit = "";
							//}
							if ((!ingredientUnit.equals("")) && (ingredientUnit.endsWith("/"))) {
								ingredientUnit = ingredientUnit.substring(0, ingredientUnit.length() - 1);
							}
							
							SourceDrug sourceDrug = source.getSourceDrug(drugNrString);
							if (sourceDrug != null) {
								sourceDrug.addIngredient(ingredientCode, ingredientName, ingredientNameEnglish, amount, ingredientUnit, null);
							}
						}
					}
				}
			}
		}
		else {
			System.out.println("ERROR: Cannot open compounds file \"" + compoundsFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "      Done");
		return result;
	}
	
	
	private boolean getCountData(ExcelInputFileGUI countsFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading Laegemiddelstyrelsen ScanReport File ...");
		Integer countCount = 0;
		if (countsFile.openFileForReading(true)) {
			Integer drugCodeColumnNr = countsFile.getColumnNr("DrugCode");
			if (drugCodeColumnNr != null) {
				System.out.println("    Loading drug use counts");
				
				Integer countColumnNr = drugCodeColumnNr + 1;
				
				while (countsFile.hasNext()) {
					Row row = countsFile.getNext();
					Long drugCode = countsFile.getAsLong(row, "DrugCode");
					Long count = countsFile.getAsLong(row, countColumnNr);
					
					if ((drugCode != null) && (count != null)) {
						SourceDrug sourceDrug = source.getSourceDrug(drugCode.toString());
						if (sourceDrug != null) {
							sourceDrug.setCount(count);
							countCount++;
						}
					}
				}

				System.out.println(DrugMappingDateUtilities.getCurrentTime() + "      Found " + countCount + " drug use counts.");
			}
			else {
				System.out.println("ERROR: Cannot get counts column in file \"" + countsFile.getFileName() + "\"!");
			}
		}
		else {
			System.out.println("ERROR: Cannot open counts file \"" + countsFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}

}
