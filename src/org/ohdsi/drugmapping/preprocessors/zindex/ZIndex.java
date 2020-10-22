package org.ohdsi.drugmapping.preprocessors.zindex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.files.DelimitedFileRow;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.gui.files.DelimitedInputFileGUI;
import org.ohdsi.drugmapping.preprocessors.Preprocessor;
import org.ohdsi.drugmapping.source.SourceDrug;
import org.ohdsi.drugmapping.source.SourceIngredient;
import org.ohdsi.drugmapping.utilities.DrugMappingDateUtilities;
import org.ohdsi.drugmapping.utilities.DrugMappingNumberUtilities;
import org.ohdsi.drugmapping.utilities.DrugMappingStringUtilities;

public class ZIndex extends Preprocessor {
	private static final long serialVersionUID = 8907817283501911409L;
	

	private static final boolean IGNORE_EMPTY_GPK_NAMES   = false;
	private static final boolean IGNORE_STARRED_GPK_NAMES = false;
	private static final boolean TRY_EXTRACT              = false;
	
	private static final String NUMBER_CHARS = "1234567890,.";
	
	private Map<Integer, GNK>           gnkMap;
	private Map<String, GNK>            gnkNameMap;
	private Map<Integer, GSK>           gskMap;
	private List<GPK>                   gpkList;
	private Map<Integer, GPK>           gpkMap;
	private Map<Integer, List<GPKIPCI>> gpkIPCIMap;
	private Map<GNK, List<GPK>>         gnkOneIngredientGpkMap;
	private Map<Integer, Long>          gpkCountMap;
	private List<String>                wordsToRemove = new ArrayList<String>();
	
	
	public ZIndex(DrugMappingPreprocessor drugMapping, MainFrame mainFrame) {
		super(drugMapping, mainFrame, "ZIndex", new ZIndexInputFiles());
	}
	
	
	public String getOutputFileName() {
		DelimitedInputFileGUI gpkIPCIFile = (DelimitedInputFileGUI) getInputFile("ZIndex GPK IPCI Compositions File");
		return "ZIndex" + (((gpkIPCIFile != null) && gpkIPCIFile.isSelected()) ? " IPCI" : "") + " - GPK" + ".csv";
	}


