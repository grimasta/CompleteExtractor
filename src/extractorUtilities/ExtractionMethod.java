package extractorUtilities;

public interface ExtractionMethod {

	String checkLanguage();

	ExtractionMethod getNewInstance(String target, String rootPath, String projectPath);
	
	boolean doExtraction(String language);

	void storeExtraction();

	void clearExtractionLocation();

}