package extractorUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import configurations.RunConfiguration;
import console.commanders.ConsoleFactory;
import fileOperationUtilities.PathVMRectifier;
import paths.DynamicPaths;

public class SupplementalExtractor implements ExtractionMethod {

	private String target;
	private String rootPath;
	@SuppressWarnings("unused")
	private String projectPath;
	private String projectName; 
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public SupplementalExtractor(String target, String rootPath, String projectPath) {
		this.target = target;
		this.rootPath = PathVMRectifier.rectify(rootPath);
		this.projectPath = projectPath;
		this.projectName = rootPath.split("/")[rootPath.split("/").length - 1];
	}
	
	public SupplementalExtractor() {
		
	}
	@Override
	public String checkLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExtractionMethod getNewInstance(String target, String rootPath, String projectPath) {
		return new SupplementalExtractor(target, rootPath, projectPath);
	}

	@Override
	public boolean doExtraction(String language) {
//		String CSourceNavigatorLoadCommand = "$SN_HOME/./snavigator --batchmode --create -D parser-ext=\"c++\", \"*.[ch]pp *.cc *.hh *.c *.h *.[ch]xx\" >> log_file";
//		String JavaSourceNavigatorLoadCommand = "$SN_HOME/./snavigator --batchmode --create -D parser-ext=\"java\", \"*.java\" >> log_file";
		String[] command;
//		language = checkLanguage();
		if (language != "none") {
			if (language.equals("java")) {
				try {
				new File(PathVMRectifier.deRectify(this.rootPath) + "/stored_" + this.target + "/dbdump/" + this.target + ".condcomp").createNewFile();
				new File(PathVMRectifier.deRectify(this.rootPath) + "/stored_" + this.target + "/dbdump/" + this.target + ".includes2").createNewFile();
				new File(PathVMRectifier.deRectify(this.rootPath) + "/stored_" + this.target + "/dbdump/" + this.target + ".namespaces").createNewFile();
				}catch(IOException ioe) {
					System.out.println(ioe.getMessage() + " when trying to create empty files for Supplemental extractor for Java project");
				}
//				clearExtractionLocation();
				return false;
			}
			
//			String s = "$SN_HOME/./snavigator --batchmode --create -D parser-ext="c++","*.[ch]pp *.cc *.hh *.c *.h *.[ch]xx" >> log_file",
		} else {
			System.out.println("Something is wrong when calculating language");
			return false;
		}

		String SRC_PATH = "/vagrant/ExtractorUtilities/increments/" + RunConfiguration.SELECTED_COMMITS + "/";
		String DBDUMP_PATH = SRC_PATH + this.projectName + "/stored_" + this.target + "/dbdump/";
		new File(PathVMRectifier.deRectify(DBDUMP_PATH)).mkdirs();
		String PathToCondCompScript;
		String PathToSNavDbDumpScript = "/home/vagrant/extractor/fetch-Cpp/src/snavtofamix/snav_dbdumps.sh";
		String[] SourceNavigatorDBDumpCommand = { "bash", PathToSNavDbDumpScript,
				SRC_PATH + this.projectName + "/" + this.target, this.target, DBDUMP_PATH };

		File targetCommit = new File(this.rootPath + this.target);
		String PathToIncludes2File = "grep \"#include\" " + this.rootPath + this.target + "/dbdump/$PROJ_NAME.condcomp > " + this.rootPath + "stored_" + this.target + "/dbdump/$PROJ_NAME.includes2";
		String PathToNamespacesScript = "perl /home/vagrant/extractor/fetch-Cpp/scripts/parserExt/namespaceScript/getNamespaces.pl -s . c cc cpp cxx h hh hpp hxx > " + this.rootPath + "stored_" + this.target + "/dbdump/$PROJ_NAME.namespaces";
//		int extractionCount = 0;
//		System.out.println("target = " + this.target);
		String fullProjectPath = this.rootPath + "/" + this.target + "/";
		try {
			new BufferedWriter(new FileWriter(new File(PathVMRectifier.deRectify(SRC_PATH) + this.projectName + "/stored_" + this.target + "/dbdump/" + this.target + ".condcomp"))).close();
			new BufferedWriter(new FileWriter(new File(PathVMRectifier.deRectify(SRC_PATH) + this.projectName + "/stored_" + this.target + "/dbdump/" + this.target + ".namespaces"))).close();
		}catch(IOException ioe) {
			System.out.println("Error creating empty files");
			System.out.println(ioe.getMessage());
		}
//		System.out.println(fullProjectPath);
		
		String list_of_files_path = DynamicPaths.getPath() + "scripts/list_of_files_for_" + this.target;
		list_of_files_path = PathVMRectifier.rectify(list_of_files_path);
//		System.out.println(list_of_files_path);
		String[] commandArgs = { "find ", fullProjectPath, " > ",  list_of_files_path};
//		
//		File targetCommit = new File(this.rootPath + this.target);
		ConsoleFactory.getConsole().run(commandArgs, null, null, this.target);
		try {
//			read the list of file
			BufferedReader br = new BufferedReader(new FileReader(new File(PathVMRectifier.deRectify(list_of_files_path))));

//			build the list of files to be used as input for the CCCC script
			String perlScriptInput = "";
//			initialize a counter, testing has showed an abnormal behavior when the number of files to be analyzed exceeds 
//			certain numbers (around 100) so we keep it to around 50 and will further test to make sure it always works 
//				int i = 0;
			
			
			String long_command = "allFiles=`find . \\( -name \"*.cpp\" -o -name \"*.c\" -o -name \"*.cc\" -o -name \"*.cxx\" -o -name \"*.hpp\" -o -name \"*.h\" -o -name \"*.hh\" -o -name \"*.hxx\" \\)`\n"
					+ "for file in $allFiles; do\n"
					+ "        perl /home/vagrant/extractor/fetch-Cpp/scripts/parserExt/preprocDirectives.pl \"$file\" | sed \"s/\\.\\///g\" >> " + this.rootPath + "/stored_" + this.target + "/dbdump/" + this.target + ".condcomp\n"
					+ "done\n"
					+ "grep \"#include\" " + this.rootPath + "/stored_" + this.target + "/dbdump/" + this.target + ".condcomp > " + this.rootPath + "/stored_" + this.target + "/dbdump/" + this.target + ".includes2\n"
					+ "perl /home/vagrant/extractor/fetch-Cpp/scripts/parserExt/namespaceScript/getNamespaces.pl -s . c cc cpp cxx h hh hpp hxx > " + this.rootPath + "/stored_" + this.target + "/dbdump/" + this.target + ".namespaces";
			
					ConsoleFactory.getConsole().run(long_command, null, new File(SRC_PATH + this.projectName + "/" + this.target), this.target);
			br.close();
			new File(PathVMRectifier.deRectify(list_of_files_path)).delete();
			storeExtraction();
			clearExtractionLocation();
		}catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		return true;
	}

