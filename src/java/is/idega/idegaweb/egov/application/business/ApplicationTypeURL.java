package is.idega.idegaweb.egov.application.business;

import is.idega.idegaweb.egov.application.IWBundleStarter;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.presentation.UIApplicationTypeURLHandler;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

/**
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.7 $
 *
 * Last modified: $Date: 2008/04/18 23:35:59 $ by $Author: anton $
 *
 */
public class ApplicationTypeURL implements ApplicationType, ApplicationContextAware, ApplicationListener {

	private ApplicationContext ctx;
	private static final String beanIdentifier = "appTypeURL";
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

	public void setApplicationContext(ApplicationContext applicationcontext)
			throws BeansException {
		ctx = applicationcontext;		
	}

	public void onApplicationEvent(ApplicationEvent applicationevent) {
		
		if(applicationevent instanceof ContextRefreshedEvent) {
			
			ApplicationTypePluggedInEvent event = new ApplicationTypePluggedInEvent(this);
			event.setAppTypeBeanIdentifier(beanIdentifier);
			ctx.publishEvent(event);
		}
	}

	public boolean afterStore(IWContext iwc, Application app) {
		return false;
	}

	public String getUrl(IWApplicationContext iwac, Application app) {
		return app.getUrl();
	}
}