package org.ohdsi.drugmapping.aemps;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ohdsi.drugmapping.files.DelimitedFileRow;
import org.ohdsi.drugmapping.files.XMLFile.XMLNode;
import org.ohdsi.drugmapping.gui.files.DelimitedInputFileGUI;
import org.ohdsi.drugmapping.gui.files.Folder;
import org.ohdsi.drugmapping.gui.files.XMLInputFileGUI;
import org.ohdsi.drugmapping.utilities.DrugMappingFileUtilities;

public class AEMPSPreprocessor {
	
	public Map<String, XMLNode> ingredientsMap = new HashMap<String, XMLNode>(); 
	public Map<String, XMLNode> formulationsMap = new HashMap<String, XMLNode>(); 
	public List<XMLNode> drugsList = new ArrayList<XMLNode>();
	public Map<String, Long> countsMap = new HashMap<String, Long>();
	
	private boolean debug = false;


	public AEMPSPreprocessor(XMLInputFileGUI principiosActivosFile, XMLInputFileGUI formasFarmaceuticasFile, XMLInputFileGUI prescripcionFile, DelimitedInputFileGUI codeCountsFile, Folder outputFolder, boolean debug) {
		this.debug = debug;

		getData(principiosActivosFile, formasFarmaceuticasFile, prescripcionFile, codeCountsFile);
		writeDrugMappingFile(outputFolder);
	}


	private void getData(XMLInputFileGUI principiosActivosFile, XMLInputFileGUI formasFarmaceuticasFile, XMLInputFileGUI prescripcionFile, DelimitedInputFileGUI codeCountsFile) {
		System.out.println("  Loading data");
		getAEMPSPrecsripcionPrincipiosActivos(principiosActivosFile);
		getAEMPSPrescripcionFormasFarmaceuticas(formasFarmaceuticasFile);
		getAEMPSPrescripcion(prescripcionFile);
		getAEMPSSourceDataCounts(codeCountsFile);
		System.out.println("  Done");
		System.out.println();
	}


	private void getAEMPSPrecsripcionPrincipiosActivos(XMLInputFileGUI principiosActivosFile) {
		Integer ingredientCount = 0;
		if (principiosActivosFile.openFileForReading()) {
			for (XMLNode principiosActivo : principiosActivosFile.getXMLRoot().getChildren("principiosactivos")) {
				String nroprincipioactivo = principiosActivo.getValue("nroprincipioactivo");
				if (nroprincipioactivo != null) {
					ingredientsMap.put(nroprincipioactivo, principiosActivo);
					ingredientCount++;
				}
			}
		}
        System.out.println("    Principios Activos  : " + ingredientCount);  
	}


	private void getAEMPSPrescripcionFormasFarmaceuticas(XMLInputFileGUI formasFarmaceuticasFile) {
		Integer formulationCount = 0;
		if (formasFarmaceuticasFile.openFileForReading()) {
			for (XMLNode formaFarmaceutica : formasFarmaceuticasFile.getXMLRoot().getChildren("formasfarmaceuticas")) {
				String codigoformafarmaceutica = formaFarmaceutica.getValue("codigoformafarmaceutica");
				if (codigoformafarmaceutica != null) {
					formulationsMap.put(codigoformafarmaceutica, formaFarmaceutica);
					formulationCount++;
				}
			}
		}
        System.out.println("    Formas Farmaceuticas: " + formulationCount);  
	}


