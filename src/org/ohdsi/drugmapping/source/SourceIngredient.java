package org.ohdsi.drugmapping.source;

import org.ohdsi.drugmapping.utilities.DrugMappingNumberUtilities;
import org.ohdsi.drugmapping.utilities.DrugMappingStringUtilities;

public class SourceIngredient {
	private String code = "";
	private String name = "";
	private String nameEnglish = "";
	private Double dosage = null;
	private String dosageUnit = "";
	private String casNumber = "";
    
    
    public static String getHeader() {
    	String header = "IngredientCode";
    	header += "," + "IngredientName";
    	header += "," + "IngredientNameEnglish";
    	header += "," + "Dosage";
    	header += "," + "DosageUnit";
    	header += "," + "CASNumber";
    	return header;
    }
    
    
    public static String emptyRecord() {
    	String emptyRecord = "";
    	emptyRecord += "," + "";
    	emptyRecord += "," + "";
    	emptyRecord += "," + "";
    	emptyRecord += "," + "";
    	emptyRecord += "," + "";
    	return emptyRecord;
    }

	
	public SourceIngredient(String code, String name, String nameEnglish, Double dosage, String dosageUnit, String casNumber) {
		this.code = code;
		this.name = name;
		this.nameEnglish = nameEnglish;
		this.dosage = dosage;
		this.dosageUnit = dosageUnit;
		this.casNumber = casNumber;
	}
	
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public void setNameEnglish(String nameEnglish) {
		this.nameEnglish = nameEnglish;
	}
	
	
	public void setDosage(Double dosage) {
		this.dosage = dosage;
	}
	
	
	public void setDosageUnit(String dosageUnit) {
		this.dosageUnit = dosageUnit;
	}
	
	
	public String getDosageUnit() {
		return dosageUnit;
	}
	
	
	public void setCASNumber(String casNumber) {
		this.casNumber = casNumber;
	}
	
	
	public String toString() {
		String outputDosage = DrugMappingNumberUtilities.doubleWithPrecision(dosage, Source.dosagePrecision);
		String record = DrugMappingStringUtilities.escapeFieldValue(code == null ? "" : code.replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " "));
		record += "," + DrugMappingStringUtilities.escapeFieldValue(name == null ? "" : name.replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " "));
		record += "," + DrugMappingStringUtilities.escapeFieldValue(nameEnglish == null ? "" : nameEnglish.replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " "));
		record += "," + (outputDosage == null ? "" : outputDosage);
		record += "," + DrugMappingStringUtilities.escapeFieldValue(dosageUnit == null ? "" : dosageUnit.replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " "));
		record += "," + DrugMappingStringUtilities.escapeFieldValue(casNumber == null ? "" : casNumber).replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " ");
		
		return record;
	}
}
