package csvBeans;

import com.univocity.parsers.annotations.Parsed;

public class AccessesBean extends CsvBean {

	@Parsed(index = 0)
    private String commitID;

	@Parsed(index = 1)
    private String source;

	@Parsed(index = 2)
    private String target;

	@Parsed(index = 3)
    private int totalAccesses;
    
	@Parsed(index = 4)
    private int addedAccesses;
    
	@Parsed(index = 5)
    private int deletedAccesses;

	
	public String getCommitID() {
		return commitID;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public int getTotalAccesses() {
		return totalAccesses;
	}

	public int getAddedAccesses() {
		return addedAccesses;
	}

	public int getDeletedAccesses() {
		return deletedAccesses;
	}
}
