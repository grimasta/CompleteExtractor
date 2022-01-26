package console.commanders;

import java.io.File;

public interface Console{
	int run(String command);

	int run(String command, String[] envp, File dir);

	String runGetOutput(String command);
}
