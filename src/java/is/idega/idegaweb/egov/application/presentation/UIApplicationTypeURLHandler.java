package is.idega.idegaweb.egov.application.presentation;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlMessage;

import org.apache.commons.validator.UrlValidator;

import is.idega.idegaweb.egov.application.data.Application;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.ui.BooleanInput;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.TextInput;


/**
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/02/20 14:27:23 $ by $Author: anton $
 *
 */
public class UIApplicationTypeURLHandler extends Block {
	
	private Application application;
	public static final String urlParam = "url";
	public static final String elecParam = "elect";
	
	@Override
	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		String urlValue = iwc.getParameter(urlParam);
		String elecValue = iwc.getParameter(elecParam);
		String action = iwc.getParameter(ApplicationCreator.ACTION);
		if(ApplicationCreator.SAVE_ACTION.equals(action)) {
			UrlValidator validator = new UrlValidator();
			if((urlValue == null || urlValue.trim().equals(""))) {
				iwc.addMessage(urlParam, new FacesMessage(iwrb.getLocalizedString("url_empty", "'Url' field should not be empty")));
			} else {
				if(!validator.isValid(urlValue)) {
					iwc.addMessage(urlParam, new FacesMessage(iwrb.getLocalizedString("url_error", "Incorrect URL value")));
				}
			}
		}
		
		TextInput url = new TextInput(urlParam);
		url.setId(urlParam);
		BooleanInput electronic = new BooleanInput(elecParam);
		if(elecValue != null && elecValue.equals("Y")) {
			electronic.setSelected(true);
		} else {
			electronic.setSelected(false);
		}
		
		if(application != null) {		
			url.setContent(application.getUrl());
			electronic.setSelected(application.getElectronic());
		}
		if(urlValue != null && !urlValue.trim().equals("")) {
			url.setContent(urlValue);
		}
		
		Layer container = new Layer(Layer.SPAN);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("formItem");
		Label label = new Label(iwrb.getLocalizedString("electronic", "Electronic"), electronic);
		layer.add(label);
		layer.add(electronic);
		container.add(layer);
		
		layer = new Layer(Layer.DIV);
		layer.setStyleClass("formItem");
		Layer errorItem = new Layer(Layer.SPAN);
		errorItem.setStyleClass("error");
		label = new Label(iwrb.getLocalizedString("url", "Url"), url);
		HtmlMessage msg = (HtmlMessage)iwc.getApplication().createComponent(HtmlMessage.COMPONENT_TYPE);
		msg.setFor(url.getId());
		errorItem.add(msg);
		layer.add(label);
		layer.add(url);
		layer.add(errorItem);
		container.add(layer);
		
		add(container);
	}

	public void setApplication(Application application) {
		this.application = application;
	}
}