package csvBeans;

import com.univocity.parsers.annotations.Parsed;

public class CallsBean extends CsvBean {

	@Parsed(index = 0)
    private String commitID;

	@Parsed(index = 1)
    private String source;

	@Parsed(index = 2)
    private String target;

	@Parsed(index = 3)
    private int totalCalls;
    
	@Parsed(index = 4)
    private int addedCalls;
    
	@Parsed(index = 5)
    private int deletedCalls;

	
	public String getCommitID() {
		return commitID;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public int getTotalCalls() {
		return totalCalls;
	}

	public int getAddedCalls() {
		return addedCalls;
	}

	public int getDeletedCalls() {
		return deletedCalls;
	}
}
