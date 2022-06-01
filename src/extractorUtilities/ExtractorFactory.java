package extractorUtilities;

public class ExtractorFactory {

	public static ExtractionMethod create(ExtractorType extractorType) {
		switch(extractorType) {
		case CCCC: return new CcccExtractor();
		case EOD:  return new EodExtractor();
		case FETCH: return new Extractor();
		case IDS: return new IdsExtractor();
		case FINALISATION: return new FinalisationExtraction();
		case MULTIMETRIC: return new MultimetricExtractor();
		case SNAVIGATOR: return new SourceNavigatorExtractor();
		case SUPPLEMENTAL: return new SupplementalExtractor();
		case SRCML: return new SrcMLExtractor();
		case JASOME: return new JasomeExtractor();
		default:
			System.out.println("In the configuration file an illegal ExtractorType was used, program will now exit");
			System.exit(-1);
			return null;
		}
	}
	
}
