package ca.uwo.git.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import console.commanders.ConsoleFactory;
import console.commanders.DynamicCommands;
import paths.DynamicPaths;

public class GitRepo {

	private final String rootPath = DynamicPaths.getPath() + "projects_extracted/";
	private String projectPath;
	private String URL;
	private String strPath;
	private Git git = null;
	private Repository repo = null;
	private ObjectId head = null;
	private ArrayList<RevCommit> listOfCommits = new ArrayList<>();
	private ListIterator<RevCommit> iteratorOfCommits = null;
	private RevCommit currentCommit = null;
	private long currentCommitDate = -1;
	private AbstractTreeIterator oldTree = null;
	private AbstractTreeIterator newTree = new EmptyTreeIterator();
	private static final Logger logger = LogManager.getLogger(Git.class);
	private PrintWriter DataWriter = null;
	private int thread_ID = 1;
	private int skipped = 0;
	private int retries = 0;

	public int getSkipped() {
		return skipped;
	}

	public PrintWriter getDataWriter() {
		return DataWriter;
	}

	public GitRepo(String URL) {
		logger.always().log("initialised a GitRepo Class for repository: " + URL);
		this.URL = URL;
		this.strPath = URL.split("/")[URL.split("/").length - 1].replace(".git", "/");
		System.out.println(this.strPath);
		try {
			if ((new File("../../commit_level_data/" + this.strPath.replace("/", "") + "_commits.csv")).exists()) {
				(new File("../../commit_level_data/" + this.strPath.replace("/", "") + "_commits.csv")).delete();
			}
			this.DataWriter = new PrintWriter(new BufferedWriter(
					new FileWriter("../../commit_level_data/" + this.strPath.replace("/", "") + "_commits.csv", true)));
			String headerString = "commitId,commitMessage,committed_at,committer_email,committer_id,committer_name,committer_id,"
					+ "committer_name,author_email,author_id,author_name,file_name,file_added,file_deleted, commit_added, commit_deleted";
			this.DataWriter.println(headerString);
		} catch (IOException ioe) {
			System.out.println("error trying to open " + this.strPath.replace("/", "") + "_commits.csv file");
		}
	}

	public GitRepo copy(int thread_ID) {
		GitRepo copy = new GitRepo(this.URL);
		copy.thread_ID = thread_ID;
		copy.strPath += copy.thread_ID;
		return copy;
	}

	public static void main(String[] args) {
		String pla = "ruqola\\";
		System.out.println(pla.replace("\\", ""));
	}

	public void deleteRepo(File file) {
		try {
		if (file.isDirectory()) {
		    File[] entries = file.listFiles();
		    if (entries != null) {
		      for (File entry : entries) {
		        deleteRepo(entry);
		      }
		    }
		  }
		  if (!file.delete()) {
		    throw new IOException("Failed to delete " + file);
		  }
		} catch(IOException ioe) {
			System.out.println("error when deleting bad repo = " + ioe.getMessage());
		}
	}

