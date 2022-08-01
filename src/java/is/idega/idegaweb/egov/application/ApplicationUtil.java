package is.idega.idegaweb.egov.application;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpServletRequest;

import com.idega.block.text.TextUtil;
import com.idega.block.text.model.LocalizedTextModel;
import com.idega.builder.business.BuilderLogic;
import com.idega.core.builder.data.ICPage;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.IDOLookup;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.repository.data.Singleton;
import com.idega.servlet.filter.IWAuthenticator;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.URIUtil;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.egov.application.business.ApplicationType;
import is.idega.idegaweb.egov.application.business.ApplicationTypesManager;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationBMPBean;
import is.idega.idegaweb.egov.application.data.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.ApplicationCategoryHome;
import is.idega.idegaweb.egov.application.data.ApplicationHome;
import is.idega.idegaweb.egov.application.data.dao.ApplicationDAO;
import is.idega.idegaweb.egov.application.model.ApplicationCategoryModel;
import is.idega.idegaweb.egov.application.model.ApplicationModel;
import is.idega.idegaweb.egov.application.presentation.ApplicationBlock;
import is.idega.idegaweb.egov.application.presentation.ApplicationFormInIframe;
import is.idega.idegaweb.egov.application.presentation.DisabledApplicationView;
import is.idega.idegaweb.egov.application.servlet.filter.ApplicationUrlRedirector;

public class ApplicationUtil implements Singleton {

	private static final Logger LOGGER = Logger.getLogger(ApplicationUtil.class.getName());

	private static String getUriForModule(IWMainApplication iwma, Class<? extends UIComponent> uiClass, String appPropName) {
		String uri = iwma.getSettings().getProperty(appPropName);
		if (!StringUtil.isEmpty(uri)) {
			LOGGER.info("Using app property value (" + uri + ") for " + appPropName);
			return uri;
		}

		List<ICPage> pagesWithModule = null;
		try {
			pagesWithModule = BuilderLogic.getInstance().findPagesForModule(uiClass);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error looking up for pages with module " + uiClass.getName(), e);
		}
		if (ListUtil.isEmpty(pagesWithModule)) {
			uri = iwma.getSettings().getProperty(appPropName, CoreConstants.PAGES_URI_PREFIX);
			LOGGER.warning("Did not find page for module: " + uiClass.getName() + ", using app property value (" + uri + "). Will clear all caches");
			CoreUtil.clearAllCaches();
			return uri;
		}

		ICPage page = null;
		for (Iterator<ICPage> pagesIter = pagesWithModule.iterator(); (pagesIter.hasNext() && page == null); pagesIter.hasNext()) {
			page = pagesIter.next();
			if (page.getDeleted()) {
				page = null;
			}
		}
		if (page == null) {
			uri = iwma.getSettings().getProperty(appPropName, CoreConstants.PAGES_URI_PREFIX);
			LOGGER.warning("Did not find not deleted page for module: " + uiClass.getName() + ", using app property value (" + uri + ")");
			return uri;
		} else {
			uri = CoreConstants.PAGES_URI_PREFIX + page.getDefaultPageURI();
		}

		return uri;
	}

	public static boolean isAvailabe(IWContext iwc, ApplicationModel application) {
		if (application == null) {
			LOGGER.warning("Application is not provided!");
			return false;
		}

		if ((!application.isEnabled() || !application.getVisible()) && !iwc.isSuperAdmin()) {
			return false;
		}
		return true;
	}

