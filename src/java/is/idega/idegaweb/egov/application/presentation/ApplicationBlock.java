/*
 * $Id: ApplicationBlock.java,v 1.5 2006/01/17 11:48:57 gimmi Exp $ Created on Jan 12,
 * 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.data.Application;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.data.ICApplicationBinding;
import com.idega.core.data.ICApplicationBindingHome;
import com.idega.data.IDOLookup;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.servlet.filter.IWAuthenticator;
import com.idega.util.Age;

public abstract class ApplicationBlock extends Block {

	public static final String BUNDLE_IDENTIFIER = "is.idega.idegaweb.egov.application";
	protected static final String PARAMETER_APPLICATION_PK = "prm_application_pk";
	protected static final String PARAMETER_IDENTIFIER_NAME = "prm_identifier_name";
	private static final String BINDING_LOGIN_PAGE_URI = "LOGIN_PAGE_URI";

	public String getBundleIdentifier() {
		return BUNDLE_IDENTIFIER;
	}
	
	public void main(IWContext iwc) throws Exception {
		present(iwc);
	}
	
	protected abstract void present(IWContext iwc) throws Exception;
	protected abstract String getUniqueIdentifier();
	
	protected boolean forward(IWContext iwc, Page page) throws RemoteException {
		if (iwc.isParameterSet(PARAMETER_APPLICATION_PK)) {
			if (iwc.getParameter(PARAMETER_IDENTIFIER_NAME).equals(getUniqueIdentifier())) {
				try {
					Application application = getApplicationBusiness(iwc).getApplication(new Integer(iwc.getParameter(PARAMETER_APPLICATION_PK)));
					getApplicationBusiness(iwc).updateTimesClicked(application);
					if (application.getElectronic() && !iwc.isLoggedOn()) {
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
							iwc.forwardToURL(page, uri, false);
							return true;
						} catch (FinderException f) {
							iwc.forwardToURL(page, application.getUrl(), false);
							return true;
						}
					}
					iwc.forwardToURL(page, application.getUrl(), false);
					return true;
				}
				catch (FinderException fe) {
					fe.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	protected Lists getApplicationList(IWContext iwc, boolean checkAges, Collection applications, Age[] ages) {
		Lists list = new Lists();
		
		Iterator iter = applications.iterator();
		while (iter.hasNext()) {
			Application application = (Application) iter.next();

			boolean displayApplication = true;
			try {
				displayApplication = (checkAges && getApplicationBusiness(iwc).displayApplicationForAges(application, ages));
			}
			catch (RemoteException re) {
				throw new IBORuntimeException(re);
			}
			
			if (!checkAges || displayApplication) {
				ListItem li = new ListItem();
				if (application.getElectronic()) {
					li.setStyleClass("electronic");
				}
				String url = application.getUrl();
				if (url != null && !url.trim().equals("")) {
					Link link = new Link(new Text(application.getName()));
					link.addParameter(PARAMETER_APPLICATION_PK, application.getPrimaryKey().toString());
					link.addParameter(PARAMETER_IDENTIFIER_NAME, getUniqueIdentifier());
					li.add(link);
				}
				list.add(li);
			}
		}
		
		return list;
	}

	protected ApplicationBusiness getApplicationBusiness(IWContext iwc) {
		try {
			return (ApplicationBusiness) IBOLookup.getServiceInstance(iwc, ApplicationBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
}
