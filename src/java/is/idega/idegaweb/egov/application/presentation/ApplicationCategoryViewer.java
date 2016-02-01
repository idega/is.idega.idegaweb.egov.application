/*
 * $Id: ApplicationCategoryViewer.java,v 1.22 2009/02/23 17:59:24 laddi Exp $
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
import is.idega.idegaweb.egov.application.data.ApplicationCategoryHome;
import is.idega.idegaweb.egov.application.data.ApplicationHome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.block.text.data.LocalizedText;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Paragraph;
import com.idega.util.Age;
import com.idega.util.ListUtil;

public class ApplicationCategoryViewer extends ApplicationBlock {

	private String layerID = "applicationCategoryViewer";
	public static final String CACHE_KEY = "app_application_category_viewer";
	private boolean iGroupCategories = true;

	public ApplicationCategoryViewer() {
		setCacheable(getCacheKey(), (20 * 60 * 1000));
	}

	private ApplicationHome getApplicationHome() {
		try {
			return (ApplicationHome) IDOLookup.getHome(Application.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get " + ApplicationHome.class + 
					" cause of: ", e);
		}

		return null;
	}

	private ApplicationCategoryHome getApplicationCategoryHome() {
		try {
			return (ApplicationCategoryHome) IDOLookup.getHome(ApplicationCategory.class);
		} catch (IDOLookupException e) {
			getLogger().log(Level.WARNING, 
					"Failed to get " + ApplicationCategoryHome.class + 
					" cause of: ", e);
		}

		return null;
	}

	@Override
	public String getCacheKey() {
		return CACHE_KEY;
	}

	@Override
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

	@Override
	public void present(IWContext iwc) throws Exception {
		Layer mainLayer = new Layer();
		mainLayer.setID(this.layerID);

		ApplicationBusiness bus = getApplicationBusiness(iwc);
		Age[] ages = null;
		boolean checkAges = false;
		if (useAgeRestriction(iwc) && iwc.isLoggedOn()) {
			ages = bus.getAgesForUserAndChildren(iwc.getCurrentUser());
			checkAges = (ages != null);
		}

		try {
			int icLocaleId = iwc.getCurrentLocaleId();

			Collection<ApplicationCategory> categoriesCollection = getApplicationCategoryHome().findAllOrderedByPriority();
			for (ApplicationCategory cat : categoriesCollection) {
				List<Application> apps = new ArrayList<Application>(getApplicationHome().findAllByCategoryOrderedByPriority(cat));
				if(ListUtil.isEmpty(apps)) {
					continue;
				}

				Layer l = new Layer();
				l.setStyleClass("applicationCategory");

				LocalizedText locText = cat.getLocalizedText(icLocaleId);
				String heading = null;
				if(locText != null) {
					heading = locText.getBody();
				} else {
					heading = cat.getName();
				}

				l.add(new Heading1(heading));
				if (cat.getDescription() != null && cat.getDescription().length() > 0) {
					Paragraph paragraph = new Paragraph();
					paragraph.add(cat.getDescription());
					l.add(paragraph);
				}

				Lists appList = getApplicationList(iwc, checkAges, apps, ages);
				if(appList.getChildrenCount() < 1) {
					continue;
				}

				l.add(appList);
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


	/**
	 * @return the groupCategories
	 */
	private boolean isGroupCategories() {
		return this.iGroupCategories;
	}

	/**
	 * @param groupCategories the groupCategories to set
	 */
	public void setGroupCategories(boolean groupCategories) {
		this.iGroupCategories = groupCategories;
	}
}