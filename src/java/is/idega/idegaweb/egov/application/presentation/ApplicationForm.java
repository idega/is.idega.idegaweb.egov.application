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

import java.rmi.RemoteException;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.PostalCode;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.PersonalIDFormatter;


public abstract class ApplicationForm extends Block {
	
	public void main(IWContext iwc) {
		present(iwc);
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
	
	protected Layer getReceipt(String heading, String subject, String text) {
		return getPhasesReceipt(heading, subject, text, -1, -1);
	}

	protected Layer getPhasesReceipt(String heading, String subject, String text, int phase, int totalPhases) {
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
		
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("receipt");
		
		Layer image = new Layer(Layer.DIV);
		image.setStyleClass("receiptImage");
		layer.add(image);
		
		Heading1 heading1 = new Heading1(subject);
		layer.add(heading1);
		
		layer.add(new Text(text));
		
		return layer;
	}
		
	protected UserBusiness getUserBusiness(IWApplicationContext iwac) {
		try {
			return (UserBusiness) IBOLookup.getServiceInstance(iwac, UserBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	protected abstract void present(IWContext iwc);
}