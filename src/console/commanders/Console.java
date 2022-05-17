package console.commanders;

import java.io.File;

public interface Console{
//	int run(String command);

	int run(String command, String target);
	
	int run(String command, String[] envp, File dir);

	int run(String command, String[] envp, File dir, String target);
	
	int run(String[] commandArgs, String[] envp, File dir);
	
	int run(String[] command, String[] envp, File dir, String target);
	
	String runGetOutput(String command);
	
	String runGetOutput(String[] commandArgs);
}
