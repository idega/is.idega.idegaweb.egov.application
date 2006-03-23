/*
 * $Id$
 * Created on Mar 23, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;


public class ApplicationLegend extends ApplicationBlock {

	protected void present(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("applicationLegend");
		
		Layer image = new Layer(Layer.DIV);
		image.setStyleClass("applicationLegendImage");
		layer.add(image);
		
		Lists list = new Lists();
		list.setStyleClass("applicationLegendList");
		layer.add(list);
		
		ListItem item = new ListItem();
		item.setStyleClass("electronic");
		item.setStyleClass("requiresLogin");
		item.add(new Text(iwrb.getLocalizedString("legend.requires_login", "Electronic and requires login")));
		list.add(item);

		item = new ListItem();
		item.setStyleClass("electronic");
		item.add(new Text(iwrb.getLocalizedString("legend.electronic", "Electronic")));
		list.add(item);
		
		item = new ListItem();
		item.setStyleClass("nonElectronic");
		item.add(new Text(iwrb.getLocalizedString("legend.non_electronic", "Non electronic")));
		list.add(item);
		
		add(layer);
	}
}