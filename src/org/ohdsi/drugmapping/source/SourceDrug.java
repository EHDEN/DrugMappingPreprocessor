package org.ohdsi.drugmapping.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ohdsi.drugmapping.utilities.DrugMappingStringUtilities;

public class SourceDrug {
	private String code = "";
	private String name = "";
	private Long count = null;
	private List<String> atcCodes = new ArrayList<String>();
	private List<String> formulations = new ArrayList<String>();
    private List<SourceIngredient> ingredients = new ArrayList<SourceIngredient>();
    
    
    public static String getHeader() {
    	String header = "SourceCode";
    	header += "," + "SourceName";
    	header += "," + "SourceATCCode";
    	header += "," + "SourceFormulation";
    	header += "," + "SourceCount";
    	header += "," + SourceIngredient.getHeader();
    	return header;
    }
	

    public SourceDrug(String code, String name, Long count) {
    	this.code = code;
    	this.name = name;
    	this.count = count; 
    }
	
	
	public void setName(String name) {
		this.name = name;
	}
    
    
    public void addATC(String atcCode) {
    	if ((atcCode != null) && (!atcCodes.contains(atcCode))) {
    		atcCodes.add(atcCode);
    	}
    }
    
    
    public void addFormulation(String formulation) {
    	if ((formulation != null) && (!formulations.contains(formulation))) {
    		formulations.add(formulation);
    	}
    }
    
    
    public void setCount(Long count) {
    	this.count = count;
    }
    
    
    public void addIngredient(String code, String name, String nameEnglish, Double dosage, String dosageUnit, String casNumber) {
    	ingredients.add(new SourceIngredient(code, name, nameEnglish, dosage, dosageUnit, casNumber));
    }
    
    
    public void addIngredient(SourceIngredient ingredient) {
    	ingredients.add(ingredient);
    }
    
    
    public String toString() {
    	String atc = "";
    	for (String atcCode : atcCodes) {
    		atc += (atc.equals("") ? "" : "|") + atcCode;
    	}
    	
    	String form = "";
    	for (String formulation : formulations) {
    		form += (form.equals("") ? "" : "|") + formulation;
    	}
    	
    	String record = DrugMappingStringUtilities.escapeFieldValue(code);
    	record += "," + DrugMappingStringUtilities.escapeFieldValue(name);
    	record += "," + DrugMappingStringUtilities.escapeFieldValue(atc);
    	record += "," + DrugMappingStringUtilities.escapeFieldValue(form);
    	record += "," + (count == null ? "" : count);
    	
    	return record;
    }
    
    
    public List<String> getDescription(boolean sortATCCodes, boolean sortFormulations) {
    	List<String> description = new ArrayList<String>();
    	if (sortATCCodes) {
    		Collections.sort(atcCodes);
    	}
    	if (sortFormulations) {
    		Collections.sort(formulations);
    	}
    	String drugRecord = toString();
    	if (ingredients.size() > 0) {
    		for (SourceIngredient ingredient : ingredients) {
    			description.add(drugRecord + "," + ingredient);
    		}
    	}
    	else {
			description.add(drugRecord + "," + SourceIngredient.emptyRecord());
    	}
    	
    	return description;
    }
}