	@Override
	public void storeExtraction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearExtractionLocation() {
		String deleteCommand = "rm -r ";
		File extractionLocation = new File(PathVMRectifier.deRectify(this.rootPath));
		for (File f : extractionLocation.listFiles())
			if (f.getName().contains(".rsf") || f.getName().contains(".log") || f.getName().contains(".cdif"))
				f.delete();
		String folderTreeForDeletion = this.rootPath + "/" + this.target;
//		System.out.println(folderTreeForDeletion);
		ConsoleFactory.getConsole().run(deleteCommand + folderTreeForDeletion, target);
		
//		File extractionLocation = new File(PathVMRectifier.deRectify(this.rootPath));
//		List<String> fileToDelete = new ArrayList<>();
//		try {
//			Files.find(Paths.get(PathVMRectifier.deRectify(this.rootPath)), 999,
//					(p, bfa) -> ((bfa.isRegularFile() && (p.getFileName().toString().toLowerCase().matches(".*\\.c")))
//							|| (bfa.isDirectory())))
//					.forEach(bfa -> fileToDelete.add(bfa.toString()));
//			List<String> deleted = new ArrayList<String>();
//			while (deleted.size())
//			for (String fileName : fileToDelete) {
//				if (new File(filename).isFile()) {
//					new File(filename).delete();
//					deleted.add(fileName);
//				}
//				
//				
//			}
//		} catch (IOException ioe) {
//			System.out.println(ioe.getMessage());
//		}
	}

}
