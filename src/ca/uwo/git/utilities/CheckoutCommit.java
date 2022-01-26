package ca.uwo.git.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class CheckoutCommit{

	private final String rootPath = "/media/or10n/63BB205438159B9C/projects_extracted_2020/";

	private String URL;
	private String strPath;
	private Git git = null;
	private Repository repo = null;

	public CheckoutCommit(String URL){
		this.URL = URL;
		this.strPath = URL.split("/")[URL.split("/").length - 1].replace(".git", "/");
	}

	public Repository list_commits(){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			Path path = new File(rootPath + this.strPath).toPath();
			if (Files.exists(path)) {
				this.git = Git.open(new File(rootPath + this.strPath + ".git"));
				this.git.checkout();
				this.repo = git.getRepository();
			} else {
				System.out.println("started pulling : " + this.strPath);
				CloneCommand cloneCommand = Git.cloneRepository();
				cloneCommand.setURI(URL);
				cloneCommand
						.setCredentialsProvider(new UsernamePasswordCredentialsProvider("964b0757662e731e43d14db28de0f59a3a233f22", ""));
				this.git = cloneCommand.setDirectory(new File(rootPath + this.strPath)).setCloneAllBranches(true).call();
				System.out.println("done pulling : " + this.strPath);
				this.repo = git.getRepository();
			}
			System.out.println("doing : " + this.strPath);
			// list commits
			// String treeName = "refs/heads/master";
			int counter = 0;
			SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd:HH:mm:ss");
			ArrayList<RevCommit> listOfCommits = new ArrayList<>();
			ObjectId head = this.repo.resolve(Constants.HEAD);
			for (RevCommit commit : this.git.log().call()) {
				counter++;
				listOfCommits.add(commit);
			}
			Collections.sort(listOfCommits, new Comparator<RevCommit>(){
				@Override
				public int compare(RevCommit o1, RevCommit o2){
					int i1 = o1.getCommitTime();
					int i2 = o2.getCommitTime();
					return Integer.compare(i1, i2);
				}
			});
			for (RevCommit commit : listOfCommits) {
				// .add(this.repo.resolve(treeName))
				// if (counter % 1000 == 0) {
				// git.clean().setCleanDirectories(true).call();
				if (br.ready()) {
					if (br.readLine().contains("break"))
						git.checkout().setForceRefUpdate(true).setName(head.getName()).call();
					break;
				}
				git.checkout().setForceRefUpdate(true).setName(commit.getName()).call();
				System.out.println(originalFormat.format(commit.getCommitTime() * 1000L));
				// OutputStream outputStream = new FileOutputStream(new File("kill"));
				List<DiffEntry> diffEntries = git.diff().call();

				for (DiffEntry de : diffEntries) {
					System.out.println(de.getOldPath() + " -> " + de.getOldPath() + " || " + de.toString());
				}

				try (Stream<Path> walk = Files.walk(Paths.get(rootPath + this.strPath))) {
					List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
					System.out.println("commit at : " + originalFormat.format(commit.getCommitTime() * 1000L) + " | " + commit.getName()
							+ " : " + result.size());
				}
				// git.checkout().setName("master").setStartPoint(Constants.HEAD).call();
				// git.branchDelete().setBranchNames("transient_marios" + counter).call();
				// System.out.println(list_all_files_in_folder(rootPath + this.strPath));
				// }
				// git.checkout().setName("master").call();
				// for (RevCommit commit : thisgit.log().add(this.repo.resolve(treeName)).call()) {
				// System.out.println(commit.getName());
				// }

			}
			System.out.println("total Commits in " + this.strPath + " = " + listOfCommits.size());
			return this.repo;
		} catch (GitAPIException gapie) {
			System.out.println(gapie.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;

	}

	public static void main(String[] args){
		String[] repos = {
				// "https://github.com/kde/digikam",
				// "https://github.com/kde/amarok",
				// "https://github.com/kde/umbrello",
				// "https://github.com/kde/kmail",
				// "https://github.com/kde/kdevelop",
				"https://github.com/kde/korganizer"
				// , "https://github.com/kde/ktorrent", "https://github.com/kde/akonadi",
				// "https://github.com/kde/kexi", "https://github.com/kde/k3b", "https://github.com/kde/kdelibs",
				// "https://github.com/kde/marble", "https://github.com/kde/konversation", "https://github.com/kde/kmix",
				// "https://github.com/kde/kolourpaint", "https://github.com/kde/kdevplatform", "https://github.com/kde/calligra",
				// "https://github.com/kde/oxygen", "https://github.com/kde/discover", "https://github.com/kde/breeze",
				// "https://github.com/KDE/akregator", "https://github.com/kde/lokalize", "https://github.com/kde/ark",
				// // "https://github.com/kde/kstars",
				// "https://github.com/kde/ktimetracker", "https://github.com/kde/solid", "https://github.com/kde/gwenview",
				// "https://github.com/kde/juk", "https://github.com/kde/plasma-nm", "https://github.com/kde/kompare",
				// "https://github.com/kde/kget", "https://github.com/kde/kontact", "https://github.com/kde/kopete"
		};
		String language;
		System.out.println("starting");
		for (String repoAddress : repos) {
			CheckoutCommit cCommit = new CheckoutCommit(repoAddress + ".git");
			cCommit.list_commits();
		}
	}
}