	public static String getRedirectUrl(
			IWMainApplication iwma,
			IWContext iwc,
			HttpServletRequest request,
			ApplicationTypesManager appTypesManager,
			ApplicationModel application,
			String pk,
			boolean isLoggedOn
	) {
		if (application == null) {
			LOGGER.warning("Application is not provided!");
			return CoreConstants.PAGES_URI_PREFIX;
		}

		String url = null;
		try {
			if (!isAvailabe(iwc, application)) {
				String uri = getUriForModule(iwma, DisabledApplicationView.class, "disabled_app_page");
				URIUtil util = new URIUtil(uri);
				util.setParameter(ApplicationConstants.PARAM_APP_ID, pk);
				return util.getUri();
			}

			String appType = application.getAppType();
			if (StringUtil.isEmpty(appType)) {
				LOGGER.info("App type is unknown, will execute raw query to double-check it");
				String query = "select app_type from EGOV_APPLICATION where EGOV_APPLICATION_ID = " + pk;
				try {
					String[] results = SimpleQuerier.executeStringQuery(query);
					if (!ArrayUtil.isEmpty(results)) {
						appType = results[0];
					}
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Error gettin app type for application with ID: " + pk);
				}
			}

			if (application.getShowInIframe()) {
				String uri = getUriForModule(iwma, ApplicationFormInIframe.class, "form_in_iframe_page");
				URIUtil util = new URIUtil(uri);
				util.setParameter(ApplicationConstants.PARAM_APP_ID, pk);
				return util.getUri();
			} else if (StringUtil.isEmpty(appType)) {
				LOGGER.info("App type was not resolved");
				url = application.getUrlByLocale(iwc.getCurrentLocale());
			} else {
				ApplicationType at = appTypesManager.getApplicationType(appType);
				url = at.getUrl(iwc, application);
			}

			if (StringUtil.isEmpty(url)) {
				LOGGER.warning("URL is null for " + application.getPrimaryKey() + ", app type: " + appType) ;
			}

			String encoding = System.getProperty(ApplicationUrlRedirector.PROP_FILE_ENCODING);

			if (application.getElectronic() && application.getRequiresLogin() && !isLoggedOn) {
				IWMainApplicationSettings settings = iwma.getSettings();

				String loginPage = settings.getProperty(ApplicationUrlRedirector.PROP_LOGIN_PAGE_URI, CoreConstants.PAGES_URI_PREFIX + CoreConstants.SLASH);
				if (CoreConstants.EMPTY.equals(loginPage)) {
					//backup
					loginPage = settings.getProperty(CoreConstants.PAGE_ERROR_403_HANDLER_PORPERTY, CoreConstants.PAGES_URI_PREFIX + CoreConstants.SLASH);
				}

				if (application.getLoginPageURL() != null) {
					loginPage = application.getLoginPageURL();
				}

				StringBuilder uri = new StringBuilder(loginPage);
				if (loginPage.indexOf(CoreConstants.QMARK) == -1) {
					uri.append(CoreConstants.QMARK);
				} else {
					uri.append(CoreConstants.AMP);
				}

				StringBuilder appUrlBuilder = new StringBuilder(url);
				Enumeration<String> enumeration = request.getParameterNames();
				while (enumeration.hasMoreElements()) {
					String parameter = enumeration.nextElement();

					if (!parameter.equals(ApplicationBlock.PARAMETER_APPLICATION_PK)) {
						String[] values = request.getParameterValues(parameter);
						if (values != null) {
							for (int i = 0; i < values.length; i++) {
								String value = values[i];
								if (url.indexOf(CoreConstants.QMARK) == -1) {
									appUrlBuilder.append(CoreConstants.QMARK);
								}
								else {
									appUrlBuilder.append(CoreConstants.AMP);
								}
								appUrlBuilder.append(parameter).append(CoreConstants.EQ).append(value);
							}
						}
					}
				}

				String applUrlEncoded = URLEncoder.encode(appUrlBuilder.toString(), encoding);
				uri.append(IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON).append(CoreConstants.EQ).append(applUrlEncoded);

				return uri.toString();
			} else if (isLoggedOn) {
				StringBuilder appUrlBuilder = new StringBuilder(url);
				Enumeration<String> enumeration = request.getParameterNames();
				while (enumeration.hasMoreElements()) {
					String parameter = enumeration.nextElement();

					if (!parameter.equals(ApplicationBlock.PARAMETER_APPLICATION_PK) && !url.contains(parameter)) {
						String[] values = request.getParameterValues(parameter);
						if (values != null) {
							for (String paramVal : values) {
								if (appUrlBuilder.toString().indexOf(CoreConstants.QMARK) == -1) {
									appUrlBuilder.append(CoreConstants.QMARK);
								}
								else {
									appUrlBuilder.append(CoreConstants.AMP);
								}
								appUrlBuilder.append(parameter).append(CoreConstants.EQ).append(paramVal);
							}
						}
					}
				}

				return url = appUrlBuilder.toString();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting redirect URL for application " + application, e);
		}

		return url;
	}

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
		return TextUtil.getLocalizedText(texts, icLocaleId);
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

	public static final <T extends LocalizedTextModel> String getQueryForTxLocalizedText(int localeId, Collection<T> localNamesIds) {
		String queryForLocalizedEntry = "select * from " + ApplicationBMPBean.TX_LOCALIZED_TEXT + " where " + ApplicationBMPBean.IC_LOCALE_ID + " = " + localeId + " AND (";
		for (Iterator<T> localNameIdsIterator = localNamesIds.iterator(); localNameIdsIterator.hasNext();) {
			T element = localNameIdsIterator.next();
			if (localNameIdsIterator.hasNext()) {
				queryForLocalizedEntry += ApplicationBMPBean.TX_LOCALIZED_TEXT_ID + " = " + element.getPrimaryKey() + " OR ";
			} else {
				queryForLocalizedEntry += ApplicationBMPBean.TX_LOCALIZED_TEXT_ID + " = " + element.getPrimaryKey() + ")";
			}
		}
		return queryForLocalizedEntry;
	}

	public static final String getNameOrUrlByLocale(ApplicationModel app, Locale locale) {
		if (app instanceof ApplicationBMPBean) {
			return ((ApplicationBMPBean) app).getNameOrUrlByLocale(locale);
		} else if (app instanceof is.idega.idegaweb.egov.application.data.bean.Application) {
			return ((is.idega.idegaweb.egov.application.data.bean.Application) app).getLocalizedHeadline(locale);
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

	public static final String getLocalizedName(ApplicationModel app) {
		Locale locale = CoreUtil.getCurrentLocale();
		return getLocalizedName(app, ICLocaleBusiness.getLocaleId(locale));
	}

	public static final String getLocalizedName(ApplicationModel app, Integer icLocaleId) {
		if (app instanceof is.idega.idegaweb.egov.application.data.bean.Application) {
			String localizedBody = ((is.idega.idegaweb.egov.application.data.bean.Application) app).getLocalizedBody(icLocaleId);
			if (localizedBody != null) {
				return localizedBody;
			}
		} else {
			LocalizedTextModel locText = app.getLocalizedText(icLocaleId);
			if (locText != null) {
				return locText.getBody();
			}
		}
		return app.getName();
	}

}