package console.commanders;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import paths.DynamicPaths;

public class UnixConsole implements Console {

	private static UnixConsole theConsole;

	private UnixConsole() {
	}

	public static UnixConsole getInstance() {
		if (theConsole == null)
			theConsole = new UnixConsole();
		return theConsole;
	}

	public int run(String command) {
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

	public int run(String command, String[] envp, File dir) {
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

	public String runGetOutput(String command) {
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
	public int run(String command, String target) {
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			Process executionProcess = thisRuntime.exec(new String[]{"sh" , command});
			executionProcess.waitFor();

			if (executionProcess.exitValue() != 0) {
				System.out.println("Command : " + command + " ||| exited with code : " + executionProcess.exitValue());
				BufferedReader br = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
				String s;
				while ((s = br.readLine()) != null)
					System.out.println("Output : " + s);
			}
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

	@Override
	public int run(String command, String[] envp, File dir, String target) {
		int retries = 4;
		int currentTry = 0;
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			String scriptWindowsFilePath = "../../ExtractorUtilities/scripts/run_script_" + target + ".sh";
			File scriptFileDescriptor = new File("../../ExtractorUtilities/scripts/run_script_" + target + ".sh");
			BufferedWriter bw = new BufferedWriter(new FileWriter(scriptFileDescriptor));
			System.out.println(dir.getAbsolutePath());
			String[] Parts = dir.getAbsolutePath().split("\\\\");
			String pwd = "";
			for (int i = 1; i < Parts.length; i++)
				pwd += "/" + Parts[i];
			bw.write("cd " + pwd + "\n");
			bw.write(command + "\n");
			bw.write("vagrant\n");
			bw.close();
			Process executionProcess = thisRuntime.exec(new String[]{"sh" , scriptWindowsFilePath});
			executionProcess.waitFor();

			currentTry++;
			while (executionProcess.exitValue() != 0) {
				if (currentTry < retries) {
					try {
						Thread.sleep(1000 * currentTry);
					} catch (InterruptedException ie) {
						System.out.println(ie.getMessage());
					}
					currentTry++;
					executionProcess = thisRuntime.exec(new String[]{"sh" , scriptWindowsFilePath});
					executionProcess.waitFor();
				} else
					break;
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
			scriptFileDescriptor.delete();
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

	public static void main(String[] args) {
		File f = new File(DynamicPaths.getPath() + "/selected_commits_sibyl/3000commits/elisa.csv");
		System.out.println(f.getAbsolutePath());
		try {
		BufferedReader brrr = new BufferedReader(new FileReader(f));
		System.exit(0);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			System.exit(0);
		}
		
		Runtime thisRuntime = Runtime.getRuntime();
		int retries = 4;
		int currentTry = 0;
		String target = "marios";
		String[] command = new String[] {"ls"};
		File dir = new File("../../ExtractorUtilities/scripts/");
		try {
			String scriptWindowsFilePath = "../../ExtractorUtilities/scripts/run_script_" + target + ".sh";
			File scriptFileDescriptor = new File(scriptWindowsFilePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(scriptFileDescriptor));
			String Command = String.join(" ", command);
			if (dir != null) {
				String[] Parts = dir.getAbsolutePath().split("/");
				String pwd = "";
				for (int i = 1; i < Parts.length; i++)
					pwd += "/" + Parts[i];
				bw.write("cd " + pwd + "\n");
			}
			bw.write(Command);
			bw.close();
			String bashScriptPath = "../../bash_script" + target + ".bat";
			File bashScript = new File(bashScriptPath);
//			BufferedWriter bashScriptWriter = new BufferedWriter(new FileWriter(bashScript));
//			bashScriptWriter.write("ssh -F " + RunConfiguration.getVagrantSshScriptLocation()
//					+ " default \"bash --login -c 'bash " + PathVMRectifier.rectify(scriptWindowsFilePath) + "'\"");
//			bashScriptWriter.close();
			Process executionProcess = thisRuntime.exec(new String[]{"sh", scriptWindowsFilePath});
			executionProcess.waitFor();

			currentTry++;
			while (executionProcess.exitValue() != 0) {
				if (currentTry < retries) {
					try {
						Thread.sleep(1000 * currentTry);
					} catch (InterruptedException ie) {
						System.out.println(ie.getMessage());
					}
					currentTry++;
					executionProcess = thisRuntime.exec(new String[]{"sh", scriptWindowsFilePath});
					executionProcess.waitFor();
				} else
					break;
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
			}
			bashScript.delete();
			scriptFileDescriptor.delete();
		} catch (IOException ioe) {
			System.out.println("exception raised when executing command : ");
			for (String s : command)
				System.out.print(s + "\t");
			System.out.println("in UnixConsole");
			System.out.println("IOException message = " + ioe.getMessage());
		} catch (InterruptedException ie) {
			System.out.println("exception raised when executing command : ");
			for (String s : command)
				System.out.print(s + "\t");
			System.out.println("in UnixConsole");
			System.out.println("InterruptedException message = " + ie.getMessage());
		}
	}
	
	@Override
	public int run(String[] command, String[] envp, File dir, String target) {
		int retries = 4;
		int currentTry = 0;
		Runtime thisRuntime = Runtime.getRuntime();
		try {
			String scriptWindowsFilePath = "../../ExtractorUtilities/scripts/run_script_" + target + ".sh";
			File scriptFileDescriptor = new File(scriptWindowsFilePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(scriptFileDescriptor));
			String Command = String.join(" ", command);
			if (dir != null) {
				String[] Parts = dir.getAbsolutePath().split("\\\\");
				String pwd = "";
				for (int i = 1; i < Parts.length; i++)
					pwd += "/" + Parts[i];
				bw.write("cd " + pwd + "\n");
			}
			bw.write(Command);
			bw.close();
			Process executionProcess = thisRuntime.exec(new String[]{"sh", scriptWindowsFilePath});
			executionProcess.waitFor();

			currentTry++;
			while (executionProcess.exitValue() != 0) {
				if (currentTry < retries) {
					try {
						Thread.sleep(1000 * currentTry);
					} catch (InterruptedException ie) {
						System.out.println(ie.getMessage());
					}
					currentTry++;
					executionProcess = thisRuntime.exec(new String[]{"sh", scriptWindowsFilePath});
					executionProcess.waitFor();
				} else
					break;
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
			scriptFileDescriptor.delete();
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

}
