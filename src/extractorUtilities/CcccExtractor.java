package extractorUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

public class CcccExtractor implements Extraction {

	private String target;
	private String rootPath;
	private String projectPath;
	private String projectName;

	public CcccExtractor(String target, String rootPath, String projectPath) {
		this.target = target;
		this.rootPath = rootPath;
		this.projectPath = projectPath;
		this.projectName = rootPath.split("/")[rootPath.split("/").length - 1];
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
		int extractionCount = 0;
		System.out.println("target = " + this.target);
		String fullProjectPath = this.rootPath.split("increments")[0] + "projects_extracted/" + this.projectName + "/";
//		System.out.println(this.rootPath);
		System.out.println(fullProjectPath);
//		System.exit(0);
		String CcccScriptPrefix = "find";
		String CcccScriptSuffix = "/bin/cccc -";
//		String rsfWithNamesScript = "python /home/or10n/extractor/fetch-Java/scripts/createRSFwithNames.py";
//		get list of files to run CCCC on
		String[] commandArgs = { "/bin/sh", "-c", "find " + fullProjectPath + " > list_of_files" };
//		following line probably useless
//		String[] args2 = {"find", "/home/or10n/ExtractorUtilities/projects_extracted/clazy"};
//		run the find all files command
//		System.out.println("before listing");
		File targetCommit = new File(this.rootPath + this.target);
		if (targetCommit.exists()) {
//			for (String s : commandArgs)
//				System.out.println(s);
			ConsoleFactory.getConsole().run(commandArgs, null, null);
//			System.out.println("listing done");
			try {
//			read the list of file
				BufferedReader br = new BufferedReader(new FileReader(new File("list_of_files")));

//			build the list of files to be used as input for the CCCC script
				String cccc_input = "";
//			initialize a counter, testing has showed an abnormal behavior when the number of files to be analyzed exceeds 
//			certain numbers (around 100) so we keep it to around 50 and will further test to make sure it always works 
				int i = 0;
				while (br.ready()) {
					String line = br.readLine();
//				filter out all files that are not c or cpp source or header files as well java source files
					if (line.endsWith(".c") || line.endsWith(".cc") || line.endsWith(".h") || line.endsWith(".cpp")
							|| line.endsWith(".hpp") || line.endsWith(".java"))
						cccc_input += " " + line;
				}
				br.close();
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("list_of_files")));
				bw.write(cccc_input);
				bw.close();
				System.out.println("before cccc command");
				String[] ccccCommand = new String[] { "/bin/bash", "-c", "./run_cccc.sh" };
				System.out.println(ConsoleFactory.getConsole().run(ccccCommand, null, null));
				System.out.println("after cccc command");
				writeToResultToFile(null, null);
			} catch (IOException ioe) {
				System.out.println("Error while opening the list of files in CcccExtractor: \nError Message \n"
						+ ioe.getLocalizedMessage());
			}

			System.out.println("finished_extraction");
			return true;
		} else
			return false;
	}

	private void writeToResultToFile(String result, String fileName) {
		File ccccFolder = new File(".cccc");
		if (ccccFolder.list().length > 0) {
		String pythonCommand = "python3 /home/or10n/PycharmProjects/XmlToCsvConverter/main.py " + this.target + " "
				+ this.projectName;
//		String[] commandArgs = { "/bin/sh", "-c", pythonCommand };
//		for (String sss : commandArgs)
//			System.out.println(sss);
		System.out.println("before python command");
		String result1 = ConsoleFactory.getConsole().runGetOutput(pythonCommand);
		System.out.println("Python command result  = " + result1);
		System.out.println("after python command");
		} else {
			System.out.println("no input for python tool");
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
	}

	@Override
	public void clearExtractionLocation() {
		int result = ConsoleFactory.getConsole().run(new String[] { "/bin/sh", "-c", "rm .cccc/*" }, null, null);
		System.out.println(result);

	}

}