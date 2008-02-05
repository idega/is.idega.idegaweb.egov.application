package is.idega.idegaweb.egov.application.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/02/05 09:11:19 $ by $Author: civilis $
 *
 */
public class ApplicationTypesManager implements ApplicationListener, ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	private Map<String, String> appTypesBeanIdentifiers;
	
	public List<ApplicationType> getApplicationTypes() {

		List<ApplicationType> appTypes = new ArrayList<ApplicationType>(getAppTypesBeanIdentifiers().size());
		
		for (String beanIdentifier : getAppTypesBeanIdentifiers().values())
			appTypes.add((ApplicationType)getApplicationContext().getBean(beanIdentifier));
		
		return appTypes;
	}
	
	public ApplicationType getApplicationType(String appType) {
		
		String beanIdentifier = getAppTypesBeanIdentifiers().get(appType);
		
		if(beanIdentifier != null) {
			
			return (ApplicationType)getApplicationContext().getBean(beanIdentifier);
		}
		
		return null;
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		
		if(applicationEvent instanceof ApplicationTypePluggedInEvent) {
			
			ApplicationTypePluggedInEvent ae = (ApplicationTypePluggedInEvent)applicationEvent;
			getAppTypesBeanIdentifiers().put(ae.getAppType().getType(), ae.getAppTypeBeanIdentifier());
		}
	}

	public void setApplicationContext(ApplicationContext applicationcontext)
			throws BeansException {
		applicationContext = applicationcontext;
	}

	public Map<String, String> getAppTypesBeanIdentifiers() {
		
		if(appTypesBeanIdentifiers == null)
			appTypesBeanIdentifiers = new HashMap<String, String>();
		
		return appTypesBeanIdentifiers;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}