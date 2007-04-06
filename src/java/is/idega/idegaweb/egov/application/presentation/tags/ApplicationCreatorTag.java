package is.idega.idegaweb.egov.application.presentation.tags;

import is.idega.idegaweb.egov.application.presentation.ApplicationCreator;

import javax.faces.component.UIComponent;

import org.apache.myfaces.shared_tomahawk.taglib.UIComponentTagBase;

/**
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version 1.0
 *
 */
public class ApplicationCreatorTag extends UIComponentTagBase {
	
	public ApplicationCreatorTag() {
		super();
	}

	public String getComponentType() {
		return ApplicationCreator.COMPONENT_TYPE;
	}

	public String getRendererType() {
		return null;
	}
	
	public void release() {
		super.release();
	}
	
	public void setProperties(UIComponent component) {
		super.setProperties(component);
	}
}