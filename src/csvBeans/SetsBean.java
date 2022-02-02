package csvBeans;

import com.univocity.parsers.annotations.Parsed;

public class SetsBean extends CsvBean {

	@Parsed(index = 0)
    private String commitID;

	@Parsed(index = 1)
    private String source;

	@Parsed(index = 2)
    private String target;

	@Parsed(index = 3)
    private int totalSets;
    
	@Parsed(index = 4)
    private int addedSets;
    
	@Parsed(index = 5)
    private int deletedSets;

	
	public String getCommitID() {
		return commitID;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public int getTotalSets() {
		return totalSets;
	}

	public int getAddedSets() {
		return addedSets;
	}

	public int getDeletedSets() {
		return deletedSets;
	}
}
