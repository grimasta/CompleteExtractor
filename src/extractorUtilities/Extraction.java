package extractorUtilities;

public interface Extraction {

	String checkLanguage();

	boolean doExtraction(String language);

	void storeExtraction();

	void clearExtractionLocation();

}