package extractorUtilities;

public enum ExtractorType {
	
		CCCC("cccc"),
		EOD("eod"),
		FETCH("fetch"),
		IDS("ids"),
		MULTIMETRIC("multimetric"),
		SRCML("srcml"),
		FINALISATION("finalize"),
		SNAVIGATOR("snavigator");
		
		public final String label;
		
		private ExtractorType(String s) {
			this.label = s;
		}
		
		public static ExtractorType valueOfLabel(String label) {
		    for (ExtractorType e : values()) {
		        if (e.label.equals(label)) {
		            return e;
		        }
		    }
		    return null;
		}
	

}
