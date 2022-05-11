package facades;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.uwo.git.utilities.GitRepo;
import extractorUtilities.Extractor;
import fileOperationUtilities.MoveFilesAndFolders;
import paths.DynamicPaths;

public class FinalisationFacade extends ExtractionFacade {

	FinalisationFacade() {
		super();
	}

	public void doExtraction() {
		for (String repoAddress : repos) {
			List<String> alreadyExtacted = new ArrayList<String>();
			GitRepo gitRepo = new GitRepo(repoAddress + ".git");
			gitRepo.initializeGitRepo();
			this.commitSelection.init(gitRepo.getProjectName());
			File alreadyDone = new File(DynamicPaths.getPath() + "increments/" + gitRepo.getProjectName());
			if (alreadyDone.exists())
				for (File foile : alreadyDone.listFiles()) {
					alreadyExtacted.add(foile.getName().replace("stored_", ""));
				}
			List<String> allCommits = gitRepo.getOnlyCommitNames();
			for (String name : gitRepo.getOnlyCommitNames())
				if (!alreadyExtacted.contains(name))
					allCommits.remove(name);

			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(DynamicPaths.getPath()
						+ "analyzed_commits/" + gitRepo.getProjectName().replace("/", "") + ".csv")));
				// selectedFromYear = gitRepo.getAllCommitNamesForYear("2016");
				for (String name : allCommits)
					bw.write(name + "\n");
				bw.close();
			} catch (IOException ioe) {
				System.out.println(
						"IOException caught while writing the analyzed_commits file. Message : " + ioe.getMessage());
			}
			// if(true == true)
			// continue;

			// allCommits.get(0);
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
		}
	}
}
