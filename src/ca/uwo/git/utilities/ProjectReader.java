package ca.uwo.git.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ProjectReader {
	
	private BufferedReader br = null;
	private ArrayList<String> targetProjects = new ArrayList<String>();
	
	public static void main(String[] args) {
		ProjectReader pr = new ProjectReader();
		
	}
	
	public ProjectReader() {
		String inputFile = "projects.txt";
		try {
			br = new BufferedReader(new FileReader(new File(inputFile)));
			while (br.ready()) {
				String line = br.readLine();
				if (!line.contains("## "))
					targetProjects.add(line);
				else
					continue;
			}
		} catch(IOException ioe) {
			System.out.println("Error reading from " + inputFile + "\n" + ioe.getMessage());
		}
	}
	
	public ArrayList<String> getListOfProjects() {
		return targetProjects;
	}
}
