/*
 * $Id: ApplicationUrlRedirector.java,v 1.20 2008/06/30 08:30:11 alexis Exp $ Created on
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

import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.idega.util.CoreUtil;
import com.idega.util.expression.ELUtil;
import com.idega.webface.WFUtil;

public class ApplicationUrlRedirector extends BaseFilter implements Filter {

	private static final String PROP_LOGIN_PAGE_URI = "LOGIN_PAGE_URI";
	private static final String PROP_UPDATE_TIMES_CLICKED = "egov.application.updateclicks";

	private static final String PROP_VALUE_UPDATE_TIMES_CLICKED_DISABLED = "disabled";

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
		@SuppressWarnings("unchecked")
		Map map = request.getParameterMap();
		return map.containsKey(ApplicationBlock.PARAMETER_APPLICATION_PK);
	}

	public String getNewRedirectURL(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginBusinessBean loginBusiness = getLoginBusiness(request);
			boolean isLoggedOn = loginBusiness.isLoggedOn(request);

			String pk = request.getParameter(ApplicationBlock.PARAMETER_APPLICATION_PK);
			IWMainApplication iwma = getIWMainApplication(request);
			
			FacesContext fctx = WFUtil.createFacesContext(request.getSession().getServletContext(), request, response);
			
			IWContext iwc = IWContext.getIWContext(fctx);
			
			Application application = getApplicationBusiness(iwc).getApplication(new Integer(pk));
			
			updateTimesClicked(iwma, application);
			
			String url;
			
			if(application.getAppType() != null) {
				
				ApplicationType at = getAppTypesManager(request.getSession().getServletContext()).getApplicationType(application.getAppType());
				url = at.getUrl(iwc, application);
			} else
				url = application.getUrl();
			
			if (application.getElectronic() && application.getRequiresLogin() && !isLoggedOn) {
				//try {
					
					IWMainApplicationSettings settings = iwma.getSettings();
					
					String loginPage = settings.getProperty(PROP_LOGIN_PAGE_URI);
					if (application.getLoginPageURL() != null) {
						loginPage = application.getLoginPageURL();
					}
					
					String uri = loginPage;
					if (uri.indexOf("?") == -1) {
						uri += "?";
					} else {
						uri += "&";
					}
					
					String encoding = System.getProperty("file.encoding");
					String applUrl = application.getUrlByLocale(iwc.getCurrentLocale());
					
					Enumeration enumeration = request.getParameterNames();
					while (enumeration.hasMoreElements()) {
						String parameter = (String) enumeration.nextElement();

						if (!parameter.equals(ApplicationBlock.PARAMETER_APPLICATION_PK)) {
							String[] values = request.getParameterValues(parameter);
							if (values != null) {
								for (int i = 0; i < values.length; i++) {
									String value = values[i];
									if (applUrl.indexOf("?") == -1) {
										applUrl += "?";
									}
									else {
										applUrl += "&";
									}

									applUrl += parameter + "=" + value;
								}
							}
						}
					}

					String applUrlEncoded = URLEncoder.encode(applUrl, encoding);
					uri += IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON + "=" + applUrlEncoded;
					
					return uri;
			}
			else if (isLoggedOn) {
				
				String uri = application.getUrlByLocale(iwc.getCurrentLocale());
				uri = IWAuthenticator.getUriParsedWithVariables(request, uri);

				Enumeration enumeration = request.getParameterNames();
				while (enumeration.hasMoreElements()) {
					String parameter = (String) enumeration.nextElement();

					if (!parameter.equals(ApplicationBlock.PARAMETER_APPLICATION_PK)) {
						String[] values = request.getParameterValues(parameter);
						if (values != null) {
							for (int i = 0; i < values.length; i++) {
								String value = values[i];
								if (uri.indexOf("?") == -1) {
									uri += "?";
								}
								else {
									uri += "&";
								}
								uri += parameter + "=" + value;
							}
						}
					}
				}

				return url;
			}

			return application.getUrlByLocale(iwc.getCurrentLocale());
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

	protected ApplicationTypesManager getAppTypesManager(ServletContext ctx) {
		
		return ELUtil.getInstance().getBean(ApplicationBlock.appTypesManagerBeanIdentifier, ctx);
	}
}