package is.idega.idegaweb.egov.application.component;

import is.idega.idegaweb.egov.application.ApplicationConstants;
import is.idega.idegaweb.egov.application.bean.ApplicationBean;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.facelets.ui.FaceletComponent;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.util.PresentationUtil;

public abstract class DisplayLayer extends IWBaseComponent {

	private String headline;
	private String body;
	
	private static final String HEADLINE_PROPERTY = "headline";
	private static final String BODY_PROPERTY = "body";

	public String getBundleIdentifier() {
		return ApplicationConstants.IW_BUNDLE_IDENTIFIER;
	}

	@Override
	protected void initializeComponent(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);
		handleExpressions(context);
		
		ApplicationBean bean = getBeanInstance("applicationBean");
		bean.setHeadline(getHeadline());
		bean.setBody(getBody());
		
		PresentationUtil.addStyleSheetToHeader(iwc, iwc.getIWMainApplication().getBundle(ApplicationConstants.IW_BUNDLE_IDENTIFIER).getVirtualPathWithFileNameString("style/application.css"));

		FaceletComponent facelet = (FaceletComponent) iwc.getApplication().createComponent(FaceletComponent.COMPONENT_TYPE);
		facelet.setFaceletURI(getBundle(context, getBundleIdentifier()).getFaceletURI(getFaceletUri()));
		add(facelet);
	}
	
	protected abstract String getFaceletUri();

	private void handleExpressions(FacesContext context) {
		ValueExpression ve = getValueExpression(HEADLINE_PROPERTY);
    	if (ve != null) {
	    	String headline = (String) ve.getValue(context.getELContext());
	    	setHeadline(headline);
    	}    	

		ve = getValueExpression(BODY_PROPERTY);
    	if (ve != null) {
    		String body = (String) ve.getValue(context.getELContext());
	    	setBody(body);
    	}    	
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}