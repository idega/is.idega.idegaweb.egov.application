package is.idega.idegaweb.egov.application;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import com.idega.block.text.model.LocalizedTextModel;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.repository.data.Singleton;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationBMPBean;
import is.idega.idegaweb.egov.application.data.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.ApplicationCategoryHome;
import is.idega.idegaweb.egov.application.data.ApplicationHome;
import is.idega.idegaweb.egov.application.data.dao.ApplicationDAO;
import is.idega.idegaweb.egov.application.model.ApplicationCategoryModel;
import is.idega.idegaweb.egov.application.model.ApplicationModel;

public class ApplicationUtil implements Singleton {

	private static final Logger LOGGER = Logger.getLogger(ApplicationUtil.class.getName());

	public static final boolean isEnabled(ApplicationModel app) {
		if (app == null) {
			return false;
		}

		Timestamp enabledFrom = app.getEnabledFrom();
		Timestamp enabledTo = app.getEnabledTo();
		if (enabledFrom == null && enabledTo == null) {
			return true;
		}

		IWTimestamp now = IWTimestamp.RightNow();
		boolean enabled = false;
		if (enabledFrom != null && enabledTo != null) {
			enabled = now.isLaterThanOrEquals(new IWTimestamp(enabledFrom)) && now.isEarlierThan(new IWTimestamp(enabledTo));
		} else if (enabledFrom != null) {
			enabled = now.isLaterThanOrEquals(new IWTimestamp(enabledFrom));
		} else if (enabledTo != null) {
			enabled = now.isEarlierThan(new IWTimestamp(enabledTo));
		}

		if (!enabled) {
			LOGGER.warning("Application (ID: " + app.getPrimaryKey() + ") is disabled. It is enabled from " + enabledFrom + " to " + enabledTo + ". Currently it's " + now);
		}
		return enabled;
	}

	public static final <T extends LocalizedTextModel> T getLocalizedText(ApplicationModel appModel, int icLocaleId) {
		Collection<T> texts = appModel.getLocalizedTexts();
		return getLocalizedText(texts, icLocaleId);
	}

	public static final <T extends LocalizedTextModel> T getLocalizedText(ApplicationCategoryModel appCatModel, int icLocaleId) {
		Collection<T> texts = appCatModel.getLocalizedTexts();
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

	private static <T extends LocalizedTextModel> T getLocalizedText(Collection<T> texts, int icLocaleId) {
		if (texts != null) {
			for (Iterator<T> it = texts.iterator(); it.hasNext();) {
				T temp = it.next();
				if (temp.getLocaleId() == icLocaleId) {
					return temp;
				}
			}
		}
		return null;
	}

	public static boolean isHibernateTurnedOn(IWMainApplicationSettings settings) {
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
			List<is.idega.idegaweb.egov.application.data.bean.Application> results = applicationDAO.getApplicationsByCategoryOrderedByPriority(
					(is.idega.idegaweb.egov.application.data.bean.ApplicationCategory) category
			);
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

	public static final <T extends LocalizedTextModel> String getQueryForTxLocalizedText(int localeId, Collection<T> localNamesIds){
		String queryForLocalizedEntry = "select * from "+ApplicationBMPBean.TX_LOCALIZED_TEXT+" where "+ApplicationBMPBean.IC_LOCALE_ID+" = "+localeId+" AND (";
		for (Iterator<T> localNameIdsIterator = localNamesIds.iterator(); localNameIdsIterator.hasNext();) {
			T element = localNameIdsIterator.next();
			if(localNameIdsIterator.hasNext()){
				queryForLocalizedEntry += ApplicationBMPBean.TX_LOCALIZED_TEXT_ID+" = "+element.getPrimaryKey()+ " OR ";
			}
			else{
				queryForLocalizedEntry += ApplicationBMPBean.TX_LOCALIZED_TEXT_ID+" = "+element.getPrimaryKey()+")";
			}
		}
		return queryForLocalizedEntry;
	}

	public static final String getNameOrUrlByLocale(ApplicationModel app, Locale locale) {
		if (app instanceof ApplicationBMPBean) {
			return ((ApplicationBMPBean) app).getNameOrUrlByLocale(locale);
		} else if (app instanceof is.idega.idegaweb.egov.application.data.bean.Application) {
			is.idega.idegaweb.egov.application.data.bean.Application appBean = (is.idega.idegaweb.egov.application.data.bean.Application) app;
			List<com.idega.block.text.data.bean.LocalizedText> texts = appBean.getUrlLocalizedTexts();
			if (ListUtil.isEmpty(texts)) {
				return null;
			}

			int localeId = ICLocaleBusiness.getLocaleId(locale);
			for (com.idega.block.text.data.bean.LocalizedText text: texts) {
				if (text.getLocaleId() == localeId) {
					return text.getHeadline();
				}
			}
		}

		return null;
	}

	public static final String getUrlByLocale(ApplicationModel app, Locale locale) {
		String localizedName = getNameOrUrlByLocale(app, locale);
		if (StringUtil.isEmpty(localizedName)) {
			return app.getUrl();
		}
		return localizedName;
	}

}