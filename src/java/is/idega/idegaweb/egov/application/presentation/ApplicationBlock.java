/*
 * $Id: ApplicationBlock.java,v 1.22 2008/09/02 12:52:49 civilis Exp $ Created on Jan 12,
 * 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.accounting.business.CitizenBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationTypesManager;
import is.idega.idegaweb.egov.application.data.Application;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.block.text.data.LocalizedText;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.util.Age;
import com.idega.util.expression.ELUtil;

public abstract class ApplicationBlock extends Block {

	@Autowired private ApplicationTypesManager applicationTypesManager;
	
	public static final String BUNDLE_IDENTIFIER = "is.idega.idegaweb.egov.application";
	public static final String PARAMETER_APPLICATION_PK = "prm_app_application_pk";
	public static final String PARAMETER_IDENTIFIER_NAME = "prm_app_identifier_name";
	public static final String ATTRIBUTE_USE_AGE_RESTRICTION = "application_use_age_restriction";
	
	public String getBundleIdentifier() {
		return BUNDLE_IDENTIFIER;
	}
	
	public void main(IWContext iwc) throws Exception {
		present(iwc);
	}
	
	protected boolean useAgeRestriction(IWContext iwc) {
		return new Boolean(iwc.getApplicationSettings().getProperty(ATTRIBUTE_USE_AGE_RESTRICTION, Boolean.TRUE.toString())).booleanValue();
	}
	
	protected abstract void present(IWContext iwc) throws Exception;

	protected Lists getApplicationList(IWContext iwc, boolean checkAges, Collection<Application> applications, Age[] ages) throws RemoteException {
		Lists list = new Lists();
		
		Collection<ListItem> applicationList = new ArrayList<ListItem>(applications.size());
		Iterator<Application> iter = applications.iterator();
		
		while (iter.hasNext()) {
			Application application = iter.next();

			boolean displayApplication = true;
			try {
				displayApplication = (checkAges && getApplicationBusiness(iwc).displayApplicationForAges(application, ages));
			}
			catch (RemoteException re) {
				throw new IBORuntimeException(re);
			}
			
			if (application.getVisible() && (!checkAges || displayApplication) && !(iwc.isLoggedOn() && application.getHiddenFromGuests() && getUserBusiness(iwc).hasGuestAccount(iwc.getCurrentUser()))) {
				ListItem li = new ListItem();
				if (application.getElectronic()) {
					li.setStyleClass("electronic");
				}
				if (application.getCaseCode() != null) {
					li.setStyleClass("caseConnected");
				}
				if (application.getRequiresLogin()) {
					li.setStyleClass("requiresLogin");
				}
				
				int icLocaleId = iwc.getCurrentLocaleId();
				
				LocalizedText locText = application.getLocalizedText(icLocaleId);
				String heading = null;
				if(locText != null) {
					heading = locText.getBody();
				} else {
					heading = application.getName();
				}
				
				Link link = new Link(new Text(heading));
				link.addParameter(PARAMETER_APPLICATION_PK, application.getPrimaryKey().toString());
				if (application.getOpensInNewWindow()) {
					link.setTarget(Link.TARGET_BLANK_WINDOW);
				}
				li.add(link);
				applicationList.add(li);
			}
		}
		
		boolean first = true;
		Iterator<ListItem> iterator = applicationList.iterator();
		while (iterator.hasNext()) {
			ListItem element = iterator.next();
			if (first) {
				element.setStyleClass("firstChild");
				first = false;
			}
			
			if (!iter.hasNext()) {
				element.setStyleClass("lastChild");
			}
			
			list.add(element);
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
	
	protected CitizenBusiness getUserBusiness(IWContext iwc) {
		try {
			return (CitizenBusiness) IBOLookup.getServiceInstance(iwc, CitizenBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
	
	protected Layer getFormSection(String label, Map<String, List<UIComponent>> formSectionItems) {
		Layer formSection = new Layer();
		formSection.setStyleClass("formSection");
		
		Text heading = new Text(label);
		heading.setStyleClass("formSectionTitle");
		formSection.add(heading);
		
		if (formSectionItems != null) {
			for (Collection<UIComponent> sectionItems: formSectionItems.values()) {
				Layer formItem = new Layer(Layer.DIV);
				formItem.setStyleClass("formItem");
				formSection.add(formItem);
				
				for (UIComponent formSectionItem: sectionItems) {
					formItem.add(formSectionItem);
				}
			}
		}
		
		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		formSection.add(clearLayer);
		return formSection;
	}

	public ApplicationTypesManager getApplicationTypesManager() {
		
		if(applicationTypesManager == null)
			ELUtil.getInstance().autowire(this);
		
		return applicationTypesManager;
	}

	public void setApplicationTypesManager(
			ApplicationTypesManager applicationTypesManager) {
		this.applicationTypesManager = applicationTypesManager;
	}
}