package is.idega.idegaweb.egov.application.component;

import is.idega.idegaweb.egov.application.ApplicationConstants;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.Span;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;

public class Button extends IWBaseComponent {

	private String action;
	private String value = "undefined";
	private String parameter;
	private String formId;
	private String styleClass;
	private String url;
	
	public static final String ACTION_PROPERTY = "action";
	public static final String VALUE_PROPERTY = "value";
	public static final String PARAMETER_PROPERTY = "parameter";
	public static final String FORM_ID_PROPERTY = "formId";
	public static final String STYLE_CLASS_PROPERTY = "styleClass";
	public static final String URL_PROPERTY = "url";

	public String getBundleIdentifier() {
		return ApplicationConstants.IW_BUNDLE_IDENTIFIER;
	}

	@Override
	protected void initializeComponent(FacesContext context) {
		handleExpressions(context);

		Span all = new Span();
		all.setStyleClass("buttonSpan");

		Span left = new Span();
		left.setStyleClass("left");
		all.add(left);

		Span middle = new Span();
		middle.setStyleClass("middle");
		middle.add(new Text(getValue()));
		all.add(middle);

		Span right = new Span();
		right.setStyleClass("right");
		all.add(right);

		Link link = new Link(all);
		link.setStyleClass("button");
		if (getStyleClass() != null) {
			link.setStyleClass(getStyleClass());
		}
		if (getParameter() != null && getAction() != null) {
			link.setValueOnClick(getParameter(), getAction());
		}
		if (getFormId() != null) {
			link.setToFormSubmit(getFormId());
		}
		if (getURL() != null) {
			link.setURL(getURL());
		}
		add(link);
	}

	private void handleExpressions(FacesContext context) {
		ValueExpression ve = getValueExpression(ACTION_PROPERTY);
    	if (ve != null) {
	    	String action = (String) ve.getValue(context.getELContext());
	    	setAction(action);
    	}    	

		ve = getValueExpression(VALUE_PROPERTY);
    	if (ve != null) {
	    	String value = (String) ve.getValue(context.getELContext());
	    	setValue(value);
    	}    	

		ve = getValueExpression(PARAMETER_PROPERTY);
    	if (ve != null) {
	    	String parameter = (String) ve.getValue(context.getELContext());
	    	setParameter(parameter);
    	}    	

		ve = getValueExpression(FORM_ID_PROPERTY);
    	if (ve != null) {
	    	String formId = (String) ve.getValue(context.getELContext());
	    	setFormId(formId);
    	}    	

		ve = getValueExpression(STYLE_CLASS_PROPERTY);
    	if (ve != null) {
	    	String styleClass = (String) ve.getValue(context.getELContext());
	    	setStyleClass(styleClass);
    	}    	

		ve = getValueExpression(URL_PROPERTY);
    	if (ve != null) {
	    	String url = (String) ve.getValue(context.getELContext());
	    	setURL(url);
    	}    	
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}
}