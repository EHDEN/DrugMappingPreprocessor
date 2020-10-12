package org.ohdsi.drugmapping.aemps;

import java.util.ArrayList;
import java.util.Arrays;

import org.ohdsi.drugmapping.files.FileColumnDefinition;
import org.ohdsi.drugmapping.files.FileDefinition;
import org.ohdsi.drugmapping.files.InputFileDefinition;


public class AEMPSPreprocessorInputFiles extends InputFileDefinition {

	
	public AEMPSPreprocessorInputFiles() {
		inputFiles = new ArrayList<FileDefinition>(
				Arrays.asList(
						new FileDefinition(
								"Drugs File",
								new String[] {
										"This file should contain the drugs and their composition."
						  		},
								FileDefinition.XML_FILE,
								true,
								true
						),
						new FileDefinition(
								"Active Ingredients File",
								new String[] {
										"This file should contain the active ingredients."
						  		},
								FileDefinition.XML_FILE,
								true,
								true
						),
						new FileDefinition(
								"Dose Forms File",
								new String[] {
										"This file should contain the dose forms."
						  		},
								FileDefinition.XML_FILE,
								true,
								true
						),
						new FileDefinition(
								"Code Counts File",
								new String[] {
										"This file should contain the use counts of the drugs in the source database."
						  		},
								FileDefinition.DELIMITED_FILE,
								new FileColumnDefinition[] {
										new FileColumnDefinition(
												"SourceCode",
												new String[] {
														"This is the code of the source drug."
												}
										),
										new FileColumnDefinition(
												"SourceCount",
												new String[] {
														"The number of occurrences of the source drug in the source database."
												}
										)
								},
								true,
								true
						)
				)
			);
	}
}
