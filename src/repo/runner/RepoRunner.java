package repo.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.diff.DiffEntry;

import ca.uwo.git.utilities.CommitSelection;
import ca.uwo.git.utilities.GitRepo;
import configurations.RunConfiguration;
import extractorUtilities.ExtractionMethod;
import extractorUtilities.ExtractorType;
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

	private List<String> done() {
		List<String> doneList = new ArrayList<String>();
		File outputFolder = new File(DynamicPaths.getPath() + RunConfiguration.EXTRACTOR_TYPES.get(0).label + "/"
				+ RunConfiguration.SELECTED_COMMITS + "/" + gitRepo.getProjectName());
		for (File f : outputFolder.listFiles()) {
			for (File dbdumpFolder : f.listFiles()) {
				for (String outputDataFile : dbdumpFolder.list()) {
					if (outputDataFile.contains("includes2"))
						doneList.add(f.getName().replace("stored_", ""));
				}
			}
		}
		return doneList;
	}

	private void clearSupplemental() {
		File outputFolder = new File(DynamicPaths.getPath() + RunConfiguration.EXTRACTOR_TYPES.get(0).label + "/"
				+ RunConfiguration.SELECTED_COMMITS + "/" + gitRepo.getProjectName() + "/stored_"
				+ gitRepo.getCurrentCommitName());
		System.out.println(outputFolder);
		if (outputFolder.exists())
			for (File dbdumpFolder : outputFolder.listFiles()) {
				for (File outputDataFile : dbdumpFolder.listFiles()) {
					if (outputDataFile.getName().contains("includes2")
							|| outputDataFile.getName().contains("namespaces")
							|| outputDataFile.getName().contains("condcomp"))
						outputDataFile.delete();

				}
			}
	}

