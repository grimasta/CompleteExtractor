package console.commanders;

public class DynamicCommands {
	
	public static String getDynamicCopy() {
		switch(System.getProperty("os.name")) {
		case "Linux":
			return "cp ";
		case "Windows 10":
			return "cp ";
		default:
			return "cp ";
		}
	}
	
	public static String getDynamicRecursiveCopy() {
		switch(System.getProperty("os.name")) {
		case "Linux":
			return "cp -R ";
		case "Windows 10":
			return "cp -R ";
		default:
			return "cp -R ";
		}
	}	
	
	public static String getDynamicDelete() {
		switch(System.getProperty("os.name")) {
		case "Linux":
			return "rm -r ";
		case "Windows 10":
			return "rm -r ";
		default:
			return "rm -r ";
		}
	}
	
	public static String getDynamicMakeDir() {
		switch(System.getProperty("os.name")) {
		case "Linux":
			return "mkdir ";
		case "Windows 10":
			return "mkdir ";
		default:
			return "mkdir ";
		}
	}
	
	public static String getDynamicFetch() {
		switch(System.getProperty("os.name")) {
		case "Linux":
			return "/home/or10n/extractor/fetch-Java/scripts/";
		case "Windows 10":
			return "/home/vagrant/extractor/fetch-Java/scripts/";
		default:
			return "/home/or10n/extractor/fetch-Java/scripts/";
		}
	}
	
	public static String getDynamicRsfWithNames() {
		switch(System.getProperty("os.name")) {
		case "Linux":
			return "python /home/or10n/extractor/fetch-Java/scripts/createRSFwithNames.py";
		case "Windows 10":
			return "python /home/vagrant/extractor/fetch-Java/scripts/createRSFwithNames.py";
		default:
			return "python /home/or10n/extractor/fetch-Java/scripts/createRSFwithNames.py"; 
		}
	}
}