	private void getAEMPSPrescripcion(XMLInputFileGUI prescripcionFile) {
		Integer drugCount = 0;
		if (prescripcionFile.openFileForReading()) {
			for (XMLNode prescription : prescripcionFile.getXMLRoot().getChildren("prescription")) {
				drugsList.add(prescription);
				drugCount++;
			}
		}  
        System.out.println("    Prescriptions       : " + drugCount);
	}
	
	
	private void getAEMPSSourceDataCounts(DelimitedInputFileGUI codeCountsFile) {
		Integer countsCount = 0;
		if (codeCountsFile.openFileForReading()) {
			while (codeCountsFile.hasNext()) {
				DelimitedFileRow row = codeCountsFile.next();
				String drugNr = codeCountsFile.get(row, "SourceCode", true).trim();
				String countString = codeCountsFile.get(row, "SourceCount", false).trim();

				Long count = -1L;
				if ((countString != null) && (!countString.equals(""))) {
					try {
						count = Long.parseLong(countString);
					} catch (NumberFormatException e) {
						count = -1L;
						System.out.println("Illegal count \"" + countString + "\"");
					}
				}

				countsMap.put(drugNr, count);
				countsCount++;
			}
	        System.out.println("    Counts              : " + countsCount);
		}
		else {
			System.out.println("Cannot read file \"" + codeCountsFile.getFileName() + "\"!");
		}
	}
	
	
	public void writeDrugMappingFile(Folder outputFolder) {
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
		
		String fileName = debug ? DrugMappingFileUtilities.getNextFileName(outputFolder.getFolderName(), "AEMPS.csv") : "AEMPS.csv";
		String logFileName = ((fileName.lastIndexOf(".") != -1) ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName) + " Log.txt";
		
		fileName = outputFolder.getFolderName() + (outputFolder.getFolderName().contains("\\") ? "\\" : "/") + fileName;
		logFileName = outputFolder.getFolderName() + (outputFolder.getFolderName().contains("\\") ? "\\" : "/") + logFileName;

		System.out.println("    Output file: " + fileName);
		System.out.println("    Log file   : " + logFileName);
		
		PrintWriter file = DrugMappingFileUtilities.openOutputFile(fileName, header);
		PrintWriter logFile = DrugMappingFileUtilities.openOutputFile(logFileName, null);

		Integer drugWarningCount = 0;
		Integer warningCount = 0;
		XMLNode lastPrescription = null;
		try {
			for (XMLNode prescription : drugsList) {
				lastPrescription = prescription;
				boolean warnings = false;
				
				String drugRecord = prescription.getValue("cod_nacion");                                                                                               // SourceCode
				drugRecord += "," + "\"" + prescription.getValue("des_nomco") + "\"";                                                                                  // SourceName
				drugRecord += "," + (prescription.getChild("atc") == null ? "" : prescription.getChild("atc").getValue("cod_atc"));                                    // SourceATCCode
				
				XMLNode formasfarmaceuticas = prescription.getChild("formasfarmaceuticas");
				if (formasfarmaceuticas != null) {
					XMLNode formulation = formasfarmaceuticas.getValue("cod_forfar") == null ? null : formulationsMap.get(formasfarmaceuticas.getValue("cod_forfar"));
					if (formulation != null) {
						drugRecord += "," + "\"" + formulation.getValue("formafarmaceutica") + "\"";                                                                  // SourceFormulation
						drugRecord += "," + (countsMap.get(prescription.getValue("cod_nacion")) == null ? "-1" : countsMap.get(prescription.getValue("cod_nacion"))); // SourceCount
						
						// Get ingredients
						List<String> ingredientRecords = new ArrayList<String>();
						if (formasfarmaceuticas.getChildren("composicion_pa").size() > 0) {
							for (XMLNode ingredientDefinition : formasfarmaceuticas.getChildren("composicion_pa")) {
								String ingredientRecord = "";
								if (ingredientDefinition.getValue("cod_principio_activo") != null) {
									String ingredientId = ingredientDefinition.getValue("cod_principio_activo");
									ingredientRecord += "," + (ingredientId == null ? "" : ingredientDefinition.getValue("cod_principio_activo"));                // IngredientCode
									XMLNode ingredient = ingredientId == null ? null : ingredientsMap.get(ingredientId);
									if (ingredient != null) {
										ingredientRecord += "," + "\"" + (ingredient.getValue("principioactivo") == null ? "" : ingredient.getValue("principioactivo")) + "\""; // IngredientName
										ingredientRecord += ",";                                                                                                  // IngredientNameEnglish
										
										Double dosage_pa = null;
										try {
											dosage_pa = ingredientDefinition.getValue("dosis_pa") == null ? null : Double.parseDouble(ingredientDefinition.getValue("dosis_pa").replace(",", "."));
										}
										catch (NumberFormatException exception) {
											dosage_pa = null;
										}
										String unit_pa  = (ingredientDefinition.getValue("unidad_dosis_pa") == null ? "" : ingredientDefinition.getValue("unidad_dosis_pa"));
										if (unit_pa.toLowerCase().startsWith("n/a")) {
											unit_pa = "";
										}
										/*
										Double dosis_administracion  = ingredientDefinition.getValue("dosis_administracion") == null ? null : Double.parseDouble(ingredientDefinition.getValue("dosis_administracion").replace(",", "."));
										String unidad_administracion  = (ingredientDefinition.getValue("unidad_administracion() == null ? "" : ingredientDefinition.getValue("unidad_administracion"));
										
										Double dosis_prescripcion  = ingredientDefinition.getValue("dosis_prescripcion") == null ? null : Double.parseDouble(ingredientDefinition.getValue("dosis_prescripcion").replace(",", "."));
										String unidad_prescripcion  = (ingredientDefinition.getValue("unidad_prescripcion") == null ? "" : ingredientDefinition.getValue("unidad_prescripcion"));
										*/
										Double dosage_com = null;
										try {
											dosage_com = ingredientDefinition.getValue("dosis_composicion") == null ? null : Double.parseDouble(ingredientDefinition.getValue("dosis_composicion").replace(",", "."));
										}
										catch (NumberFormatException exception) {
											dosage_com = null;
										}
										String unit_com = (ingredientDefinition.getValue("unidad_composicion") == null ? "" : ingredientDefinition.getValue("unidad_composicion"));
										if (unit_com.toLowerCase().startsWith("n/a")) {
											unit_com = "";
										}
										/*
										Double cantidad_volumen_unidad_administracion = ingredientDefinition.getValue("cantidad_volumen_unidad_administracion") == null ? null : Double.parseDouble(ingredientDefinition.getValue("cantidad_volumen_unidad_administracion").replace(",", "."));
										String unidad_volumen_unidad_administracion = (ingredientDefinition.getValue("unidad_volumen_unidad_administracion") == null ? "" : ingredientDefinition.getValue("unidad_volumen_unidad_administracion"));
										*/
										String unit = unit_pa;
										Double dosage = (dosage_pa == null ? null : dosage_pa);

										if (!unit_com.equals("")) {
											unit = unit_pa + "/" + unit_com;
										}
										if ((dosage_pa != null) && (dosage_com != null)) {
											dosage = dosage_pa / dosage_com; 
										}
										if (dosage == null) {
											logFile.println("WARNING: No dosage available for ingredient " + ingredientDefinition.getValue("cod_principio_activo"));
											logFile.println(prescription.toString("         "));
											warningCount++;
											warnings = true;
										}

										if ((dosage == null) || (dosage <= 0)) {
											dosage = null;
										}
										ingredientRecord += "," + (dosage == null ? "" : dosage);                                                                 // Dosage
										ingredientRecord += "," + unit;                                                                     // DosageUnit
										ingredientRecord += ",";                                                                                                  // CASNumber
									}
									else {
										ingredientRecord += ",";                                                                                                  // IngredientName
										ingredientRecord += ",";                                                                                                  // IngredientNameEnglish
										ingredientRecord += ",";                                                                                                  // Dosage
										ingredientRecord += ",";                                                                                                  // DosageUnit
										ingredientRecord += ",";                                                                                                  // CASNumber
									}
									ingredientRecords.add(ingredientRecord);
								}
							}
						}
						if (ingredientRecords.size() > 0) {
							for (String ingredientRecord : ingredientRecords) {
								file.println(drugRecord + ingredientRecord); 
							}
						}
						else {
							drugRecord += ",";                                                                                                                    // IngredientCode
							drugRecord += ",";                                                                                                                    // IngredientName
							drugRecord += ",";                                                                                                                    // IngredientNameEnglish
							drugRecord += ",";                                                                                                                    // Dosage
							drugRecord += ",";                                                                                                                    // DosageUnit
							drugRecord += ",";                                                                                                                    // CASNumber
							
							file.println(drugRecord);
						}
					}
					else {
						// No formulation and no ingredients specified
						drugRecord += ",";                                                                                                                        // SourceFormulation
						drugRecord += ",";                                                                                                                        // SourceCount
						drugRecord += ",";                                                                                                                        // IngredientCode
						drugRecord += ",";                                                                                                                        // IngredientName
						drugRecord += ",";                                                                                                                        // IngredientNameEnglish
						drugRecord += ",";                                                                                                                        // Dosage
						drugRecord += ",";                                                                                                                        // DosageUnit
						drugRecord += ",";                                                                                                                        // CASNumber
						
						file.println(drugRecord);
						
					}
				}
				else {
					// No formulation and no ingredients specified
					drugRecord += ",";                                                                                                                            // SourceFormulation
					drugRecord += ",";                                                                                                                            // SourceCount
					drugRecord += ",";                                                                                                                            // IngredientCode
					drugRecord += ",";                                                                                                                            // IngredientName
					drugRecord += ",";                                                                                                                            // IngredientNameEnglish
					drugRecord += ",";                                                                                                                            // Dosage
					drugRecord += ",";                                                                                                                            // DosageUnit
					drugRecord += ",";                                                                                                                            // CASNumber
					
					file.println(drugRecord);
				}
				if (warnings) {
					drugWarningCount++;
				}
			}
		}
		catch (Exception exception) {
			if (lastPrescription != null) {
				System.out.println(lastPrescription);
			}
			exception.printStackTrace();
		}
		
		DrugMappingFileUtilities.closeOutputFile(file);
		DrugMappingFileUtilities.closeOutputFile(logFile);
		
		System.out.println("    WARNINGS   : " + warningCount + " on " + drugWarningCount + " drugs");
		System.out.println("  Done");
		System.out.println();
	}


