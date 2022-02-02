package console.commanders;

public class ConsoleFactory{

	public static Console getConsole(){
		switch (System.getProperty("os.name")) {
		case "Linux":
			return UnixConsole.getInstance();
		case "Windows 10":
			return SshConsole.getInstance();
		default:
			System.out.println(System.getProperty("os.name"));
			return null;
		}
	}
}
