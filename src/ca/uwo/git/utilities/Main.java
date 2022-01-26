package ca.uwo.git.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import extractorUtilities.Extractor;
import fileOperationUtilities.InitializeFolderStructure;
import fileOperationUtilities.MoveFilesAndFolders;
import paths.DynamicPaths;

public class Main {
	private static boolean debug = false;
	private static final Logger logger = LogManager.getLogger(Main.class);
	
	public static void main(String[] args) {
		String log4jConfPath = "Properties/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		BasicConfigurator.configure();
		boolean stop = false;
		InitializeFolderStructure.initializeFolders();
		
		//		load from file
		ArrayList<String> repos = (new ProjectReader()).getListOfProjects();
		System.out.println(repos);
		
//		System.exit(0);
		
		Boolean haltExecution = false;
		String language;
		String MO = "run";
		System.out.println("starting");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			MO = br.readLine();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage() + " exception in Main.java while reading MO");
		}
		if (MO.equals("finalize")) 
			for (String repoAddress : repos) {
				List<String> alreadyExtacted = new ArrayList();
				GitRepo gitRepo = new GitRepo(repoAddress + ".git");
				gitRepo.initializeGitRepo();
				File alreadyDone = new File(DynamicPaths.getPath() + "increments/" + gitRepo.getProjectName());
				if (alreadyDone.exists())
					for (File foile : alreadyDone.listFiles()) {
						alreadyExtacted.add(foile.getName().replace("stored_",""));
					}
				List<String> allCommits = gitRepo.getOnlyCommitNames();
				for(String name : gitRepo.getOnlyCommitNames())
					if (!alreadyExtacted.contains(name))
						allCommits.remove(name);
				
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(new File(DynamicPaths.getPath()
							+ "analyzed_commits/" + gitRepo.getProjectName().replace("/", "") + ".csv")));
//					selectedFromYear = gitRepo.getAllCommitNamesForYear("2016");
					for (String name : allCommits)
						bw.write(name + "\n");
					bw.close();
				} catch (IOException ioe) {
					System.out.println("IOException caught while writing the analyzed_commits file. Message : "
							+ ioe.getMessage());
				}
//				if(true == true)
//					continue;

//				allCommits.get(0);
				while (gitRepo.hasNext()) {
					// checkout the next commit in the repo
					gitRepo.moveToNextCommit();
					if (allCommits.get(0).equals(gitRepo.getCurrentCommitName())) {
						gitRepo.checkoutNextCommit();
						
					} else {
						continue;
					}
					System.out.println(gitRepo.getProjectName());
					MoveFilesAndFolders moveFilesAndFoldersOfCommit = new MoveFilesAndFolders(gitRepo.getProjectPath(),
							gitRepo.getRootPath().replace("/projects_extracted", "") + "increments/"
									+ gitRepo.getProjectName() + gitRepo.getCurrentCommitName() + "/");
					
					moveFilesAndFoldersOfCommit.moveAll();
					
					Extractor extractor = new Extractor(gitRepo.getCurrentCommitName(),
							gitRepo.getRootPath().replace("/projects_extracted", "") + "increments/"
									+ gitRepo.getProjectName(),
							gitRepo.getCurrentCommitName());
					if (extractor.doExtraction("cpp")) {
						extractor.storeExtraction();
						extractor.clearExtractionLocation();
					}
			}	
				gitRepo.resetToHead();
//			do finalisation
		} else
			for (String repoAddress : repos) {
				List<String> selectedFromYear = new ArrayList();
				if (stop)
					break;
				// initialize the repository (clone from remote or simply load an existing repo)
				GitRepo gitRepo = new GitRepo(repoAddress + ".git");
				// do some initialization operations necessary for iterating through the repo
				gitRepo.initializeGitRepo();

//				if (System.getProperty("os.name").equals("Windows 10"))
//					continue;
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
				if (haltExecution)
					return;
				// listofDone.add(
				System.out.println(gitRepo.getProjectName());
				while (gitRepo.hasNext()) {
					i++;
					if (stop)
						break;
					// checkout the next commit in the repo
					gitRepo.moveToNextCommit();
					if (listofDone.contains(gitRepo.getCurrentCommitName()) || gitRepo.currentCommitIsMerge()) {
						continue;
					}
					if (!selectedFromYear.contains(gitRepo.getCurrentCommitName()))
						continue;
					gitRepo.checkoutNextCommit();
					System.out.println("Commits analysed so far: " + i);
//					System.out.println(gitRepo.getCurrentCommitDate());
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

		System.out.println("Done");

	}

	public static void debug(GitRepo gitRepo) {
		if (debug) {
			gitRepo.printBranches();
			gitRepo.mergeAverage();
		}
	}

}
