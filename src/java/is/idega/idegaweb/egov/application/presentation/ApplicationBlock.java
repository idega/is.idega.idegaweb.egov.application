/*
 * $Id: ApplicationBlock.java,v 1.25 2008/11/27 02:41:43 laddi Exp $ Created on Jan 12,
 * 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

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
import com.idega.user.data.User;
import com.idega.util.Age;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.egov.accounting.business.CitizenBusiness;
import is.idega.idegaweb.egov.application.ApplicationConstants;
import is.idega.idegaweb.egov.application.business.ApplicationBusiness;
import is.idega.idegaweb.egov.application.business.ApplicationTypesManager;
import is.idega.idegaweb.egov.application.model.ApplicationModel;

public abstract class ApplicationBlock extends Block {

	@Autowired private ApplicationTypesManager applicationTypesManager;

	public static final String BUNDLE_IDENTIFIER = "is.idega.idegaweb.egov.application";
	public static final String PARAMETER_APPLICATION_PK = "prm_app_application_pk";
	public static final String PARAMETER_IDENTIFIER_NAME = "prm_app_identifier_name";
	public static final String ATTRIBUTE_USE_AGE_RESTRICTION = "application_use_age_restriction";

	@Override
	public String getBundleIdentifier() {
		return BUNDLE_IDENTIFIER;
	}

	@Override
	public void main(IWContext iwc) throws Exception {
		PresentationUtil.addStyleSheetToHeader(iwc, iwc.getIWMainApplication().getBundle(ApplicationConstants.IW_BUNDLE_IDENTIFIER).getVirtualPathWithFileNameString("style/application.css"));
		present(iwc);
	}

	protected boolean useAgeRestriction(IWContext iwc) {
		return new Boolean(iwc.getApplicationSettings().getProperty(ATTRIBUTE_USE_AGE_RESTRICTION, Boolean.TRUE.toString())).booleanValue();
	}

	protected abstract void present(IWContext iwc) throws Exception;

	protected <T extends ApplicationModel> Lists getApplicationList(IWContext iwc, boolean checkAges, Collection<T> applications, Age[] ages) throws RemoteException {
		Lists list = new Lists();

		if (ListUtil.isEmpty(applications)) {
			return list;
		}

		Collection<ListItem> applicationList = new ArrayList<ListItem>(applications.size());
		Iterator<T> iter = applications.iterator();

		boolean isLogged = iwc.isLoggedOn();
		User currentUser = isLogged ? iwc.getCurrentUser() : null;

		boolean showDisabled = iwc.getIWMainApplication().getSettings().getBoolean("app.show_disabled_applications", Boolean.TRUE);

		for (T app: applications) {
			if (app == null) {
				continue;
			}

			boolean displayApplication = true;
			try {
				displayApplication = (checkAges && getApplicationBusiness(iwc).displayApplicationForAges(app, ages));
			} catch (RemoteException re) {
				throw new IBORuntimeException(re);
			}

			boolean isVisibile = app.getVisible() &&
						(app.getAppType() == null ||
						(app.getAppType() != null && getApplicationTypesManager().getApplicationType(app.getAppType()).isVisible(app)));

			if (!app.isEnabled()) {
				isVisibile = isVisibile || showDisabled;
			}

			if (isVisibile &&
					(!checkAges || displayApplication) &&
					!(isLogged && app.getHiddenFromGuests() && getUserBusiness(iwc).hasGuestAccount(currentUser))
			) {
				ListItem li = new ListItem();
				if (app.getElectronic()) {
					li.setStyleClass("electronic");
				}
				if (app.getCaseCode() != null) {
					li.setStyleClass("caseConnected");
				}
				if (app.getRequiresLogin()) {
					li.setStyleClass("requiresLogin");
				}
				if (app.getElectronic() && !app.getRequiresLogin()) {
					li.setStyleClass("electronic-does-not-require-login");
				}

				int icLocaleId = iwc.getCurrentLocaleId();

				LocalizedText locText = app.getLocalizedText(icLocaleId);
				String heading = null;
				if(locText != null) {
					heading = locText.getBody();
				} else {
					heading = app.getName();
				}

				Link link = new Link(new Text(heading));
				link.addParameter(PARAMETER_APPLICATION_PK, app.getPrimaryKey().toString());
				if (app.getOpensInNewWindow()) {
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
			return IBOLookup.getServiceInstance(iwc, ApplicationBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	protected CitizenBusiness getUserBusiness(IWContext iwc) {
		try {
			return IBOLookup.getServiceInstance(iwc, CitizenBusiness.class);
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