/*
 * $Id: ApplicationUrlRedirector.java,v 1.5 2006/02/21 19:24:02 laddi Exp $ Created on
 * Jan 17, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.servlet.filter;

import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.presentation.ApplicationBlock;
import java.io.IOException;
import java.util.Map;
import javax.ejb.FinderException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.data.ICApplicationBinding;
import com.idega.core.data.ICApplicationBindingHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.servlet.filter.BaseFilter;
import com.idega.servlet.filter.IWAuthenticator;

public class ApplicationUrlRedirector extends BaseFilter implements Filter  {

	private static final String BINDING_LOGIN_PAGE_URI = "LOGIN_PAGE_URI";

	public void init(FilterConfig arg0) {
	}

	public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) srequest;
		HttpServletResponse response = (HttpServletResponse) sresponse;
		initializeDefaultDomain(request);
		boolean doRedirect = getIfDoRedirect(request);
		if (doRedirect) {
			String newUrl = getNewRedirectURL(request);
			response.sendRedirect(newUrl);
		}
		else {
			chain.doFilter(srequest, sresponse);
		}
	}

	private boolean getIfDoRedirect(HttpServletRequest request) {
		Map map = request.getParameterMap();
		return map.containsKey(ApplicationBlock.PARAMETER_APPLICATION_PK);
	}

	public String getNewRedirectURL(HttpServletRequest request) {
		try {
			LoginBusinessBean loginBusiness = getLoginBusiness(request);
			boolean isLoggedOn = loginBusiness.isLoggedOn(request);
			
			String pk = request.getParameter(ApplicationBlock.PARAMETER_APPLICATION_PK);
			IWApplicationContext iwc = getIWMainApplication(request).getIWApplicationContext();
			Application application = getApplicationBusiness(iwc).getApplication(new Integer(pk));
			getApplicationBusiness(iwc).updateTimesClicked(application);
			if (application.getElectronic() && !isLoggedOn) {
				try {
					ICApplicationBindingHome abHome = (ICApplicationBindingHome) IDOLookup.getHome(ICApplicationBinding.class);
					ICApplicationBinding binding = abHome.findByPrimaryKey(BINDING_LOGIN_PAGE_URI);
					String uri = binding.getValue();
					if (uri.indexOf("?") == -1) {
						uri += "?";
					} else {
						uri += "&";
					}
					uri += IWAuthenticator.PARAMETER_REDIRECT_URI_ONLOGON+"="+application.getUrl();
					return uri;
				} catch (FinderException f) {
					return application.getUrl();
				}
			}
			else if(isLoggedOn){
				
				String uri = application.getUrl();
				uri = IWAuthenticator.getUriParsedWithVariables(request,uri);
				return uri;
			}
			return application.getUrl();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Error handling redirect Url");
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

}
