package is.idega.idegaweb.egov.application.presentation;

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
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/02/05 19:31:36 $ by $Author: civilis $
 *
 */
public class UIApplicationTypeURLHandler extends Block {
	
	private Application application;
	public static final String urlParam = "url";
	public static final String elecParam = "elect";
	
	@Override
	public void main(IWContext iwc) throws Exception {
		
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		TextInput url = new TextInput(urlParam);
		BooleanInput electronic = new BooleanInput(elecParam);
		
		if(application != null) {
		
			url.setContent(application.getUrl());
			electronic.setSelected(application.getElectronic());
		}
		
		Layer container = new Layer(Layer.SPAN);
		
		Layer layer = new Layer(Layer.DIV);
		Label label = new Label(iwrb.getLocalizedString("electronic", "Electronic"), electronic);
		layer.add(label);
		layer.add(electronic);
		container.add(layer);
		
		layer = new Layer(Layer.DIV);
		label = new Label(iwrb.getLocalizedString("url", "Url"), url);
		layer.add(label);
		layer.add(url);
		container.add(layer);
		
		add(container);
	}

	public void setApplication(Application application) {
		this.application = application;
	}
}