/*
 * $Id: ApplicationCategoryViewer.java,v 1.2 2006/01/13 18:58:47 gimmi Exp $
 * Created on Jan 13, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationCategory;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.util.Age;

public class ApplicationCategoryViewer extends ApplicationBlock {

	private String id = "applicationCategoryViewer";

	public void main(IWContext iwc) throws Exception {
		ApplicationBusiness bus = getApplicationBusiness(iwc);
		Age[] ages = null;
		boolean checkAges = false;
		if (iwc.isLoggedOn()) {
			ages = bus.getAgesForUserAndChildren(iwc.getCurrentUser());
			checkAges = (ages != null);
		}
		try {
			Collection coll = bus.getApplicationCategoryHome().findAllOrderedByName();
			Iterator iter = coll.iterator();
			Layer mainLayer = new Layer();
			mainLayer.setId(id);
			while (iter.hasNext()) {
				ApplicationCategory cat = (ApplicationCategory) iter.next();
				Layer l = new Layer();
				l.setStyleClass("applicationCategory");
				l.add(new Heading1(cat.getName()));
				Lists appList = new Lists();
				int counter = 0;
				try {
					Collection apps = bus.getApplicationHome().findAllByCategory(cat);
					Iterator fiter = apps.iterator();
					while (fiter.hasNext()) {
						Application app = (Application) fiter.next();
						if (!checkAges || (checkAges && bus.displayApplicationForAges(app, ages))) {
							ListItem li = new ListItem();
							if (app.getElectronic()) {
								li.setStyleClass("electronic");
							}
							String url = app.getUrl();
							if (url != null && !url.trim().equals("")) {
								li.add(new Link(new Text(app.getName()), url));
							}
							else {
								li.add(new Link(new Text(app.getName())));
							}
							appList.add(li);
							++counter;
						}
					}
				}
				catch (FinderException f) {
				}
				l.add(appList);
				if (counter == 0) {
					l.setStyleClass("empty");
				}
				mainLayer.add(l);
			}
			add(mainLayer);
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
	}

	public void setId(String id) {
		super.setId(id + "_1");
		this.id = id;
	}
}
