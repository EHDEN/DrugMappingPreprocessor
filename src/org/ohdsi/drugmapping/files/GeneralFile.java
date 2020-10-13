package org.ohdsi.drugmapping.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GeneralFile {
	
	private String fileName = null;
	private BufferedReader fileReader = null;

	
	public GeneralFile(String fileName) {
		this.fileName = fileName;
	}
	
	
	public String getFileName() {
		return fileName;
	}
	
	
	public boolean openForReading() {
		boolean result = false;
		
		File file = new File(fileName);
		if (file.canRead()) {
			try {
				if (fileReader != null) {
					close();
				}
				fileReader = new BufferedReader(new FileReader(file));
				result = true;
			} catch (FileNotFoundException e) {
				result = false;
			} 
		}
		
		return result;
	}
	
	
	public boolean close() {
		boolean result = false;
		
		try {
			fileReader.close();
			fileReader = null;
			result = true;
		} catch (IOException e) {
			result = false;
		}
		
		return result;
	}
	
	
	public String readLine() {
		try {
			return fileReader.readLine();
		} catch (IOException e) {
			return null;
		}
	}
}
