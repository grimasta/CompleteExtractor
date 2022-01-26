package ca.uwo.git.utilities;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class SelectByDate {
	private Repository repo = null;
	private Git git = null;
	public SelectByDate(Git git, Repository repo) {
		this.git = git;
		this.repo = repo;
	}
	
	public List<RevCommit> getCommitsByDate(Long time) {
		
		String treeName = "refs/heads/master"; // tag or branch
		List<RevCommit> listOfCommits = new ArrayList<>();
		try {
			System.out.println(git + " " + repo);
			for (RevCommit commit : git.log().add(repo.resolve(treeName)).call()) {
				if (commit.getCommitTime() < time) {
					listOfCommits.add(commit);
				}
			}
			return listOfCommits;
		} catch (RevisionSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AmbiguousObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println(listOfCommits.size());
		for(RevCommit c : listOfCommits) {
			c.getCommitTime();
//			Timestamp ts = new Timestamp(c.getCommitTime());
//			System.out.println(c.getCommitTime());
			LocalDateTime date = LocalDateTime.ofEpochSecond(c.getCommitTime(), 0, ZoneOffset.UTC);
//			System.out.println(date);
			
		}
		return null;
	}
}
