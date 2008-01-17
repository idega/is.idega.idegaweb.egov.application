/*
 * $Id: ApplicationCategoryViewer.java,v 1.14 2008/01/17 08:15:23 alexis Exp $
 * Created on Jan 13, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationComparator;
import is.idega.idegaweb.egov.application.data.ApplicationCategory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.block.text.data.LocalizedText;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Lists;
import com.idega.util.Age;

public class ApplicationCategoryViewer extends ApplicationBlock {

	private String layerID = "applicationCategoryViewer";
	public static final String CACHE_KEY = "app_application_category_viewer";

	public ApplicationCategoryViewer() {
		setCacheable(getCacheKey(), (20 * 60 * 1000));
	}
	
	public String getCacheKey() {
		return CACHE_KEY;
	}

	protected String getCacheState(IWContext iwc, String cacheStatePrefix) {
		if (useAgeRestriction(iwc) && iwc.isLoggedOn()) {
			return cacheStatePrefix + "_" + iwc.getCurrentUser().getPrimaryKey().toString();
		}
		else {
			return cacheStatePrefix;
		}
	}
	
	protected String getUniqueIdentifier() {
		return "applicationCategoryViewer";
	}

	public void present(IWContext iwc) throws Exception {
		ApplicationBusiness bus = getApplicationBusiness(iwc);
		Age[] ages = null;
		boolean checkAges = false;
		if (useAgeRestriction(iwc) && iwc.isLoggedOn()) {
			ages = bus.getAgesForUserAndChildren(iwc.getCurrentUser());
			checkAges = (ages != null);
		}
		try {
			int icLocaleId = iwc.getCurrentLocaleId();
			Collection categories = getApplicationBusiness(iwc).getApplicationCategoryHome().findAllOrderedByPriority();
			Iterator iter = categories.iterator();
			Layer mainLayer = new Layer();
			mainLayer.setID(this.layerID);
			List nextCategoryApps = null;
			
			ApplicationCategory cat = null;
			ApplicationCategory nextCat = null;
			boolean nextCompact = false;
			
			while (iter.hasNext()) {
				if(nextCat != null) {
					cat = nextCat;
				} else {
					cat = (ApplicationCategory) iter.next();
				}
				
				LocalizedText locText = cat.getLocalizedText(icLocaleId);
				Layer l = new Layer();
				l.setStyleClass("applicationCategory");
				String heading = null;
				if(locText != null) {
					heading = locText.getBody();
				} else {
					heading = cat.getName();
				}
				l.add(new Heading1(heading));
				try {
					List apps = new ArrayList(bus.getApplicationHome().findAllByCategoryOrderedByPriority(cat));
					nextCat = (ApplicationCategory) iter.next();
					nextCategoryApps = new ArrayList(bus.getApplicationHome().findAllByCategory(nextCat));
					if(nextCompact) {
						l.setStyleClass("compactCategory");
						nextCompact = false;
					} else {
						if(apps.size() > 0 && apps.size() < 3 && nextCategoryApps.size() > 0 && nextCategoryApps.size() < 3) {
							l.setStyleClass("compactCategory");
							nextCompact = true;
						}
					}
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

	public void setLayerID(String id) {
		this.layerID = id;
	}
}
