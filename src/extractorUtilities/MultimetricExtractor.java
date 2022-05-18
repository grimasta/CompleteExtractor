
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

public class MultimetricExtractor implements ExtractionMethod {

	private String target;
	private String rootPath;
	@SuppressWarnings("unused")
	private String projectPath;
	private String projectName; 
	
	public MultimetricExtractor(String target, String rootPath, String projectPath) {
		this.target = target;
		this.rootPath = PathVMRectifier.rectify(rootPath);
		this.projectPath = projectPath;
		this.projectName = rootPath.split("/")[rootPath.split("/").length - 1];
	}
	
	public MultimetricExtractor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ExtractionMethod getNewInstance(String target, String rootPath, String projectPath) {
		return new MultimetricExtractor(target, rootPath, projectPath);
	}
	
	@Override
	public String checkLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean doExtraction(String language) {
//		int extractionCount = 0;
//		System.out.println("target = " + this.target);
		String fullProjectPath = this.rootPath.split("increments")[0] + "projects_extracted/" + this.projectName + "/";

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
			String multimetricInput = "";
//			initialize a counter, testing has showed an abnormal behavior when the number of files to be analyzed exceeds 
//			certain numbers (around 100) so we keep it to around 50 and will further test to make sure it always works 
//				int i = 0;
			
			while (br.ready()) {
				String line = br.readLine();
				if (line.endsWith(".c") || line.toLowerCase().endsWith(".cc") || line.toLowerCase().endsWith(".cpp") || 
						line.toLowerCase().endsWith(".c++") || line.toLowerCase().endsWith(".h") || 
						line.toLowerCase().endsWith(".hpp") || line.toLowerCase().endsWith(".h++") || 
						line.toLowerCase().endsWith(".java") || line.toLowerCase().endsWith(".py") ||
						line.toLowerCase().endsWith(".rb") || line.toLowerCase().endsWith(".js") ||
						line.toLowerCase().endsWith(".go") || line.toLowerCase().endsWith(".sh")) {
					
					//				filter out all files that are not c or cpp source or header files as well java source files
					multimetricInput += " " + line;
				}
			}
			br.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(PathVMRectifier.deRectify(list_of_files_path))));
			bw.write(multimetricInput);
			bw.close();
			new File(DynamicPaths.getPath() + "multimetric/" + this.projectName + "/").mkdirs();
//			System.out.println("before multimetric command");
//			String[] ccccCommand = new String[] { "/bin/bash", "-c", "./run_multimetric.sh" };
			File multimetricScript = new File(DynamicPaths.getPath() + "scripts/run_multimetric_for_" + this.target + ".sh");
			BufferedWriter scriptWriter = new BufferedWriter(new FileWriter(multimetricScript)); 
			scriptWriter.write("#!/bin/bash\n"
					+ "\n"
					+ "multimetric `cat /vagrant/ExtractorUtilities/scripts/list_of_files_for_" + this.target + "` > /vagrant/ExtractorUtilities/multimetric/" + RunConfiguration.SELECTED_COMMITS + "/" + this.projectName + "/" + this.target + ".json");
			String multimetricCommand = "/vagrant/ExtractorUtilities/scripts/run_multimetric_for_" + this.target + ".sh";
			scriptWriter.close();
			
			if (ConsoleFactory.getConsole().run(multimetricCommand, this.target) != 0) {
				System.out.println(this.projectName + " experienced an error at " + this.target + " running command : " + multimetricCommand);
				return false;
			}
			multimetricScript.delete();
			new File(PathVMRectifier.deRectify(list_of_files_path)).delete();
//			System.out.println("after multimetric command");
			
		} catch (IOException ioe) {
			System.out.println("Error while opening the list of files in multimetricExtractor: \nError Message \n"
					+ ioe.getLocalizedMessage());
			return false;
		}
		return true;
	}


//	public boolean doExtraction(String language){
//		System.out.println("target = " + this.target);
//		String fetchScript = "/home/or10n/extractor/fetch-Java/scripts/" + language + "2rsf.sh";
//		String rsfWithNamesScript = "python /home/or10n/extractor/fetch-Java/scripts/createRSFwithNames.py";
//		int i = 0;
//		File targetProject = new File(this.rootPath + this.target);
//		boolean done = false;
//		if (targetProject.exists() && targetProject.isDirectory()) {
//			String pwd = this.rootPath;
//			System.out.println("started_extraction");
//			done = (ConsoleFactory.getConsole().run(fetchScript + " " + this.target, null, new File(pwd)) == 0);
//			System.out.println("finished_extraction");
//			done = done && (ConsoleFactory.getConsole().run(rsfWithNamesScript + " " + this.target + ".rsf", null, new File(pwd)) == 0);
//			return done;
//		}
//		return done;
//	}

	public void storeExtraction() {
		// this.rootPath;
		String PathToStore = DynamicPaths.getPath() + "multimetric/" + this.projectName + "/";
		File storage = new File(PathToStore);
		if (!storage.exists())
			storage.mkdirs();
		String[] ccccCommand = {"cp",  "/vagrant/multimetric_output.json", PathVMRectifier.rectify(PathToStore) + this.target + ".json"};
		System.out.println(ConsoleFactory.getConsole().run(ccccCommand, null, null));
	}

	public void clearExtractionLocation() {
		String deleteCommand = "rm -r ";
		File extractionLocation = new File(PathVMRectifier.deRectify(this.rootPath));
		for (File f : extractionLocation.listFiles())
			if (f.getName().contains(".rsf") || f.getName().contains(".log") || f.getName().contains(".cdif"))
				f.delete();
		String folderTreeForDeletion = this.rootPath + this.target;
		ConsoleFactory.getConsole().run(deleteCommand + folderTreeForDeletion, target);
	}

}
