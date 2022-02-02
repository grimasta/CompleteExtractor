package extractorUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import console.commanders.ConsoleFactory;
import fileOperationUtilities.MoveFilesAndFolders;
import paths.DynamicPaths;

public class Extractor{

	private String target;
	private String rootPath;
	private String projectPath;

	public Extractor(String target, String rootPath, String projectPath){
		this.target = target;
		this.rootPath = rootPath;
		this.projectPath = projectPath;
	}

	public String checkLanguage(){
		String localPath = rootPath + this.projectPath;
		List<String> fileNames = new ArrayList<>();
		List<String> folderNames = new ArrayList<>();
		String sourcePath = DynamicPaths.getPath() + "/projects_extracted/";
		String destinationPath = DynamicPaths.getPath() + " test_files/";
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
		// move test files
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

	public boolean doExtraction(String language){
		System.out.println("target = " + this.target);
		String fetchScript = "/home/or10n/extractor/fetch-Java/scripts/" + language + "2rsf.sh";
		String rsfWithNamesScript = "python /home/or10n/extractor/fetch-Java/scripts/createRSFwithNames.py";
		int i = 0;
		File targetProject = new File(this.rootPath + this.target);
		boolean done = false;
		if (targetProject.exists() && targetProject.isDirectory()) {
			String pwd = this.rootPath;
			System.out.println("started_extraction");
			done = (ConsoleFactory.getConsole().run(fetchScript + " " + this.target, null, new File(pwd)) == 0);
			System.out.println("finished_extraction");
			done = done && (ConsoleFactory.getConsole().run(rsfWithNamesScript + " " + this.target + ".rsf", null, new File(pwd)) == 0);
			return done;
		}
		return done;
	}

	public void storeExtraction(){
		// this.rootPath;
		MoveFilesAndFolders moveFilesAndFoldersResultsOfExtraction = new MoveFilesAndFolders(this.rootPath,
				this.rootPath + "stored_" + this.target + "/");
		try {
			Stream<Path> allFiles = Files.walk(Paths.get(this.rootPath));
			List<String> fileNames = new ArrayList<>();
			allFiles.forEach(path -> fileNames.add(path.toString()));
			// for (String fileName : fileNames) {
			moveFilesAndFoldersResultsOfExtraction.move(this.target + ".rsf");
			moveFilesAndFoldersResultsOfExtraction.move(this.target + "_final.rsf");
			moveFilesAndFoldersResultsOfExtraction.move(this.target + "_names.rsf");
			moveFilesAndFoldersResultsOfExtraction.move(this.target + ".cdif");
			moveFilesAndFoldersResultsOfExtraction.moveFolder(this.target + "/", "dbdump");

			// }
		} catch (IOException ioe) {
			System.out.println("IOException caught in method Extractor::storeExtraction. Message : " + ioe.getMessage());
		}
	}

	public void clearExtractionLocation(){
		String deleteCommand = "rm -r ";
		File extractionLocation = new File(this.rootPath);
		for (File f : extractionLocation.listFiles())
			if (f.getName().contains(".rsf") || f.getName().contains(".log") || f.getName().contains(".cdif"))
				f.delete();
		String folderTreeForDeletion = this.rootPath + this.target;
		ConsoleFactory.getConsole().run(deleteCommand + folderTreeForDeletion);
	}

}
