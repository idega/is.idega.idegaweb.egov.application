package is.idega.idegaweb.egov.application.presentation.beans;

import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationType;
import is.idega.idegaweb.egov.application.business.ApplicationTypesManager;
import is.idega.idegaweb.egov.application.data.Application;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

/**
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/02/20 17:29:27 $ by $Author: anton $
 *
 */
public class ApplicationTypesHandler {
	
	private ApplicationTypesManager applicationTypesManager;

	public org.jdom.Document getApplicationTypeHandler(Integer appId, String applicationType) {
		
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
			return (ApplicationBusiness) IBOLookup.getServiceInstance(iwc, ApplicationBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
}