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
 * Interface is meant to be extended by beans, reflecting application type for egov applications
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/02/05 12:44:07 $ by $Author: civilis $
 *
 */
public class UIApplicationTypeURLHandler extends Block {
	
	private Application application;
	
	@Override
	public void main(IWContext iwc) throws Exception {
		
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		TextInput url = new TextInput("url");
		BooleanInput electronic = new BooleanInput("elec");
		
		if(application != null) {
		
			url.setContent(application.getUrl());
			electronic.setSelected(application.getElectronic());
		}
		
		Layer layer = new Layer(Layer.DIV);
		Label label = new Label(iwrb.getLocalizedString("electronic", "Electronic"), electronic);
		layer.add(label);
		layer.add(electronic);
		add(layer);
		
		layer = new Layer(Layer.DIV);
		label = new Label(iwrb.getLocalizedString("url", "Url"), url);
		layer.add(label);
		layer.add(url);
		add(layer);
	}

	public void setApplication(Application application) {
		this.application = application;
	}
}