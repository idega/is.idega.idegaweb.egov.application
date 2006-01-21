/*
 * $Id: ApplicationCategoryViewer.java,v 1.8 2006/01/21 15:51:59 laddi Exp $
 * Created on Jan 13, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationCategoryComparator;
import is.idega.idegaweb.egov.application.business.ApplicationComparator;
import is.idega.idegaweb.egov.application.data.ApplicationCategory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.ejb.FinderException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Lists;
import com.idega.util.Age;

public class ApplicationCategoryViewer extends ApplicationBlock {

	private String id = "applicationCategoryViewer";

	protected String getUniqueIdentifier() {
		return "applicationCategoryViewer";
	}

	public void present(IWContext iwc) throws Exception {
		ApplicationBusiness bus = getApplicationBusiness(iwc);
		Age[] ages = null;
		boolean checkAges = false;
		if (iwc.isLoggedOn()) {
			ages = bus.getAgesForUserAndChildren(iwc.getCurrentUser());
			checkAges = (ages != null);
		}
		try {
			List coll = new ArrayList(bus.getApplicationCategoryHome().findAllOrderedByName());
			Collections.sort(coll, new ApplicationCategoryComparator(iwc.getCurrentLocale()));
			Iterator iter = coll.iterator();
			Layer mainLayer = new Layer();
			mainLayer.setId(id);
			while (iter.hasNext()) {
				ApplicationCategory cat = (ApplicationCategory) iter.next();
				Layer l = new Layer();
				l.setStyleClass("applicationCategory");
				l.add(new Heading1(cat.getName()));
				try {
					List apps = new ArrayList(bus.getApplicationHome().findAllByCategory(cat));
					Collections.sort(apps, new ApplicationComparator(iwc.getCurrentLocale()));
					Lists appList = getApplicationList(iwc, checkAges, apps, ages);
					l.add(appList);
					if (appList.getChildrenCount() == 0) {
						l.setStyleClass("empty");
					}
				}
				catch (FinderException f) {
				}
				mainLayer.add(l);
			}
			
			Layer clearLayer = new Layer(Layer.DIV);
			clearLayer.setStyleClass("Clear");
			mainLayer.add(clearLayer);
			
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
