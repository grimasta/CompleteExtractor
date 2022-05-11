package console.commanders;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.util.Strings;
import org.bouncycastle.util.test.TestRandomBigInteger;

import com.jcraft.jsch.jce.Random;

public class SshConsole implements Console {

	private static SshConsole theConsole;

	private SshConsole() {
	}

	public static SshConsole getInstance() {
		if (theConsole == null)
			theConsole = new SshConsole();
		return theConsole;
		// TODO Auto-generated constructor stub
	}

	// ssh -F C:\Users\spawn\vagrant_workspaces\ExtractorSpace\vagrant-ssh default
	// "bash --login -c 'env'"
//	ssh -F C:\Users\spawn\vagrant_workspaces\ExtractorSpace\vagrant-ssh default "/vagrant/run_extractor.sh"

	public int run(String command, String target) {
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("../../run_script.sh")));
			bw.write(command);
			bw.close();
//			System.out.println("command being run:" + command);
			String bashScriptPath = "../../bash_script" + target + ".bat"; 
			File bashScript = new File(bashScriptPath);
			BufferedWriter bashScriptWriter = new BufferedWriter(new FileWriter(bashScript));
			bashScriptWriter.write("ssh -F C:\\Users\\spawn\\vagrant_workspaces\\ExtractorSpace\\vagrant-ssh default \"bash --login -c '" + command + "'\"");
			bashScriptWriter.close();
			Process executionProcess = thisRuntime.
					exec("cmd /c " + "..\\\\..\\\\bash_script" + target + ".bat");
			executionProcess.waitFor();
			
//			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
//			String s;
//			while ((s = br.readLine()) != null)
//				System.out.println("Output : " + s);
			if (executionProcess.exitValue() != 0) {
				System.out.println("Command : " + command + " ||| exited with code : " + executionProcess.exitValue() + target);
				BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
				String s;
				while ((s = br.readLine()) != null)
					System.out.println("Output : " + s);
				return executionProcess.exitValue();	
			}
			bashScript.delete();
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

	public int run(String command) {
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("../../run_script.sh")));
			bw.write(command);
//			System.out.println("command being run:" + command);
			bw.close();
			Process executionProcess = thisRuntime
					.exec("cmd /c ..\\\\..\\\\bash_script.bat");
			executionProcess.waitFor();
//			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
//			String s;
//			while ((s = br.readLine()) != null)
//				System.out.println("Output : " + s);
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

	
	public int run(String command, String[] envp, File dir, String target) {
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
			Process executionProcess = thisRuntime
					.exec("cmd /c ..\\\\..\\\\bash_script.bat");
			executionProcess.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
			String s;
			while ((s = br.readLine()) != null)
				System.out.println("command running with environment variable Output : " + s);
			if (executionProcess.exitValue() != 0)
				System.out.println("command running with environment variable Command : " + command
						+ " ||| exited with code : " + executionProcess.exitValue() + target);
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

	public int run(String[] command, String[] envp, File dir, String target) {

		Runtime thisRuntime = Runtime.getRuntime();
		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("../../run_script.sh")));
//			bw.write(command);
//			bw.close();
//			System.out.println("command being run:" + command);
			String Command = String.join(" ", command);
			String bashScriptPath = "../../bash_script" + target + ".bat"; 
			File bashScript = new File(bashScriptPath);
			BufferedWriter bashScriptWriter = new BufferedWriter(new FileWriter(bashScript));
			bashScriptWriter.write("ssh -F C:\\Users\\spawn\\vagrant_workspaces\\ExtractorSpace\\vagrant-ssh default \"bash --login -c '" + Command + "'\"");
			bashScriptWriter.close();
			Process executionProcess = thisRuntime.
					exec("cmd /c " + "..\\\\..\\\\bash_script" + target + ".bat");
			executionProcess.waitFor();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("../../run_script.sh")));
			if (dir != null) {
				System.out.println(dir.getAbsolutePath());
				String[] Parts = dir.getAbsolutePath().split("\\\\");
				String pwd = "";
				for (int i = 1; i < Parts.length; i++)
					pwd += "/" + Parts[i];
				bw.write("cd " + pwd + "\n");
			}
			
