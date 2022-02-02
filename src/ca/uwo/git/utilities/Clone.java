package ca.uwo.git.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import fileOperationUtilities.MoveFilesAndFolders;

public class Clone{
	private final String rootPath = "/media/or10n/63BB205438159B9C/projects_extracted_2020/";

	private String URL;
	private String strPath;
	private Git git = null;
	private Repository repo = null;

	public Clone(String URL){
		this.URL = URL;
		this.strPath = URL.split("/")[URL.split("/").length - 1].replace(".git", "/");
	}

	public Repository doClone(){
		try {
			Path path = new File(rootPath + this.strPath).toPath();
			if (Files.exists(path)) {
				this.git = Git.open(new File(rootPath + this.strPath + ".git"));
				this.git.checkout();
				this.repo = git.getRepository();
			} else {
				System.out.println("starting : " + this.strPath);
				CloneCommand cloneCommand = Git.cloneRepository();
				cloneCommand.setURI(URL);
				cloneCommand
						.setCredentialsProvider(new UsernamePasswordCredentialsProvider("964b0757662e731e43d14db28de0f59a3a233f22", ""));
				this.git = cloneCommand.setDirectory(new File(rootPath + this.strPath)).setCloneAllBranches(true).call();
				System.out.println("finished : " + this.strPath);
				this.repo = git.getRepository();
			}
			return this.repo;
		} catch (GitAPIException gapie) {
			System.out.println(gapie.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;

	}

	public static void main(String[] args){
		String[] repos = { "https://github.com/kde/digikam", "https://github.com/kde/amarok", "https://github.com/kde/umbrello",
				"https://github.com/kde/kmail", "https://github.com/kde/kdevelop", "https://github.com/kde/korganizer",
				"https://github.com/kde/ktorrent", "https://github.com/kde/akonadi", "https://github.com/kde/kexi",
				"https://github.com/kde/k3b", "https://github.com/kde/kdelibs", "https://github.com/kde/marble",
				"https://github.com/kde/konversation", "https://github.com/kde/kmix", "https://github.com/kde/kolourpaint",
				"https://github.com/kde/kdevplatform", "https://github.com/kde/calligra", "https://github.com/kde/oxygen",
				"https://github.com/kde/discover", "https://github.com/kde/breeze", "https://github.com/KDE/akregator",
				"https://github.com/kde/lokalize", "https://github.com/kde/ark",
				// "https://github.com/kde/kstars",
				"https://github.com/kde/ktimetracker", "https://github.com/kde/solid", "https://github.com/kde/gwenview",
				"https://github.com/kde/juk", "https://github.com/kde/plasma-nm", "https://github.com/kde/kompare",
				"https://github.com/kde/kget", "https://github.com/kde/kontact", "https://github.com/kde/kopete" };
		String language;
		System.out.println("starting");
		for (String repoAddress : repos) {
			Clone clone = new Clone(repoAddress + ".git");
			// Clone clone = new Clone("https://github.com/eclipse/jgit.git");
			clone.doClone();

			// SelectByDate selectByDate = new SelectByDate(clone.git, clone.repo);
			// selectByDate.getCommitsByDate((long) 5);
			// System.out.println("done");
			String destinationPath = "/media/or10n/63BB205438159B9C/git_files/";
			// MoveFilesAndFolders mfaf = new MoveFilesAndFolders(clone.rootPath + clone.path, destinationPath + clone.path);
			// mfaf.move(".git");
			// TODO remove any top level directories containing the word test
			System.out.println("currently doing : " + clone.getPath());
			// clone.listDir(clone.rootPath + clone.getPath());
			language = clone.checkLanguage(clone.getPath());
			// if (clone.path.contains("solid"))
			clone.checkPwd(clone.getPath().replace("/", ""), language);

		}

	}

	public String checkLanguage(String target){
		String localPath = rootPath + this.strPath;
		List<String> fileNames = new ArrayList<>();
		List<String> folderNames = new ArrayList<>();
		String sourcePath = "/media/or10n/63BB205438159B9C/projects_extracted_2020/";
		String destinationPath = "/media/or10n/63BB205438159B9C/test_files/";
		int cppCounter = 0;
		int javaCounter = 0;
		Stream<Path> allFolders;
		Stream<Path> allFiles;
		try {
			allFolders = Files.walk(Paths.get(localPath)).filter(Files::isDirectory);
			allFolders.forEach(path -> folderNames.add(path.toString()));

		} catch (IOException ioe) {
			System.out.println("exception handled at " + ioe.getMessage());
		}
		for (String p : folderNames) {
			String[] pathParts = p.split("/");
			if (pathParts[pathParts.length - 1].contains("test")) {
				System.out.println(p);
				MoveFilesAndFolders mfaf = new MoveFilesAndFolders(sourcePath, destinationPath);
				mfaf.moveInDepth(p.replace(sourcePath, ""));
			}
		}
		try {
			allFiles = Files.walk(Paths.get(localPath)).filter(Files::isRegularFile);
			allFiles.forEach(path -> fileNames.add(path.toString()));
		} catch (IOException ioe) {
			System.out.println("exception handled at " + ioe.getMessage());
		}
		for (String p : fileNames) {
			String[] pathString = p.split("\\.");
			if (pathString.length > 1) {
				if (new File(p).isDirectory())
					if (pathString[0].contains("test")) {
						System.out.println(pathString[0]);
						MoveFilesAndFolders mfaf = new MoveFilesAndFolders(sourcePath, destinationPath);
						mfaf.move(p.replace(sourcePath, ""));
					}
				if (pathString[pathString.length - 1].toLowerCase().equals("cpp"))
					cppCounter += 1;
				if (pathString[pathString.length - 1].toLowerCase().equals("java"))
					javaCounter += 1;
			}
		}
		System.out.println("cppCounter = " + cppCounter + " javaCounter = " + javaCounter);
		if (javaCounter > cppCounter)
			return "java";
		else
			return "cpp";

	}

	public void checkPwd(String target, String language){
		System.out.println("target = " + target);
		String fetchScript = "/home/or10n/extractor/fetch-Java/scripts/" + language + "2rsf.sh";
		String rsfWithNamesScript = "python /home/or10n/extractor/fetch-Java/scripts/createRSFwithNames.py";
		String s;
		Process p;
		Process p2;
		int i = 0;
		try {
			Runtime r = Runtime.getRuntime();
			String pwd = "/media/or10n/63BB205438159B9C/projects_extracted_2020/";

			p2 = r.exec(fetchScript + " " + target, null, new File(pwd));
			System.out.println("started");
			p2.waitFor();
			System.out.println("finished");

			p = r.exec(rsfWithNamesScript + " " + target + ".rsf", null, new File(pwd));
			// r.ex
			// p2 = r.exec("ls");
			// p = Runtime.getRuntime().exec("pwd");

			BufferedReader br = new BufferedReader(new InputStreamReader(p2.getInputStream()));
			while ((s = br.readLine()) != null)
				System.out.println("line: " + s);
			// p.waitFor();
			p2.waitFor();
			// System.out.println("exit: " + p.exitValue());
			System.out.println("exit p2: " + p2.exitValue());
			p2.destroy();
			// p.destroy();
		} catch (Exception e) {
		}

	}

	public void listDir(String path){
		File projectDir = new File(path);
		List<String> filenamesInProjectDir = new ArrayList<>();
		for (String f : projectDir.list()) {
			if (f.contains("test"))
				System.out.println(f);
		}
	}

	public Git getGit(){
		return git;
	}

	public Repository getRepo(){
		return repo;
	}

	/**
	 * @return the path
	 */
	public String getPath(){
		return strPath;
	}

}
