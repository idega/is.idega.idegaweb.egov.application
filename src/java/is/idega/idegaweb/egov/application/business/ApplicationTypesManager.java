package is.idega.idegaweb.egov.application.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/09/02 12:51:57 $ by $Author: civilis $
 *
 */
@Scope("singleton")
@Service
public class ApplicationTypesManager implements ApplicationContextAware {
	
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

	public void setApplicationContext(ApplicationContext applicationcontext)
			throws BeansException {
		applicationContext = applicationcontext;
	}
	
	@Autowired(required=false)
	public void setApplicationTypes(List<ApplicationType> appTypes) {
		
		appTypesBeanIdentifiers = new HashMap<String, String>(appTypes == null ? 0 : appTypes.size());
		
		if(appTypes != null) {
			
			for (ApplicationType appType : appTypes) {
				
				appTypesBeanIdentifiers.put(appType.getType(), appType.getBeanIdentifier());
			}
		}
	}

	public Map<String, String> getAppTypesBeanIdentifiers() {
		
		return appTypesBeanIdentifiers;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}