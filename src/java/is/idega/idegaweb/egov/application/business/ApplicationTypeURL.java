package is.idega.idegaweb.egov.application.business;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.business.DefaultSpringBean;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.util.CoreConstants;

import is.idega.idegaweb.egov.application.IWBundleStarter;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.model.ApplicationModel;
import is.idega.idegaweb.egov.application.presentation.UIApplicationTypeURLHandler;

/**
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.10 $
 *
 * Last modified: $Date: 2008/09/03 13:50:51 $ by $Author: civilis $
 *
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(ApplicationTypeURL.beanIdentifier)
public class ApplicationTypeURL extends DefaultSpringBean implements ApplicationType {

	static final String beanIdentifier = "appTypeURL";
	private static final String appType = "EGOV_URL";

	@Override
	public ApplicationTypeHandlerComponent getHandlerComponent() {
		UIApplicationTypeURLHandler h = new UIApplicationTypeURLHandler();
		return h;
	}

	public UIComponent getHandlerComponent(FacesContext ctx, Application app) {
		UIApplicationTypeURLHandler h = new UIApplicationTypeURLHandler();
		h.setApplication(app);
		return h;
	}

	@Override
	public String getLabel(IWContext iwc) {
		IWMainApplication iwma = null;
		if (iwc != null) {
			iwma = iwc.getApplicationContext().getIWMainApplication();
		}
		iwma = iwma == null ? IWMainApplication.getDefaultIWMainApplication() : iwma;
		if (iwma == null) {
			return CoreConstants.EMPTY;
		}

		IWResourceBundle iwrb = getResourceBundle(iwma.getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER));
		return iwrb.getLocalizedString("app_type.url", "Url");
	}

	@Override
	public String getType() {
		return appType;
	}

	@Override
	public void beforeStore(IWContext iwc, Application app) {
		String url = iwc.getParameter(UIApplicationTypeURLHandler.urlParam);
		String elec = iwc.getParameter(UIApplicationTypeURLHandler.elecParam);

		app.setElectronic(CoreConstants.Y.equalsIgnoreCase(elec));
		app.setUrl(url);
	}

	@Override
	public boolean isVisible(ApplicationModel app) {
		return true;
	}

	@Override
	public String getBeanIdentifier() {
		return beanIdentifier;
	}

	@Override
	public boolean afterStore(IWContext iwc, Application app) {
		return false;
	}

	@Override
	public String getUrl(IWContext iwc, ApplicationModel app) {
		return app.getUrl();
	}

	@Override
	public String getSelectedElement(ApplicationModel app) {
		return CoreConstants.EMPTY;
	}

	@Override
	public List<String> getRolesCanStartProcessDWR(String pdId, String applicationId) {
		return Collections.emptyList();
	}

	@Override
	public <T extends Serializable> List<String> getRolesCanStartProcess(T pdId, Object applicationId) {
		return Collections.emptyList();
	}

	@Override
	public void fillMenu(DropdownMenu menu) {
	}

}