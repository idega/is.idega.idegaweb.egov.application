package is.idega.idegaweb.egov.application.component;

import is.idega.idegaweb.egov.application.ApplicationConstants;

import javax.faces.context.FacesContext;

import com.idega.facelets.ui.FaceletComponent;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.util.PresentationUtil;

public class ApplicationErrors extends IWBaseComponent {

	public String getBundleIdentifier() {
		return ApplicationConstants.IW_BUNDLE_IDENTIFIER;
	}

	@Override
	protected void initializeComponent(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);
		PresentationUtil.addStyleSheetToHeader(iwc, iwc.getIWMainApplication().getBundle(ApplicationConstants.IW_BUNDLE_IDENTIFIER).getVirtualPathWithFileNameString("style/application.css"));
		
		FaceletComponent facelet = (FaceletComponent) iwc.getApplication().createComponent(FaceletComponent.COMPONENT_TYPE);
		facelet.setFaceletURI(getBundle(context, getBundleIdentifier()).getFaceletURI("applicationErrors.xhtml"));
		add(facelet);
	}
}