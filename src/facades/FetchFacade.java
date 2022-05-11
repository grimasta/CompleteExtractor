package facades;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.uwo.git.utilities.GitRepo;
import ca.uwo.git.utilities.Main;
import extractorUtilities.Extractor;
import fileOperationUtilities.MoveFilesAndFolders;
import paths.DynamicPaths;

public class FetchFacade extends ExtractionFacade{
	
	FetchFacade() {
		super();
	}
	
	public void doExtraction() {
		for (String repoAddress : repos) {
			List<String> selectedFromYear = new ArrayList<String>();
			if (stop)
				break;
			// initialize the repository (clone from remote or simply load an existing repo)
			GitRepo gitRepo = new GitRepo(repoAddress + ".git");
			// do some initialization operations necessary for iterating through the repo
			gitRepo.initializeGitRepo();
			this.commitSelection.init(gitRepo.getProjectName());
//			if (System.getProperty("os.name").equals("Windows 10"))
//				continue;
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
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(DynamicPaths.getPath()
						+ "analyzed_commits/" + gitRepo.getProjectName().replace("/", "") + ".csv")));
				selectedFromYear = gitRepo.getAllCommitNamesForYear(
						new String[] { "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003",
								"2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013",
								"2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021" });
				for (String name : selectedFromYear)
					bw.write(name + "\n");
				bw.close();
			} catch (IOException ioe) {
				System.out.println("IOException caught while writing the analyzed_commits file. Message : "
						+ ioe.getMessage());
			}
			// listofDone.add(
			System.out.println(gitRepo.getProjectName());
			while (gitRepo.hasNext()) {
				i++;
				if (stop)
					break;
				// checkout the next commit in the repo
				gitRepo.moveToNextCommit();
				if (!commitSelection.contains(gitRepo.getCurrentCommitDate()))
					continue;
				if (listofDone.contains(gitRepo.getCurrentCommitName()) || gitRepo.currentCommitIsMerge()) {
					continue;
				}
				if (!selectedFromYear.contains(gitRepo.getCurrentCommitName()))
					continue;
				gitRepo.checkoutNextCommit();
				System.out.println("Commits analysed so far: " + i);
//				System.out.println(gitRepo.getCurrentCommitDate());
				// get the map pointing from old file names and locations to new filenames and
				// locations after the current commit was
				// committed to the repository
				// TODO remove the printing of the calculated diffs between the current commit
				// and its parent(s)
				// for (Entry<String, String> e : gitRepo.getChangedFiles().entrySet()) {
				// System.out.println(e.getKey() + " -> " + e.getValue());
				// }
				// initialize a mover for this commit
				MoveFilesAndFolders moveFilesAndFoldersOfCommit = new MoveFilesAndFolders(gitRepo.getProjectPath(),
						gitRepo.getRootPath().replace("/projects_extracted", "") + "increments/"
								+ gitRepo.getProjectName() + gitRepo.getCurrentCommitName() + "/",
						gitRepo.getChangedFiles());
				// using the initialized mover move all files from their old location to a new
				// temporary location to run the extractor on
				moveFilesAndFoldersOfCommit.moveAllFromMap();
				System.out.println("+++++++++++++++++++++done moving files and folders++++++++++++++++++++++");
				// TODO

				System.out.println("---------------------Starting extraction part-------------------");
				Extractor extractor = new Extractor(gitRepo.getCurrentCommitName(),
						gitRepo.getRootPath().replace("/projects_extracted", "") + "increments/"
								+ gitRepo.getProjectName(),
						gitRepo.getCurrentCommitName());
				if (extractor.doExtraction("cpp")) {
					extractor.storeExtraction();
					extractor.clearExtractionLocation();
				}
				System.out.println("======================Finished Extraction Part=====================");
				// run the extractor on the new location and then store all results including
				// the dbdump folder to another termporary
				// location by creating another instance of Mover.
				// TODO probably at this point call the python reconciler and calculate the new
				// values which should be stored in a db
				// probably
				// TODO perform clean up by deleting all moved files and moved_To locations
				// minus the results of the extractor
				try {
					if (br.ready()) {
						if (br.readLine().contains("break"))
							stop = true;
					}
					// if (i == 100)
					// break;
				} catch (IOException ioe) {
					System.out.println(
							"Error while reading from System.in in Main.java. Message : " + ioe.getMessage());
					gitRepo.resetToHead();
				}

			}
			gitRepo.resetToHead();
		}
	}
}
