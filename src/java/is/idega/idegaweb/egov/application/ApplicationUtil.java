package is.idega.idegaweb.egov.application;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.idega.block.text.data.LocalizedText;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.repository.data.Singleton;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationBMPBean;
import is.idega.idegaweb.egov.application.data.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean;
import is.idega.idegaweb.egov.application.data.ApplicationCategoryHome;
import is.idega.idegaweb.egov.application.data.ApplicationHome;
import is.idega.idegaweb.egov.application.data.dao.ApplicationDAO;
import is.idega.idegaweb.egov.application.model.ApplicationCategoryModel;
import is.idega.idegaweb.egov.application.model.ApplicationModel;

public class ApplicationUtil implements Singleton {

	public static final boolean isEnabled(ApplicationModel app) {
		if (app == null) {
			return false;
		}

		Timestamp enabledFrom = app.getEnabledFrom();
		Timestamp enabledTo = app.getEnabledTo();
		if (enabledFrom == null || enabledTo == null) {
			return true;
		}

		IWTimestamp now = IWTimestamp.RightNow();
		boolean enabled = now.isLaterThanOrEquals(new IWTimestamp(enabledFrom)) && now.isEarlierThan(new IWTimestamp(enabledTo));
		return enabled;
	}

	public static final LocalizedText getLocalizedText(ApplicationModel appModel, int icLocaleId) {
		Collection<LocalizedText> texts = null;
		try {
			ApplicationBMPBean app = null;
			if (appModel instanceof is.idega.idegaweb.egov.application.data.bean.Application) {
				ApplicationHome appHome = (ApplicationHome) IDOLookup.getHome(Application.class);
				app = (ApplicationBMPBean) appHome.findByPrimaryKey(appModel.getPrimaryKey());
			} else if (appModel instanceof ApplicationBMPBean) {
				app = (ApplicationBMPBean) appModel;
			}
			if (app != null) {
				texts = app.getLocalizedTexts();
			} else {
				Logger.getLogger(ApplicationUtil.class.getName()).warning("Application model is incorrect type: " + (appModel == null ? "null" : appModel.getClass().getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getLocalizedText(texts, icLocaleId);
	}

	public static final LocalizedText getLocalizedText(ApplicationCategoryModel appCatModel, int icLocaleId) {
		Collection<LocalizedText> texts = null;
		try {
			ApplicationCategoryBMPBean appCat = null;
			if (appCatModel instanceof is.idega.idegaweb.egov.application.data.bean.ApplicationCategory) {
				ApplicationCategoryHome appCatHome = (ApplicationCategoryHome) IDOLookup.getHome(ApplicationCategory.class);
				appCat = (ApplicationCategoryBMPBean) appCatHome.findByPrimaryKey(appCatModel.getPrimaryKey());
			} else if (appCatModel instanceof ApplicationCategoryBMPBean) {
				appCat = (ApplicationCategoryBMPBean) appCatModel;
			}
			if (appCat != null) {
				texts = appCat.getLocalizedTexts();
			} else {
				Logger.getLogger(ApplicationUtil.class.getName()).warning("Application categiry model is incorrect type: " + (appCatModel == null ? "null" : appCatModel.getClass().getName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getLocalizedText(texts, icLocaleId);
	}

	public static final String getName(ApplicationCategoryModel appCatModel) {
		String localizedName = appCatModel.getLocalizedName();
		if (localizedName == null) {
			return appCatModel.getDefaultName();
		} else {
			return localizedName;
		}
	}

	public static final String getLocalizedName(ApplicationCategoryModel appCatModel, String defaultName) {
		IWContext iwc = CoreUtil.getIWContext();
		String localizedName = iwc == null ? null : appCatModel.getLocalizedName(ICLocaleBusiness.getLocaleId(iwc.getLocale()));
		if (localizedName == null) {
			return defaultName;
		}
		else {
			return localizedName;
		}
	}

	private static LocalizedText getLocalizedText(Collection<LocalizedText> texts, int icLocaleId) {
		if (texts != null) {
			for (Iterator<LocalizedText> it = texts.iterator(); it.hasNext();) {
				LocalizedText temp = it.next();
				if (temp.getLocaleId() == icLocaleId) {
					return temp;
				}
			}
		}
		return null;
	}

	private static boolean isHibernateTurnedOn(IWMainApplicationSettings settings) {
		return settings.getBoolean("app_cat_viewer_use_hibernate", true);
	}

	public static final List<ApplicationCategoryModel> getAllCategoriesOrderedByPriority(IWMainApplicationSettings settings) {
		if (isHibernateTurnedOn(settings)) {
			ApplicationDAO applicationDAO = ELUtil.getInstance().getBean(ApplicationDAO.BEAN_NAME);
			List<is.idega.idegaweb.egov.application.data.bean.ApplicationCategory> results = applicationDAO.getAllCategoriesOrderedByPriority();
			if (ListUtil.isEmpty(results)) {
				return Collections.emptyList();
			}

			return new ArrayList<>(results);
		} else {
			try {
				ApplicationCategoryHome appCatHome = (ApplicationCategoryHome) IDOLookup.getHome(ApplicationCategory.class);
				Collection<ApplicationCategory> categories = appCatHome.findAllOrderedByPriority();
				if (ListUtil.isEmpty(categories)) {
					return Collections.emptyList();
				}

				return new ArrayList<>(categories);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Collections.emptyList();
	}

	public static final List<ApplicationModel> getApplicationsByCategoryOrderedByPriority(IWMainApplicationSettings settings, ApplicationCategoryModel category) {
		if (isHibernateTurnedOn(settings) || category instanceof is.idega.idegaweb.egov.application.data.bean.ApplicationCategory) {
			ApplicationDAO applicationDAO = ELUtil.getInstance().getBean(ApplicationDAO.BEAN_NAME);
			List<is.idega.idegaweb.egov.application.data.bean.Application> results = applicationDAO.getApplicationsByCategoryOrderedByPriority((is.idega.idegaweb.egov.application.data.bean.ApplicationCategory) category);
			if (ListUtil.isEmpty(results)) {
				return Collections.emptyList();
			}

			return new ArrayList<>(results);
		} else if (category instanceof ApplicationCategory) {
			try {
				ApplicationHome appHome = (ApplicationHome) IDOLookup.getHome(Application.class);
				Collection<Application> results = appHome.findAllByCategoryOrderedByPriority((ApplicationCategory) category);
				if (ListUtil.isEmpty(results)) {
					return Collections.emptyList();
				}

				return new ArrayList<>(results);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Collections.emptyList();
	}

}