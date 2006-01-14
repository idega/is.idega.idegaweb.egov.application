/*
 * $Id: ApplicationBlock.java,v 1.2 2006/01/14 21:17:26 laddi Exp $ Created on Jan 12,
 * 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.data.Application;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.event.IWPageEventListener;
import com.idega.idegaweb.IWException;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.util.Age;

public class ApplicationBlock extends Block implements IWPageEventListener {

	public static final String BUNDLE_IDENTIFIER = "is.idega.idegaweb.egov.application";
	protected static final String PARAMETER_APPLICATION_PK = "prm_application_pk";

	public String getBundleIdentifier() {
		return BUNDLE_IDENTIFIER;
	}
	
	public boolean actionPerformed(IWContext iwc) throws IWException {
		if (iwc.isParameterSet(PARAMETER_APPLICATION_PK)) {
			try {
				Application application = getApplicationBusiness(iwc).getApplication(iwc.getParameter(PARAMETER_APPLICATION_PK));
				application.setTimesClicked(application.getTimesClicked() + 1);
				application.store();
			}
			catch (FinderException fe) {
				fe.printStackTrace();
			}
			catch (RemoteException re) {
				throw new IBORuntimeException(re);
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
					Link link = new Link(new Text(application.getName()), url);
					link.addParameter(PARAMETER_APPLICATION_PK, application.getPrimaryKey().toString());
					link.setEventListener(this.getClass());
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
