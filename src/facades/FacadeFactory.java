package facades;

public class FacadeFactory {
	
	public static ExtractionFacade create(FacadeType ft) {
		switch(ft){
		case CCCC:
			return new CcccFacade();
		case EOD:
			return new EodFacade();
		case FETCH:
			return new FetchFacade();
		case IDS:
			return new IdsFacade();
		case MULTIMETRIC:
			return new MultimetricFacade();
		case SRCML:
			return new SrcMLFacade();
		case FINALISATION:
			return new FinalisationFacade();
		case SNAVIGATOR:
			return new SNFacade();
		default:
			System.out.println("Invalid Choice, program will now exit");
			System.exit(-1);
			return null;
		}
			
	}
	
}
