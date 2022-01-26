package ca.uwo.git.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.tools.javac.util.List;

public class ProjectReader {
	
	private BufferedReader br = null;
	private ArrayList<String> targetProjects = new ArrayList<String>();
	public ProjectReader() {
		try {
			br = new BufferedReader(new FileReader(new File("listOfProjects.txt")));
			while (br.ready()) {
				String line = br.readLine();
				if (!line.contains("## "))
					targetProjects.add(line);
				else
					continue;
			}
		} catch(IOException ioe) {
			System.out.println("Error reading from listOfProjects.txt\n" + ioe.getMessage());
		}
	}
	
	public ArrayList<String> getListOfProjects() {
		return targetProjects;
	}
}