	public static void main(String[] args) {
		String settingsFile = null;
		boolean debug = false;
		
		boolean ok = true;

		System.out.println("AEMPS Preprocessor");
		
		// Get parameters
		for (String arg : args) {
			String[] argSplit = arg.split("=");
			if (argSplit[0].toLowerCase().equals("settingsfile")) {
				settingsFile = argSplit[1]; 
			}
			if (argSplit[0].toLowerCase().equals("debug")) {
				debug = true; 
			}
		}

		String principiosActivosFileName   = null;
		String formasFarmaceuticasFileName = null;
		String prescripcionFileName        = null;
		String codeCountsFileName          = null;
		String outputPath                  = null;
		
		if (settingsFile != null) {
			DrugMappingSettings settings = new DrugMappingSettings(settingsFile, false);
			if (settings.isOK()) {
				principiosActivosFileName   = settings.getStringValue("PrincipiosActivosFile");
				formasFarmaceuticasFileName = settings.getStringValue("FormasFarmaceuticasFile");
				prescripcionFileName        = settings.getStringValue("PrescripcionFile");
				codeCountsFileName          = settings.getStringValue("CodeCountsFile");
				outputPath                  = settings.getStringValue("OutputPath");
				
				if (principiosActivosFileName == null) {
					System.out.println("ERROR: PrincipiosActivosFile not specified!");
					ok = false;
				}
				if (formasFarmaceuticasFileName == null) {
					System.out.println("ERROR: FormasFarmaceuticasFile not specified!");
					ok = false;
				}
				if (prescripcionFileName == null) {
					System.out.println("ERROR: PrescripcionFile not specified!");
					ok = false;
				}
				if (codeCountsFileName == null) {
					System.out.println("ERROR: CodeCountsFile not specified!");
					ok = false;
				}
				if (outputPath == null) {
					System.out.println("ERROR: OutputPath not specified!");
					ok = false;
				}
			}
			else {
				ok = false;
			}
		}
		else {
			System.out.println("ERROR: No settings file specified!");
			ok = false;
		}
		
		if (ok) {
			DelimitedInputFileGUI countsFile = new DelimitedInputFileGUI(new AEMPSPreprocessorInputFiles().getInputFileDefinition("Code Counts File"));
			countsFile.setFileName(codeCountsFileName);
			Map<String, String> countsFileColumMapping = new HashMap<String, String>();
			countsFileColumMapping.put("SourceCode", "pf");
			countsFileColumMapping.put("SourceCount", "Count");
			countsFile.setColumnMapping(countsFileColumMapping);
			AEMPSPreprocessor aemps = new AEMPSPreprocessor(
					new XMLInputFileGUI("Principios Activos file", true, principiosActivosFileName),
					new XMLInputFileGUI("FormasFarmaceuticas file", true, formasFarmaceuticasFileName),
					new XMLInputFileGUI("Prescripcion file", true, prescripcionFileName),
					countsFile,
					new Folder("Output folder", "Output folder", outputPath),
					true
					);
		}
		
		System.out.println("Finished");
	}
}
