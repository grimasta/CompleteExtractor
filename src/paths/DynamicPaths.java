package paths;

public class DynamicPaths{

	public static String getPath(){
		switch (System.getProperty("os.name")) {
		case "Linux":
			return LinuxPaths.ROOT;
		case "Windows 10":
			return WindowsPaths.ROOT;
		default:
			return null;
		}
	}
}
