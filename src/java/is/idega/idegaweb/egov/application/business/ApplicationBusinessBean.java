/*
 * $Id: ApplicationBusinessBean.java,v 1.16 2008/09/25 10:33:58 valdas Exp $
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.block.process.business.CaseBusiness;
import com.idega.block.process.business.CaseBusinessBean;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORuntimeException;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.Age;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

public class ApplicationBusinessBean extends CaseBusinessBean implements CaseBusiness, ApplicationBusiness {

	private static final long serialVersionUID = 3838991543604095561L;

	public Application getApplication(String caseCode) throws FinderException {
		return getApplicationHome().findByCaseCode(getCaseCode(caseCode));
	}
	
	public Application getApplication(Object primaryKey) throws FinderException {
		return getApplicationHome().findByPrimaryKey(primaryKey);
	}
	
	public void updateTimesClicked(Application application) {
		int timesClicked = application.getTimesClicked() + 1;
		if (timesClicked == 0) {
			timesClicked++;
		}
		
		application.setTimesClicked(timesClicked);
		application.store();
	}
	
	public void checkApplicationCategoryPriorityConstraint() {
		Collection categories = null;
		try {
			categories = getApplicationCategoryHome().findAllOrderedByPriority();
		}
		catch (FinderException fe) {
			fe.printStackTrace();
		}
		if(categories != null) {
			Collection<ApplicationCategory> unprioritized = new ArrayList<ApplicationCategory>();
			int currentPriority = 1;
			for(Iterator<ApplicationCategory> it = categories.iterator(); it.hasNext(); ) {
				ApplicationCategory cat = it.next();
				Integer priority = cat.getPriority();
				
				if(priority == null) {
					unprioritized.add(cat);
					continue;
				}
				
				if(priority.intValue() != currentPriority) {
					cat.setPriority(currentPriority);
					cat.store();
				}
				currentPriority++;
			}
			
			for(Iterator<ApplicationCategory> it = unprioritized.iterator(); it.hasNext(); ) {
				ApplicationCategory cat = it.next();
				cat.setPriority(currentPriority);
				currentPriority++;
				cat.store();
			}
		}
	}
	
	public void checkApplicationPriorityConstraint(ApplicationCategory cat) {
		Collection apps = null;
		try {
			apps = getApplicationHome().findAllByCategoryOrderedByPriority(cat);
		}
		catch (FinderException fe) {
			fe.printStackTrace();
		}
		if(apps != null) {
			Collection<Application> unprioritized = new ArrayList<Application>();
			int currentPriority = 1;
			for(Iterator<Application> it = apps.iterator(); it.hasNext(); ) {
				Application app = it.next();
				Integer priority = app.getPriority();
				
				if(priority == null) {
					unprioritized.add(app);
					continue;
				}
				
				if(priority.intValue() != currentPriority) {
					app.setPriority(currentPriority);
					app.store();
				}
				currentPriority++;
			}
			
			for(Iterator<Application> it = unprioritized.iterator(); it.hasNext(); ) {
				Application app = it.next();
				app.setPriority(currentPriority);
				currentPriority++;
				app.store();
			}
		}
	}
	
	public Collection getMostClickedApplications(int numberOfEntries) {
		try {
			return getApplicationHome().findMostClicked(numberOfEntries);
		}
		catch (FinderException fe) {
			fe.printStackTrace();
			return new ArrayList();
		}
	}
	
	public Collection getUserApplications(User user) {
		Collection list = new ArrayList();
		try {
			Age[] ages = getAgesForUserAndChildren(user);
			Collection applications = getApplicationHome().findElectronicApplications();
			
			Iterator iter = applications.iterator();
			while (iter.hasNext()) {
				Application application = (Application) iter.next();
				if (displayApplicationForAges(application, ages)) {
					list.add(application);
				}
			}
		}
		catch (FinderException fe) {
			fe.printStackTrace();
		}
		
		return list;
	}

	public Age[] getAgesForUserAndChildren(User user) {
		FamilyLogic famLog = null;
		try {
			famLog = (FamilyLogic) IBOLookup.getServiceInstance(getIWApplicationContext(), FamilyLogic.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
		Age[] ages;
		Collection coll = null;
		Iterator iter = null;
		try {
			coll = famLog.getChildrenInCustodyOf(user);
			iter = coll.iterator();
			ages = new Age[coll.size() + 1];
		} catch (NoChildrenFound e) {
				//User has no children...
			ages = new Age[1];
		} catch (RemoteException e) {
			ages = new Age[1];
		}
			
		int i = 0;
		Date date = user.getDateOfBirth();
		if (date != null) {
			ages[i] = new Age(date);
		}
		i++;
		if(coll != null) {
			iter = coll.iterator();
			
			while (iter.hasNext()) {
				User child = (User) iter.next();
				date = child.getDateOfBirth();
				if (date != null) {
					ages[i] = new Age(date);
				}
				i++;
			}
		}		
		return ages;
	}

	public boolean displayApplicationForAges(Application application, Age[] ages) {
		if (ages == null) {
			return false;
		}
		
		int from = application.getAgeFrom();
		int to = application.getAgeTo();
		if (from < 0 && to < 0) {
			return true;
		}
		
		for (int i = 0; i < ages.length; i++) {
			if (ages[i] != null) {
				int years = ages[i].getYears();
				if (from <= years && to >= years) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean canApplyForApplication(String casecode, User user) {
		try {
			Application application = getApplication(casecode);
			if (!application.getVisible()) {
				return false;
			}
			int from = application.getAgeFrom();
			int to = application.getAgeTo();
			if (from < 0 && to < 0) {
				return true;
			}

			if (user.getDateOfBirth() != null) {
				IWTimestamp stamp = new IWTimestamp(user.getDateOfBirth());
				stamp.setDay(1);
				stamp.setMonth(1);
				
				Age age = new Age(stamp.getDate());
				int years = age.getYears();
				if (from <= years && to >= years) {
					return true;
				}
			}
		}
		catch (FinderException fe) {
			log(fe);
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

	@SuppressWarnings("unchecked")
	public Collection<Application> getAvailableApplications(IWContext iwc) {
		Collection<Application> applications = null;
		try {
			applications = getApplicationHome().findAll();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		if (ListUtil.isEmpty(applications)) {
			return null;
		}
		if (iwc.isSuperAdmin()) {
			return applications;
		}
		
		List<Application> availableApplications = new ArrayList<Application>();
		User currentUser = null;
		try {
			currentUser = iwc.getCurrentUser();
		} catch(NotLoggedOnException e) {
			e.printStackTrace();
		}
		UserBusiness userBusiness = null;
		try {
			userBusiness = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		}
		
		Collection<Group> groups = null;
		for (Application app: applications) {
			groups = app.getGroups();
			
			if (ListUtil.isEmpty(groups)) {
				availableApplications.add(app);
			}
			else {
				if (currentUser != null) {
					boolean applicationAdded = false;
					for (Iterator<Group> it = groups.iterator(); (it.hasNext() && !applicationAdded);) {
						try {
							if (userBusiness.isMemberOfGroup(Integer.valueOf(it.next().getId()), currentUser)) {
								applicationAdded = true;
								availableApplications.add(app);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		return availableApplications;
	}
	
	public String getApplicationName(Application application, Locale locale) {
		String name = application.getLocalizedName(locale);
		if (StringUtil.isEmpty(name)) {
			name = application.getNameByLocale(locale);
		}
		if (StringUtil.isEmpty(name)) {
			name = application.getName();
		}
		return name;
	}
}
