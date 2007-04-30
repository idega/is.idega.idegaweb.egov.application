/*
 * $Id$ Created on Jan 12, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.block.family.business.FamilyLogic;
import is.idega.block.family.business.NoChildrenFound;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.data.Application;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.FinderException;
import javax.faces.component.UIComponent;
import javax.servlet.http.Cookie;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.PostalCode;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.Age;
import com.idega.util.IWTimestamp;
import com.idega.util.PersonalIDFormatter;

public abstract class ApplicationForm extends Block {

	private static final String BUNDLE_IDENTIFIER = "is.idega.idegaweb.egov.application";
	private static final String COOKIE_NAME = "applicationWindow_";

	protected static final String PARAMETER_NO_USER = "prm_no_user";

	private ICPage iWindowPage;
	private int iWidth = 400;
	private int iHeight = 400;

	private boolean iHasErrors = false;
	private Map iErrors;

	public void main(IWContext iwc) {
		if (iwc.isParameterSet(PARAMETER_NO_USER)) {
			setError(PARAMETER_NO_USER, getResourceBundle(iwc).getLocalizedString("application_error.no_user_selected", "No user selected."));
		}

		present(iwc);
	}

	public String getBundleIdentifier() {
		return BUNDLE_IDENTIFIER;
	}

	protected void setError(String parameter, String error) {
		if (this.iErrors == null) {
			this.iErrors = new HashMap();
		}

		this.iHasErrors = true;
		this.iErrors.put(parameter, error);
	}

	protected boolean hasErrors() {
		return this.iHasErrors;
	}

	protected boolean hasError(String parameter) {
		if (hasErrors()) {
			return this.iErrors.containsKey(parameter);
		}
		return false;
	}

	/**
	 * Adds the errors encountered
	 * 
	 * @param iwc
	 * @param errors
	 */
	protected void addErrors(IWContext iwc, UIComponent parent) {
		if (this.iHasErrors) {
			Layer layer = new Layer(Layer.DIV);
			layer.setStyleClass("errorLayer");

			Layer image = new Layer(Layer.DIV);
			image.setStyleClass("errorImage");
			layer.add(image);

			Heading1 heading = new Heading1(getResourceBundle(iwc).getLocalizedString("application_errors_occured", "There was a problem with the following items"));
			layer.add(heading);

			Lists list = new Lists();
			layer.add(list);

			Iterator iter = this.iErrors.values().iterator();
			while (iter.hasNext()) {
				String element = (String) iter.next();
				ListItem item = new ListItem();
				item.add(new Text(element));

				list.add(item);
			}

			parent.getChildren().add(layer);
		}
	}

	protected Layer getReceiptLayer(IWContext iwc, String heading, String body) {
		addWindowToOpen(iwc, getParentPage());
		return getDisplayLayer(heading, body, "receipt", "receiptImage");
	}

	private void addWindowToOpen(IWContext iwc, Page page) {
		if (this.iWindowPage != null) {
			boolean hasSeenWindow = false;
			Cookie[] cookies = iwc.getCookies();
			if (cookies != null) {
				if (cookies.length > 0) {
					for (int i = 0; i < cookies.length; i++) {
						if (cookies[i].getName().equals(COOKIE_NAME + this.iWindowPage.getPrimaryKey().toString())) {
							hasSeenWindow = true;
							continue;
						}
					}
				}
			}

			if (!hasSeenWindow) {
				page.setWindowToOpenOnLoad(this.iWindowPage, this.iWidth, this.iHeight);

				Cookie cookie = new Cookie(COOKIE_NAME + this.iWindowPage.getPrimaryKey().toString(), "true");
				cookie.setMaxAge(24 * 60 * 60);
				cookie.setPath("/");
				iwc.addCookies(cookie);
			}
		}

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

		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(body));
		layer.add(paragraph);

		return layer;
	}

	protected Layer getPersonInfo(IWContext iwc, User user) throws RemoteException {
		return getPersonInfo(iwc, user, true);
	}

	protected Layer getPersonInfo(IWContext iwc, User user, boolean showAddress) throws RemoteException {
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("info");

		if (user != null) {
			Address address = getUserBusiness(iwc).getUsersMainAddress(user);
			PostalCode postal = null;
			if (address != null) {
				postal = address.getPostalCode();
			}

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
			if (address != null && showAddress) {
				personInfo.add(new Text(address.getStreetAddress()));
			}
			layer.add(personInfo);

			personInfo = new Layer(Layer.DIV);
			personInfo.setStyleClass("personInfo");
			personInfo.setID("postal");
			if (postal != null && showAddress) {
				personInfo.add(new Text(postal.getPostalAddress()));
			}
			layer.add(personInfo);
		}

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
			// Nothing found, continuing...
		}

		DropdownMenu menu = new DropdownMenu(parameterName);
		menu.setStyleClass("userSelector");
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			User element = (User) iter.next();
			boolean addUser = true;

			if (application != null) {
				if (application.getAgeFrom() > -1 && application.getAgeTo() > -1) {
					if (element.getDateOfBirth() != null) {
						IWTimestamp stamp = new IWTimestamp(element.getDateOfBirth());
						stamp.setDay(1);
						stamp.setMonth(1);

						Age age = new Age(stamp.getDate());
						addUser = (application.getAgeFrom() <= age.getYears() && application.getAgeTo() >= age.getYears());
					}
					else {
						addUser = false;
					}
				}
			}

			if (!addUser && addOveragedUser(iwc, user)) {
				addUser = true;
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

	protected boolean addOveragedUser(IWContext iwc, User user) {
		return false;
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
		return getPhasesHeader(text, phase, totalPhases, true);
	}

	protected Layer getPhasesHeader(String text, int phase, int totalPhases, boolean showNumberInText) {
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("header");

		if (phase != -1) {
			Heading1 heading = new Heading1((showNumberInText ? (String.valueOf(phase) + ". ") : "") + text);
			layer.add(heading);
			layer.add(getPhases(phase, totalPhases));
		}
		else {
			Heading1 heading = new Heading1(text);
			layer.add(heading);
		}

		return layer;
	}

	protected void addReceipt(IWContext iwc, String heading, String subject, String text) {
		addPhasesReceipt(iwc, heading, subject, text, -1, -1);
	}

	protected void addPhasesReceipt(IWContext iwc, String heading, String subject, String text, int phase, int totalPhases) {
		addPhasesReceipt(iwc, heading, subject, text, phase, totalPhases, true);
	}

	protected void addPhasesReceipt(IWContext iwc, String heading, String subject, String text, int phase, int totalPhases, boolean showNumberInText) {
		Layer header = new Layer(Layer.DIV);
		header.setStyleClass("header");
		add(header);

		if (phase != -1) {
			Heading1 heading1 = new Heading1((showNumberInText ? (String.valueOf(phase) + ". ") : "") + heading);
			header.add(heading1);
			header.add(getPhases(phase, totalPhases));
		}
		else {
			Heading1 heading1 = new Heading1(heading);
			header.add(heading1);
		}

		add(getReceiptLayer(iwc, subject, text));
	}

	protected Link getButtonLink(String text) {
		Layer all = new Layer(Layer.SPAN);
		all.setStyleClass("buttonSpan");

		Layer left = new Layer(Layer.SPAN);
		left.setStyleClass("left");
		all.add(left);

		Layer middle = new Layer(Layer.SPAN);
		middle.setStyleClass("middle");
		middle.add(new Text(text));
		all.add(middle);

		Layer right = new Layer(Layer.SPAN);
		right.setStyleClass("right");
		all.add(right);

		Link link = new Link(all);
		link.setStyleClass("button");

		return link;
	}

	protected Layer getAttentionLayer(String text) {
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("attention");

		Layer imageLayer = new Layer(Layer.DIV);
		imageLayer.setStyleClass("attentionImage");
		layer.add(imageLayer);

		Layer textLayer = new Layer(Layer.DIV);
		textLayer.setStyleClass("attentionText");
		layer.add(textLayer);

		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(text));
		textLayer.add(paragraph);

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("attentionClear");
		layer.add(clearLayer);

		return layer;
	}

	protected ApplicationBusiness getApplicationBusiness(IWApplicationContext iwac) {
		try {
			return (ApplicationBusiness) IBOLookup.getServiceInstance(iwac, ApplicationBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	private FamilyLogic getMemberFamilyLogic(IWApplicationContext iwac) {
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

	public void setWindowPage(ICPage page) {
		this.iWindowPage = page;
	}

	public void setWindowPage(ICPage page, int width, int height) {
		this.iWindowPage = page;
		this.iWidth = width;
		this.iHeight = height;
	}
}