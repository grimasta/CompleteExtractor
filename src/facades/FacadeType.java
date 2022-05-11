package facades;

public enum FacadeType {
	CCCC("cccc"),
	EOD("eod"),
	FETCH("fetch"),
	IDS("ids"),
	MULTIMETRIC("multimetric"),
	SRCML("srcml"),
	FINALISATION("finalize"),
	SNAVIGATOR("snavigator");
	
	public final String label;
	
	private FacadeType(String s) {
		this.label = s;
	}
	
	public static FacadeType valueOfLabel(String label) {
	    for (FacadeType e : values()) {
	        if (e.label.equals(label)) {
	            return e;
	        }
	    }
	    return null;
	}
}
