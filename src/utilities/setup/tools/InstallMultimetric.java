package utilities.setup.tools;

import java.io.File;

import ca.uwo.git.utilities.GitRepo;
import console.commanders.ConsoleFactory;

public class InstallMultimetric {

	public static void main(String[] args) {
		InstallMultimetric im = new InstallMultimetric();
	}
	
	
	private String multimetric_source = "https://github.com/grimasta/multimetric";
	
	private GitRepo gr = new GitRepo(multimetric_source + ".git");
	
	public InstallMultimetric() {
		gr.initializeGitRepo();
		gr.checkoutByName("4c7771759539dec42f5656c1e7563ac1eb01c149");
//		ConsoleFactory.getConsole().run("python3 sudo setup.py build", null, new File("/vagrant/ExtractorUtilities/projects_extracted/multimetric/"),  "build_multimetric");
//		ConsoleFactory.getConsole().run("python3 sudo setup.py install", null, new File("/vagrant/ExtractorUtilities/projects_extracted/multimetric/"), "install_multimetric");
		
	}
	
}
