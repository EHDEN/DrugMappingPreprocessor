package org.ohdsi.drugmapping.preprocessors.laegemiddelstyrelsen;

import java.util.ArrayList;
import java.util.Arrays;

import org.ohdsi.drugmapping.files.FileColumnDefinition;
import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.files.InputFileDefinition;

public class LaegemiddelstyrelsenInputFiles extends InputFileDefinition {

	
	public LaegemiddelstyrelsenInputFiles() {
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
														"This is the unit of the drug."
												}
										)
								}
						),
						new FileDefinition(
								"Laegemiddelstyrelsen Active Compounds File",
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
								"Laegemiddelstyrelsen Retired Compounds File",
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
