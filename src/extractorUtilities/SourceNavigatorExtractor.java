
package extractorUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import configurations.RunConfiguration;
import console.commanders.ConsoleFactory;
import fileOperationUtilities.MoveFilesAndFolders;
import fileOperationUtilities.PathVMRectifier;
import paths.DynamicPaths;

public class SourceNavigatorExtractor implements ExtractionMethod {

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
	public ExtractionMethod getNewInstance(String target, String rootPath, String projectPath) {
		return new SourceNavigatorExtractor(target, rootPath, projectPath);
	}

	public static void main(String[] args) {
		try {
			String top_level_ = DynamicPaths.getPath() + "/projects_extracted/";
			File top = new File(top_level_);
			for (String fileName : top.list()) {
				if (new File(top_level_ + fileName).isDirectory()) {
					System.out.println(fileName);
					List<String> cFiles = new ArrayList<>();
					Files.find(Paths.get(top_level_ + fileName + "/"), 999, (p,
							bfa) -> bfa.isRegularFile() && (p.getFileName().toString().toLowerCase().matches(".*\\.c")
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

					Files.find(Paths.get(top_level_ + fileName + "/"), 999,
							(p, bfa) -> bfa.isRegularFile()
									&& (p.getFileName().toString().toLowerCase().matches(".*\\.java")))
							.forEach(bfa -> javaFiles.add(bfa.toString()));
//					System.out.println(javaFiles.size());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String checkLanguage() {
		String localPath = rootPath + this.projectPath;
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
		language = checkLanguage();
		if (language != "none") {
			if (language.equals("java")) {
				String[] java_command = { "$SN_HOME/./snavigator", "--batchmode", "--create", "-D",
						"parser-ext=\"java\",\"*.java\"", ">> log_file" };
				command = java_command;
			} else {
				String[] c_command = { "$SN_HOME/./snavigator", "--batchmode", "--create", "-D",
						"parser-ext=\"c++\",\"*.[ch]pp *.cc *.hh *.c *.h *.[ch]xx\"", ">> log_file" };
				command = c_command;
			}
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
		System.out.println("target = " + this.target);
		String fullProjectPath = this.rootPath.split("increments")[0] + "projects_extracted/" + this.projectName + "/";

		System.out.println(fullProjectPath);

		File targetCommit = new File(this.rootPath + this.target);
//			read the list of file

		System.out.println("before sourceNavigator command");
//			String[] ccccCommand = new String[] { "/bin/bash", "-c", "./run_multimetric.sh" };
//		String[] ccccCommand = { "/vagrant/run_srcml.sh" };
		System.out.println(ConsoleFactory.getConsole().run(command, null,
				new File(SRC_PATH + this.projectName + "/" + this.target)));
		System.out.println(ConsoleFactory.getConsole().run(SourceNavigatorDBDumpCommand, null,
				new File(SRC_PATH + this.projectName + "/" + this.target)));

		System.out.println("after sourceNavigator command");

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
