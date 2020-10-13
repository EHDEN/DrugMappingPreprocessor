package org.ohdsi.drugmapping.medaman;

import java.util.ArrayList;
import java.util.Arrays;

import org.ohdsi.drugmapping.files.FileColumnDefinition;
import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.files.InputFileDefinition;

public class MEDAMANPreprocessorInputFiles extends InputFileDefinition {

	
	public MEDAMANPreprocessorInputFiles() {
		inputFiles = new ArrayList<FileDefinition>(
				Arrays.asList(
						new FileDefinition(
								"MEDAMAN Drug File",
								new String[] {
										"This file should contain MEDAMAN Drug definitions."
						  		},
								FileDefinition.EXCEL_FILE,
								new FileColumnDefinition[] {
										new FileColumnDefinition(
												"DrugCode",
												new String[] {
														"This is the code of the drug."
												}
										),
										new FileColumnDefinition(
												"DrugName",
												new String[] {
														"This is the name of the drug."
												}
										),
										new FileColumnDefinition(
												"DoseForm",
												new String[] {
														"This is the dose form of the drug."
												}
										),
										new FileColumnDefinition(
												"IngredientCode",
												new String[] {
														"This is code of the ingredient."
												}
										),
										new FileColumnDefinition(
												"IngredientName",
												new String[] {
														"This is the name of the ingredient."
												}
										),
										new FileColumnDefinition(
												"AmountNumerator",
												new String[] {
														"This is the amount numerator of the ingredient."
												}
										),
										new FileColumnDefinition(
												"AmountNumeratorUnit",
												new String[] {
														"This is the unit of the amount numerator of the ingredient."
												}
										),
										new FileColumnDefinition(
												"AmountDenominator",
												new String[] {
														"This is the amount denominator of the ingredient."
												}
										),
										new FileColumnDefinition(
												"AmountDenominatorUnit",
												new String[] {
														"This is the unit of the amount denominator of the ingredient."
												}
										),
										new FileColumnDefinition(
												"AmountNumeratorTop",
												new String[] {
														"This is the maximum amount numerator of the ingredient."
												}
										),
										new FileColumnDefinition(
												"AmountNumeratorTopUnit",
												new String[] {
														"This is the unit of the maximum amount numerator of the ingredient."
												}
										),
								}
						),
						new FileDefinition(
								"MEDAMAN ATC File",
								new String[] {
										"This file should contain the ATC codes of the drugs."
						  		},
								FileDefinition.EXCEL_FILE,
								new FileColumnDefinition[] {
										new FileColumnDefinition(
												"DrugCode",
												new String[] {
														"This is the code of the drug."
												}
										),
										new FileColumnDefinition(
												"ATC",
												new String[] {
														"This is the ATC code of the drug."
												}
										)
								}
						)
				)
			);
	}
}
