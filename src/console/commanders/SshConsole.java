package console.commanders;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bouncycastle.util.Arrays;

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
	
	//	ssh -F C:\Users\spawn\vagrant_workspaces\ExtractorSpace\vagrant-ssh default "bash --login -c 'env'"
//	ssh -F C:\Users\spawn\vagrant_workspaces\ExtractorSpace\vagrant-ssh default "/vagrant/run_extractor.sh"
	
	public int run(String command){
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("../../run_script.sh")));
			bw.write(command);
			System.out.println("command being run:" + command);
			bw.close();
			Process executionProcess = thisRuntime.exec("cmd /c C:/Users/spawn/vagrant_workspaces/ExtractorSpace/bash_script.bat");
			executionProcess.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
			String s;
			while ((s = br.readLine()) != null)
				System.out.println("Output : " + s);
			if (executionProcess.exitValue() != 0)
				System.out.println("Command : " + command + " ||| exited with code : " + executionProcess.exitValue());
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
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("../../run_script.sh")));
			System.out.println(dir.getAbsolutePath());
			String[] Parts = dir.getAbsolutePath().split("\\\\");
			String pwd = "";
			for (int i = 1; i < Parts.length; i++)
				pwd += "/" + Parts[i];
			bw.write("cd " + pwd + "\n");
			bw.write(command);
			System.out.println("command running with environment variable: " + command);
			bw.close();
			Process executionProcess = thisRuntime.exec("cmd /c C:/Users/spawn/vagrant_workspaces/ExtractorSpace/bash_script.bat");
			executionProcess.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
			String s;
			while ((s = br.readLine()) != null)
				System.out.println("command running with environment variable Output : " + s);
			if (executionProcess.exitValue() != 0)
				System.out.println("command running with environment variable Command : " + command + " ||| exited with code : " + executionProcess.exitValue());
			return executionProcess.exitValue();
		} catch (IOException ioe) {
			System.out.println("exception raised when executing command running with environment variable : ");
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

	public String runGetOutput(String command){
		return "no Output";
	}

}
