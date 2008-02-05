package is.idega.idegaweb.egov.application.business;

import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/02/05 09:11:19 $ by $Author: civilis $
 */
public class ApplicationTypePluggedInEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4674284959898134958L;
	
	private String appTypeBeanIdentifier;

	public ApplicationTypePluggedInEvent(Object source) {
        super(source);
    }

	public String getAppTypeBeanIdentifier() {
		return appTypeBeanIdentifier;
	}

	public void setAppTypeBeanIdentifier(String appTypeBeanIdentifier) {
		this.appTypeBeanIdentifier = appTypeBeanIdentifier;
	}
	
	public ApplicationType getAppType() {
		return (ApplicationType)getSource();
	}
}