package ca.uwo.git.utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class ConcurrentDiff implements Callable<String> {
	
	private static boolean lock = false;
	private Git git = null;
	private RevCommit currentCommit = null;
	private AbstractTreeIterator oldTree = null;
	private AbstractTreeIterator newTree = new EmptyTreeIterator();
	private Repository repo = null;
	private String outputString = "";
	private PrintWriter writer = null;

	public ConcurrentDiff(Git git, RevCommit currentCommit, Repository repo, PrintWriter writer) {
		this.git = git;
		this.currentCommit = currentCommit;
		this.repo = repo;
		this.writer = writer;
	}
	
	public String call() {
		getChanges();
		return outputString;
	}
	
	public void getChanges(){
		try {
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			df.setRepository(this.repo);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			
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
			for (DiffEntry de : this.calculateDiffs()){
				int added = 0;
				int deleted = 0;
//				for (Edit edit : df.toFileHeader(de).toEditList()) {
//					added += edit.getEndB() - edit.getBeginB();
//					deleted += edit.getEndA() - edit.getBeginA();
//				}
				String file_name = de.getNewPath(); 
				outputString += commitId + "," + commitMessage + "," + committed_at + "," + committer_email
						+ "," + committer_id + "," + committer_name + "," + committer_id + "," + committer_name
						+ "," + author_email + "," + author_id + "," + author_name + "," + file_name + "," + added + "," + deleted + "\n";
//				if (outputString.length() > 400000)
//					synchronized (this) {
//						writer.print(outputString);
//						outputString = "";	
//					}

//				System.out.println(outputString);
			}
//			synchronized (this) {
//				writer.print(outputString);
//				outputString = "";
//			}
			df.close();
		} catch (GitAPIException gapie) {
			System.out.println("GitAPIException caught in method GitRepo.getChangedFiles, Full Message : " + gapie.getMessage());
		} catch (IncorrectObjectTypeException iote) {
			System.out.println("IncorectObjectTypeException caught in Method GitRepo.getChangedFiles. Message = " + iote.getMessage());
		} catch (IOException e) {
			System.out.println("IOException caught in Method GitRepo.getChangedFiles. Message = " + e.getMessage());
		}
	}
	
	private List<DiffEntry> calculateDiffs() throws MissingObjectException, IncorrectObjectTypeException, IOException, GitAPIException{
		List<DiffEntry> diffEntries = new ArrayList<>();

		// set old treeIterator equal to old treeIterator and new TreeIterator equal to current TreeIterator
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
	
	public String getOutputString() {
		return outputString;
	}

}
