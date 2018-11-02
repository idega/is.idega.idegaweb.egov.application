package is.idega.idegaweb.egov.application.bean;

public enum InvoicingType {
	WORKED_HOURS {
		@Override
		public String toString() {
			return "WORKED_HOURS";
		}
	},
	FIXED_HOUR {
		@Override
		public String toString() {
			return "FIXED_HOUR";
		}
	}
}
