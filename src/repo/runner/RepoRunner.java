package repo.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ca.uwo.git.utilities.CommitSelection;
import ca.uwo.git.utilities.GitRepo;
import configurations.RunConfiguration;
import extractorUtilities.ExtractionMethod;
import facades.ProxyFacade;
import fileOperationUtilities.MoveFilesAndFolders;
import fileOperationUtilities.PathVMRectifier;
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

	private void delete(String folder) {
		
		for (File file : new File(folder + "dbdump/").listFiles()) {
			file.delete();
		}
		new File(folder + "dbdump").delete();
		new File(folder).delete();
	}
	
	
//	TODO differentiate commands to the commit id level so that they can be run in parallel
	@Override
	public void run() {
		if (threads == 0) {
			gitRepo.initializeGitRepo();
			List<String> selectedFromYear;
			File alreadydone = new File(DynamicPaths.getPath() + RunConfiguration.EXTRACTOR_TYPES.get(0).label + "/" + RunConfiguration.SELECTED_COMMITS + "/" + gitRepo.getProjectName());
			if (!alreadydone.exists())
				alreadydone.mkdirs();
			List<String> listofDone = new ArrayList<>();
			if (alreadydone.exists())
				for (File file : alreadydone.listFiles()) {
					listofDone.add(file.getName().replace("stored_", "").replace(".json", ""));
				}
			if (RunConfiguration.SELECTED_YEARS.length > 0) {
				selectedFromYear = gitRepo.getAllCommitNamesForYear(RunConfiguration.SELECTED_YEARS);
				commitSelection.setYearlySelectedCommits(selectedFromYear);
			}
			for (String s : listofDone) {
				if (!commitSelection.contains(s)) {
					Path path = Paths.get(alreadydone + "/" + s + ".json");
					File uselessFileOrFolder = new File(path.toString());
					if (!Files.exists(path)) {
						this.delete(alreadydone + "/stored_" + s + "/");
					}else
						uselessFileOrFolder.delete();
					
				}
			}
			System.out.println(gitRepo.getProjectName() + " has a total of " + gitRepo.getAllCommitNames().size());
			String language = this.checkLanguage();
			if (language == "none")
				return;
			
			
			
			while (gitRepo.hasNext() && !ProxyFacade.stop.get("stop")) {

				// checkout the next commit in the repo
				gitRepo.moveToNextCommit();
				if (!commitSelection.contains(gitRepo.getCurrentCommitName())) {
					continue;
				}
				if (listofDone.contains(gitRepo.getCurrentCommitName())) {
					continue;
				}
//				System.out.println(gitRepo.getCurrentCommitName());
				if (gitRepo.checkoutNextCommit()) {

					for (MoveFilesAndFolders mfaf : this.moverUtilities) {
						MoveFilesAndFolders moveFilesAndFoldersOfCommit = mfaf.getNewInstance(gitRepo.getProjectPath(),
								gitRepo.getRootPath().replace("/projects_extracted", "") + "increments/"
										+ RunConfiguration.SELECTED_COMMITS + "/" + gitRepo.getProjectName()
										+ gitRepo.getCurrentCommitName() + "/",
								gitRepo.getChangedFiles());
						// using the initialized mover move all files from their old location to a new
						// temporary location to run the extractor on
						moveFilesAndFoldersOfCommit.moveAllFromMap();
					}
					// initialize an extractor for this commit
					for (ExtractionMethod em : this.extractionMethods) {
						if (em.getNewInstance(gitRepo.getCurrentCommitName(),
								gitRepo.getRootPath().replace("/projects_extracted", "") + "increments/"
										+ RunConfiguration.SELECTED_COMMITS + "/"
										+ gitRepo.getProjectName().replace("/", ""),
								gitRepo.getCurrentCommitName()).doExtraction(language)) {
						} else {
							break;
						}
					}
				} else {
					break;
				}
				if (ProxyFacade.stop.get("stop")) {
					break;
				}

			}
			System.out.println("finished_extraction for " + this.gitRepo.getProjectName());
			gitRepo.resetToHead();
		} else {
			System.out.println("oops");
		}
	}

	public String checkLanguage() {

		String localPath = gitRepo.getRootPath() + "/" + gitRepo.getProjectName() + "/";
//		System.out.println(localPath);
		localPath = PathVMRectifier.deRectify(localPath);
		try {
			List<String> cFiles = new ArrayList<>();
			Files.find(Paths.get(localPath), 999,
					(p, bfa) -> bfa.isRegularFile() && (p.getFileName().toString().toLowerCase().matches(".*\\.c")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.h")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.cpp")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.hpp")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.cxx")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.cpp")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.cc")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.hh")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.c++")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.h++")))
					.forEach(bfa -> cFiles.add(bfa.toString()));
//					System.out.println(cFiles.size());
			List<String> javaFiles = new ArrayList<>();

			Files.find(Paths.get(localPath), 999,
					(p, bfa) -> bfa.isRegularFile() && (p.getFileName().toString().toLowerCase().matches(".*\\.java")))
					.forEach(bfa -> javaFiles.add(bfa.toString()));
//					System.out.println(javaFiles.size());
			if ((javaFiles.size() != 0) || (cFiles.size() != 0))
				if (javaFiles.size() > cFiles.size())
					return "java";
				else
					return "cpp";
			else
				return "none";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "none";
		}

	}

}
