/*
 * $Id: ApplicationBusinessBean.java,v 1.4 2006/01/13 02:11:50 gimmi Exp $
 * Created on Jan 12, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.business;

import is.idega.block.family.business.FamilyLogic;
import is.idega.block.family.business.NoChildrenFound;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.ApplicationCategoryHome;
import is.idega.idegaweb.egov.application.data.ApplicationHome;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.block.process.business.CaseBusiness;
import com.idega.block.process.business.CaseBusinessBean;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORuntimeException;
import com.idega.user.data.User;
import com.idega.util.Age;

public class ApplicationBusinessBean extends CaseBusinessBean implements CaseBusiness, ApplicationBusiness {

	public Application getApplication(String caseCode) throws FinderException {
		return getApplicationHome().findByCaseCode(getCaseCode(caseCode));
	}

	public Age[] getAgesForUserAndChildren(User user) throws RemoteException {
		FamilyLogic famLog = null;
		try {
			famLog = (FamilyLogic) IBOLookup.getServiceInstance(getIWApplicationContext(), FamilyLogic.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
		try {
			Collection coll = famLog.getChildrenInCustodyOf(user);
			Iterator iter = coll.iterator();
			Age[] ages = new Age[coll.size() + 1];
			int i = 0;
			Date date = user.getDateOfBirth();
			if (date != null) {
				ages[i++] = new Age(date);
			}
			while (iter.hasNext()) {
				User child = (User) iter.next();
				date = child.getDateOfBirth();
				if (date != null) {
					ages[i++] = new Age(date);
				}
			}
			return ages;
		}
		catch (NoChildrenFound e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean displayApplicationForAges(Application application, Age[] ages) {
		int from = application.getAgeFrom();
		int to = application.getAgeTo();
		int years;
		for (int i = 0; i < ages.length; i++) {
			if (ages[i] != null) {
				years = ages[i].getYears();
				if (from <= years && to >= years) {
					return true;
				}
			}
		}
		return false;
	}

	public ApplicationHome getApplicationHome() {
		try {
			return (ApplicationHome) IDOLookup.getHome(Application.class);
		}
		catch (IDOLookupException e) {
			throw new IDORuntimeException(e);
		}
	}

	public ApplicationCategoryHome getApplicationCategoryHome() {
		try {
			return (ApplicationCategoryHome) IDOLookup.getHome(ApplicationCategory.class);
		}
		catch (IDOLookupException e) {
			throw new IDORuntimeException(e);
		}
	}
}
