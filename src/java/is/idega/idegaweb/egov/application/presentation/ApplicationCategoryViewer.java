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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.block.text.data.LocalizedText;
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
	private int columns = 3;
	private boolean iGroupCategories = true;

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

		List<Layer> columnList = new ArrayList<Layer>();
		for(int i = 0; i < this.columns; i++) {
			Layer column = new Layer(Layer.DIV);
			column.setStyleClass("applicationCategoryColumn");
			column.setId("appCatColumn" + i);
			mainLayer.add(column);
			columnList.add(column);
		}

		ApplicationBusiness bus = getApplicationBusiness(iwc);
		Age[] ages = null;
		boolean checkAges = false;
		if (useAgeRestriction(iwc) && iwc.isLoggedOn()) {
			ages = bus.getAgesForUserAndChildren(iwc.getCurrentUser());
			checkAges = (ages != null);
		}
		try {
			int icLocaleId = iwc.getCurrentLocaleId();
			Collection<ApplicationCategory> categoriesCollection = getApplicationBusiness(iwc).getApplicationCategoryHome().findAllOrderedByPriority();
			Object[] categories = categoriesCollection.toArray();

			ApplicationCategory cat = null;
			ApplicationCategory nextCat = null;
			List<Application> nextCategoryApps = null;

			for (int i = 0, j = 0; i < columnList.size() && j < categories.length; i++, j++) {
				Layer column = columnList.get(i);

				if(nextCat != null) {
					cat = nextCat;
				} else {
					cat = (ApplicationCategory) categories[j];
				}
				List<Application> apps = new ArrayList<Application>(bus.getApplicationHome().findAllByCategoryOrderedByPriority(cat));

				if(ListUtil.isEmpty(apps)) {
					i--;
					continue;
				} else {

					Layer l = new Layer();
					l.setStyleClass("applicationCategory");

					LocalizedText locText = cat.getLocalizedText(icLocaleId);
					String heading = null;
					if(locText != null) {
						heading = locText.getBody();
					} else {
						heading = cat.getName();
					}
					Lists appList = getApplicationList(iwc, checkAges, apps, ages);

					if(appList.getChildrenCount() < 1) {
						i--;
						continue;
					}

					if(j < categories.length - 1) {
						nextCat = (ApplicationCategory) categories[j+1];
						nextCategoryApps = new ArrayList<Application>(bus.getApplicationHome().findAllByCategory(nextCat));

						if(nextCategoryApps.isEmpty()) {
							l.add(new Heading1(heading));
							if (cat.getDescription() != null && cat.getDescription().length() > 0) {
								Paragraph paragraph = new Paragraph();
								paragraph.add(cat.getDescription());
								l.add(paragraph);
							}
							l.add(appList);

							nextCat = null;
						} else {
							Lists nextAppList = getApplicationList(iwc, checkAges, nextCategoryApps, ages);
							if(isGroupCategories() && nextAppList.getChildCount() > 0 && appList.getChildCount() > 0 && (appList.getChildCount() + nextAppList.getChildCount() < 8)) {
								Layer l1 = new Layer();
								l1.setStyleClass("compactCategory");
								l1.add(new Heading1(heading));
								if (cat.getDescription() != null && cat.getDescription().length() > 0) {
									Paragraph paragraph = new Paragraph();
									paragraph.add(cat.getDescription());
									l1.add(paragraph);
								}
								l1.add(appList);


								locText = nextCat.getLocalizedText(icLocaleId);
								if(locText != null) {
									heading = locText.getBody();
								} else {
									heading = nextCat.getName();
								}

								Layer l2 = new Layer();
								l2.setStyleClass("compactCategory");
								l2.add(new Heading1(heading));
								if (nextCat.getDescription() != null && nextCat.getDescription().length() > 0) {
									Paragraph paragraph = new Paragraph();
									paragraph.add(nextCat.getDescription());
									l2.add(paragraph);
								}
								l2.add(nextAppList);

								l.add(l1);
								l.add(l2);

								j++;

								nextCat = null;
							} else {
								l.add(new Heading1(heading));
								if (cat.getDescription() != null && cat.getDescription().length() > 0) {
									Paragraph paragraph = new Paragraph();
									paragraph.add(cat.getDescription());
									l.add(paragraph);
								}
								l.add(appList);

								nextCat = null;
							}
						}
					} else {
						l.add(new Heading1(heading));
						if (cat.getDescription() != null && cat.getDescription().length() > 0) {
							Paragraph paragraph = new Paragraph();
							paragraph.add(cat.getDescription());
							l.add(paragraph);
						}

						l.add(appList);
					}

					if(i == columnList.size() - 1) {
						i = -1;
					}

					column.add(l);

				}

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

	public void setColumns(int columns) {
		this.columns = columns;
	}
}