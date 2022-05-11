package console.commanders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class UnixConsole implements Console{

	private static UnixConsole theConsole;

	private UnixConsole(){
	}

	public static UnixConsole getInstance(){
		if (theConsole == null)
			theConsole = new UnixConsole();
		return theConsole;
	}

	public int run(String command){
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			Process executionProcess = thisRuntime.exec(command);
			executionProcess.waitFor();
//			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
//			String s;
//			while ((s = br.readLine()) != null) {}
////				System.out.println("Output : " + s);
			if (executionProcess.exitValue() != 0)
				System.out.println("Command : " + command + " ||| exited with code : " + executionProcess.exitValue());
			return executionProcess.exitValue();
		} catch (IOException ioe) {
			System.out.println("exception raised when executing command : ");
			System.out.println(command);
			System.out.println("in UnixConsole");
			System.out.println("IOException message = " + ioe.getMessage());
			return -1;
		} catch (InterruptedException ie) {
			System.out.println("exception raised when executing command : ");
			System.out.println(command);
			System.out.println("in UnixConsole");
			System.out.println("InterruptedException message = " + ie.getMessage());
			return -1;
		}

	}

	public int run(String command, String[] envp, File dir){
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			Process executionProcess = thisRuntime.exec(command, envp, dir);
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
			System.out.println("in UnixConsole");
			System.out.println("IOException message = " + ioe.getMessage());
			return -1;
		} catch (InterruptedException ie) {
			System.out.println("exception raised when executing command : ");
			System.out.println(command);
			System.out.println("in UnixConsole");
			System.out.println("InterruptedException message = " + ie.getMessage());
			return -1;
		}
	}

	public int run(String[] command, String[] envp, File dir) {
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			Process executionProcess = thisRuntime.exec(command, envp, dir);
			executionProcess.waitFor(20, TimeUnit.SECONDS);
//			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
//			String s;
//			while ((s = br.readLine()) != null)
//				System.out.println("Output : " + s);
			if (executionProcess.exitValue() != 0) {
//				System.out.print("Command : ");
//				for (String sss : command)
//					System.out.print(s + "\t");
				System.out.println(" ||| exited with code : " + executionProcess.exitValue());
			}
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
	
	public String runGetOutput(String command){
		Runtime thisRuntime = Runtime.getRuntime();
		String output = "";
		try {
			Process executionProcess = thisRuntime.exec(command);
			executionProcess.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
			String s;
			while ((s = br.readLine()) != null)
				output += s;
			if (executionProcess.exitValue() != 0)
				System.out.println("Command : " + command + " ||| exited with code : " + executionProcess.exitValue());
			return output;
		} catch (IOException ioe) {
			System.out.println("exception raised when executing command : ");
			System.out.println(command);
			System.out.println("in UnixConsole");
			System.out.println("IOException message = " + ioe.getMessage());
			return "";
		} catch (InterruptedException ie) {
			System.out.println("exception raised when executing command : ");
			System.out.println(command);
			System.out.println("in UnixConsole");
			System.out.println("InterruptedException message = " + ie.getMessage());
			return "";
		}
	}

	public String runGetOutput(String[] commandArgs){
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
	public int run(String command, String target) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int run(String command, String[] envp, File dir, String target) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int run(String[] command, String[] envp, File dir, String target) {
		// TODO Auto-generated method stub
		return 0;
	}
		
}
