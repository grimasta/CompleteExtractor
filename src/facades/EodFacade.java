package facades;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ca.uwo.git.utilities.ConcurrentDiff;
import ca.uwo.git.utilities.GitRepo;

public class EodFacade extends ExtractionFacade{

	EodFacade() {
		super();
	}
	
	public void doExtraction() {
		for (String repoAddress : repos) {
			if (stop)
				break;
			GitRepo gitRepo = new GitRepo(repoAddress + ".git");
			gitRepo.initializeGitRepo();
			this.commitSelection.init(gitRepo.getProjectName());
			gitRepo.resetToHead();

			int i = 0;
			ExecutorService executor = Executors.newFixedThreadPool(10);
			List<Future<String>> results = new ArrayList<>();
			while (gitRepo.hasNext()) {
				// checkout the next commit in the repo
				gitRepo.moveToNextCommit();

				i += 1;
				results.add(executor.submit(new ConcurrentDiff(gitRepo.getGit(), gitRepo.getCurrentCommit(),
						gitRepo.getRepo(), gitRepo.getDataWriter())));
				if (i % 1000 == 0)
					System.out.println(i);

				try {
					if (br.ready()) {
						if (br.readLine().contains("break"))
							stop = true;
						break;
					}
					// if (i == 100)
					// break;
				} catch (IOException ioe) {
					System.out
							.println("Error while reading from System.in in Main.java. Message : " + ioe.getMessage());
					gitRepo.resetToHead();
				}

			}
			executor.shutdown();
			while (!executor.isShutdown()) {

			}

			for (Future<String> f : results) {
//				if (f.isDone())
				try {
					gitRepo.write(f.get());
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			gitRepo.closeWriter();
			gitRepo.resetToHead();
		}
	}
}
