package org.ohdsi.drugmapping.preprocessors.article57;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.files.DelimitedFileRow;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.gui.files.DelimitedInputFileGUI;
import org.ohdsi.drugmapping.preprocessors.Preprocessor;
import org.ohdsi.drugmapping.source.Source;
import org.ohdsi.drugmapping.source.SourceDrug;
import org.ohdsi.drugmapping.utilities.DrugMappingDateUtilities;
import org.ohdsi.drugmapping.utilities.DrugMappingNumberUtilities;
import org.ohdsi.drugmapping.utilities.DrugMappingStringUtilities;

public class Article57 extends Preprocessor {
	private static final long serialVersionUID = -3188572300141559426L;
	
	
	private Map<String, Set<SourceDrug>> atcMap = new HashMap<String, Set<SourceDrug>>();
	

	public Article57(DrugMappingPreprocessor drugMapping, MainFrame mainFrame) {
		super(drugMapping, mainFrame, "Article57", new Article57InputFiles());
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
	public String getOutputFileName() {
		return "Article57.csv";
	}

	@Override
	public boolean getData() {
		Source.dosagePrecision = 9;
		boolean result = getDrugs((DelimitedInputFileGUI) getInputFile("Article57 Product Information File"));
		result = result && getDrugCompounds((DelimitedInputFileGUI) getInputFile("Article57 Substances Information File"));
		result = result && getCountData((DelimitedInputFileGUI) getInputFile("Article57 ATC Counts File"));
		return result;
	}
	
/* EXCEL BEGIN	
	@Override
	public boolean getData() {
		boolean result = getDrugs((ExcelInputFileGUI) getInputFile("Article57 Product Information File"));
		result = result && getDrugCompounds((ExcelInputFileGUI) getInputFile("Article57 Substances Information File"));
		result = result && getCountData((DelimitedInputFileGUI) getInputFile("Article57 ATC Counts File"));
		return result;
	}
	
	
	private boolean getDrugs(ExcelInputFileGUI drugsFile) {
		boolean result = true;
		
		Integer totalCount = 0;
		Integer subsetCount = 0;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading Article57 Product Information File ...");
		if (drugsFile.openFileForReading(true)) {
			while (drugsFile.hasNext()) {
				Row row = drugsFile.getNext();
				
				String drugCode = drugsFile.getStringValue(row, "DrugCode");
				String name = drugsFile.getStringValue(row, "DrugName");
				String doseForm = drugsFile.getStringValue(row, "DoseForm");
				String atcCode = drugsFile.getStringValue(row, "ATCCode");
				
				drugCode = drugCode == null ? null : drugCode.trim();
				name = name == null ? null : name.trim();
				doseForm = doseForm == null ? null : doseForm.trim();
				atcCode = atcCode == null ? null : atcCode.trim();
				
				if ((drugCode != null) && (!drugCode.equals(""))) {
					SourceDrug sourceDrug = source.addSourceDrug(drugCode, name, null);
					if ((doseForm != null) && (!doseForm.equals(""))) {
						sourceDrug.addFormulation(doseForm);
					}
					if ((atcCode != null) && (!atcCode.equals(""))) {
						sourceDrug.addATC(atcCode);
						Set<SourceDrug> atcSet = atcMap.get(atcCode);
						if (atcSet == null) {
							atcSet = new HashSet<SourceDrug>();
							atcMap.put(atcCode, atcSet);
						}
						atcSet.add(sourceDrug);
					}
				}
				
				totalCount++;
				subsetCount++;
				if (subsetCount == 100) {
					System.out.println("  " + totalCount);
					subsetCount = 0;
				}
			}
			System.out.println("  " + totalCount);
			System.out.println(DrugMappingDateUtilities.getCurrentTime() + "      Found " + source.getDrugCount() + " drugs.");
		}
		else {
			System.out.println("ERROR: Cannot open drugs file \"" + drugsFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}
	
	
	private boolean getDrugCompounds(ExcelInputFileGUI compoundsFile) {
		boolean result = true;
		
		Integer totalCount = 0;
		Integer subsetCount = 0;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading Article57 Substances Information File ...");
		if (compoundsFile.openFileForReading(true)) {
			while (compoundsFile.hasNext()) {
				Row row = compoundsFile.getNext();

				String drugCode = compoundsFile.getStringValue(row, "DrugCode");
				String ingredientName = compoundsFile.getStringValue(row, "IngredientName");
				Double numeratorAmount = compoundsFile.getAsDouble(row, "NumeratorAmount");
				String numeratorUnit = compoundsFile.getStringValue(row, "NumeratorUnit");
				Double denominatorAmount = compoundsFile.getAsDouble(row, "DenominatorAmount");
				String denominatorUnit = compoundsFile.getStringValue(row, "DenominatorUnit");
				String casNumber = compoundsFile.getStringValue(row, "CASNumber");
				
				drugCode = drugCode == null ? null : drugCode.trim();
				ingredientName = ingredientName == null ? null : ingredientName.trim();
				numeratorUnit = numeratorUnit == null ? null : numeratorUnit.trim();
				denominatorUnit = denominatorUnit == null ? null : denominatorUnit.trim();
				casNumber = casNumber == null ? null : casNumber.trim();
				
				String ingredientCode = ((ingredientName == null) || ingredientName.equals("")) ? "" : DrugMappingStringUtilities.safeToUpperCase(DrugMappingStringUtilities.convertToStandardCharacters(ingredientName));
				
				if ((drugCode != null)  && (!ingredientCode.equals(""))) {
					SourceDrug sourceDrug = source.getSourceDrug(drugCode);
					if (sourceDrug != null) {
						Double amount = (numeratorAmount == null ? 1.0 : numeratorAmount) / (denominatorAmount == null ? 1.0 : denominatorAmount);
						String unit = numeratorUnit + (((denominatorUnit == null) || denominatorUnit.equals("")) ? "" : "/" + denominatorUnit);
						
						sourceDrug.addIngredient(ingredientCode, ingredientName, null, amount, unit, casNumber);
					}
				}
				
				totalCount++;
				subsetCount++;
				if (subsetCount == 100) {
					System.out.println("  " + totalCount);
					subsetCount = 0;
				}
			}
			System.out.println("  " + totalCount);
		}
		else {
			System.out.println("ERROR: Cannot open compounds file \"" + compoundsFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		return result;
	}
   EXCEL END */
	
	
	private boolean getDrugs(DelimitedInputFileGUI drugsFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading Article57 Product Information File ...");
		if (drugsFile.openFileForReading(true)) {
			while (drugsFile.hasNext()) {
				DelimitedFileRow row = drugsFile.next();
				
				String drugCode = drugsFile.get(row, "DrugCode", false);
				String name = drugsFile.get(row, "DrugName", false);
				String doseForm = drugsFile.get(row, "DoseForm", false);
				String atcCode = drugsFile.get(row, "ATCCode", false);
				
				drugCode = drugCode == null ? null : drugCode.trim();
				name = name == null ? null : fixSpecificTexts(name.trim());
				doseForm = doseForm == null ? null : fixSpecificTexts(doseForm.trim());
				atcCode = atcCode == null ? null : ((atcCode.startsWith("NOT") || atcCode.equals("VARIOUS")) ? null : atcCode.trim());
				
				if ((drugCode != null) && (!drugCode.equals(""))) {
					SourceDrug sourceDrug = source.addSourceDrug(drugCode, name, 0L);
					if ((doseForm != null) && (!doseForm.equals(""))) {
						sourceDrug.addFormulation(doseForm);
					}
					if ((atcCode != null) && (!atcCode.equals(""))) {
						sourceDrug.addATC(atcCode);
						Set<SourceDrug> atcSet = atcMap.get(atcCode);
						if (atcSet == null) {
							atcSet = new HashSet<SourceDrug>();
							atcMap.put(atcCode, atcSet);
						}
						atcSet.add(sourceDrug);
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
	
	
	private boolean getDrugCompounds(DelimitedInputFileGUI compoundsFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading Article57 Substances Information File ...");
		if (compoundsFile.openFileForReading(true)) {
			while (compoundsFile.hasNext()) {
				DelimitedFileRow row = compoundsFile.next();

				String drugCode = compoundsFile.get(row, "DrugCode", false);
				String ingredientName = compoundsFile.get(row, "IngredientName", false);
				String numeratorAmountString = compoundsFile.get(row, "NumeratorAmount", false);
				String numeratorUnit = compoundsFile.get(row, "NumeratorUnit", false);
				String denominatorAmountString = compoundsFile.get(row, "DenominatorAmount", false);
				String denominatorUnit = compoundsFile.get(row, "DenominatorUnit", false);
				String casNumber = compoundsFile.get(row, "CASNumber", false);
				
				drugCode = drugCode == null ? null : drugCode.trim();
				ingredientName = ingredientName == null ? null : fixSpecificTexts(ingredientName.trim());
				numeratorAmountString = numeratorAmountString == null ? null : numeratorAmountString.trim();
				numeratorUnit = numeratorUnit == null ? null : fixSpecificTexts(numeratorUnit.trim());
				denominatorAmountString = denominatorAmountString == null ? null : denominatorAmountString.trim();
				denominatorUnit = denominatorUnit == null ? null : fixSpecificTexts(denominatorUnit.trim());
				casNumber = casNumber == null ? null : DrugMappingNumberUtilities.uniformCASNumber(fixSpecificTexts(casNumber.trim()));
				
				String ingredientCode = ((ingredientName == null) || ingredientName.equals("")) ? "" : ingredientName;
				ingredientName = ingredientName == null ? null : ingredientName;
				
				if ((drugCode != null) && (!ingredientCode.equals(""))) {
					SourceDrug sourceDrug = source.getSourceDrug(drugCode);
					if (sourceDrug != null) {
						Double numeratorAmount = null;
						try {
							numeratorAmount = Double.parseDouble(numeratorAmountString);
						} catch (NumberFormatException e) {
							numeratorAmount = null;
						}

						Double denominatorAmount = null;
						try {
							denominatorAmount = Double.parseDouble(denominatorAmountString);
						} catch (NumberFormatException e) {
							denominatorAmount = null;
						}
						
						Double amount = (numeratorAmount == null ? 1.0 : numeratorAmount) / (denominatorAmount == null ? 1.0 : denominatorAmount);
						String unit = numeratorUnit + (((denominatorUnit == null) || denominatorUnit.equals("")) ? "" : "/" + denominatorUnit);
						
						sourceDrug.addIngredient(ingredientCode, ingredientName, null, amount, unit, casNumber);
					}
				}
			}
		}
		else {
			System.out.println("ERROR: Cannot open compounds file \"" + compoundsFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		return result;
	}
	
	
	private boolean getCountData(DelimitedInputFileGUI countsFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading Article57 ATC Counts File ...");
		Integer countCount = 0;
		if (countsFile.openFileForReading(true)) {
			while (countsFile.hasNext()) {
				DelimitedFileRow row = countsFile.next();
				String atcCode = countsFile.get(row, "ATCCode", false);
				String countString = countsFile.get(row, "Count", false);

				atcCode = atcCode == null ? null : atcCode.trim();
				countString = countString == null ? null : countString.trim();
				
				Long count = 0L;
				if ((countString != null) && (!countString.trim().equals(""))) {
					try {
						count = Long.parseLong(countString.trim());
					} catch (NumberFormatException e) {
						count = null;
					}
				}
				
				if ((atcCode != null) && (count != null)) {
					countCount++;
					Set<SourceDrug> sourceDrugSet = atcMap.get(atcCode);
					if (sourceDrugSet != null) {
						Long atcCount = 0L;
						if (count != null) {
							atcCount = (long) Math.ceil((double) count / (double) sourceDrugSet.size());
						}
						for (SourceDrug sourceDrug : sourceDrugSet) {
							sourceDrug.setMaxCount(atcCount);
						}
					}
				}
			}

			System.out.println(DrugMappingDateUtilities.getCurrentTime() + "      Found " + countCount + " drug use counts.");
		}
		else {
			System.out.println("ERROR: Cannot open counts file \"" + countsFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}
	
	
	private String fixSpecificTexts(String text) {
		String result = DrugMappingStringUtilities.convertToANSI(text);

		if (result.contains("DL-?-TOCOPHEROL")) {
			result.replaceAll("DL-?-TOCOPHEROL", "DL-ALPHA-TOCOPHEROL");
		}

		result = result.replaceAll("–", "-");
		result = result.replaceAll("’", "'");
		result = result.replaceAll("”", "\"");
		result = result.replaceAll("ß", "SS");
		result = result.replaceAll("•", "*");
		result = result.replaceAll("\r\n", " ");
		result = result.replaceAll("\r", " ");
		result = result.replaceAll("\n", " ");
		result = result.replaceAll("\t", " ");
		
		return result;
	}

}
