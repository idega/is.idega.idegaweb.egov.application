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

import java.util.Collection;
import java.util.List;

import com.idega.block.text.model.LocalizedTextModel;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Paragraph;
import com.idega.util.Age;
import com.idega.util.ListUtil;

import is.idega.idegaweb.egov.application.ApplicationUtil;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.model.ApplicationCategoryModel;
import is.idega.idegaweb.egov.application.model.ApplicationModel;

public class ApplicationCategoryViewer extends ApplicationBlock {

	public static final String CACHE_KEY = "app_application_category_viewer";

	private boolean iGroupCategories = true;

	private int columns;

	private String layerID = "applicationCategoryViewer";

	public ApplicationCategoryViewer() {
		setCacheable(getCacheKey(), (20 * 60 * 1000));
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

			IWMainApplicationSettings settings = iwc.getApplicationSettings();
			Collection<ApplicationCategoryModel> categoriesCollection = ApplicationUtil.getAllCategoriesOrderedByPriority(settings);
			for (ApplicationCategoryModel cat: categoriesCollection) {
				List<ApplicationModel> apps = ApplicationUtil.getApplicationsByCategoryOrderedByPriority(settings, cat);
				if (ListUtil.isEmpty(apps)) {
					continue;
				}

				Layer l = new Layer();
				l.setStyleClass("applicationCategory");

				LocalizedTextModel locText = cat.getLocalizedText(icLocaleId);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setLayerID(String id) {
		this.layerID = id;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	/**
	 * @return the groupCategories
	 */
	public boolean isGroupCategories() {
		return this.iGroupCategories;
	}

	/**
	 * @param groupCategories the groupCategories to set
	 */
	public void setGroupCategories(boolean groupCategories) {
		this.iGroupCategories = groupCategories;
	}

	public boolean isiGroupCategories() {
		return iGroupCategories;
	}

	public void setiGroupCategories(boolean iGroupCategories) {
		this.iGroupCategories = iGroupCategories;
	}

	public String getLayerID() {
		return layerID;
	}

}