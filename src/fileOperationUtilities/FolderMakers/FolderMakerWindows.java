package fileOperationUtilities.FolderMakers;

import java.io.File;

public class FolderMakerWindows implements FolderMaker{

	private static FolderMakerWindows theFolderMaker;

	private FolderMakerWindows(){
	}

	public static FolderMakerWindows getInstance(){
		if (theFolderMaker == null)
			theFolderMaker = new FolderMakerWindows();
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
