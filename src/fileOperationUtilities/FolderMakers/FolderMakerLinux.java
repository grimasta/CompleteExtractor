package fileOperationUtilities.FolderMakers;

import java.io.File;

public class FolderMakerLinux implements FolderMaker{

	private static FolderMakerLinux theFolderMaker;

	private FolderMakerLinux(){
	}

	public static FolderMakerLinux getInstance(){
		if (theFolderMaker == null)
			theFolderMaker = new FolderMakerLinux();
		return theFolderMaker;
	}

	@Override
	public void mkdir(String folderName){
		File fileToMake = new File(folderName);
		if (fileToMake.exists())
			return;
		else {
			fileToMake.mkdir();
			return;
		}
		// TODO Auto-generated method stub

	}

}
