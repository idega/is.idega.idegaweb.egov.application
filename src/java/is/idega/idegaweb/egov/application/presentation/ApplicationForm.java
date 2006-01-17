/*
 * $Id$
 * Created on Jan 12, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.block.family.business.FamilyLogic;
import is.idega.block.family.business.NoChildrenFound;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.data.Application;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.PostalCode;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.Age;
import com.idega.util.PersonalIDFormatter;


public abstract class ApplicationForm extends Block {
	
	public void main(IWContext iwc) {
		present(iwc);
	}
	
	protected Layer getReceiptLayer(String heading, String body) {
		return getDisplayLayer(heading, body, "receipt", "receiptImage");
	}
	
	protected Layer getStopLayer(String heading, String body) {
		return getDisplayLayer(heading, body, "stop", "stopImage");
	}
	
	private Layer getDisplayLayer(String header, String body, String layerClass, String imageClass) {
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(layerClass);
		
		Layer image = new Layer(Layer.DIV);
		image.setStyleClass(imageClass);
		layer.add(image);
		
		Heading1 heading = new Heading1(header);
		layer.add(heading);
		
		layer.add(new Text(body));
		
		return layer;
	}
	
	protected Layer getPersonInfo(IWContext iwc, User user) throws RemoteException {
		Address address = getUserBusiness(iwc).getUsersMainAddress(user);
		PostalCode postal = null;
		if (address != null) {
			postal = address.getPostalCode();
		}

		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("info");
		
		Layer personInfo = new Layer(Layer.DIV);
		personInfo.setStyleClass("personInfo");
		personInfo.setID("name");
		personInfo.add(new Text(user.getName()));
		layer.add(personInfo);
		
		personInfo = new Layer(Layer.DIV);
		personInfo.setStyleClass("personInfo");
		personInfo.setID("personalID");
		personInfo.add(new Text(PersonalIDFormatter.format(user.getPersonalID(), iwc.getCurrentLocale())));
		layer.add(personInfo);
		
		personInfo = new Layer(Layer.DIV);
		personInfo.setStyleClass("personInfo");
		personInfo.setID("address");
		if (address != null) {
			personInfo.add(new Text(address.getStreetAddress()));
		}
		layer.add(personInfo);
		
		personInfo = new Layer(Layer.DIV);
		personInfo.setStyleClass("personInfo");
		personInfo.setID("postal");
		if (postal != null) {
			personInfo.add(new Text(postal.getPostalAddress()));
		}
		layer.add(personInfo);
		
		return layer;
	}
	
	protected DropdownMenu getUserChooser(IWContext iwc, User user, User chosenUser, String parameterName, IWResourceBundle iwrb) throws RemoteException {
		Collection children = null;
		try {
			children = getMemberFamilyLogic(iwc).getChildrenInCustodyOf(user);
		}
		catch (NoChildrenFound e) {
			children = new ArrayList();
		}
		children.add(user);

		Application application = null;
		try {
			application = getApplicationBusiness(iwc).getApplication(getCaseCode());
		}
		catch (FinderException fe) {
			//Nothing found, continuing...
		}
		
		DropdownMenu menu = new DropdownMenu(parameterName);
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			User element = (User) iter.next();
			boolean addUser = true;
			
			if (application != null) {
				if (application.getAgeFrom() > -1 && application.getAgeTo() > -1) {
					if (element.getDateOfBirth() != null) {
						Age age = new Age(element.getDateOfBirth());
						addUser = (application.getAgeFrom() <= age.getYears() && application.getAgeTo() >= age.getYears());
					}
					else {
						addUser = false;
					}
				}
			}
			
			if (addUser) {
				menu.addMenuElement(element.getPrimaryKey().toString(), element.getName());
			}
		}
		menu.addMenuElementFirst("", iwrb.getLocalizedString("select_applicant", "Select applicant"));
		
		if (chosenUser != null) {
			menu.setSelectedElement(chosenUser.getPrimaryKey().toString());
		}
		
		return menu;
	}
	
	private Layer getPhases(int phase, int totalPhases) {
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("phases");
		
		Lists list = new Lists();
		for (int a = 1; a <= totalPhases; a++) {
			ListItem item = new ListItem();
			item.add(new Text(String.valueOf(a)));
			if (a == phase) {
				item.setStyleClass("current");
			}
			
			list.add(item);
		}
		layer.add(list);
		
		return layer;
	}
	
	protected Layer getHeader(String text) {
		return getPhasesHeader(text, -1, -1);
	}
	
	protected Layer getPhasesHeader(String text, int phase, int totalPhases) {
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("header");
		
		if (phase != -1) {
			Heading1 heading = new Heading1(String.valueOf(phase) + ". " + text);
			layer.add(heading);
			layer.add(getPhases(phase, totalPhases));
		}
		else {
			Heading1 heading = new Heading1(text);
			layer.add(heading);
		}

		return layer;
	}
	
	protected void addReceipt(String heading, String subject, String text) {
		addPhasesReceipt(heading, subject, text, -1, -1);
	}

	protected void addPhasesReceipt(String heading, String subject, String text, int phase, int totalPhases) {
		Layer header = new Layer(Layer.DIV);
		header.setStyleClass("header");
		add(header);
		
		if (phase != -1) {
			Heading1 heading1 = new Heading1(String.valueOf(phase) + ". " + heading);
			header.add(heading1);
			header.add(getPhases(phase, totalPhases));
		}
		else {
			Heading1 heading1 = new Heading1(heading);
			header.add(heading1);
		}
		
		add(getReceiptLayer(subject, text));
	}
		
	private ApplicationBusiness getApplicationBusiness(IWApplicationContext iwac) throws RemoteException {
		try {
			return (ApplicationBusiness) IBOLookup.getServiceInstance(iwac, ApplicationBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	private FamilyLogic getMemberFamilyLogic(IWApplicationContext iwac) throws RemoteException {
		try {
			return (FamilyLogic) IBOLookup.getServiceInstance(iwac, FamilyLogic.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	protected UserBusiness getUserBusiness(IWApplicationContext iwac) {
		try {
			return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	protected abstract String getCaseCode();
	protected abstract void present(IWContext iwc);
}