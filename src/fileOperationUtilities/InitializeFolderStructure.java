package fileOperationUtilities;

import java.io.File;

import fileOperationUtilities.FolderMakers.FolderMakerFactory;
import paths.LinuxPaths;
import paths.WindowsPaths;

public class InitializeFolderStructure{

	public static void initializeFolders(){
		String[] folders = { "analyzed_commits", "git_files", "increments", "projects_extracted", "reconciled_incremental", "test_files" };
		File root;
		for (String folderName : folders) {
			switch (System.getProperty("os.name")) {
			case "Linux":
				FolderMakerFactory.getFolderMaker().mkdir(LinuxPaths.ROOT + folderName);
				root = new File(LinuxPaths.ROOT);
//				for (String s : root.list())
//					System.out.println(s);
			case "Windows 10":
				FolderMakerFactory.getFolderMaker().mkdir(WindowsPaths.ROOT + folderName);
				root = new File(WindowsPaths.ROOT);
//				for (String s : root.list())
//					System.out.println(s);
			}
		}
	}
}
