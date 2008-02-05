package is.idega.idegaweb.egov.application.business;

import is.idega.idegaweb.egov.application.data.Application;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.presentation.IWContext;

/**
 * Interface is meant to be extended by beans, reflecting application type for egov applications
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/02/05 09:11:19 $ by $Author: civilis $
 *
 */
public interface ApplicationType {
	
	public abstract String getLabel(IWContext iwc);
	
	public abstract String getType();
	
	public abstract UIComponent getHandlerComponent(FacesContext ctx);
	
	public abstract void save(IWContext iwc, Application app);
}