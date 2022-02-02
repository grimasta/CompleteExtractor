package console.commanders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class SshConsole implements Console{

	private static SshConsole theConsole;

	private SshConsole(){
	}

	public static SshConsole getInstance(){
		if (theConsole == null)
			theConsole = new SshConsole();
		return theConsole;
		// TODO Auto-generated constructor stub
	}

	public int run(String command){
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			Process executionProcess = thisRuntime.exec("cmd /c " + command);
			executionProcess.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
			String s;
			while ((s = br.readLine()) != null)
				System.out.println("Output : " + s);
			if (executionProcess.exitValue() != 0)
				System.out.println("Command : cmd " + command + " ||| exited with code : " + executionProcess.exitValue());
			return executionProcess.exitValue();
		} catch (IOException ioe) {
			System.out.println("exception raised when executing command : ");
			System.out.println(command);
			System.out.println("in SshConsole");
			System.out.println("IOException message = " + ioe.getMessage());
			return -1;
		} catch (InterruptedException ie) {
			System.out.println("exception raised when executing command : ");
			System.out.println(command);
			System.out.println("in SshConsole");
			System.out.println("InterruptedException message = " + ie.getMessage());
			return -1;
		}

	}

	public int run(String command, String[] envp, File dir){
		return 0;
	}

	public String runGetOutput(String command){
		return "no Output";
	}
	
	public int run(String[] commandArgs, String[] envp, File dir_) {
		return 0;
	}
	
	public String runGetOutput(String[] commandArgs) {
		return "not implemented";
	}

}
