package is.idega.idegaweb.egov.application.presentation.beans;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationType;
import is.idega.idegaweb.egov.application.business.ApplicationTypesManager;
import is.idega.idegaweb.egov.application.data.Application;

/**
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2008/09/02 12:54:24 $ by $Author: civilis $
 *
 */
@Service("appTypesHandler")
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ApplicationTypesHandler {

	@Autowired private ApplicationTypesManager applicationTypesManager;

	public org.jdom2.Document getApplicationTypeHandler(Integer appId, String applicationType) {

		ApplicationType appType = getApplicationTypesManager().getApplicationType(applicationType);

		if(appType == null)
			return null;

		FacesContext ctx = FacesContext.getCurrentInstance();
		IWContext iwc = IWContext.getIWContext(ctx);

		try {
			Application application = appId == null || appId == -1 ? null : getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(appId);

			UIComponent handlerComponent = appType.getHandlerComponent().getUIComponent(ctx, application);

			if(handlerComponent == null)
				return null;

			return getBuilderService().getRenderedComponent(iwc, handlerComponent, true);

		} catch (Exception e) {

			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception while resolving application type handler component", e);
			return null;
		}
	}

	protected BuilderService getBuilderService() {

		try {
			return BuilderServiceFactory.getBuilderService(IWMainApplication.getDefaultIWApplicationContext());
		} catch (RemoteException e) {
			throw new RuntimeException("Error while retrieving builder service", e);
		}
	}

	public ApplicationTypesManager getApplicationTypesManager() {
		return applicationTypesManager;
	}

	public void setApplicationTypesManager(
			ApplicationTypesManager applicationTypesManager) {
		this.applicationTypesManager = applicationTypesManager;
	}

	protected ApplicationBusiness getApplicationBusiness(IWContext iwc) {
		try {
			return IBOLookup.getServiceInstance(iwc, ApplicationBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
}