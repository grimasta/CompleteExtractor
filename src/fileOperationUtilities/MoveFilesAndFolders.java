package fileOperationUtilities;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import console.commanders.ConsoleFactory;
import console.commanders.DynamicCommands;

public class MoveFilesAndFolders {
	private String rootPath;
	private String destinationPath;
	private Map<String, String> listOfFiles;
	private String currentCommitID;
	/**
	 * 
	 * @param rootPath example ../../ExtractorUtilities/projects_extracted/kwin/
	 * @param destinationPath example ../../ExtractorUtilities/increments/3000commits/kwin/2e7bc0df87845e2a5e22f2280f7047033fa83af3/
	 * @param listOfFiles 
	 */
	
	public MoveFilesAndFolders(String rootPath, String destinationPath, Map<String, String> listOfFiles) {
		this.rootPath = PathVMRectifier.rectify(rootPath); // example rootPath = /vagrant/ExtractorUtilities/projects_extracted/yetus/
		this.destinationPath = PathVMRectifier.rectify(destinationPath); //example destinationPath = /vagrant/ExtractorUtilities/increments/3000commits/yetus//32153d6737981e61bbfe53b944724bedcb0c5e06/
		this.currentCommitID = destinationPath.split("/")[destinationPath.split("/").length-1];
		this.listOfFiles = listOfFiles;
	}

	public MoveFilesAndFolders(String rootPath, String destinationPath) {
			this.rootPath = PathVMRectifier.rectify(rootPath);
			this.destinationPath = PathVMRectifier.rectify(destinationPath);
	}
	
	public MoveFilesAndFolders() {
	}

	public MoveFilesAndFolders getNewInstance(String rootPath, String destinationPath, Map<String, String> listOfFiles) {
		return new MoveFilesAndFolders(rootPath, destinationPath, listOfFiles);
	}
	
	/**
	 * move filename from the rootPath to the destinationPath provided at
	 * instantiation of the Class doesn't work if files are within any folder depth.
	 * If your files are nested in folder use moveInDepth()
	 * 
	 * @param fileName
	 */
	public void move(String fileName) {
		String source = this.rootPath + fileName;
		String target = this.destinationPath + fileName;
		File destination =  new File(PathVMRectifier.deRectify(this.destinationPath));
		if (!destination.exists())
			mkdir(this.destinationPath);
		ConsoleFactory.getConsole().run(DynamicCommands.getDynamicCopy() + source + " " + target, currentCommitID);
	}

	/**
	 * move filename from the rootPath to the destinationPath provided at
	 * instantiation of the Class doesn't work if files are within any folder depth.
	 * If your files are nested in folder use moveInDepth()
	 * 
	 * @param fileName
	 */
	public void moveFolder(String location, String fileName) {
		String source = this.rootPath + location + fileName;
		String target = this.destinationPath + fileName;
		File destination = new File(PathVMRectifier.deRectify(this.destinationPath));
		if (!destination.exists())
			mkdir(this.destinationPath);
		System.out.println(source + " -> " + target);
		ConsoleFactory.getConsole().run(DynamicCommands.getDynamicRecursiveCopy() + source + " " + target, currentCommitID);
	}

	/**
	 * move any file from rootPath to destinationPath and create all necessary
	 * folders to mimic the exact folder structure of the sourcePath and filename
	 * 
	 * @param fileName
	 */
	public void moveInDepth(String fileName) {
		String source = this.rootPath + fileName;
		String target = this.destinationPath + fileName;
		String[] sourceParts = target.split("/");
		String sourcePathIncremental = sourceParts[0] + "/";
		int i = 1;
		while (i < sourceParts.length - 1) {
			sourcePathIncremental += sourceParts[i] + "/";
			i++;
			if (Files.notExists(Paths.get(PathVMRectifier.deRectify(sourcePathIncremental))))
				mkdir(sourcePathIncremental);
		}
//		System.out.println(source + " -> " + target);
		ConsoleFactory.getConsole().run(DynamicCommands.getDynamicCopy() + source + " " + target, currentCommitID);
	}

	public void moveAllFromMap() {
		if (this.listOfFiles.isEmpty()) {
			System.out.println("Nothing to move");
		} else {
			for (Entry<String, String> e : this.listOfFiles.entrySet()) {
				if (!e.getValue().contains("/dev/null"))
					moveInDepth(e.getValue());
			}
		}
	}

	public void moveAll() {
		System.out.println(this.rootPath + " - > " + this.destinationPath);

		ConsoleFactory.getConsole().run(DynamicCommands.getDynamicRecursiveCopy()
				+ this.rootPath.substring(0, this.rootPath.length() - 1) + " " + this.destinationPath, currentCommitID);
	}

	public void deleteDestinationFolder() {
		ConsoleFactory.getConsole().run(DynamicCommands.getDynamicDelete() + this.destinationPath);
	}

	public void moveBack(String fileName) {
		String source = this.destinationPath + fileName;
		String target = this.rootPath + fileName;
		ConsoleFactory.getConsole().run(DynamicCommands.getDynamicCopy() + source + " " + target, currentCommitID);
	}

	private void mkdir(String directoryPath) {
		ConsoleFactory.getConsole().run(DynamicCommands.getDynamicMakeDir() + directoryPath, currentCommitID);
	}

}
