package extractorUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import console.commanders.ConsoleFactory;
import fileOperationUtilities.MoveFilesAndFolders;
import paths.DynamicPaths;

public class McabeExtractor implements Extraction {

	private String target;
	private String rootPath;
	private String projectPath;

	public McabeExtractor(String target, String rootPath, String projectPath) {
		this.target = target;
		this.rootPath = rootPath;
		this.projectPath = projectPath;
	}

	@Override
	public String checkLanguage() {
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

	@Override
	public boolean doExtraction(String language) {
		System.out.println("target = " + this.target);
		String pmcCabeScript = "/home/or10n/extractor/fetch/bin/pmccabe";
//		String rsfWithNamesScript = "python /home/or10n/extractor/fetch-Java/scripts/createRSFwithNames.py";
		int i = 0;
		File targetProject = new File(this.rootPath + this.target);
		boolean done = false;
		if (targetProject.exists() && targetProject.isDirectory()) {
			String pwd = this.rootPath;
			System.out.println("started_extraction");
//			System.out.println(ConsoleFactory.getConsole().runGetOutput("pwd"));
			for (String fileName : listFiles(this.rootPath, this.target)) {
//				System.out.println("filename = " + fileName);
//				System.out.println("this.target = " + this.target);
				String[] subarray = IntStream.range(0, fileName.split("/").length - 1)
						.mapToObj(j -> fileName.split("/")[j]).toArray(String[]::new);
				String transientWorkingDir = String.join("/", subarray);
				String result = ConsoleFactory.getConsole().runGetOutput(pmcCabeScript + " " + fileName);
				writeToResultToFile(result, fileName);
//				System.out.println("printing from McabeExtractor " + result);
			}

			System.out.println("finished_extraction");
//			done = done && (ConsoleFactory.getConsole().run(rsfWithNamesScript + " " + this.target + ".rsf", null, new File(pwd)) == 0);
			return done;
		}
		return done;
	}

	private void writeToResultToFile(String result, String fileName) {
		String projectName = this.rootPath.split("/")[this.rootPath.split("/").length - 1];
		String commitAndFileName = fileName.split("/" + projectName + "/")[1];
		ArrayList<String> reportParts = new ArrayList<>();
		for (String part : commitAndFileName.split("/")) {
			reportParts.add(part);
		}
		String commitId = reportParts.get(0);
		reportParts.remove(0);
		String localFileName = String.join("/", reportParts);
//		System.out.println("commitID  " + commitId);
//		System.out.println("localFileName  " + localFileName);

//		new File(fileName);
		try {
			FileWriter fw = null;
			File outputFile = new File("pmcCabe/" + projectName + ".csv");
			if (!outputFile.isFile()) {
				fw = new FileWriter(outputFile);
				fw.write(
						"commitId, filename, Modified McCabe Cyclomatic Complexity, Traditional McCabe Cyclomatic Complexity, # Statements in function,"
								+ "First line of function, # lines in function, definition line number, function \n");
			} else {
				fw = new FileWriter(outputFile, true);
			}
			if (result.length() > 0) {
				for (String resultLine : result.split("\n")) {
					if (resultLine.split("\t").length > 1)
						fw.write(convertToCSV(this.projectPath, localFileName, resultLine.split("\t")));
				}
			}
			fw.close();
		} catch (IOException ioe) {
			System.out.println("Error while writing pmccabe result to file ");
			System.err.println(ioe.getMessage());
		}
	}

	public String convertToCSV(String first, String second, String[] data) {
		String csvFormatted = "";
		csvFormatted += first;
		csvFormatted += ", ";
		csvFormatted += second;
		csvFormatted += ", ";
		csvFormatted += data[0];
		csvFormatted += ", ";
		csvFormatted += data[1];
		csvFormatted += ", ";
		csvFormatted += data[2];
		csvFormatted += ", ";
		csvFormatted += data[3];
		csvFormatted += ", ";
		csvFormatted += data[4];
		csvFormatted += ", ";
		csvFormatted += data[5].split(" ")[0].split("\\(")[1].split("\\)")[0];
		csvFormatted += ", ";
		csvFormatted += data[5].split(" ")[1];
		csvFormatted += "\n";
		return csvFormatted;
	}

	private List<String> listFiles(String rootPath, String folder) {
		List<String> listOfFiles = new ArrayList<>();
		for (String f : new File(rootPath + folder).list()) {
			if (!new File(f).isDirectory()) {
				listOfFiles.add(rootPath + folder + '/' + f);
			} else {
				listOfFiles.addAll(listFiles(rootPath + folder + "/", f));
			}
		}
		return listOfFiles;
	}

	@Override
	public void storeExtraction() {
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
			System.out
					.println("IOException caught in method Extractor::storeExtraction. Message : " + ioe.getMessage());
		}
	}

	@Override
	public void clearExtractionLocation() {
		String deleteCommand = "rm -r ";
		File extractionLocation = new File(this.rootPath);
		for (File f : extractionLocation.listFiles())
			if (f.getName().contains(".rsf") || f.getName().contains(".log") || f.getName().contains(".cdif"))
				f.delete();
		String folderTreeForDeletion = this.rootPath + this.target;
		ConsoleFactory.getConsole().run(deleteCommand + folderTreeForDeletion);
	}

}