package is.idega.idegaweb.egov.application.business;

import is.idega.idegaweb.egov.application.data.ApplicationHome;

import javax.ejb.FinderException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORuntimeException;
import com.idega.formbuilder.business.egov.Application;
import com.idega.formbuilder.business.egov.ApplicationBusiness;

/**
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version 1.0
 *
 */
public class ApplicationBusinessSpringBean implements ApplicationBusiness {

	public Application getApplication(Object primaryKey) throws FinderException {
		return (Application)getApplicationHome().findByPrimaryKey(primaryKey);
	}
	
	public ApplicationHome getApplicationHome() {
		try {
			return (ApplicationHome) IDOLookup.getHome(is.idega.idegaweb.egov.application.data.Application.class);
		}
		catch (IDOLookupException e) {
			throw new IDORuntimeException(e);
		}
	}
}