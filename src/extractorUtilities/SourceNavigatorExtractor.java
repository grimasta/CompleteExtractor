
package extractorUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import configurations.RunConfiguration;
import console.commanders.ConsoleFactory;
import fileOperationUtilities.MoveFilesAndFolders;
import fileOperationUtilities.PathVMRectifier;

public class SourceNavigatorExtractor implements ExtractionMethod {

	private String target;
	private String rootPath;
	private String projectPath;
	private String projectName;

	public SourceNavigatorExtractor() {
	}

	public SourceNavigatorExtractor(String target, String rootPath, String projectPath) {
		this.target = target;
		this.rootPath = PathVMRectifier.rectify(rootPath);
		this.projectPath = projectPath;
		this.projectName = rootPath.split("/")[rootPath.split("/").length - 1];
	}

	@Override
	public ExtractionMethod getNewInstance(String target, String rootPath, String projectPath) {
		SourceNavigatorExtractor sne = new SourceNavigatorExtractor(target, rootPath, projectPath);
		return sne;
	}

	@Override
	public String checkLanguage() {
		String localPath = this.rootPath + "/" + this.projectPath + "/";
		localPath = PathVMRectifier.deRectify(localPath);
		try {
			List<String> cFiles = new ArrayList<>();
			Files.find(Paths.get(localPath), 999,
					(p, bfa) -> bfa.isRegularFile() && (p.getFileName().toString().toLowerCase().matches(".*\\.c")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.h")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.cpp")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.hpp")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.cxx")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.cpp")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.cc")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.hh")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.c++")
							|| p.getFileName().toString().toLowerCase().matches(".*\\.h++")))
					.forEach(bfa -> cFiles.add(bfa.toString()));
//					System.out.println(cFiles.size());
			List<String> javaFiles = new ArrayList<>();

			Files.find(Paths.get(localPath), 999,
					(p, bfa) -> bfa.isRegularFile() && (p.getFileName().toString().toLowerCase().matches(".*\\.java")))
					.forEach(bfa -> javaFiles.add(bfa.toString()));
//					System.out.println(javaFiles.size());
			if ((javaFiles.size() != 0) || (cFiles.size() != 0))
				if (javaFiles.size() > cFiles.size())
					return "java";
				else
					return "cpp";
			else
				return "none";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "none";
		}

	}

	@Override
	public boolean doExtraction(String language) {
//		String CSourceNavigatorLoadCommand = "$SN_HOME/./snavigator --batchmode --create -D parser-ext=\"c++\", \"*.[ch]pp *.cc *.hh *.c *.h *.[ch]xx\" >> log_file";
//		String JavaSourceNavigatorLoadCommand = "$SN_HOME/./snavigator --batchmode --create -D parser-ext=\"java\", \"*.java\" >> log_file";
		String[] command;
//		language = checkLanguage();
		if (language != "none") {
			if (language.equals("java")) {
				String[] java_command = { "$SN_HOME/./snavigator", "--batchmode", "--create", "-D", "parser-ext=\"java\",\"*.java\"", ">> log_file" };
				command = java_command;
			} else {
				String[] c_command = { "$SN_HOME/./snavigator", "--batchmode", "--create", "-D", 
						"parser-ext=\"c++\",\"*.[ch]pp *.cc *.hh *.c *.h *.[ch]xx\"", ">> log_file" };
				command = c_command;
			}
			
//			String s = "$SN_HOME/./snavigator --batchmode --create -D parser-ext="c++","*.[ch]pp *.cc *.hh *.c *.h *.[ch]xx" >> log_file",
		} else {
			System.out.println("Something is wrong when calculating language");
			return false;
		}

		String SRC_PATH = "/vagrant/ExtractorUtilities/increments/" + RunConfiguration.SELECTED_COMMITS + "/";
		String DBDUMP_PATH = SRC_PATH + this.projectName + "/" + this.target + "/dbdump/";
		String PathToSNavDbDumpScript = "/home/vagrant/extractor/fetch-Cpp/src/snavtofamix/snav_dbdumps.sh";
		String[] SourceNavigatorDBDumpCommand = { "bash", PathToSNavDbDumpScript,
				SRC_PATH + this.projectName + "/" + this.target, this.target, DBDUMP_PATH };
//		String SourceNavigatorDBDumpCommand = "./snav_dbdumps.sh $SRC_PATH $PROJ_NAME $SRC_PATH/DBDUMP >> log_file";
//		System.out.println("target = " + this.target);
		String fullProjectPath = this.rootPath.split("increments")[0] + "projects_extracted/" + this.projectName + "/";

//		System.out.println(fullProjectPath);

		File targetCommit = new File(this.rootPath + this.target);
//			read the list of file

		
		ConsoleFactory.getConsole().run("Xvfb :1 -screen 0 1024x768x16 &", this.target);
		ConsoleFactory.getConsole().run(command, null, new File(SRC_PATH + this.projectName + "/" + this.target),
				this.target);
		ConsoleFactory.getConsole().run(SourceNavigatorDBDumpCommand, null,
				new File(SRC_PATH + this.projectName + "/" + this.target), this.target);
		storeExtraction();
		clearExtractionLocation();
		return true;

	}

	@Override
	public void storeExtraction() {
//		System.out.println(this.rootPath + " ||||| " + this.rootPath + "/stored_" + this.target + "/");
		MoveFilesAndFolders moveFilesAndFoldersResultsOfExtraction = new MoveFilesAndFolders(this.rootPath + "/",
				this.rootPath + "/stored_" + this.target + "/");
//		moveFilesAndFoldersResultsOfExtraction.move(this.target + ".rsf");
//		moveFilesAndFoldersResultsOfExtraction.move(this.target + "_final.rsf");
//		moveFilesAndFoldersResultsOfExtraction.move(this.target + "_names.rsf");
//		moveFilesAndFoldersResultsOfExtraction.move(this.target + ".cdif");
		moveFilesAndFoldersResultsOfExtraction.moveFolder(this.target + "/", "dbdump");
	}

	@Override
	public void clearExtractionLocation() {
		String deleteCommand = "rm -r ";
		File extractionLocation = new File(PathVMRectifier.deRectify(this.rootPath));
		for (File f : extractionLocation.listFiles())
			if (f.getName().contains(".rsf") || f.getName().contains(".log") || f.getName().contains(".cdif"))
				f.delete();
		String folderTreeForDeletion = this.rootPath + "/" + this.target;
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
