package repo.runner;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ca.uwo.git.utilities.CommitSelection;
import ca.uwo.git.utilities.GitRepo;
import extractorUtilities.MultimetricExtractor;
import facades.MultimetricFacade;
import paths.DynamicPaths;

public class RepoRunner implements Runnable {

	BufferedReader br = null;
	CommitSelection commitSelection = null;
	GitRepo gitRepo = null;
	Boolean stop = null;

	public RepoRunner() {
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
		gitRepo.initializeGitRepo();
		List<String> selectedFromYear = new ArrayList<String>();
		File alreadydone = new File(DynamicPaths.getPath() + "multimetric/" + gitRepo.getProjectName());
		List<String> listofDone = new ArrayList<>();
		if (alreadydone.exists())
			for (File file : alreadydone.listFiles()) {
				listofDone.add(file.getName().replace(".json", ""));
			}
		selectedFromYear = gitRepo.getAllCommitNamesForYear(new String[] { "1995", "1996", "1997", "1998", "1999",
				"2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012",
				"2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022" });
		System.out.println(gitRepo.getProjectName() + " has a total of " + gitRepo.getAllCommitNames().size());
		while (gitRepo.hasNext() && !MultimetricFacade.stop.get("stop")) {
			// checkout the next commit in the repo
			gitRepo.moveToNextCommit();
			if (!commitSelection.contains(gitRepo.getCurrentCommitName())) {
				continue;
			}
			if (listofDone.contains(gitRepo.getCurrentCommitName()) || gitRepo.currentCommitIsMerge()) {
				continue;
			}

			if (gitRepo.checkoutNextCommit()) {
				// initialize an extractor for this commit
				MultimetricExtractor extractor = new MultimetricExtractor(
						gitRepo.getCurrentCommitName(), gitRepo.getRootPath().replace("/projects_extracted", "")
								+ "increments/" + gitRepo.getProjectName().replace("/", ""),
						gitRepo.getCurrentCommitName());
				if (extractor.doExtraction(null)) {
//				extractor.storeExtraction();
				} else {
					break;
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
	}

}