	@Override
	public boolean hasGeneralSettings() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void loadGeneralSettingsFile(List<String> fileSettings) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void saveGeneralSettingsFile() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean getData() {
		gnkMap = new HashMap<Integer, GNK>();
		gnkNameMap = new HashMap<String, GNK>();
		gskMap = new HashMap<Integer, GSK>();
		gpkList = new ArrayList<GPK>();
		gpkMap = new HashMap<Integer, GPK>();
		gpkIPCIMap = new HashMap<Integer, List<GPKIPCI>>();
		gpkCountMap = new HashMap<Integer, Long>();
		gnkOneIngredientGpkMap = new HashMap<GNK, List<GPK>>();

		boolean result = getGNK((DelimitedInputFileGUI) getInputFile("ZIndex GNK File"));
		result = result && getGSK((DelimitedInputFileGUI) getInputFile("ZIndex GSK File"));
		result = result && getGPK((DelimitedInputFileGUI) getInputFile("ZIndex GPK File"));
		result = result && getGPKIPCI((DelimitedInputFileGUI) getInputFile("ZIndex GPK IPCI Compositions File"));
		result = result && getGPKStatistics((DelimitedInputFileGUI) getInputFile("ZIndex GPK Statistics File"));
		result = result && buildSource();
		return result;
	}
	
	
	public boolean getGNK(DelimitedInputFileGUI gnkFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading ZIndex GNK File ...");
		if (gnkFile.openFileForReading()) {
			Integer lineNr = 2;
			while (gnkFile.hasNext()) {
				DelimitedFileRow row = gnkFile.next();

				String gnkCode            = DrugMappingStringUtilities.removeExtraSpaces(gnkFile.get(row, "GNKCode", true));
				String gnkDescription     = DrugMappingStringUtilities.removeExtraSpaces(gnkFile.get(row, "Description", true));
				String gnkCASNumber       = DrugMappingStringUtilities.removeExtraSpaces(gnkFile.get(row, "CASNumber", true));
				String gnkBaseName        = DrugMappingStringUtilities.removeExtraSpaces(gnkFile.get(row, "BaseName", true));
				String gnkChemicalFormula = DrugMappingStringUtilities.removeExtraSpaces(gnkFile.get(row, "ChemicalFormula", true));
				
				Integer gnkNr = null;
				if ((gnkCode != null) && (!gnkCode.trim().equals(""))) {
					try {
						gnkNr = Integer.valueOf(gnkCode.trim());
					} catch (NumberFormatException e) {
						warnings.add("Illegal GNK number on line " + lineNr + " of ZIndex GNK File \"" + gnkFile.getFileName() + "\"");
					}
				}

				if (gnkNr != null) {
					GNK gnk = new GNK(gnkNr, gnkDescription, gnkCASNumber, gnkBaseName, gnkChemicalFormula);
					result = result && gnk.ok; 
				}
				else {
					warnings.add("Missing GNK code on line " + lineNr + " of ZIndex GNK File \"" + gnkFile.getFileName() + "\"");
				}
				lineNr++;
			}
		}
		else {
			System.out.println("   ERROR: Cannot read ZIndex GNK File '" + gnkFile.getFileName() + "'");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}
	
	
	private boolean getGSK(DelimitedInputFileGUI gskFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading ZIndex GSK File ...");
		if (gskFile.openFileForReading()) {
			Integer lineNr = 2;
			while (gskFile.hasNext()) {
				DelimitedFileRow row = gskFile.next();

				String gskCode          = DrugMappingStringUtilities.removeExtraSpaces(gskFile.get(row, "GSKCode", true));
				String gskPartNumber    = DrugMappingStringUtilities.removeExtraSpaces(gskFile.get(row, "PartNumber", true));
				String gskType          = DrugMappingStringUtilities.removeExtraSpaces(gskFile.get(row, "Type", true).trim());
				String gskAmount        = DrugMappingStringUtilities.removeExtraSpaces(gskFile.get(row, "Amount", true));
				String gskAmountUnit    = DrugMappingStringUtilities.removeExtraSpaces(gskFile.get(row, "AmountUnit", true));
				String gskGNKCode       = DrugMappingStringUtilities.removeExtraSpaces(gskFile.get(row, "GNKCode", true));
				String gskGenericName   = DrugMappingStringUtilities.removeExtraSpaces(gskFile.get(row, "GenericName", true));
				String gskCASNumber     = DrugMappingStringUtilities.removeExtraSpaces(gskFile.get(row, "CASNumber", true));
				
				Integer gskNr = null;
				if ((gskCode != null) && (!gskCode.trim().equals(""))) {
					try {
						gskNr = Integer.valueOf(gskCode);
					} catch (NumberFormatException e) {
						warnings.add("Illegal GSK number on line " + lineNr + " of ZIndex GSK File \"" + gskFile.getFileName() + "\"");
					}
				}
				
				Integer gskGNKNr = null;
				if ((gskGNKCode != null) && (!gskGNKCode.trim().equals(""))) {
					try {
						gskGNKNr = Integer.valueOf(gskGNKCode.trim());
					} catch (NumberFormatException e) {
						warnings.add("Illegal GNK number on line " + lineNr + " of ZIndex GSK File \"" + gskFile.getFileName() + "\"");
					}
				}

				if (gskNr != null) {
					GSK gsk = gskMap.get(gskNr);
					if (gsk == null) {
						gsk = new GSK(gskNr);
					}
					gsk.addGNK(gskGNKNr, gskPartNumber, gskType, gskAmount, gskAmountUnit, gskGenericName, gskCASNumber);
				}
				else {
					warnings.add("Missing GSK code on line " + lineNr + " of ZIndex GSK File \"" + gskFile.getFileName() + "\"");
				}
				lineNr++;
			}
		}
		else {
			System.out.println("   ERROR: Cannot read ZIndex GSK File '" + gskFile.getFileName() + "'");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}
	
	
	private boolean getGPK(DelimitedInputFileGUI gpkFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading ZIndex GPK File ...");
		if (gpkFile.openFileForReading()) {
			Integer lineNr = 2;
			while (gpkFile.hasNext()) {
				DelimitedFileRow row = gpkFile.next();

				String gpkCode             = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "GPKCode", true));
				String gpkMemoCode         = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "MemoCode", true));
				String gpkLabelName        = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "LabelName", true));
				String gpkShortName        = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "ShortName", true));
				String gpkFullName         = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "FullName", true));
				String gpkATCCode          = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "ATCCode", true));
				String gpkGSKCode          = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "GSKCode", true));
				String gpkDDDPerHPKUnit    = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "DDDPerHPKUnit", true));
				String gpkPrescriptionDays = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "PrescriptionDays", true));
				String gpkHPKMG            = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "HPKMG", true));
				String gpkHPKMGUnit        = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "HPKMGUnit", true));
				String gpkPharmForm        = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "PharmForm", true));
				String gpkBasicUnit        = DrugMappingStringUtilities.removeExtraSpaces(gpkFile.get(row, "BasicUnit", true));
				
				if ((gpkCode != null) && (!gpkCode.trim().equals(""))) {
					Integer gpkNr = null;
					try {
						gpkNr = Integer.valueOf(gpkCode.trim());
					} catch (NumberFormatException e) {
						warnings.add("Illegal GPK number on line " + lineNr + " of ZIndex GPK File \"" + gpkFile.getFileName() + "\"");
					}

					Integer gskNr = null;
					if (!gpkGSKCode.trim().equals("")) {
						try {
							gskNr = Integer.valueOf(gpkGSKCode.trim());
						} catch (NumberFormatException e) {
							warnings.add("Illegal GSK number on line " + lineNr + " of ZIndex GPK File \"" + gpkFile.getFileName() + "\"");
						}
					}
					
					GPK gpk = gpkMap.get(gpkNr);
					if (gpk == null) {
						gpk = new GPK(gpkNr, gpkMemoCode, gpkLabelName, gpkShortName, gpkFullName, gpkATCCode, gskNr, gpkDDDPerHPKUnit, gpkPrescriptionDays, gpkHPKMG, gpkHPKMGUnit, gpkPharmForm, gpkBasicUnit);
					}
				}
				else {
					warnings.add("Missing GPK code on line " + lineNr + " of ZIndex GPK File \"" + gpkFile.getFileName() + "\"");
				}
				lineNr++;
			}
			
			for (Integer gpkNr : gpkMap.keySet()) {
				GPK gpk = gpkMap.get(gpkNr);
				if ((gpk != null) && (gpk.gsk != null) && (gpk.gsk.gnkList != null) && (gpk.gsk.gnkList.size() == 1)) {
					GNK gnk = gpk.gsk.gnkList.get(0).gnk;
					if (gnk != null) {
						List<GPK> oneIngredientGPKs = gnkOneIngredientGpkMap.get(gnk);
						if (oneIngredientGPKs == null) {
							oneIngredientGPKs = new ArrayList<GPK>();
							gnkOneIngredientGpkMap.put(gnk, oneIngredientGPKs);
						}
						oneIngredientGPKs.add(gpk);
					}
				}
			}
		}
		else {
			System.out.println("   ERROR: Cannot read ZIndex GPK File '" + gpkFile.getFileName() + "'");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}
	
	
	private boolean getGPKIPCI(DelimitedInputFileGUI gpkIPCIFile) {
		boolean result = true;
		
		if ((gpkIPCIFile != null) && gpkIPCIFile.isSelected()) {
			System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading ZIndex GPK IPCI Compositions File ...");
			if (gpkIPCIFile.openFileForReading()) {
				Integer lineNr = 2;
				while (gpkIPCIFile.hasNext()) {
					DelimitedFileRow row = gpkIPCIFile.next();

					String code            = DrugMappingStringUtilities.removeExtraSpaces(gpkIPCIFile.get(row, "GPKCode", true));
					String partNumber      = DrugMappingStringUtilities.removeExtraSpaces(gpkIPCIFile.get(row, "PartNumber", true));
					String type            = DrugMappingStringUtilities.removeExtraSpaces(gpkIPCIFile.get(row, "Type", true));
					String amount          = DrugMappingStringUtilities.removeExtraSpaces(gpkIPCIFile.get(row, "Amount", true));
					String amountUnit      = DrugMappingStringUtilities.removeExtraSpaces(gpkIPCIFile.get(row, "AmountUnit", true));
					String gnkCode         = DrugMappingStringUtilities.removeExtraSpaces(gpkIPCIFile.get(row, "GNKCode", true));
					String gnkName         = DrugMappingStringUtilities.removeExtraSpaces(gpkIPCIFile.get(row, "GenericName", true));
					String casNumber       = DrugMappingStringUtilities.removeExtraSpaces(gpkIPCIFile.get(row, "CASNumber", true));
					String baseName        = DrugMappingStringUtilities.removeExtraSpaces(gpkIPCIFile.get(row, "BaseName", true));
					String chemicalFormula = DrugMappingStringUtilities.removeExtraSpaces(gpkIPCIFile.get(row, "ChemicalFormula", true));
					
					if ((code != null) && (!code.equals(""))) {
						Integer gpkNr = null;
						try {
							gpkNr = Integer.valueOf(code);
						} catch (NumberFormatException e) {
							warnings.add("Illegal GPK code on line " + lineNr + " of ZIndex GPK IPCI Compositions File \"" + gpkIPCIFile.getFileName() + "\"");
						}
						
						Integer gnkNr = null;
						if ((gnkCode != null) && (!gnkCode.trim().equals(""))) {
							try {
								gnkNr = Integer.valueOf(gnkCode);
							} catch (NumberFormatException e) {
								warnings.add("Illegal GNK code on line " + lineNr + " of ZIndex GPK IPCI Compositions File \"" + gpkIPCIFile.getFileName() + "\"");
							}
						}
						
						if (gpkNr != null) {
							new GPKIPCI(gpkNr, partNumber, type, amount, amountUnit, gnkNr, gnkName, casNumber, baseName, chemicalFormula);
						}
					}
					else {
						warnings.add("Missing GPK code on line " + lineNr + " of ZIndex GPK IPCI Compositions File \"" + gpkIPCIFile.getFileName() + "\"");
					}
					lineNr++;
				}
				
				for (Integer gpkNr : gpkMap.keySet()) {
					List<GPKIPCI> gpkIPCIList = gpkIPCIMap.get(gpkNr);
					GPK gpk = gpkMap.get(gpkNr);
					if ((gpk != null) && (gpkIPCIList != null) && (gpkIPCIList.size() == 1)) {
						GNK gnk =gpkIPCIList.get(0).gnk;
						if (gnk != null) {
							List<GPK> oneIngredientGPKs = gnkOneIngredientGpkMap.get(gnk);
							if (oneIngredientGPKs == null) {
								oneIngredientGPKs = new ArrayList<GPK>();
								gnkOneIngredientGpkMap.put(gnk, oneIngredientGPKs);
							}
							if (!oneIngredientGPKs.contains(gpk)) {
								oneIngredientGPKs.add(gpk);
							}
						}
					}
				}
			}
			System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		}
		else {
			System.out.println(DrugMappingDateUtilities.getCurrentTime() + "     No GPK IPCI Compositions File used.");
		}
		return result;
	}
	
	
	private boolean getGPKStatistics(DelimitedInputFileGUI gpkStatisticsFile) {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading ZIndex GPK Statistics File ...");
		if (gpkStatisticsFile.openFileForReading()) {
			Integer lineNr = 2;
			while (gpkStatisticsFile.hasNext()) {
				DelimitedFileRow row = gpkStatisticsFile.next();

				String gpkCode  = DrugMappingStringUtilities.removeExtraSpaces(gpkStatisticsFile.get(row, "GPKCode", true));
				String gpkCount = DrugMappingStringUtilities.removeExtraSpaces(gpkStatisticsFile.get(row, "GPKCount", true));
				
				Integer gpkNr = null;
				try {
					gpkNr = Integer.valueOf(gpkCode);
				} catch (NumberFormatException e) {
					warnings.add("Illegal GPK number on line " + lineNr + " of ZIndex GPK Statistics File \"" + gpkStatisticsFile.getFileName() + "\"");
				}

				Long count = null;
				if ((gpkNr != null) && (!gpkCode.trim().equals(""))) {
					try {
						count = Long.valueOf(gpkCount);
						gpkCountMap.put(gpkNr, count);
					} catch (NumberFormatException e) {
						warnings.add("Illegal count value on line " + lineNr + " of ZIndex GPK Statistics File \"" + gpkStatisticsFile.getFileName() + "\"");
					}
				}
				else {
					warnings.add("Missing GSK code on line " + lineNr + " of ZIndex GPK Statistics File \"" + gpkStatisticsFile.getFileName() + "\"");
				}
				lineNr++;
			}
		}
		else {
			System.out.println("   ERROR: Cannot read ZIndex GPK Statistics File '" + gpkStatisticsFile.getFileName() + "'");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}
	
	
	private boolean buildSource() {
		boolean result = true;

		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Building Source Drugs ...");
		for (GPK gpk : gpkList) {
			
			// When no ATC-code try to get it from other GPK's with one and the same ingredient.
			if (gpk.atcCode.equals("")) {
				if ((gpk.gsk != null) && (gpk.gsk.gnkList.size() == 1)) {
					gpk.atcCode = getAtcOfSingleIngredientGPK(gpk.gsk.gnkList.get(0).gnk);
				}
			}
			
			String name = gpk.fullName;
			if (name.equals("")) name = gpk.labelName;
			if (name.equals("")) name = gpk.shortName;
			
			if ((!name.equals(""))) {
				
				SourceDrug sourceDrug = source.addSourceDrug(gpk.nr.toString(), name, null);
				sourceDrug.addFormulation(gpk.pharmForm);
				sourceDrug.setATC(gpk.atcCode);
				sourceDrug.setCount(gpkCountMap.get(gpk.nr) == null ? 0 : gpkCountMap.get(gpk.nr));
				
				// Get IPCI derivation from Marcel de Wilde if it exists
				List<GPKIPCI> gpkIPCIIngredients = gpkIPCIMap.get(gpk.nr);
				if (gpkIPCIIngredients != null) {
					// IPCI derivation found
					
					// When no ATC-code try to get it from other GPK's with one and the same ingredient.
					if ((gpkIPCIIngredients.size() == 1) && gpk.atcCode.equals("")) {
						String foundATC = getAtcOfSingleIngredientGPK(gpkIPCIIngredients.get(0).gnk);
						if (foundATC != null) {
							gpk.atcCode = foundATC;
							sourceDrug.setATC(gpk.atcCode);
						}
					}
					
					for (GPKIPCI gpkIPCIIngredient : gpkIPCIIngredients) {
						String ingredientNumeratorUnit = gpkIPCIIngredient.amountUnit;
						String ingredientDenominatorUnit = gpk.basicUnit.equals("Stuk") ? "" : gpk.basicUnit;
						String ingredientUnit = ingredientDenominatorUnit.equals("") ? ingredientNumeratorUnit : (ingredientNumeratorUnit + "/" + ingredientDenominatorUnit);
						
						Double amount = null;
						if ((gpkIPCIIngredient.amount != null) && (!gpkIPCIIngredient.amount.equals(""))) {
							try {
								amount = Double.parseDouble(gpkIPCIIngredient.amount);
							} catch (NumberFormatException e) {
								amount = null;
							}
						}
						
						if (gpkIPCIIngredient.gnk != null) {
							sourceDrug.addIngredient(gpkIPCIIngredient.gnk.nr.toString(), gpkIPCIIngredient.gnkName, null, amount, ingredientUnit, gpkIPCIIngredient.casNumber);
						}
					}
				}
				else {
					// Get Z-Index derivation

					// Ignore empty names and names that start with a '*'
					if (((!IGNORE_EMPTY_GPK_NAMES) || (!name.equals(""))) && ((!IGNORE_STARRED_GPK_NAMES) || (!name.substring(0, 1).equals("*")))) {
						
						List<GSKComponent> gnkList = new ArrayList<GSKComponent>();
						
						if (gpk.gsk != null) {
							for (GSKComponent gskComponent : gpk.gsk.gnkList) {
								if ((gskComponent.type.contentEquals("W"))) {
									gnkList.add(gskComponent);
								}
							}

							if (gnkList.size() == 0) {
								gnkList = null;
								warnings.add("No active ingredient GSK records (GSKCode = " + gpk.gsk.nr + ") found for GPK " + gpk.nr);
							}
						}
						
						// Get ingredients and dosages from GSK and GNK tables
						if ((gnkList != null) && (gnkList.size() > 0)) {
							for (GSKComponent gskObject : gnkList) {
								Double amount = null;
								if ((gskObject.amount != null) && (!gskObject.amount.equals(""))) {
									try {
										amount = Double.parseDouble(gskObject.amount);
									} catch (NumberFormatException e) {
										amount = null;
									}
								}

								String ingredientNumeratorUnit = gskObject.amountUnit;
								String ingredientDenominatorUnit = gpk.basicUnit.equals("Stuk") ? "" : gpk.basicUnit;
								String amountUnit = ingredientDenominatorUnit.equals("") ? ingredientNumeratorUnit : (ingredientNumeratorUnit + "/" + ingredientDenominatorUnit);
								
								String genericName = cleanupExtractedIngredientName(gskObject.genericName);
								
								if (gskObject.gnk != null) {
									sourceDrug.addIngredient(gskObject.gnk.nr.toString(), genericName, null, amount, amountUnit, gskObject.gnk.casNumber);
								}
							}
						}
						else if (TRY_EXTRACT) {
							// Try to extract ingredients from name (separated by '/')
							// List of words to remove from extracted parts.
							// IMPORTANT:
							//   The List wordsToRemove is an ordered list. The words are removed in the order of the list.
							//   The appearance of the words are checked with surrounding parenthesis, with
							//   surrounding spaces, and at the end of the extracted part.
							
							List<String> ingredientNames = new ArrayList<String>();
							List<String> ingredientAmounts = new ArrayList<String>();
							List<String> ingredientAmountUnits = new ArrayList<String>();

							String ingredients = gpk.shortName;
							if (ingredients.contains(" ") && (!ingredients.substring(0, 1).equals("*"))) {
								ingredients = ingredients.substring(0, ingredients.lastIndexOf(" "));
							}
							if (!ingredients.equals("")) {
								if (ingredients.contains("/") || ingredients.contains("+")) {
									String[] ingredientsSplit = ingredients.contains("/") ? ingredients.split("/") :  ingredients.split("\\+");
									String doseString = getDoseString(gpk.fullName);
									String[] doseStringSplit = doseString != null ? doseString.split("/") : null;
									String denominatorUnit = DrugMappingStringUtilities.removeExtraSpaces((((doseStringSplit != null) && (doseStringSplit.length > ingredientsSplit.length)) ? doseStringSplit[ingredientsSplit.length] : ""));
									String lastAmountUnit = null;
									
									for (int ingredientNr = 0; ingredientNr < ingredientsSplit.length; ingredientNr++) {
										String ingredientName = ingredientsSplit[ingredientNr];
										ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName);
										if (ingredientName != null) {
											ingredientNames.add(ingredientName);
											
											String amount = "";
											String amountUnit = "";
											if (doseStringSplit != null) {
												if (ingredientNr < doseStringSplit.length) {
													String ingredientDoseString = doseStringSplit[ingredientNr];
													String numberChars = NUMBER_CHARS;
													for (int charNr = 0; charNr < ingredientDoseString.length(); charNr++) {
														if (numberChars.indexOf(ingredientDoseString.charAt(charNr)) < 0) {
															break;
														}
														amount += ingredientDoseString.charAt(charNr);
													}
													amount = amount.replace(",", ".");
													amountUnit = ingredientDoseString.substring(amount.length());
													if ((!amountUnit.equals("")) && (amountUnit.substring(0, 1).equals("-"))) { // Solve things like 5-WATER
														amount = "";
														amountUnit = "";
													}
												}
											}
											lastAmountUnit = amountUnit;
											ingredientAmounts.add(amount);
											ingredientAmountUnits.add(amountUnit);
										}
										else {
											ingredientNames.add(null);
											ingredientAmounts.add(null);
											ingredientAmountUnits.add(null);
										}
									}

									// Fill missing units
									for (int ingredientNr = 0; ingredientNr < ingredientNames.size(); ingredientNr++) {
										if (ingredientNames.get(ingredientNr) != null) {
											String amountUnit = ingredientAmountUnits.get(ingredientNr);
											if (amountUnit.equals("")) {
												amountUnit = lastAmountUnit;
											}
											ingredientAmountUnits.set(ingredientNr, amountUnit.equals("") ? denominatorUnit : (denominatorUnit.equals("") ? amountUnit : amountUnit + "/" + denominatorUnit));
										}
									}
								}
								else {
									String ingredientName = ingredients;
									
									if (ingredientName != null) {
										String amount = "";
										String amountUnit = "";

										String doseString = getDoseString(gpk.fullName);
										String[] doseStringSplit = doseString != null ? doseString.split("/") : null;
										if (doseStringSplit != null) {
											String denominatorUnit = "";
											if (doseString.contains("/")) {
												doseString = doseStringSplit[0];
												denominatorUnit = DrugMappingStringUtilities.removeExtraSpaces((doseStringSplit.length > 1 ? doseStringSplit[1] : ""));
											}
											String numberChars = NUMBER_CHARS;
											for (int charNr = 0; charNr < doseString.length(); charNr++) {
												if (numberChars.indexOf(doseString.charAt(charNr)) < 0) {
													break;
												}
												amount += doseString.charAt(charNr);
											}
											amount = amount.replace(",", ".");
											amountUnit = doseString.substring(amount.length());
											if ((!amountUnit.equals("")) && (amountUnit.substring(0, 1).equals("-"))) { // Solve things like 5-WATER
												amount = "";
												amountUnit = "";
											}
											else {
												amountUnit = amountUnit.equals("") ? denominatorUnit : (denominatorUnit.equals("") ? amountUnit : amountUnit + "/" + denominatorUnit);
											}
										}
										
										ingredientNames.add(ingredientName);
										ingredientAmounts.add(amount);
										ingredientAmountUnits.add(amountUnit);
									}
								}

								for (int ingredientNr = 0; ingredientNr < ingredientNames.size(); ingredientNr++) {
									String ingredientName = ingredientNames.get(ingredientNr);
									
									if (ingredientName != null) {
										Double amount = null;
										if ((ingredientAmounts.get(ingredientNr) != null) && (!ingredientAmounts.get(ingredientNr).equals(""))) {
											try {
												amount = Double.parseDouble(ingredientAmounts.get(ingredientNr));
											} catch (NumberFormatException e) {
												amount = null;
											}
										}
										String amountUnit = ingredientAmountUnits.get(ingredientNr);
										
										GNK gnk = gnkNameMap.get(ingredientName);
										
										sourceDrug.addIngredient(gnk == null ? "" : gnk.nr.toString(), ingredientName, null, amount, amountUnit, gnk == null ? null : gnk.casNumber);
									}
								}
							}
						}
					}

					for (SourceIngredient sourceIngredient : sourceDrug.getIngredients()) {
						sourceIngredient.setDosageUnit(sourceIngredient.getDosageUnit().replaceAll("/Stuk", ""));
						if (sourceIngredient.getDosageUnit().equals("Stuk")) {
							sourceIngredient.setDosageUnit("");
						}
					}
				}
			}
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}
	
	
	private String getAtcOfSingleIngredientGPK(GNK gnk) {
		String foundATC = null;
		if (gnk != null) {
			List<GPK> gpksWithSameIngredient = gnkOneIngredientGpkMap.get(gnk);
			if (gpksWithSameIngredient != null) {
				for (GPK gpkWithSameIngredient : gpksWithSameIngredient) {
					if ((gpkWithSameIngredient.atcCode != null) && (!gpkWithSameIngredient.atcCode.equals(""))) {
						if (foundATC == null) {
							foundATC = gpkWithSameIngredient.atcCode;
						}
						else if (!foundATC.equals(gpkWithSameIngredient.atcCode)) {
							foundATC = null;
							break;
						}
					}
				}
			}
		}
		if ((foundATC != null) && (foundATC.length() != 7)) {
			foundATC = null;
		}
		return foundATC;
	}
	
	
	private String cleanupExtractedIngredientName(String ingredientName) {
		for (String word : wordsToRemove) {
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName);
			
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll("^" + word + " ", " "));
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll(" " + word + " ", " "));
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll(" " + word + ",", " "));
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll("," + word + " ", " "));
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll("," + word + ",", " "));
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll(", " + word + " ", " "));
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll(", " + word + ", ", " "));
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll(", " + word + ",", " "));

			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll(" " + word + "$", ""));
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll("," + word + "$", ""));
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll(", " + word + "$", ""));
			ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.replaceAll(",$", ""));
			
			if (ingredientName.endsWith(" " + word)) {
				ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName.substring(0, ingredientName.length() - word.length()));
			}
			else if ((ingredientName.startsWith(word + " "))) {
				ingredientName = null;
				break;
			}
			else if (ingredientName.equals(word)) {
				ingredientName = null;
				break;
			}
			if (ingredientName != null) {
				ingredientName = DrugMappingStringUtilities.removeExtraSpaces(ingredientName);
				if ((ingredientName.length() > 0) && (("(),".contains(ingredientName.substring(0, 1))) || (")".contains(ingredientName.substring(ingredientName.length() - 1))))) {
					ingredientName = null;
					break;
				}
			}
		}
		
		if (ingredientName != null) {
			if (!ingredientName.equals("")) {
				try {
					Double.valueOf(ingredientName);
					ingredientName = null;
				}
				catch (NumberFormatException exception) {
					// Do nothing
				}
			}
			else {
				ingredientName = null;
			}
		}
		return ingredientName;
	}
	
	
	private String getDoseString(String fullName) {
		String doseString = null;
		
		// Remove piece between parenthesis at the end
		if ((!fullName.equals("")) && fullName.substring(fullName.length() - 1).equals(")")) {
			int openParenthesisIndex = fullName.lastIndexOf("(");
			if (openParenthesisIndex > 0) {
				fullName = fullName.substring(0, openParenthesisIndex).trim();
			}
		}
		
		// Get the last part as dose information
		int lastSpaceIndex = fullName.lastIndexOf(" ");
		if (lastSpaceIndex > -1) {
			doseString = fullName.substring(lastSpaceIndex + 1);
		}
		return doseString;
	}
	
	
	private class GNK {
		public Integer nr              = null;
		public String  description     = null;
		public String  casNumber       = null;
		public String  baseName        = null;
		public String  chemicalFormula = null;
		
		public boolean ok = true;
		
		public GNK(Integer nr, String description, String casNumber, String baseName, String chemicalFormula) {
			this.nr              = nr;
			this.description     = description == null ? null : description.trim();
			this.casNumber       = casNumber == null ? null : DrugMappingNumberUtilities.uniformCASNumber(casNumber.trim());
			this.baseName        = baseName == null ? null : baseName.trim();
			this.chemicalFormula = chemicalFormula == null ? null : chemicalFormula.trim();
			
			gnkMap.put(nr, this);
			if ((description != null) && (!description.equals(""))) {
				if (gnkNameMap.get(description) == null) {
					gnkNameMap.put(description, this);
				}
				else {
					System.out.println("   ERROR: Duplicate GNK name '" + description + "' (" + gnkNameMap.get(description).nr + " <-> " + nr + ")");
					ok = false;
				}
			}
		}
	}
	
	
	private class GSK {
		public Integer            nr      = null;
		public List<GSKComponent> gnkList = new ArrayList<GSKComponent>();
		
		public GSK(Integer nr) {
			this.nr = nr;
			
			gskMap.put(nr, this);
		}
		
		public void addGNK(Integer gnkNr, String partNumber, String type, String amount, String amountUnit, String genericName, String casNumber) {
			if (gnkNr != null) {
				GNK gnk = gnkMap.get(gnkNr);
				if (gnk != null) {
					gnkList.add(new GSKComponent(nr, partNumber, type, amount, amountUnit, gnk, genericName, casNumber));
				}
			}
		}
	}
	
	
	private class GSKComponent {
		public Integer   gskNr       = null;
		public String    partNumber  = null;
		public String    type        = null;
		public String    amount      = null;
		public String    amountUnit  = null;
		public GNK       gnk         = null;
		public String    genericName = null;
		public String    casNumber   = null;
		
		public GSKComponent(Integer gskNr, String partNumber, String type, String amount, String amountUnit, GNK gnk, String genericName, String casNumber) {
			this.gskNr       = gskNr;
			this.partNumber  = partNumber == null ? null : partNumber.trim();
			this.type        = type == null ? "" : type.trim();
			this.amount      = amount == null ? null : amount.trim();
			this.amountUnit  = amountUnit == null ? null : amountUnit.trim();
			this.gnk         = gnk;
			this.genericName = genericName == null ? null : genericName.trim();
			this.casNumber   = casNumber == null ? null : DrugMappingNumberUtilities.uniformCASNumber(casNumber.trim());
		}
	}
	
	
	private class GPK {
		public Integer nr               = null;
		public String  memoCode         = null;
		public String  labelName        = null;
		public String  shortName        = null;
		public String  fullName         = null;
		public String  atcCode          = null;
		public GSK     gsk              = null;
		public String  dddPerHPKUnit    = null;
		public String  prescriptionDays = null;
		public String  hpkmg            = null;
		public String  hpkmgUnit        = null;
		public String  pharmForm        = null;
		public String  basicUnit        = null;
		
		public GPK(Integer nr, String memoCode, String labelName, String shortName, String fullName, String atcCode, Integer gskNr, String dddPerHPKUnit, String prescriptionDays, String hpkmg, String hpkmgUnit, String pharmForm, String basicUnit) {
			this.nr               = nr;
			this.memoCode         = memoCode == null ? "" : memoCode.trim();
			this.labelName        = labelName == null ? "" : labelName.trim();
			this.shortName        = shortName == null ? "" : shortName.trim();
			this.fullName         = fullName == null ? "" : fullName.trim();
			this.atcCode          = atcCode == null ? "" : atcCode.trim();
			this.gsk              = gskNr == null ? null : gskMap.get(gskNr);
			this.dddPerHPKUnit    = dddPerHPKUnit == null ? "" : dddPerHPKUnit.trim();
			this.prescriptionDays = prescriptionDays == null ? "" : prescriptionDays.trim();
			this.hpkmg            = hpkmg == null ? "" : hpkmg.trim();
			this.hpkmgUnit        = hpkmgUnit == null ? "" : hpkmgUnit.trim();
			this.pharmForm        = pharmForm == null ? "" : pharmForm.trim();
			this.basicUnit        = basicUnit == null ? "" : basicUnit.trim();

			gpkList.add(this);
			gpkMap.put(nr, this);
		}
	}
	
	
	private class GPKIPCI {
		public Integer nr              = null;
		public String  partNumber      = null;
		public String  type            = null;
		public String  amount          = null;
		public String  amountUnit      = null;
		public GNK     gnk             = null;
		public String  gnkName         = null;
		public String  casNumber       = null;
		public String  baseName        = null;
		public String  chemicalFormula = null;
		
		public GPKIPCI(Integer nr, String partNumber, String type, String amount, String amountUnit, Integer gnkNr, String gnkName, String casNumber, String baseName, String chemicalFormula) {
			this.nr              = nr;
			this.partNumber      = partNumber == null ? null : partNumber.trim();
			this.type            = type == null ? null : type.trim();
			this.amount          = amount == null ? null : amount.trim();
			this.amountUnit      = amountUnit == null ? null : amountUnit.trim();
			this.gnk             = gnkNr == null ? null : gnkMap.get(gnkNr);
			this.gnkName         = gnkName == null ? null : gnkName.trim();
			this.casNumber       = casNumber == null ? null : DrugMappingNumberUtilities.uniformCASNumber(casNumber.trim());
			this.baseName        = baseName == null ? null : baseName.trim();
			this.chemicalFormula = chemicalFormula == null ? null : chemicalFormula.trim();
			
			List<GPKIPCI> gpkIPCI = gpkIPCIMap.get(nr);
			if (gpkIPCI == null) {
				gpkIPCI = new ArrayList<GPKIPCI>();
				gpkIPCIMap.put(nr, gpkIPCI);
			}
			gpkIPCI.add(this);
		}
	}
}