//	TODO differentiate commands to the commit id level so that they can be run in parallel
	@Override
	public void run() {
		Map<String, String> repo_file_ids = new HashMap<String, String>();
		Map<String, String> deleted_files = new HashMap<>();

		if (threads == 0) {
			gitRepo.initializeGitRepo();
			List<String> selectedFromYear;
			File alreadydone = new File(DynamicPaths.getPath() + RunConfiguration.EXTRACTOR_TYPES.get(0).label + "/"
					+ RunConfiguration.SELECTED_COMMITS + "/" + gitRepo.getProjectName());
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
			if (RunConfiguration.EXTRACTOR_TYPES.contains(ExtractorType.SNAVIGATOR)) {
				for (String s : listofDone) {
					if (!commitSelection.contains(s)) {
						Path path = Paths.get(alreadydone + "/" + s + ".json");
						File uselessFileOrFolder = new File(path.toString());
						if (!Files.exists(path)) {
							this.delete(alreadydone + "/stored_" + s + "/");
						} else
							uselessFileOrFolder.delete();

					}
				}
			}
			System.out.println(gitRepo.getProjectName() + " has a total of " + gitRepo.getAllCommitNames().size());
			String language = this.checkLanguage();
			if (language == "none")
				return;
			try {
				BufferedWriter id_bw = null;
//				if (RunConfiguration.EXTRACTOR_TYPES.contains(ExtractorType.IDS)) {
				new File(alreadydone.getCanonicalPath().replace(RunConfiguration.EXTRACTOR_TYPES.get(0).label, "ids")
						+ "/").mkdirs();
				id_bw = new BufferedWriter(new FileWriter(new File(
						alreadydone.getCanonicalPath().replace(RunConfiguration.EXTRACTOR_TYPES.get(0).label, "ids")
								+ "/" + gitRepo.getProjectName().replace("/", "") + "_ids.txt")));
//				}
				List<String> supplementallyDone = this.done();
				boolean found = false;
				while (gitRepo.hasNext() && !ProxyFacade.stop.get("stop")) {

					// checkout the next commit in the repo
					gitRepo.moveToNextCommit();
					if (!commitSelection.contains(gitRepo.getCurrentCommitName())) {
						continue;
					}
//					if (!listofDone.contains(gitRepo.getCurrentCommitName())) {
//						continue;
//					}
					if (commitSelection.contains(gitRepo.getCurrentCommitName())) {
						if (!supplementallyDone.contains(gitRepo.getCurrentCommitName()) && !found) {
							gitRepo.moveToPreviousCommit();
							this.clearSupplemental();
							found = true;
							System.out.println("found it");
						}
					}
//					if (listofDone.contains(gitRepo.getCurrentCommitName())) {
//						if (gitRepo.checkoutNextCommit()) {
//							if (RunConfiguration.EXTRACTOR_TYPES.contains(ExtractorType.IDS)) {
//								for (DiffEntry de : gitRepo.getChangedFilesId()) {
//									if (de.getOldPath().contains("null")) {
//										repo_file_ids.put(de.getNewPath(), de.getNewId().name());
//										id_bw.write(gitRepo.getCurrentCommitName() + ", " + de.getNewPath() + ", "
//												+ de.getNewId().name() + ", +" + "\n");
//									} else {
//										if (repo_file_ids.containsKey(de.getOldPath())) {
//											if (de.getNewPath().contains("null")) {
//												id_bw.write(gitRepo.getCurrentCommitName() + ", " + de.getOldPath()
//														+ ", " + de.getOldId().name() + ", -" + "\n");
//												deleted_files.put(de.getOldPath(), de.getOldId().name());
//											} else {
//												id_bw.write(gitRepo.getCurrentCommitName() + ", " + de.getNewPath()
//														+ ", " + repo_file_ids.get(de.getOldPath()) + ", ^" + "\n");
//												repo_file_ids.put(de.getNewPath(), repo_file_ids.get(de.getOldPath()));
//
//											}
//										}
//									}
//
//								}
//							}
//						}
//						continue;
//					}
//				System.out.println(gitRepo.getCurrentCommitName());
					if (gitRepo.checkoutNextCommit()) {

						for (MoveFilesAndFolders mfaf : this.moverUtilities) {
							MoveFilesAndFolders moveFilesAndFoldersOfCommit = mfaf.getNewInstance(
									gitRepo.getProjectPath(),
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

//						id_bw = new BufferedWriter(new FileWriter(new File(alreadydone.getCanonicalPath().replace(RunConfiguration.EXTRACTOR_TYPES.get(0).label, "ids") + "/"
//								+ gitRepo.getProjectName().replace("/", "") + "_ids.txt"), true));

//						for (DiffEntry de : gitRepo.getChangedFilesId()) {
//							if (de.getOldPath().contains("null")) {
//								repo_file_ids.put(de.getNewPath(), de.getNewId().name());
//								id_bw.write(gitRepo.getCurrentCommitName() + ", " + de.getNewPath() + ", "
//										+ de.getNewId().name() + ", +" + "\n");
//							} else {
//								if (repo_file_ids.containsKey(de.getOldPath())) {
//									if (de.getNewPath().contains("null")) {
//										id_bw.write(gitRepo.getCurrentCommitName() + ", " + de.getOldPath() + ", "
//												+ de.getOldId().name() + ", -" + "\n");
//										deleted_files.put(de.getOldPath(), de.getOldId().name());
//									} else {
//										id_bw.write(gitRepo.getCurrentCommitName() + ", " + de.getNewPath() + ", "
//												+ repo_file_ids.get(de.getOldPath()) + ", ^" + "\n");
//										repo_file_ids.put(de.getNewPath(), repo_file_ids.get(de.getOldPath()));
//
//									}
//								}
//							}
//
//						}
//						
//						id_bw.close();

					} else {
						break;
					}
					if (ProxyFacade.stop.get("stop")) {
						break;
					}

				}
				if (id_bw != null)
					id_bw.close();
				System.out.println("finished_extraction for " + this.gitRepo.getProjectName());
				gitRepo.resetToHead();
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage() + " @ RepoRunner");
			}
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
