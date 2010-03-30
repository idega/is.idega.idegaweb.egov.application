package is.idega.idegaweb.egov.application.component;

import is.idega.idegaweb.egov.application.ApplicationConstants;
import is.idega.idegaweb.egov.application.bean.ApplicationBean;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.facelets.ui.FaceletComponent;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;

public class ApplicationPhases extends IWBaseComponent {

	private int count;
	private int current;
	
	private static final String COUNT_PROPERTY = "count";
	private static final String CURRENT_PROPERTY = "current";

	public String getBundleIdentifier() {
		return ApplicationConstants.IW_BUNDLE_IDENTIFIER;
	}

	@Override
	protected void initializeComponent(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);
		handleExpressions(context);
		
		ApplicationBean bean = getBeanInstance("applicationBean");
		for (int i = 1; i <= getCount(); i++) {
			bean.addPhase(i, i == getCurrent());
		}
		
		FaceletComponent facelet = (FaceletComponent) iwc.getApplication().createComponent(FaceletComponent.COMPONENT_TYPE);
		facelet.setFaceletURI(getBundle(context, getBundleIdentifier()).getFaceletURI("applicationPhases.xhtml"));
		add(facelet);
	}

	private void handleExpressions(FacesContext context) {
		ValueExpression ve = getValueExpression(COUNT_PROPERTY);
    	if (ve != null) {
	    	Integer count = (Integer) ve.getValue(context.getELContext());
	    	setCount(count);
    	}    	

		ve = getValueExpression(CURRENT_PROPERTY);
    	if (ve != null) {
	    	Integer current = (Integer) ve.getValue(context.getELContext());
	    	setCurrent(current);
    	}    	
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}
}