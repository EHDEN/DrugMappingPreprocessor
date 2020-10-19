package org.ohdsi.drugmapping.preprocessors.laegemiddelstyrelsen;

import java.util.ArrayList;
import java.util.Arrays;

import org.ohdsi.drugmapping.files.FileColumnDefinition;
import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.files.InputFileDefinition;

public class LaegemiddelstyrelsenPreprocessorInputFiles extends InputFileDefinition {

	
	public LaegemiddelstyrelsenPreprocessorInputFiles() {
		inputFiles = new ArrayList<FileDefinition>(
				Arrays.asList(
						new FileDefinition(
								"Laegemiddelstyrelsen Drugs File",
								new String[] {
										"This file should contain Laegemiddelstyrelsen Drug definitions."
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
												"Strength",
												new String[] {
														"This is the strength description of the drug."
												}
										),
										new FileColumnDefinition(
												"ATC",
												new String[] {
														"This is the ATC of the drug."
												}
										),
										new FileColumnDefinition(
												"DrugId",
												new String[] {
														"This is The ID of the drug."
												}
										),
										new FileColumnDefinition(
												"DrugUnit",
												new String[] {
														"This is the unit of the maximum amount numerator of the ingredient."
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
										)
								}
						),
						new FileDefinition(
								"Laegemiddelstyrelsen Active Ingredients File",
								new String[] {
										"This file should contain the active drugs of Laegemiddelstyrelsen."
						  		},
								FileDefinition.EXCEL_FILE,
								new FileColumnDefinition[] {
										new FileColumnDefinition(
												"DrugId",
												new String[] {
														"This is The ID of the drug."
												}
										),
										new FileColumnDefinition(
												"IngredientName",
												new String[] {
														"This is the name of the ingredient."
												}
										),
										new FileColumnDefinition(
												"IngredientNameEnglish",
												new String[] {
														"This is the English name of the ingredient."
												}
										),
										new FileColumnDefinition(
												"Amount",
												new String[] {
														"This is the amount of the ingredient."
												}
										),
										new FileColumnDefinition(
												"AmountUnit",
												new String[] {
														"This is the unit of the amount of the ingredient."
												}
										)
								}
						),
						new FileDefinition(
								"Laegemiddelstyrelsen Retired Ingredients File",
								new String[] {
										"This file should contain the retired drugs of Laegemiddelstyrelsen."
						  		},
								FileDefinition.EXCEL_FILE,
								new FileColumnDefinition[] {
										new FileColumnDefinition(
												"DrugId",
												new String[] {
														"This is The ID of the drug."
												}
										),
										new FileColumnDefinition(
												"IngredientName",
												new String[] {
														"This is the name of the ingredient."
												}
										),
										new FileColumnDefinition(
												"IngredientNameEnglish",
												new String[] {
														"This is the English name of the ingredient."
												}
										),
										new FileColumnDefinition(
												"Amount",
												new String[] {
														"This is the amount of the ingredient."
												}
										),
										new FileColumnDefinition(
												"AmountUnit",
												new String[] {
														"This is the unit of the amount of the ingredient."
												}
										)
								}
						),
						new FileDefinition(
								"Laegemiddelstyrelsen ScanReport File",
								new String[] {
										"This file should contain the WhiteRabbit drugs scanreport of Laegemiddelstyrelsen."
						  		},
								FileDefinition.EXCEL_FILE,
								new FileColumnDefinition[] {
										new FileColumnDefinition(
												"DrugCode",
												new String[] {
														"This is the code of the drug. The count is in the next column."
												}
										)
								}
						)
				)
			);
	}
}
