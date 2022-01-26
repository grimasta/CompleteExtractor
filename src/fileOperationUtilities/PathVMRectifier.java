package fileOperationUtilities;

import paths.DynamicPaths;

public class PathVMRectifier {

	public static String rectify(String javaPath) {
		switch(System.getProperty("os.name")) {
		case "Linux":
			return javaPath;
		case "Windows 10":
			return javaPath.replace(DynamicPaths.getPath(), DynamicPaths.getVagrantPath());
		default:
			return javaPath;
		}
	}
	
	public static String deRectify(String javaPath) {
		switch(System.getProperty("os.name")) {
		case "Linux":
			return javaPath;
		case "Windows 10":
			return javaPath.replace(DynamicPaths.getVagrantPath(), DynamicPaths.getPath());
		default:
			return javaPath;
		}
	}
}
