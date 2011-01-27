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

import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationType;
import is.idega.idegaweb.egov.application.business.ApplicationTypesManager;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.presentation.ApplicationBlock;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;

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
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWContext;
import com.idega.servlet.filter.BaseFilter;
import com.idega.servlet.filter.IWAuthenticator;
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;

public class ApplicationUrlRedirector extends BaseFilter implements Filter {

	private static final String PROP_LOGIN_PAGE_URI = "LOGIN_PAGE_URI";
	
	private static final String PROP_UPDATE_TIMES_CLICKED = "egov.application.updateclicks";
	private static final String PROP_FILE_ENCODING = "file.encoding";

	private static final String PROP_VALUE_UPDATE_TIMES_CLICKED_DISABLED = "disabled";
	
	@Autowired
	private ApplicationTypesManager applicationTypesManager;

	public void init(FilterConfig arg0) {
	}

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

	public String getNewRedirectURL(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginBusinessBean loginBusiness = getLoginBusiness(request);
			boolean isLoggedOn = loginBusiness.isLoggedOn(request);
			String pk = request.getParameter(ApplicationBlock.PARAMETER_APPLICATION_PK);
			IWMainApplication iwma = getIWMainApplication(request);
			
			IWContext iwc = getIWContext(request, response);
			
			Application application = getApplicationBusiness(iwc).getApplication(new Integer(pk));
			
			updateTimesClicked(iwma, application);
			
			String url;
			
			if (application.getAppType() != null) {
				ApplicationType at = getApplicationTypesManager().getApplicationType(application.getAppType());
				url = at.getUrl(iwc, application);
			} else
				url = application.getUrlByLocale(iwc.getCurrentLocale());
			
			String encoding = System.getProperty(PROP_FILE_ENCODING);
			
			if (application.getElectronic() && application.getRequiresLogin() && !isLoggedOn) {
				IWMainApplicationSettings settings = iwma.getSettings();
					
				String loginPage = settings.getProperty(PROP_LOGIN_PAGE_URI, "/pages/");
				if(CoreConstants.EMPTY.equals(loginPage)){
					//backup
					loginPage = settings.getProperty(CoreConstants.PAGE_ERROR_403_HANDLER_PORPERTY, "/pages/");
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
				
				//String applUrl = application.getUrlByLocale(iwc.getCurrentLocale());
				StringBuilder appUrlBuilder = new StringBuilder(url);
					
				@SuppressWarnings("unchecked")
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
				
//				String uri = application.getUrlByLocale(iwc.getCurrentLocale());
//				uri = IWAuthenticator.getUriParsedWithVariables(request, uri);
				
				StringBuilder appUrlBuilder = new StringBuilder(url);

				@SuppressWarnings("unchecked")
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Error handling redirect Url");
	}

	/**
	 * <p>
	 * TODO tryggvil describe method updateTimesClicked
	 * </p>
	 * 
	 * @param application
	 */
	private void updateTimesClicked(IWMainApplication iwma, Application application) {

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
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void destroy() {
	}

	protected ApplicationBusiness getApplicationBusiness(IWApplicationContext iwc) {
		try {
			return (ApplicationBusiness) IBOLookup.getServiceInstance(iwc, ApplicationBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
	
	protected ApplicationTypesManager getApplicationTypesManager() {
		
		if(applicationTypesManager == null)
			ELUtil.getInstance().autowire(this);
		
		return applicationTypesManager;
	}
}