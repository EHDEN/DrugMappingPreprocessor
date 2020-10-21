package org.ohdsi.drugmapping.source;

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
    	header += "," + "CASNumer";
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
		String record = DrugMappingStringUtilities.escapeFieldValue(code);
		record += "," + DrugMappingStringUtilities.escapeFieldValue(name);
		record += "," + DrugMappingStringUtilities.escapeFieldValue(nameEnglish);
		record += "," + (dosage == null ? "" : dosage);
		record += "," + dosageUnit;
		record += "," + (casNumber == null ? "" : casNumber);
		
		return record;
	}
}
