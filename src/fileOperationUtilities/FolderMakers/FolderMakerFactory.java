package fileOperationUtilities.FolderMakers;

public class FolderMakerFactory{

	public static FolderMaker getFolderMaker(){
		switch (System.getProperty("os.name")) {
		case "Linux":
			return FolderMakerLinux.getInstance();
		case "Windows 10":
			return FolderMakerWindows.getInstance();
		default:
			return null;
		}
	}
}
