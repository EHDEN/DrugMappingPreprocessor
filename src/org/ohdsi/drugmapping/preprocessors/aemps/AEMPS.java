package org.ohdsi.drugmapping.preprocessors.aemps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ohdsi.drugmapping.DrugMappingPreprocessor;
import org.ohdsi.drugmapping.files.DelimitedFileRow;
import org.ohdsi.drugmapping.files.XMLFile.XMLNode;
import org.ohdsi.drugmapping.gui.MainFrame;
import org.ohdsi.drugmapping.gui.files.DelimitedInputFileGUI;
import org.ohdsi.drugmapping.gui.files.XMLInputFileGUI;
import org.ohdsi.drugmapping.preprocessors.Preprocessor;
import org.ohdsi.drugmapping.source.SourceDrug;
import org.ohdsi.drugmapping.utilities.DrugMappingDateUtilities;

public class AEMPS extends Preprocessor {
	private static final long serialVersionUID = -1201878622903991675L;

	
	private Map<String, XMLNode> ingredientsMap = new HashMap<String, XMLNode>(); 
	private Map<String, XMLNode> formulationsMap = new HashMap<String, XMLNode>(); 
	private List<XMLNode> drugsList = new ArrayList<XMLNode>();
	private Map<String, Long> countsMap = new HashMap<String, Long>();


	public AEMPS(DrugMappingPreprocessor drugMapping, MainFrame mainFrame) {
		super(drugMapping, mainFrame, "AEMPS", new AEMPSInputFiles());
	}
	
	
	public String getOutputFileName() {
		return "AEMPS.csv";
	}
	
