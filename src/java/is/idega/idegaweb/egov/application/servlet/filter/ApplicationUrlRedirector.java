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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.servlet.filter.BaseFilter;
import com.idega.util.CoreConstants;
import com.idega.util.StringHandler;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.egov.application.ApplicationConstants;
import is.idega.idegaweb.egov.application.ApplicationUtil;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationTypesManager;
import is.idega.idegaweb.egov.application.data.dao.ApplicationDAO;
import is.idega.idegaweb.egov.application.model.ApplicationModel;
//import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.presentation.ApplicationBlock;

public class ApplicationUrlRedirector extends BaseFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(ApplicationUrlRedirector.class.getName());

	public static final String	PROP_LOGIN_PAGE_URI = "LOGIN_PAGE_URI",
								PROP_FILE_ENCODING = "file.encoding";

	private static final String PROP_UPDATE_TIMES_CLICKED = "egov.application.updateclicks";

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

	private String getNewRedirectURL(HttpServletRequest request, HttpServletResponse response) {
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

			String url = ApplicationUtil.getRedirectUrl(iwma, iwc, request, getApplicationTypesManager(), application, pk, isLoggedOn);
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