//			executionProcess.waitFor();
//			Process executionProcess = thisRuntime.exec(command, envp, dir);
//			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
//			String s;
//			while ((s = br.readLine()) != null)
//				System.out.println("Output : " + s);
			if (executionProcess.exitValue() != 0) {
				BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
				String s;
				while ((s = br.readLine()) != null)
					System.out.println("Output : " + s);
//				System.out.print("Command : ");
//				for (String sss : command)
//					System.out.print(s + "\t");
				System.out.println(" ||| exited with code : " + executionProcess.exitValue());
				return executionProcess.exitValue();
			}
			bashScript.delete();
			return executionProcess.exitValue();
		} catch (IOException ioe) {
			System.out.println("exception raised when executing command : ");
			for (String s : command)
				System.out.print(s + "\t");
			System.out.println("in UnixConsole");
			System.out.println("IOException message = " + ioe.getMessage());
			return -1;
		} catch (InterruptedException ie) {
			System.out.println("exception raised when executing command : ");
			for (String s : command)
				System.out.print(s + "\t");
			System.out.println("in UnixConsole");
			System.out.println("InterruptedException message = " + ie.getMessage());
			return -1;
		}
	}

	public String runGetOutput(String command) {
		String commandOutput = "";
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("../../run_script.sh")));
			bw.write(command);
			System.out.println("command being run:" + command);
			bw.close();
			Process executionProcess = thisRuntime
					.exec("cmd /c ..\\\\..\\\\bash_script.bat");
			executionProcess.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
			String s;
			while ((s = br.readLine()) != null)
				commandOutput += s;
			if (executionProcess.exitValue() != 0)
				System.out.println("Command : " + command + " ||| exited with code : " + executionProcess.exitValue());
			return commandOutput;
		} catch (IOException ioe) {
			System.out.println("exception raised when executing command : ");
			System.out.println(command);
			System.out.println("in SshConsole");
			System.out.println("IOException message = " + ioe.getMessage());
			return commandOutput;
		} catch (InterruptedException ie) {
			System.out.println("exception raised when executing command : ");
			System.out.println(command);
			System.out.println("in SshConsole");
			System.out.println("InterruptedException message = " + ie.getMessage());
			return commandOutput;
		}
	}

	public String runGetOutput(String[] commandArgs) {
		Runtime thisRuntime = Runtime.getRuntime();
		String output = "";
		try {
			Process executionProcess = thisRuntime.exec(commandArgs, null, null);
			executionProcess.waitFor(20, TimeUnit.SECONDS);
//			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
//			String s;
//			while ((s = br.readLine()) != null)
//				output += "\n" + s;
			if (executionProcess.exitValue() != 0) {
//				System.out.print("Command : ");
//				for (String sss : commandArgs)
//					System.out.print(s + "\t");
				System.out.println(" ||| exited with code : " + executionProcess.exitValue());
			}
			return output;
		} catch (IOException ioe) {
			System.out.println("exception raised when executing command : ");
			for (String s : commandArgs)
				System.out.print(s + "\t");
			System.out.println("in UnixConsole");
			System.out.println("IOException message = " + ioe.getMessage());
			return "";
		} catch (InterruptedException ie) {
			System.out.println("exception raised when executing command : ");
			for (String s : commandArgs)
				System.out.print(s + "\t");
			System.out.println("in UnixConsole");
			System.out.println("InterruptedException message = " + ie.getMessage());
			return "";
		}
	}

	@Override
	public int run(String command, String[] envp, File dir) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int run(String[] commandArgs, String[] envp, File dir) {
		// TODO Auto-generated method stub
		return 0;
	}

}
