package is.idega.idegaweb.egov.application.business;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.presentation.IWContext;

import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.model.ApplicationModel;

/**
 * Interface is meant to be extended by beans, reflecting application type for egov applications
 *
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.8 $
 *
 * Last modified: $Date: 2008/09/03 13:50:51 $ by $Author: civilis $
 *
 */
public interface ApplicationType {

	public abstract String getLabel(IWContext iwc);

	public abstract String getType();

	public abstract ApplicationTypeHandlerComponent getHandlerComponent();

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

	public abstract String getUrl(IWContext iwc, Application app);

	public abstract boolean isVisible(ApplicationModel app);

	public abstract String getBeanIdentifier();

	public interface ApplicationTypeHandlerComponent {

		public abstract UIComponent getUIComponent(FacesContext ctx, Application app);

		public abstract boolean validate(IWContext iwc);

	}

}