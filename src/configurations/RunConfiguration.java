package configurations;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import extractorUtilities.ExtractorType;

public class RunConfiguration {
	
//	public static final String SELECTED_COMMITS = "3years/";
	public static final String SELECTED_COMMITS = "3000commits";
//	public static final String SELECTED_COMMITS = "allCommits";
	public static final List<ExtractorType> EXTRACTOR_TYPES = new ArrayList<>();
	private static ExtractorType[] a = new ExtractorType[] {
//			ExtractorType.CCCC,
//			ExtractorType.EOD,
			ExtractorType.FETCH,
//			ExtractorType.FINALISATION,
//			ExtractorType.IDS,
//			ExtractorType.MULTIMETRIC,
//			ExtractorType.SNAVIGATOR,
//			ExtractorType.SUPPLEMENTAL,
//			ExtractorType.JASOME,
//			ExtractorType.SRCML,
//			ExtractorType.FINALISATION
	};
	
	private static boolean b = Collections.addAll(EXTRACTOR_TYPES, a);
	
	public static String getVagrantSshScriptLocation() {
		File f = new File(System.getProperty("user.dir"));
		File file = f.getParentFile().getParentFile();
		return file.getAbsolutePath() + "\\vagrant-ssh";
	}
	
	public static final String[] SELECTED_YEARS = { 
//			"1995", "1996", "1997", "1998", "1999",
//			"2000", "2001", "2002", "2003", "2004", 
//			"2005", "2006", "2007", "2008", "2009", 
//			"2010", "2011", "2012", "2013", "2014", 
//			"2015", "2016", "2017", "2018", "2019", 
//			"2020", "2021", "2022" 
			};
}
