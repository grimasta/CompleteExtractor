package repo.runner;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ca.uwo.git.utilities.CommitSelection;
import ca.uwo.git.utilities.GitRepo;
import configurations.RunConfiguration;
import extractorUtilities.ExtractionMethod;
import facades.ProxyFacade;
import fileOperationUtilities.MoveFilesAndFolders;
import paths.DynamicPaths;

public class RepoRunner implements Runnable {

	BufferedReader br = null;
	CommitSelection commitSelection = null;
	GitRepo gitRepo = null;
	Boolean stop = null;
	int threads = 0;
	List<ExtractionMethod> extractionMethods = new ArrayList<>();
	List<MoveFilesAndFolders> moverUtilities = new ArrayList<>();

	public RepoRunner(int threads) {
		this.threads = threads;
	}

	public RepoRunner() {
		this(0);
	}

	public void addExtractor(ExtractionMethod extractionMethod) {
		extractionMethods.add(extractionMethod);
	}
	
	public void addMover(MoveFilesAndFolders mfaf) {
		moverUtilities.add(mfaf);
	}

	public void setRepo(GitRepo gr) {
		this.gitRepo = gr;
	}

	public GitRepo getGitRepo() {
		return gitRepo;
	}

	public void setSelection(CommitSelection cs) {
		commitSelection = cs;
	}

	public void stop() {
		stop = true;
	}

//	TODO differentiate commands to the commit id level so that they can be run in parallel
	@Override
	public void run() {
		if (threads == 0) {
			gitRepo.initializeGitRepo();
			List<String> selectedFromYear;
			File alreadydone = new File(DynamicPaths.getPath() + "multimetric/" + gitRepo.getProjectName());
			List<String> listofDone = new ArrayList<>();
			if (alreadydone.exists())
				for (File file : alreadydone.listFiles()) {
					listofDone.add(file.getName().replace(".json", ""));
				}
			if (RunConfiguration.SELECTED_YEARS.length > 0) {
				selectedFromYear = gitRepo.getAllCommitNamesForYear(RunConfiguration.SELECTED_YEARS);
				commitSelection.setYearlySelectedCommits(selectedFromYear);
			}
			System.out.println(gitRepo.getProjectName() + " has a total of " + gitRepo.getAllCommitNames().size());
			while (gitRepo.hasNext() && !ProxyFacade.stop.get("stop")) {
				// checkout the next commit in the repo
				gitRepo.moveToNextCommit();
				if (!commitSelection.contains(gitRepo.getCurrentCommitName())) {
					continue;
				}
				if (listofDone.contains(gitRepo.getCurrentCommitName()) || gitRepo.currentCommitIsMerge()) {
					continue;
				}

				if (gitRepo.checkoutNextCommit()) {
					for (MoveFilesAndFolders mfaf : this.moverUtilities) {
						MoveFilesAndFolders moveFilesAndFoldersOfCommit = mfaf.getNewInstance(gitRepo.getProjectPath(),
								gitRepo.getRootPath().replace("/projects_extracted", "") + "increments/" + 
						RunConfiguration.SELECTED_COMMITS
										+ gitRepo.getProjectName() + gitRepo.getCurrentCommitName() + "/",
								gitRepo.getChangedFiles());
						// using the initialized mover move all files from their old location to a new
						// temporary location to run the extractor on
						moveFilesAndFoldersOfCommit.moveAllFromMap();
					}
					
					// initialize an extractor for this commit
					for (ExtractionMethod em : this.extractionMethods) {
						if( em.getNewInstance(gitRepo.getCurrentCommitName(), gitRepo.getRootPath().replace("/projects_extracted", "")
								+ "increments/" + gitRepo.getProjectName().replace("/", ""),
						gitRepo.getCurrentCommitName()).doExtraction(null) ) {
						} else {
							break;
						}
					}
				} else {
					break;
				}
//			if (MultimetricFacade.stop.get("stop")) {
//				break;
//			}

			}
			System.out.println("finished_extraction for " + this.gitRepo.getProjectName());
			gitRepo.resetToHead();
		} else {
			System.out.println("oops");
		}
	}
}
