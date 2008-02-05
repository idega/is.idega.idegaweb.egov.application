package is.idega.idegaweb.egov.application.business;

import is.idega.idegaweb.egov.application.business.ApplicationType;
import is.idega.idegaweb.egov.application.business.ApplicationTypePluggedInEvent;
import is.idega.idegaweb.egov.application.data.Application;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;

/**
 * Interface is meant to be extended by beans, reflecting application type for egov applications
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/02/05 09:11:19 $ by $Author: civilis $
 *
 */
public class ApplicationTypeURL implements ApplicationType, ApplicationContextAware, ApplicationListener {

	private ApplicationContext ctx;
	private static final String beanIdentifier = "appTypeURL";
	private static final String appType = "EGOV_URL";
	
	public UIComponent getHandlerComponent(FacesContext ctx) {
		return null;
	}

	public String getLabel(IWContext iwc) {
		return "EGOV URL";
	}

	public String getType() {
		return appType;
	}

	public void save(IWContext iwc, Application app) {
		
		String url = iwc.getParameter("url");
		
		if(url != null && !CoreConstants.EMPTY.equals(url))
			app.setUrl(url);
		
		System.out.println("saving ..url...");
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
}