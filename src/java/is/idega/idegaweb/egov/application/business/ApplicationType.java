package is.idega.idegaweb.egov.application.business;

import is.idega.idegaweb.egov.application.data.Application;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;

/**
 * Interface is meant to be extended by beans, reflecting application type for egov applications
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/02/06 18:19:32 $ by $Author: civilis $
 *
 */
public interface ApplicationType {
	
	public abstract String getLabel(IWContext iwc);
	
	public abstract String getType();
	
	public abstract UIComponent getHandlerComponent(FacesContext ctx, Application app);
	
	/**
	 * called before storing application
	 * 
	 * @param iwc
	 * @param app
	 */
	public abstract void beforeStore(IWContext iwc, Application app);
	
	/**
	 * called after storing application
	 * @param iwc
	 * @param app
	 * @return returns true, if app needs to be restored, false otherwise 
	 */
	public abstract boolean afterStore(IWContext iwc, Application app);
	
	public abstract String getUrl(IWApplicationContext iwac, Application app);
}