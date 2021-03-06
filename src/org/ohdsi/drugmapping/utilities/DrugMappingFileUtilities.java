package org.ohdsi.drugmapping.utilities;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.ohdsi.drugmapping.DrugMappingPreprocessor;

public class DrugMappingFileUtilities {
	
	
	public static String selectCSVFile(Component parent) {
		return selectCSVFile(parent, ".csv", "CSV Files");
	}
	
	
	public static String selectCSVFile(Component parent, String fileNameEndsWith, String description) {
		String result = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(DrugMappingPreprocessor.getCurrentPath() == null ? (DrugMappingPreprocessor.getBasePath() == null ? System.getProperty("user.dir") : DrugMappingPreprocessor.getBasePath()) : DrugMappingPreprocessor.getCurrentPath()));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if ((fileNameEndsWith != null) && (!fileNameEndsWith.equals(""))) {
			fileChooser.setFileFilter(new FileFilter() {

		        @Override
		        public boolean accept(File f) {
		            return f.getName().endsWith(fileNameEndsWith);
		        }

		        @Override
		        public String getDescription() {
		            return description;
		        }

		    });
		}
		int returnVal = fileChooser.showDialog(parent, "Select file");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			result = fileChooser.getSelectedFile().getAbsolutePath();
		}
		return result;
	}
	
	
	public static PrintWriter openOutputFile(String fileName, String header) {
		PrintWriter outputPrintWriter = null;
		String fullFileName = "";
		try {
			// Create output file
			fullFileName = DrugMappingPreprocessor.baseName + fileName;
			outputPrintWriter = new PrintWriter(new File(fullFileName));
			outputPrintWriter.println(header);
		}
		catch (FileNotFoundException e) {
			System.out.println("      ERROR: Cannot create output file '" + fullFileName + "'");
			outputPrintWriter = null;
		}
		return outputPrintWriter;
	}
	
	
	public static void closeOutputFile(PrintWriter outputFile) {
		if (outputFile != null) {
			outputFile.close();
		}
	}
	
	
	public static String getNextFileName(String path, String baseFileName) {
		String date = DrugMappingDateUtilities.getCurrentDate();
		Integer sequenceNr = 0;
		String fileName = null;
		File file = null;
		do {
			sequenceNr++;
			if (sequenceNr == 100) {
				fileName = date + " 01 " + baseFileName; // Start at 01 again.
				file = null;
			}
			else {
				fileName = date + " " + ("00" + sequenceNr.toString()).substring(sequenceNr.toString().length()) + " " + baseFileName;
				file = new File(path + "/" + fileName);
			}
		} while ((file != null) && file.exists());
		return fileName;
	}
}
