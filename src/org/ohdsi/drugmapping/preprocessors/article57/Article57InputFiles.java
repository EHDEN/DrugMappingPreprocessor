package org.ohdsi.drugmapping.preprocessors.article57;

import java.util.ArrayList;
import java.util.Arrays;

import org.ohdsi.drugmapping.files.FileColumnDefinition;
import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.files.InputFileDefinition;

public class Article57InputFiles extends InputFileDefinition {

	public Article57InputFiles() {
		inputFiles = new ArrayList<FileDefinition>(
				Arrays.asList(
						new FileDefinition(
								"Article57 Product Information File",
								new String[] {
										"This file should contain Article57 Drug definitions."
						  		},
								FileDefinition.DELIMITED_FILE,
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
												"ATCCode",
												new String[] {
														"This is the ATC code of the drug."
												}
										)
								}
						),
						new FileDefinition(
								"Article57 Substances Information File",
								new String[] {
										"This file should contain the compound definition of the Article57 drugs."
						  		},
								FileDefinition.DELIMITED_FILE,
								new FileColumnDefinition[] {
										new FileColumnDefinition(
												"DrugCode",
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
												"NumeratorAmount",
												new String[] {
														"This is the numerator amount of the ingredient."
												}
										),
										new FileColumnDefinition(
												"NumeratorUnit",
												new String[] {
														"This is the unit of the numerator amount of the ingredient."
												}
										),
										new FileColumnDefinition(
												"DenominatorAmount",
												new String[] {
														"This is the denominator amount of the ingredient."
												}
										),
										new FileColumnDefinition(
												"DenominatorUnit",
												new String[] {
														"This is the unit of the denominator amount of the ingredient."
												}
										),
										new FileColumnDefinition(
												"CASNumber",
												new String[] {
														"This is the CAS number of the ingredient."
												}
										)
								}
						),
						new FileDefinition(
								"Article57 ATC Counts File",
								new String[] {
										"This file should contain the use counts per ATC based on IPCI."
						  		},
								FileDefinition.DELIMITED_FILE,
								new FileColumnDefinition[] {
										new FileColumnDefinition(
												"ATCCode",
												new String[] {
														"This is the ATC code of the drug."
												}
										),
										new FileColumnDefinition(
												"Count",
												new String[] {
														"This is count for the ATC code."
												}
										)
								}
						)
				)
			);
	}
}
