package ca.uwo.git.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import configurations.RunConfiguration;

public class CommitSelection {

	private Set<String> currentCollection = new HashSet<String>();
	private Set<String> yearBasedSelection = new HashSet<String>();
	public CommitSelection() {
	}

	public void init(String projectName) {
		currentCollection.clear();
		try {
			projectName = projectName.replace("/", "");
			BufferedReader br = new BufferedReader(
					new FileReader(new File("..\\..\\ExtractorUtilities\\selected_commits_sibyl\\"
							+ RunConfiguration.SELECTED_COMMITS + "\\" + projectName + ".csv")));
			while (br.ready()) {
				currentCollection.add(br.readLine().replace("\n", ""));
			}
			br.close();
			System.out
					.println("A total of " + currentCollection.size() + " commits will be analysed for " + projectName);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}

	}

	public Boolean contains(String commitId) {
		boolean contains;
		contains = currentCollection.contains(commitId) || yearBasedSelection.contains(commitId);
		return contains;
	}

	public void setYearlySelectedCommits(List<String> selectedFromYear) {
		this.yearBasedSelection.addAll(selectedFromYear);
		
	}

}