	public void initializeGitRepo() {
		try {
			this.projectPath = rootPath + this.strPath;
			Path path = new File(rootPath + this.strPath).toPath();
//			System.out.println("stored in : " + this.rootPath + " and " + this.strPath);
			if (Files.exists(path)) {
				this.git = Git.open(new File(rootPath + this.strPath + ".git"));
//				this.git.checkout();
				this.repo = this.git.getRepository();
			} else {
				System.out.println("started pulling : " + this.strPath);
				System.out.println("storing in : " + this.rootPath + this.strPath);
				CloneCommand cloneCommand = Git.cloneRepository();
				cloneCommand.setURI(URL);
				cloneCommand.setCredentialsProvider(
						new UsernamePasswordCredentialsProvider("964b0757662e731e43d14db28de0f59a3a233f22", ""));
				this.git = cloneCommand.setDirectory(new File(rootPath + this.strPath)).setCloneAllBranches(true)
						.call();
				System.out.println("done pulling : " + this.strPath);
				this.repo = this.git.getRepository();
			}
			this.head = this.repo.resolve(Constants.HEAD);
			for (RevCommit commit : this.git.log().all().call()) {

				this.listOfCommits.add(commit);
			}
			Collections.sort(this.listOfCommits, new Comparator<RevCommit>() {
				@Override
				public int compare(RevCommit o1, RevCommit o2) {
					// o1.compareTo(o2);
					int i1 = o1.getCommitTime();
					int i2 = o2.getCommitTime();
					return Integer.compare(i1, i2);
				}
			});
			this.iteratorOfCommits = this.listOfCommits.listIterator();
		} catch (GitAPIException gapie) {
			System.out.println("An error occured, " + gapie.getMessage());
			System.out.println("Retrying");
			Path path = new File(rootPath + this.strPath).toPath();
			System.out.println("stored in : " + this.rootPath + " and " + this.strPath);
			if (Files.exists(path)) {
				System.out.println("Deleting existing copy");
				String projectName = this.getProjectName().replace("/", "");
				File file = new  File("../../ExtractorUtilities/projects_extracted/" + projectName);
				this.git.close();
				this.repo.close();
				this.git = null;
				this.repo = null;
				this.deleteRepo(file);
				this.initializeGitRepo();
			} else {
				System.out.println("GitAPIError" + gapie.getMessage());
			}
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}

	public void printBranches() {
		try {
			for (Ref rBranch : this.git.branchList().call())
				System.out.println(rBranch.getName());
		} catch (GitAPIException gapie) {
			System.out.println(
					"GitAPIException caught in method GitRepo:printBranches(). Message : " + gapie.getMessage());
		}
	}

	public void resetToHead() {
		try {
			this.git.checkout().setForceRefUpdate(true).setName(this.head.getName()).call();
		} catch (GitAPIException gapie) {
			System.out.println(gapie.getMessage());
		}
	}

	public boolean hasNext() {
		return this.iteratorOfCommits.hasNext();
	}

	public Map<String, String> getChangedFiles() {
		Map<String, String> changes = new HashMap<>();
		try {

			List<DiffEntry> diffEntries = this.calculateDiffs();
			for (DiffEntry de : diffEntries) {
				if (de.getOldPath().contains("null")) {
					// System.out.println(de.getOldPath() + " | " + de.getNewPath());
					changes.put(de.getNewPath(), de.getNewPath());
				} else {
					changes.put(de.getOldPath(), de.getNewPath());
				}
			}
		} catch (GitAPIException gapie) {
			System.out.println(
					"GitAPIException caught in method GitRepo.getChangedFiles, Full Message : " + gapie.getMessage());
			return changes;
		} catch (IncorrectObjectTypeException iote) {
			System.out.println("IncorectObjectTypeException caught in Method GitRepo.getChangedFiles. Message = "
					+ iote.getMessage());
			return changes;
		} catch (IOException e) {
			System.out.println("IOException caught in Method GitRepo.getChangedFiles. Message = " + e.getMessage());
			return changes;
		}

		return changes;
	}

	public void write(String outputString) {
		this.DataWriter.print(outputString);
	}

	private class Change {

	}

	public Git getGit() {
		return git;
	}

	public Repository getRepo() {
		return repo;
	}

	public RevCommit getCurrentCommit() {
		return currentCommit;
	}

	public Map<String, Change> getChanges() {
		Map<String, Change> changes = new HashMap<>();
		try {
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			df.setRepository(this.repo);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			String outputString = "";
			String commitId = this.currentCommit.getId().getName();
			String commitMessage = this.currentCommit.getFullMessage().replace("\n", " ");
			String committed_at = "" + this.currentCommit.getCommitTime();
//				String authored_at = "" + this.currentCommit.getParent(deleted); TODO IMPLEMENT
			String committer_email = this.currentCommit.getCommitterIdent().getEmailAddress();
			String committer_id = this.currentCommit.getCommitterIdent().toExternalString();
			String committer_name = this.currentCommit.getCommitterIdent().getName();
			String author_email = this.currentCommit.getAuthorIdent().getEmailAddress();
			String author_id = this.currentCommit.getAuthorIdent().toExternalString();
			String author_name = this.currentCommit.getAuthorIdent().getName();
			for (DiffEntry de : this.calculateDiffs()) {
				int added = 0;
				int deleted = 0;
//				for (Edit edit : df.toFileHeader(de).toEditList()) {
//					added += edit.getEndB() - edit.getBeginB();
//					deleted += edit.getEndA() - edit.getBeginA();
//				}
				String file_name = de.getNewPath();
				outputString += commitId + "," + commitMessage + "," + committed_at + "," + committer_email + ","
						+ committer_id + "," + committer_name + "," + committer_id + "," + committer_name + ","
						+ author_email + "," + author_id + "," + author_name + "," + file_name + "," + added + ","
						+ deleted + "\n";
//				System.out.println(outputString);
			}
			this.DataWriter.print(outputString);
			df.close();
		} catch (GitAPIException gapie) {
			System.out.println(
					"GitAPIException caught in method GitRepo.getChangedFiles, Full Message : " + gapie.getMessage());
			return changes;
		} catch (IncorrectObjectTypeException iote) {
			System.out.println("IncorectObjectTypeException caught in Method GitRepo.getChangedFiles. Message = "
					+ iote.getMessage());
			return changes;
		} catch (IOException e) {
			System.out.println("IOException caught in Method GitRepo.getChangedFiles. Message = " + e.getMessage());
			return changes;
		}
		return changes;
	}

	public void closeWriter() {
		System.out.println("../../commit_level_data/" + this.strPath.replace("/", "") + "_commits.csv was closed");
		this.DataWriter.close();
	}

	private List<DiffEntry> calculateDiffs()
			throws MissingObjectException, IncorrectObjectTypeException, IOException, GitAPIException {
		List<DiffEntry> diffEntries = new ArrayList<>();

		// set old treeIterator equal to old treeIterator and new TreeIterator equal to
		// current TreeIterator
		// this.oldTree = this.newTree;
		RevCommit[] parents = this.currentCommit.getParents();
		ObjectId[] parentTreeIds = new ObjectId[parents.length];
		RevWalk walkOfTree = new RevWalk(git.getRepository());
		// calculate treeIterators for parentCommitTree

		if (parents.length > 0) {
			for (int i = 0; i < parents.length; i++) {
				parentTreeIds[i] = parents[i].getTree().getId();
			}
			// calculate treeIterator for currentCommitTree
			RevCommit commit = walkOfTree.parseCommit(this.currentCommit);
			ObjectId treeId = commit.getTree().getId();
			ObjectReader reader = git.getRepository().newObjectReader();
			this.newTree = new CanonicalTreeParser(null, reader, treeId);
			for (ObjectId parTreeId : parentTreeIds) {
				ObjectReader parentReader = git.getRepository().newObjectReader();
				this.oldTree = new CanonicalTreeParser(null, parentReader, parTreeId);
				diffEntries.addAll(this.git.diff().setOldTree(this.oldTree).setNewTree(this.newTree).call());
			}
		} else {
			RevCommit commit = walkOfTree.parseCommit(this.currentCommit);
			ObjectId treeId = commit.getTree().getId();
			ObjectReader reader = git.getRepository().newObjectReader();
			this.newTree = new CanonicalTreeParser(null, reader, treeId);
//			ObjectReader parentReader = git.getRepository().newObjectReader();
			this.oldTree = new EmptyTreeIterator();
			diffEntries.addAll(this.git.diff().setOldTree(this.oldTree).setNewTree(this.newTree).call());
		}
		walkOfTree.close();
		return diffEntries;
		// ObjectReader parentReaders = git.getRepository().newObjectReader();

	}

	public void moveToNextCommit() {
		this.currentCommit = this.iteratorOfCommits.next();
	}

	public List<String> getOnlyCommitNames() {
		List<String> allCommitNames = new ArrayList<>();
		for (RevCommit rc : this.listOfCommits) {
			allCommitNames.add(rc.getName());
		}
		return allCommitNames;
	}

	public List<String> getAllCommitNames() {
		List<String> allCommitNames = new ArrayList<>();
		for (RevCommit rc : this.listOfCommits) {
			allCommitNames.add(rc.getName() + "," + rc.getCommitTime());
		}
		return allCommitNames;
	}

	public List<String> getAllCommitNamesForYear(String[] years) {
		List<String> listYears = new ArrayList<>();
		for (String s : years) {
			listYears.add(s);
		}
		List<String> allCommitNames = new ArrayList<>();
		for (RevCommit rc : this.listOfCommits) {
			SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy");
			long date = rc.getCommitTime() * 1000L;
			if (listYears.contains(originalFormat.format(date))) {
//				System.out.println("date = " + originalFormat.format(date));
//				allCommitNames.add(rc.getName() + "," + rc.getCommitTime());
				allCommitNames.add(rc.getName());
			}
		}
		return allCommitNames;
	}

	public List<DiffEntry> getChangedFilesId() {
		List<DiffEntry> changes = new ArrayList<>();
		try {
			changes = this.calculateDiffs();
			return changes;

		} catch (GitAPIException gapie) {
			System.out.println(
					"GitAPIException caught in method GitRepo.getChangedFiles, Full Message : " + gapie.getMessage());
			return changes;
		} catch (IncorrectObjectTypeException iote) {
			System.out.println("IncorectObjectTypeException caught in Method GitRepo.getChangedFiles. Message = "
					+ iote.getMessage());
			return changes;
		} catch (IOException e) {
			System.out.println("IOException caught in Method GitRepo.getChangedFiles. Message = " + e.getMessage());
			return changes;
		}
	}

	public boolean currentCommitIsMerge() {
		if (this.currentCommit.getParentCount() > 1)
			return true;
		else
			return false;
	}

	public boolean checkoutNextCommit() {
		try {
			this.git.checkout().setForceRefUpdate(true).setName(this.currentCommit.getName()).call();
			this.currentCommitDate = this.currentCommit.getCommitTime() * 1000L;
			return true;
		} catch (GitAPIException gapie) {
			if (this.retries == 1000) {
				return false;
			}
			System.out.println(
					"GitAPIException caught in Method GitRepo.checkoutNextCommit. Message = " + gapie.getMessage());
			String problemCommit = this.currentCommit.getName();
			System.out.println("Trying to resolve by redownloading REPO");
			this.retries++;
			String projectName = this.getProjectName().replace("/", "");
			File file = new  File("../../ExtractorUtilities/projects_extracted/" + projectName);
			this.git.close();
			this.repo.close();
			this.git = null;
			this.repo = null;
			this.deleteRepo(file);
			this.initializeGitRepo();
			while (hasNext()) {
				this.moveToNextCommit();
				if (this.currentCommit.getName().equals(problemCommit))
					break;
			}
			this.checkoutNextCommit();
			System.out.println("Resolved");
			return true;
		}
	}

	public void mergeAverage() {
		int sum = 0;
		int merges = 0;
		int distanceSoFar = 0;
		List<Integer> mergeDistance = new ArrayList<>();
		while (this.iteratorOfCommits.hasNext()) {
			RevCommit rc = this.iteratorOfCommits.next();
			if (rc.getParentCount() > 1) {
				merges++;
				mergeDistance.add(distanceSoFar);
				distanceSoFar = 0;
			}
			distanceSoFar++;
		}
		for (int i : mergeDistance) {
			sum += i;
			System.out.println(i);
		}
		// IntSummaryStatistics istats = mergeDistance.stream()
		// .collect(IntSummaryStatistics::new, IntSummaryStatistics::accept,
		// IntSummaryStatistics::combine);
		System.out
				.println("total mergeCommits in " + this.strPath + " = " + mergeDistance.size() + " | " + sum / merges);

		return;
	}

	/**
	 * @return currentCommit Name in String format
	 */
	public String getCurrentCommitName() {
		return this.currentCommit.getName();
	}

	public String getCurrentCommitDate() {
		SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.currentCommitDate = this.currentCommit.getCommitTime() * 1000L;
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(this.currentCommitDate);
		c.add(Calendar.HOUR, 4);

//		System.out.println(originalFormat.format(this.currentCommitDate) + "+00:00    " + this.currentCommit.getName());
		return originalFormat.format(c.getTimeInMillis()) + "+00:00";
	}

	/**
	 * @return the name of the project
	 */
	public String getProjectName() {
		return this.strPath;
	}

	/**
	 * @return the projectPath in String format
	 */
	public String getProjectPath() {
		return projectPath;
	}

	/**
	 * @return the rootPath in String format
	 */
	public String getRootPath() {
		return rootPath;
	}

	public boolean checkoutByName(String commitId) {
		try {
			this.git.checkout().setForceRefUpdate(true).setName(commitId).call();
//			this.currentCommitDate = this.currentCommit.getCommitTime() * 1000L;
			return true;
		} catch (GitAPIException gapie) {
			System.out.println("GitAPIException caught in Method GitRepo.checkoutNextCommit. Message = "
					+ gapie.getMessage() + " at " + commitId);
			return false;
		}
	}
}
