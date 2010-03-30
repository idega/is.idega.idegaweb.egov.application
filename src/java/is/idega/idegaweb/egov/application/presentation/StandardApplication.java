package is.idega.idegaweb.egov.application.presentation;

import com.idega.presentation.IWContext;
import com.idega.user.data.User;

public interface StandardApplication {

	public String getCaseCode();
	
	public String getBundleIdentifier();
	
	public User getUser(IWContext iwc);
	
	public void setUseSessionUser(boolean useSessionUser);
}
