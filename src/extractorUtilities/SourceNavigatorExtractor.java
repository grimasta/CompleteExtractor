package extractorUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import console.commanders.ConsoleFactory;
import fileOperationUtilities.PathVMRectifier;
import paths.DynamicPaths;

public class SourceNavigatorExtractor implements Extraction {

	private String target;
	private String rootPath;
	private String projectPath;
	private String projectName; 
	
	public SourceNavigatorExtractor(String target, String rootPath, String projectPath) {
		this.target = target;
		this.rootPath = PathVMRectifier.rectify(rootPath);
		this.projectPath = projectPath;
		this.projectName = rootPath.split("/")[rootPath.split("/").length - 1];
	}
	
	
	@Override
	public String checkLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean doExtraction(String language) {
		String CSourceNavigatorLoadCommand = "$SN_HOME/./snavigator --batchmode --create -D parser-ext=\"c++\", \"*.[ch]pp *.cc *.hh *.c *.h *.[ch]xx\" >> log_file";
		String JavaSourceNavigatorLoadCommand = "$SN_HOME/./snavigator --batchmode --create -D parser-ext=\"java\", \"*.java\" >> log_file";
		String[] command;
		if (!language.equals("java")) {
			String[] c_command = {"$SN_HOME/./snavigator", "--batchmode", "--create", "-D", "parser-ext=\"c++\",\"*.[ch]pp *.cc *.hh *.c *.h *.[ch]xx\"", ">> log_file"};
			command = c_command;
		} else {
			String[] java_command = {"$SN_HOME/./snavigator", "--batchmode", "--create", "-D", "parser-ext=\"java\",\"*.java\"", ">> log_file"};
			command = java_command;
		}
			
		String SRC_PATH = "/vagrant/ExtractorUtilities/increments/";
		String DBDUMP_PATH = SRC_PATH + this.projectName + "/" + this.target + "/dbdump/";
		String PathToSNavDbDumpScript = "/home/vagrant/extractor/fetch-Cpp/src/snavtofamix/snav_dbdumps.sh";
		String[] SourceNavigatorDBDumpCommand = {"bash", PathToSNavDbDumpScript, SRC_PATH+this.projectName + "/" + this.target, this.target, DBDUMP_PATH};
//		String SourceNavigatorDBDumpCommand = "./snav_dbdumps.sh $SRC_PATH $PROJ_NAME $SRC_PATH/DBDUMP >> log_file";
		System.out.println("target = " + this.target); 
		String fullProjectPath = this.rootPath.split("increments")[0] + "projects_extracted/" + this.projectName + "/";

		System.out.println(fullProjectPath);
		
		
		File targetCommit = new File(this.rootPath + this.target);
//			read the list of file
			
		System.out.println("before srcml command");
//			String[] ccccCommand = new String[] { "/bin/bash", "-c", "./run_multimetric.sh" };
//		String[] ccccCommand = { "/vagrant/run_srcml.sh" };
		System.out.println(ConsoleFactory.getConsole().run(command, null, new File(SRC_PATH + this.projectName + "/" + this.target)));
		System.out.println(ConsoleFactory.getConsole().run(SourceNavigatorDBDumpCommand, null, new File(SRC_PATH + this.projectName + "/" + this.target)));
		

		System.out.println("after srcml command");			

		System.out.println("finished_extraction");
		return true;

	}

	@Override
	public void storeExtraction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearExtractionLocation() {
		// TODO Auto-generated method stub

	}

}
