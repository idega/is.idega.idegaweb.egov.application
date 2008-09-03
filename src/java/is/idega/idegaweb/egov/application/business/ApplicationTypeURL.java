package is.idega.idegaweb.egov.application.business;

import is.idega.idegaweb.egov.application.IWBundleStarter;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.presentation.UIApplicationTypeURLHandler;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

/**
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.10 $
 *
 * Last modified: $Date: 2008/09/03 13:50:51 $ by $Author: civilis $
 *
 */
@Scope("singleton")
@Service(ApplicationTypeURL.beanIdentifier)
public class ApplicationTypeURL implements ApplicationType {

	static final String beanIdentifier = "appTypeURL";
	private static final String appType = "EGOV_URL";
	
	public ApplicationTypeHandlerComponent getHandlerComponent() {		
		UIApplicationTypeURLHandler h = new UIApplicationTypeURLHandler();
		return h;
	}
	
	public UIComponent getHandlerComponent(FacesContext ctx, Application app) {
		
		UIApplicationTypeURLHandler h = new UIApplicationTypeURLHandler();
		h.setApplication(app);
		return h;
	}

	public String getLabel(IWContext iwc) {
		
		IWMainApplication iwma = iwc.getApplicationContext().getIWMainApplication();
		return iwma.getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc.getCurrentLocale()).getLocalizedString("app_type.url", "Url");
	}

	public String getType() {
		return appType;
	}

	public void beforeStore(IWContext iwc, Application app) {
		
		String url = iwc.getParameter(UIApplicationTypeURLHandler.urlParam);
		String elec = iwc.getParameter(UIApplicationTypeURLHandler.elecParam);
		
		app.setElectronic("Y".equalsIgnoreCase(elec));
		app.setUrl(url);
	}
	
	public boolean isVisible(Application app) {
		return true;
	}

	public String getBeanIdentifier() {
		return beanIdentifier;
	}

	public boolean afterStore(IWContext iwc, Application app) {
		return false;
	}

	public String getUrl(IWContext iwc, Application app) {
		return app.getUrl();
	}
}