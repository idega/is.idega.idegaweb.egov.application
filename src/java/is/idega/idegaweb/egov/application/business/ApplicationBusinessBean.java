/*
 * $Id: ApplicationBusinessBean.java,v 1.21 2008/12/12 11:00:57 valdas Exp $
 * Created on Jan 12, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.business;

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
import com.idega.block.text.data.LocalizedText;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORuntimeException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserBusiness;
import com.idega.user.business.group.GroupsFilterEngine;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.Age;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

import is.idega.block.family.business.FamilyLogic;
import is.idega.block.family.business.NoChildrenFound;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.ApplicationCategoryHome;
import is.idega.idegaweb.egov.application.data.ApplicationHome;
import is.idega.idegaweb.egov.application.model.ApplicationModel;

public class ApplicationBusinessBean extends CaseBusinessBean implements CaseBusiness, ApplicationBusiness {

	private static final long serialVersionUID = 3838991543604095561L;

	@Override
	public Application getApplication(String caseCode) throws FinderException {
		return getApplicationHome().findByCaseCode(getCaseCode(caseCode));
	}

	@Override
	public Application getApplication(Object primaryKey) throws FinderException {
		return getApplicationHome().findByPrimaryKey(primaryKey);
	}

	@Override
	public void updateTimesClicked(ApplicationModel application) {
		if (application == null) {
			return;
		}

		int timesClicked = application.getTimesClicked() + 1;
		if (timesClicked == 0) {
			timesClicked++;
		}

		application.setTimesClicked(timesClicked);
		application.store();
	}

	@Override
	public void checkApplicationCategoryPriorityConstraint() {
		Collection<ApplicationCategory> categories = null;
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

	@Override
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

	@Override
	public Collection getMostClickedApplications(int numberOfEntries) {
		try {
			return getApplicationHome().findMostClicked(numberOfEntries);
		}
		catch (FinderException fe) {
			fe.printStackTrace();
			return new ArrayList();
		}
	}

	@Override
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

	@Override
	public Age[] getAgesForUserAndChildren(User user) {
		FamilyLogic famLog = null;
		try {
			famLog = IBOLookup.getServiceInstance(getIWApplicationContext(), FamilyLogic.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
		Age[] ages;
		Collection<User> coll = null;
		Iterator<User> iter = null;
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
				User child = iter.next();
				date = child.getDateOfBirth();
				if (date != null) {
					ages[i] = new Age(date);
				}
				i++;
			}
		}
		return ages;
	}

	@Override
	public boolean displayApplicationForAges(ApplicationModel application, Age[] ages) {
		if (application == null || ages == null) {
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

	@Override
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

	@Override
	public ApplicationHome getApplicationHome() {
		try {
			return (ApplicationHome) IDOLookup.getHome(Application.class);
		}
		catch (IDOLookupException e) {
			throw new IDORuntimeException(e);
		}
	}

	@Override
	public ApplicationCategoryHome getApplicationCategoryHome() {
		try {
			return (ApplicationCategoryHome) IDOLookup.getHome(ApplicationCategory.class);
		}
		catch (IDOLookupException e) {
			throw new IDORuntimeException(e);
		}
	}

	@Override
	public Collection<Application> getAvailableApplications(IWContext iwc, String caseCode) {
		Collection<Application> applications = null;
		try {
			applications = StringUtil.isEmpty(caseCode) ? getApplicationHome().findAll() : getApplicationHome().findAllByCaseCode(caseCode);
		} catch (FinderException e) {
			e.printStackTrace();
		}
		if (ListUtil.isEmpty(applications)) {
			return null;
		}

		if (iwc != null && iwc.isSuperAdmin()) {
			return applications;
		}

		List<Application> availableApplications = new ArrayList<Application>();
		User currentUser = null;
		try {
			currentUser = iwc == null ? null : iwc.isLoggedOn() ? iwc.getCurrentUser() : null;
		} catch(NotLoggedOnException e) {
			e.printStackTrace();
		}
		UserBusiness userBusiness = null;
		try {
			userBusiness = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), UserBusiness.class);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		}

		GroupsFilterEngine groupsFilter = ELUtil.getInstance().getBean(GroupsFilterEngine.SPRING_BEAN_IDENTIFIER);
		Collection<Group> userGroups = groupsFilter.getUserGroups(iwc, false);

		Collection<Group> groups = null;
		for (Application app: applications) {
			groups = app.getGroups();

			if (ListUtil.isEmpty(groups)) {
				availableApplications.add(app);
			}
			else {
				if (currentUser != null) {
					boolean applicationAdded = false;
					Group group = null;
					for (Iterator<Group> it = groups.iterator(); (it.hasNext() && !applicationAdded);) {
						group = it.next();
						try {
							if (userBusiness.isGroupUnderUsersTopGroupNode(iwc, group, currentUser, userGroups) ||
									userBusiness.isMemberOfGroup(Integer.valueOf(group.getId()), currentUser)) {
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

	@Override
	public String getApplicationName(Application application, Locale locale) {
		String name = null;
		//	1
		LocalizedText locText = application.getLocalizedText(ICLocaleBusiness.getLocaleId(locale));
		if (locText != null) {
			name = locText.getBody();
		}

		//	2
		if (StringUtil.isEmpty(name)) {
			name = application.getLocalizedName(locale);
		}

		//	3
		if (StringUtil.isEmpty(name)) {
			name = application.getNameByLocale(locale);
		}

		//	4
		if (StringUtil.isEmpty(name)) {
			name = application.getName();
		}

		return name;
	}

	@Override
	public Collection<Application> getApplicationsByType(String type) {
		if (StringUtil.isEmpty(type)) {
			return null;
		}

		try {
			return getApplicationHome().findAllByType(type);
		} catch (FinderException e) {
			e.printStackTrace();
		}

		return null;
	}
}
