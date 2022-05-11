package facades;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;

import ca.uwo.git.utilities.GitRepo;
import ca.uwo.git.utilities.Main;
import paths.DynamicPaths;

public class IdsFacade extends ExtractionFacade{

	IdsFacade() {
		super();
	}
	
	public void doExtraction() {
		for (String repoAddress : repos) {
			Map<String, String> repo_file_ids = new HashMap<String, String>();
			Map<String, String> deleted_files = new HashMap<>();
			List<String> selectedFromYear = new ArrayList<String>();
			if (stop)
				break;
			// initialize the repository (clone from remote or simply load an existing repo)
			GitRepo gitRepo = new GitRepo(repoAddress + ".git");
			// do some initialization operations necessary for iterating through the repo
			gitRepo.initializeGitRepo();
			this.commitSelection.init(gitRepo.getProjectName());
			// if debug flag is set run some debug functions on the repo to calculate stats
			// and other properties
			Main.debug(gitRepo);
			// TODO remove the i variable which should be used only during development
			int i = 0;
			File alreadydone = new File(DynamicPaths.getPath() + "increments/" + gitRepo.getProjectName());
			List<String> listofDone = new ArrayList<>();
			if (alreadydone.exists())
				for (File foile : alreadydone.listFiles()) {
					listofDone.add(foile.getName().replace("stored_", ""));
				}
			if (System.getProperty("os.name").equals("asd"))
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(DynamicPaths.getPath()
						+ "analyzed_commits/" + gitRepo.getProjectName().replace("/", "") + ".csv")));
				selectedFromYear = gitRepo.getAllCommitNamesForYear(new String[] {
						"1995", "1996", "1997", "1998", "1999", 
						"2000", "2001", "2002", "2003", "2004", 
						"2005", "2006", "2007", "2008", "2009", 
						"2010", "2011", "2012", "2013", "2014", 
						"2015", "2016", "2017", "2018", "2019", 
						"2020", "2021"});
				for (String name : selectedFromYear)
					bw.write(name + "\n");
				bw.close();
			} catch (IOException ioe) {
				System.out.println("IOException caught while writing the analyzed_commits file. Message : "
						+ ioe.getMessage());
			}
			try {
				i = 0;
				BufferedWriter id_bw = new BufferedWriter(new FileWriter(new File(gitRepo.getProjectName().replace("/",  "") +"_ids.txt")));
			while (gitRepo.hasNext()) {
				i++;
				gitRepo.moveToNextCommit();
				i += 1;
				for (DiffEntry de : gitRepo.getChangedFilesId()) {
					if( de.getOldPath().contains("null")) {
						repo_file_ids.put(de.getNewPath(), de.getNewId().name());
						id_bw.write(gitRepo.getCurrentCommitName() + ", " + de.getNewPath() + ", " + de.getNewId().name() + ", +" + "\n");
					} else {
						if(repo_file_ids.containsKey(de.getOldPath())) {
							if( de.getNewPath().contains("null")) {
								id_bw.write(gitRepo.getCurrentCommitName() + ", " + de.getOldPath() + ", " + de.getOldId().name() + ", -" + "\n");
								deleted_files.put(de.getOldPath(), de.getOldId().name());
							} else {
								id_bw.write(gitRepo.getCurrentCommitName() + ", " + de.getNewPath() + ", " + repo_file_ids.get(de.getOldPath()) + ", ^" + "\n");
								repo_file_ids.put(de.getNewPath(), repo_file_ids.get(de.getOldPath()));
								
							}
						}
					}
					
				}
				if (i % 1000 == 0)
					System.out.println(i);
				try {
					if (br.ready()) {
						if (br.readLine().contains("break"))
							stop = true;
						break;
					}
					// if (i == 100)
					// break;
				} catch (IOException ioe) {
					System.out.println(
							"Error while reading from System.in in Main.java. Message : " + ioe.getMessage());
					gitRepo.resetToHead();
				}
			}
			id_bw.close();
			} catch(IOException ioe) {
				System.out.println("exception thrown trying to print id to filenames" + ioe.getMessage());
			}
		}
	}
	
	
}
