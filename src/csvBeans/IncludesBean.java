package csvBeans;

import com.univocity.parsers.annotations.Parsed;

public class IncludesBean extends CsvBean {

	@Parsed(index = 0)
    private String commitID;

	@Parsed(index = 1)
    private String source;

	@Parsed(index = 2)
    private String target;

	@Parsed(index = 3)
    private int totalIncludes;
    
	@Parsed(index = 4)
    private int addedIncludes;
    
	@Parsed(index = 5)
    private int deletedIncludes;

	
	public String getCommitID() {
		return commitID;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public int getTotalIncludes() {
		return totalIncludes;
	}

	public int getAddedIncludes() {
		return addedIncludes;
	}

	public int getDeletedIncludes() {
		return deletedIncludes;
	}
}
