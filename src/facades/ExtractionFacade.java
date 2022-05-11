package facades;

import java.io.BufferedReader;
import java.util.ArrayList;

import ca.uwo.git.utilities.CommitSelection;

public abstract class ExtractionFacade {
	
	ArrayList<String> repos = null;
	boolean stop = false;
	BufferedReader br = null;
	CommitSelection commitSelection = null;
	
	protected ExtractionFacade() {
		this.commitSelection = new CommitSelection();
	}
	
	public void setRepos(ArrayList<String> repos) {
		this.repos = repos;
	}
	
	public void setBufferedReader(BufferedReader br) {
		this.br = br;
	}
	
	public void doExtraction() {
		System.out.println("Not Implemented yet");
	}
	
}
