package ca.uwo.git.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import console.commanders.ConsoleFactory;

public class Testbed {

	public static void main(String[] args) {
//		String pythonCommand = "python /home/or10n/PycharmProjects/XmlToCsvConverter/main.py " + "074b2da40c8faa774057a8e371a12d4876f14061" + " "
//				+ "clazy";
//		String[] commandArgs1 = { "/bin/sh", "-c", pythonCommand};
//		System.out.println("before python command");
//		System.out.println(ConsoleFactory.getConsole().runGetOutput(commandArgs1));
//		System.out.println("after python command");
//		System.exit(0);

		// TODO Auto-generated method stub
		String[] commandArgs = { "/bin/sh", "-c",
				"find /home/or10n/ExtractorUtilities/projects_extracted/clazy | egrep '\\.cpp|\\.c|\\.h|\\.hpp|\\.java' > list_of_files" };
		System.out.println("over here");
		String[] args2 = { "find", "/home/or10n/ExtractorUtilities/projects_extracted/clazy" };
		ConsoleFactory.getConsole().run(commandArgs, null, null);
		System.out.println("here");
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("list_of_files")));
			String cccc_input = "";
			int i = 0;
			while(br.ready()) {
				String line = br.readLine();
				System.out.println(line);
				if (line.endsWith(".c") || line.endsWith(".h") || line.endsWith(".cpp") || line.endsWith(".hpp") || line.endsWith(".java")) {
					i++;
					cccc_input += line + " ";
					ProcessBuilder processBuilder = new ProcessBuilder();
					System.out.println("command length = " + cccc_input.length());
					File env = new File("/home/or10n/ExtractorUtilities/projects_extracted/clazy/");
//					processBuilder.directory(env);
					processBuilder.command("/bin/bash", "-c", "./run_cccc.sh");

					Process p = processBuilder.start();
					

		            int exitCode = p.waitFor();
		            System.out.println("\nExited with error code : " + exitCode + " for #" + i + " lines");
		            break;
				}
			}
			br.close();
			

			//			int i = 0;
//			while (br.ready()) {
//				String line = br.readLine();
//				if (line.endsWith(".c") || line.endsWith(".h") || line.endsWith(".cpp") || line.endsWith(".hpp") || line.endsWith(".java")) {
//					cccc_input += " " + line;
//					i++;
//					if (i == 50) {
		

		// -- Linux --

		// Run a shell script
		
		System.out.println("starting cccc run");
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
            e.printStackTrace();
        }
//		System.out.println(ConsoleFactory.getConsole().run("/usr/bin/cccc - < list_of_files", null, null));
		System.out.println("ending cccc run");
////						System.out.println(ConsoleFactory.getConsole().run("/usr/bin/cccc " + cccc_input, null, null));
//						cccc_input = "";
//						i = 0;
//					}
//				}
//			}
//			if (cccc_input.length() > 0)
//				System.out.println(ConsoleFactory.getConsole().run("/usr/bin/cccc " + cccc_input, null, null));	
//			System.out.println(i + " " + cccc_input);
//		} catch(IOException ioe) {
//			System.out.println(ioe.getLocalizedMessage());
//		}

//		System.out.println(ConsoleFactory.getConsole().runGetOutput(args2));
//		System.out.println(ConsoleFactory.getConsole()
//				.run("/usr/bin/cccc /home/or10n/ExtractorUtilities/projects_extracted/clazy/src/ContextUtils.cpp "
//						+ "/home/or10n/ExtractorUtilities/projects_extracted/clazy/src/checkbase.cpp "
//						+ "/home/or10n/ExtractorUtilities/projects_extracted/clazy/src/FixItUtils.h "
//						+ "/home/or10n/ExtractorUtilities/projects_extracted/clazy/src/StmtBodyRange.h "
//						+ "/home/or10n/ExtractorUtilities/projects_extracted/clazy/src/MacroUtils.h "
//						+ "/home/or10n/ExtractorUtilities/projects_extracted/clazy/src/SuppressionManager.cpp" + "",
//						null, null));
		System.out.println("here");
	}

}
