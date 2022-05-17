package ca.uwo.git.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import facades.ExtractionFacade;
import facades.ProxyFacade;
import fileOperationUtilities.InitializeFolderStructure;

public class Main {
	private static boolean debug = false;
	private static final Logger logger = LogManager.getLogger(Main.class);

	public static void main(String[] args) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		System.out.println("started at " + dtf.format(now));
//		try {
//		Runtime thisRuntime = Runtime.getRuntime();
//		Process executionProcess = thisRuntime
//				.exec("cmd /c ..\\..\\cmd_script.bat");
//		executionProcess.waitFor();
//		BufferedReader brr = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()));
//		while(brr.ready())
//			System.out.println(brr.readLine());
//		} catch (Exception e) {
//			System.out.println("crap");
//		}
//		System.exit(0);
//		System.out.println(ConsoleFactory.getConsole().runGetOutput(""));
		String log4jConfPath = "Properties/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		BasicConfigurator.configure();
		boolean stop = false;
		InitializeFolderStructure.initializeFolders();

		// load from file
		ArrayList<String> repos = (new ProjectReader()).getListOfProjects();
		System.out.println(repos);

//		System.exit(0);

//		String language;
//		String MO = "run";
		System.out.println("starting");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		try {
////			System.out.println(
//					"Please select a Mode of Operation ['finalize', 'fetch', 'ids', 'eod' (Extract Only Dates), 'multimetric', 'srcML', 'snavigator']:");
//			MO = br.readLine().toLowerCase();
//		} catch (IOException ioe) {
//			System.out.println(ioe.getMessage() + " exception in Main.java while reading MO");
//		}
//		ExtractionFacade extractionFacade = FacadeFactory.create(FacadeType.valueOfLabel(MO));
		ExtractionFacade proxy = new ProxyFacade();
		proxy.setRepos(repos);
		proxy.setBufferedReader(br);
		proxy.doExtraction();
		System.out.println("Done");
		dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		now = LocalDateTime.now();
		System.out.println("finished at " + dtf.format(now));

	}

	public static void debug(GitRepo gitRepo) {
		if (debug) {
			gitRepo.printBranches();
			gitRepo.mergeAverage();
		}
	}

}
