/*
 * $Id: ApplicationUrlRedirector.java,v 1.31 2009/06/30 16:17:31 valdas Exp $ Created on
 * Jan 17, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.servlet.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.builder.business.BuilderLogic;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.builder.data.ICPage;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.servlet.filter.BaseFilter;
import com.idega.servlet.filter.IWAuthenticator;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.URIUtil;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.egov.application.ApplicationConstants;
import is.idega.idegaweb.egov.application.ApplicationUtil;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationType;
import is.idega.idegaweb.egov.application.business.ApplicationTypesManager;
import is.idega.idegaweb.egov.application.data.dao.ApplicationDAO;
import is.idega.idegaweb.egov.application.model.ApplicationModel;
//import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.presentation.ApplicationBlock;
import is.idega.idegaweb.egov.application.presentation.ApplicationFormInIframe;
import is.idega.idegaweb.egov.application.presentation.DisabledApplicationView;

public class ApplicationUrlRedirector extends BaseFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(ApplicationUrlRedirector.class.getName());

	private static final String PROP_LOGIN_PAGE_URI = "LOGIN_PAGE_URI";

	private static final String PROP_UPDATE_TIMES_CLICKED = "egov.application.updateclicks";
	private static final String PROP_FILE_ENCODING = "file.encoding";

	private static final String PROP_VALUE_UPDATE_TIMES_CLICKED_DISABLED = "disabled";

	@Autowired
	private ApplicationTypesManager applicationTypesManager;

	@Override
	public void init(FilterConfig arg0) {
	}

	@Override
	public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) srequest;
		HttpServletResponse response = (HttpServletResponse) sresponse;
		boolean doRedirect = getIfDoRedirect(request);
		if (doRedirect) {
			String newUrl = getNewRedirectURL(request, response);
			response.sendRedirect(newUrl);
		}
		else {
			chain.doFilter(srequest, sresponse);
		}
	}

	private boolean getIfDoRedirect(HttpServletRequest request) {
		@SuppressWarnings("rawtypes")
		Map map = request.getParameterMap();
		return map.containsKey(ApplicationBlock.PARAMETER_APPLICATION_PK);
	}

	private String getUriForModule(IWMainApplication iwma, Class<? extends UIComponent> uiClass, String appPropName) {
		String uri = null;
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

	public String getNewRedirectURL(HttpServletRequest request, HttpServletResponse response) {
		ApplicationModel application = null;
		try {
			LoginBusinessBean loginBusiness = getLoginBusiness(request);
			boolean isLoggedOn = loginBusiness.isLoggedOn(request);
			String pk = request.getParameter(ApplicationBlock.PARAMETER_APPLICATION_PK);
			if (!StringHandler.isNumeric(pk)) {
				LOGGER.warning("Invalid application primary key: " + pk);
				return CoreConstants.PAGES_URI_PREFIX;
			}

			IWMainApplication iwma = getIWMainApplication(request);

			IWContext iwc = getIWContext(request, response);

			ApplicationDAO applicationDAO = ELUtil.getInstance().getBean(ApplicationDAO.BEAN_NAME);
			Integer id = Integer.valueOf(pk);
			iwc.setSessionAttribute(ApplicationConstants.PARAM_APP_ID, id);
			application = ApplicationUtil.isHibernateTurnedOn(iwc.getApplicationSettings()) ? applicationDAO.getById(id) : getApplicationBusiness(iwc).getApplication(id);

			updateTimesClicked(iwma, application);

			String url = null;
			if ((!application.isEnabled() || !application.getVisible()) && !iwc.isSuperAdmin()) {
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
				ApplicationType at = getApplicationTypesManager().getApplicationType(appType);
				url = at.getUrl(iwc, application);
			}

			if (StringUtil.isEmpty(url)) {
				LOGGER.warning("URL is null for " + application.getPrimaryKey() + ", app type: " + appType) ;
			}

			String encoding = System.getProperty(PROP_FILE_ENCODING);

			if (application.getElectronic() && application.getRequiresLogin() && !isLoggedOn) {
				IWMainApplicationSettings settings = iwma.getSettings();

				String loginPage = settings.getProperty(PROP_LOGIN_PAGE_URI, CoreConstants.PAGES_URI_PREFIX + CoreConstants.SLASH);
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
								if (/*applUrl*/url.indexOf(CoreConstants.QMARK) == -1) {
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

					if (!parameter.equals(ApplicationBlock.PARAMETER_APPLICATION_PK)) {

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

			return url;
		} catch (Exception e) {
			String message = "Error constructing redirect URL to application " + application;
			LOGGER.log(Level.WARNING, message, e);
			throw new RuntimeException(message, e);
		}
	}

	/**
	 * @param application
	 */
	private void updateTimesClicked(IWMainApplication iwma, ApplicationModel application) {
		IWApplicationContext iwc = iwma.getIWApplicationContext();
		String prop = iwma.getSettings().getProperty(PROP_UPDATE_TIMES_CLICKED);
		boolean updateEveryTime = true;
		if (prop != null) {
			if (prop.equals(PROP_VALUE_UPDATE_TIMES_CLICKED_DISABLED)) {
				updateEveryTime = false;
			}
		}

		if (updateEveryTime) {
			try {
				getApplicationBusiness(iwc).updateTimesClicked(application);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void destroy() {
	}

	protected ApplicationBusiness getApplicationBusiness(IWApplicationContext iwc) {
		try {
			return IBOLookup.getServiceInstance(iwc, ApplicationBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	protected ApplicationTypesManager getApplicationTypesManager() {
		if (applicationTypesManager == null)
			ELUtil.getInstance().autowire(this);

		return applicationTypesManager;
	}
}