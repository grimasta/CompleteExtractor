package facades;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ca.uwo.git.utilities.CommitSelection;
import ca.uwo.git.utilities.GitRepo;
import configurations.RunConfiguration;
import extractorUtilities.ExtractorFactory;
import extractorUtilities.ExtractorType;
import fileOperationUtilities.MoveFilesAndFolders;
import repo.runner.RepoRunner;

public class ProxyFacade extends ExtractionFacade {
	
	public static Map<String, Boolean> stop = null;
	
	public ProxyFacade (){
		super();
	}
	
	public void doExtraction() {
		List<RepoRunner> listGitRepo = new ArrayList<RepoRunner>();
		stop = new ConcurrentHashMap<>();
		stop.put("stop", false);
		List<RepoRunner> repoRunners = new ArrayList<RepoRunner>();
		List<Future<?>> repoRunning = new ArrayList<Future<?>>();
//		ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);
		ExecutorService es = Executors.newFixedThreadPool(6);
		System.out.println(System.getProperties());
		for (String repoAddress : repos) {
			// initialize the repository (clone from remote or simply load an existing repo)
			GitRepo gitRepo = new GitRepo(repoAddress + ".git");
			this.commitSelection = new CommitSelection();
			this.commitSelection.init(gitRepo.getProjectName());
			// do some initialization operations necessary for iterating through the repo
			RepoRunner rr = new RepoRunner();
			listGitRepo.add(rr);
			rr.setRepo(gitRepo);
			rr.setSelection(commitSelection);
			for (ExtractorType extractorType : RunConfiguration.EXTRACTOR_TYPES)
				rr.addExtractor(ExtractorFactory.create(extractorType));
//			if (RunConfiguration.EXTRACTOR_TYPES.contains(ExtractorType.FETCH) || 
//					RunConfiguration.EXTRACTOR_TYPES.contains(ExtractorType.SNAVIGATOR) ||
//					RunConfiguration.EXTRACTOR_TYPES.contains(ExtractorType.SUPPLEMENTAL))
//				rr.addMover(new MoveFilesAndFolders());
			repoRunners.add(rr);
			es.execute(rr);
		}
		try {
			es.shutdown();
			while (!ProxyFacade.stop.get("stop") && !es.isTerminated()) {
//				try {
////					for (RepoRunner rr : listGitRepo) {
////						es.execute(rr);
////					}
////				Thread.sleep(10000);
////				System.out.println(Thread.activeCount());
//				}catch(InterruptedException ie) {
//					System.out.println(ie.getMessage());
//				}
				if (br.ready()) {
					if (br.readLine().contains("break")) {
						stop.put("stop", true);
//						MultimetricFacade.stop = true;
//						for (RepoRunner rr : repoRunners)
//							rr.stop();
						es.shutdown();
						while (!es.isTerminated())
							;
//						while (!this.isFinished(repoRunning))
//							;
					}
				}
			}
			// if (i == 100)
			// break;
		} catch (IOException ioe) {
			System.out.println("Error while reading from System.in in Main.java. Message : " + ioe.getMessage());
			for (RepoRunner rr : repoRunners)
				rr.stop();
			while (!this.isFinished(repoRunning))
				;
		}

	}

	private boolean isFinished(List<Future<?>> repoRunning) {
		for (Future<?> f : repoRunning)
			if (!f.isDone())
				return false;
		return true;
	}
}