	/*
	public void run(String outputFileName) {
		if (getInputFile("AEMPS Drugs File")               != null) getInputFile("AEMPS Drugs File").logFileSettings();
		if (getInputFile("AEMPS Active Ingredients File")  != null) getInputFile("AEMPS Active Ingredients File").logFileSettings();
		if (getInputFile("AEMPS Dose Forms File")          != null) getInputFile("AEMPS Dose Forms File").logFileSettings();
		if (getInputFile("AEMPS Code Counts File")         != null) getInputFile("AEMPS Code Counts File").logFileSettings();
		new AEMPSPreprocessor(
				(XMLInputFileGUI)       getInputFile("AEMPS Drugs File"),
				(XMLInputFileGUI)       getInputFile("AEMPS Active Ingredients File"), 
				(XMLInputFileGUI)       getInputFile("AEMPS Dose Forms File"),
				(DelimitedInputFileGUI) getInputFile("AEMPS Code Counts File"),
				outputFileName);
	}
	*/

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
		boolean result = getAEMPSPrecsripcionPrincipiosActivos((XMLInputFileGUI) getInputFile("AEMPS Active Ingredients File"));
		result = result && getAEMPSPrescripcionFormasFarmaceuticas((XMLInputFileGUI) getInputFile("AEMPS Dose Forms File"));
		result = result && getAEMPSPrescripcion((XMLInputFileGUI) getInputFile("AEMPS Drugs File"));
		result = result && getAEMPSSourceDataCounts((DelimitedInputFileGUI) getInputFile("AEMPS Code Counts File"));
		result = result && buildSourceDrugs();
		return result;
	}


	private boolean getAEMPSPrecsripcionPrincipiosActivos(XMLInputFileGUI principiosActivosFile) {
		boolean result = true;
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading AEMPS Active Ingredients File ...");
		Integer ingredientCount = 0;
		if (principiosActivosFile.openFileForReading()) {
			for (XMLNode principiosActivo : principiosActivosFile.getXMLRoot().getChildren("principiosactivos")) {
				String nroprincipioactivo = principiosActivo.getValue("nroprincipioactivo");
				if (nroprincipioactivo != null) {
					ingredientsMap.put(nroprincipioactivo, principiosActivo);
					ingredientCount++;
				}
			}
	        System.out.println(DrugMappingDateUtilities.getCurrentTime() + "    Principios Activos  : " + ingredientCount);
		}  
		else {
			System.out.println("ERROR: Cannot read AEMPS Active Ingredients File \"" + principiosActivosFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
        return result;
	}


	private boolean getAEMPSPrescripcionFormasFarmaceuticas(XMLInputFileGUI formasFarmaceuticasFile) {
		boolean result = true;
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading AEMPS Dose Forms File ...");
		Integer formulationCount = 0;
		if (formasFarmaceuticasFile.openFileForReading()) {
			for (XMLNode formaFarmaceutica : formasFarmaceuticasFile.getXMLRoot().getChildren("formasfarmaceuticas")) {
				String codigoformafarmaceutica = formaFarmaceutica.getValue("codigoformafarmaceutica");
				if (codigoformafarmaceutica != null) {
					formulationsMap.put(codigoformafarmaceutica, formaFarmaceutica);
					formulationCount++;
				}
			}
	        System.out.println(DrugMappingDateUtilities.getCurrentTime() + "    Formas Farmaceuticas: " + formulationCount); 
		}
		else {
			System.out.println("ERROR: Cannot read AEMPS Dose Forms File \"" + formasFarmaceuticasFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
        return result; 
	}


	private boolean getAEMPSPrescripcion(XMLInputFileGUI prescripcionFile) {
		boolean result = true;
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading AEMPS Drugs File ...");
		Integer drugCount = 0;
		if (prescripcionFile.openFileForReading()) {
			for (XMLNode prescription : prescripcionFile.getXMLRoot().getChildren("prescription")) {
				drugsList.add(prescription);
				drugCount++;
			}
	        System.out.println(DrugMappingDateUtilities.getCurrentTime() + "    Prescriptions: " + drugCount);
		}
		else {
			System.out.println("ERROR: Cannot read AEMPS Drugs File \"" + prescripcionFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
        return result;
	}
	
	
	private boolean getAEMPSSourceDataCounts(DelimitedInputFileGUI codeCountsFile) {
		boolean result = true;
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Loading AEMPS Code Counts File ...");
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
						warnings.add("Illegal count \"" + countString + "\"");
					}
				}

				countsMap.put(drugNr, count);
				countsCount++;
			}
	        System.out.println(DrugMappingDateUtilities.getCurrentTime() + "    Counts: " + countsCount);
		}
		else {
			System.out.println("ERROR: Cannot read AEMPS Code Counts File \"" + codeCountsFile.getFileName() + "\"!");
			result = false;
		}
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
        return result;
	}
	
	
	public boolean buildSourceDrugs() {
		boolean result = true;
		
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Building source drugs");

		XMLNode lastPrescription = null;
		try {
			for (XMLNode prescription : drugsList) {
				lastPrescription = prescription;
				
				SourceDrug sourceDrug = source.addSourceDrug(prescription.getValue("cod_nacion"), prescription.getValue("des_nomco"), countsMap.get(prescription.getValue("cod_nacion")) == null ? -1L : countsMap.get(prescription.getValue("cod_nacion")));
				if (prescription.getChild("atc") != null) {
					sourceDrug.addATC(prescription.getChild("atc").getValue("cod_atc"));
				}
				
				XMLNode formasfarmaceuticas = prescription.getChild("formasfarmaceuticas");
				if (formasfarmaceuticas != null) {
					XMLNode formulation = formasfarmaceuticas.getValue("cod_forfar") == null ? null : formulationsMap.get(formasfarmaceuticas.getValue("cod_forfar"));
					if (formulation != null) {
						
						sourceDrug.addFormulation(formulation.getValue("formafarmaceutica"));
						
						// Get ingredients
						if (formasfarmaceuticas.getChildren("composicion_pa").size() > 0) {
							for (XMLNode ingredientDefinition : formasfarmaceuticas.getChildren("composicion_pa")) {
								if (ingredientDefinition.getValue("cod_principio_activo") != null) {
									String ingredientId = ingredientDefinition.getValue("cod_principio_activo");
									XMLNode ingredient = ingredientId == null ? null : ingredientsMap.get(ingredientId);
									if (ingredient != null) {
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
											warnings.add("No dosage available for ingredient " + ingredientDefinition.getValue("cod_principio_activo") + "\r\n" + prescription.toString("         "));
										}

										if ((dosage == null) || (dosage <= 0)) {
											dosage = null;
										}                                                                                              // CASNumber
										
										sourceDrug.addIngredient(ingredientId, ingredient.getValue("principioactivo"), null, dosage, unit, null);
									}
								}
							}
						}
					}
				}
			}
		}
		catch (Exception exception) {
			if (lastPrescription != null) {
				System.out.println(lastPrescription);
			}
			exception.printStackTrace();
			result = false;
		}
		
		System.out.println(DrugMappingDateUtilities.getCurrentTime() + "   Done");
		
		return result;
	}
}
