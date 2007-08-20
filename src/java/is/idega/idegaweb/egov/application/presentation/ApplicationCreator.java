/*
 * $Id: ApplicationCreator.java,v 1.15.2.2 2007/08/20 14:43:58 justinas Exp $ Created on Jan 12,
 * 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.application.data.Application;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.process.data.CaseCode;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.BooleanInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

public class ApplicationCreator extends ApplicationBlock {

	private IWResourceBundle iwrb;
	private IWBundle iwb;
	
	private int urlLength = 50;

	private static final String LOCALIZED_NAME_PREFIX = "localizedName";
	private static final String LOCALIZED_URL_PREFIX = "localizedUrl";
	
	public void present(IWContext iwc) throws Exception {
		this.iwrb = super.getResourceBundle(iwc);
		this.iwb = getBundle(iwc);
		
		String action = iwc.getParameter("prm_action");
		if ("create".equals(action)) {
			getApplicationCreationForm(iwc, -1);
		}
		else if ("edit".equals(action)) {
			getApplicationCreationForm(iwc, Integer.parseInt(iwc.getParameter("id")));
		}
		else if ("save".equals(action)) {
			saveApplication(iwc);
			listExisting(iwc);
		}
		else if ("delete".equals(action)) {
			try {
				Application app = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(
						new Integer(iwc.getParameter("id")));
				app.removeLocalizedEntries();
				app.remove();
			}
			catch (FinderException f) {
				f.printStackTrace();
			}
			listExisting(iwc);
		}
		else if ("list".equals(action)) {
			listExisting(iwc);
		}
		else {
			listExisting(iwc);
		}
	}

	private void saveApplication(IWContext iwc) throws RemoteException, CreateException, FinderException {
		
		boolean isNewApplication = false;
		
		String id = iwc.getParameter("id");
		String name = iwc.getParameter("name");
		String url = iwc.getParameter("url");
		String elec = iwc.getParameter("elec");
		String requiresLogin = iwc.getParameter("reqLogin");
		String visible = iwc.getParameter("visible");
		int ageFrom = iwc.isParameterSet("ageFrom") ? Integer.parseInt(iwc.getParameter("ageFrom")) : -1;
		int ageTo = iwc.isParameterSet("ageTo") ? Integer.parseInt(iwc.getParameter("ageTo")) : -1;
		String cat = iwc.getParameter("cat");
		String code = iwc.getParameter("code");
		String opensInNew = iwc.getParameter("newin");
		String hiddenFromGuests = iwc.getParameter("hidden");
		
//		Enumeration parameterNames = iwc.getParameterNames();
		Map localizedNames = new HashMap();
		Map localizedUrls = new HashMap();
		
		List localesInUse = ICLocaleBusiness.listOfLocales(true);
		
		for (int i = 0; i < localesInUse.size(); i++){
//			Locale loc = ((ICLocale)localesInUse.get(i)).getLocaleObject();
//			String loc = ((ICLocale)localesInUse.get(i)).getLocale();
			ICLocale loc = (ICLocale)localesInUse.get(i);
System.out.println(loc+" "+iwc.getParameter(LOCALIZED_NAME_PREFIX+loc));
			localizedNames.put(loc, iwc.getParameter(LOCALIZED_NAME_PREFIX+loc));
			localizedUrls.put(loc, iwc.getParameter(LOCALIZED_URL_PREFIX+loc));
			
		}
		
		if (name != null && !name.trim().equals("")) {
			Application app = null;
			if (id != null) {
				try {
					isNewApplication = false;
					app = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(
							new Integer(iwc.getParameter("id")));
				}
				catch (FinderException f) {
					f.printStackTrace();
				}
			}
			else {
				isNewApplication = true;
				app = getApplicationBusiness(iwc).getApplicationHome().create();
			}
			app.setName(name);
			app.setUrl(url);
			app.setElectronic("Y".equalsIgnoreCase(elec));
			app.setRequiresLogin("Y".equalsIgnoreCase(requiresLogin));
			app.setVisible("Y".equalsIgnoreCase(visible));
			app.setOpensInNewWindow("Y".equalsIgnoreCase(opensInNew));
			app.setHiddenFromGuests("Y".equalsIgnoreCase(hiddenFromGuests));
			app.setAgeFrom(ageFrom);
			app.setAgeTo(ageTo);
			if (code != null && !code.equals("-1")) {
				app.setCaseCode(getApplicationBusiness(iwc).getCaseCode(code));
			}
			app.setCategory(getApplicationBusiness(iwc).getApplicationCategoryHome().findByPrimaryKey(new Integer(cat)));
			app.store();
			app.setLocalizedNames(localizedNames, isNewApplication);
			app.setLocalizedUrls(localizedUrls, isNewApplication);

			IWCacheManager.getInstance(iwc.getIWMainApplication()).invalidateCache(ApplicationCategoryViewer.CACHE_KEY);
			IWCacheManager.getInstance(iwc.getIWMainApplication()).invalidateCache(ApplicationFavorites.CACHE_KEY);
		}
	}

	private void listExisting(IWContext iwc) throws RemoteException, FinderException {
		Collection applications = getApplicationBusiness(iwc).getApplicationHome().findAll();

		Form form = new Form();
		form.setID("applicationCreator");
		form.setStyleClass("adminForm");
		
		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass("ruler");
		table.setStyleClass("adminTable");
		form.add(table);
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.setStyleClass("application");
		cell.add(new Text(this.iwrb.getLocalizedString("application", "Application")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("category");
		cell.add(new Text(this.iwrb.getLocalizedString("category", "Category")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("caseCode");
		cell.add(new Text(this.iwrb.getLocalizedString("case_code", "CaseCode")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("ageFrom");
		cell.add(new Text(this.iwrb.getLocalizedString("age_from", "Age From")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("ageTo");
		cell.add(new Text(this.iwrb.getLocalizedString("age_to", "Age To")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("electronic");
		cell.add(new Text(this.iwrb.getLocalizedString("electronic", "Electronic")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("requiresLogin");
		cell.add(new Text(this.iwrb.getLocalizedString("requires_login", "Requires login")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("new_window");
		cell.add(new Text(this.iwrb.getLocalizedString("new_window", "New window")));

		cell = row.createHeaderCell();
		cell.setStyleClass("url");
		cell.add(new Text(this.iwrb.getLocalizedString("url", "URL")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("edit");
		cell.add(Text.getNonBrakingSpace());

		cell = row.createHeaderCell();
		cell.setStyleClass("remove");
		cell.setStyleClass("lastColumn");
		cell.add(Text.getNonBrakingSpace());

		group = table.createBodyRowGroup();
		int iRow = 1;
		
		Iterator iter = applications.iterator();
		while (iter.hasNext()) {
			Application app = (Application) iter.next();
			CaseCode code = app.getCaseCode();
			
			row = table.createRow();
			
			Link edit = new Link(this.iwb.getImage("edit.png", this.iwrb.getLocalizedString("edit", "Edit")));
			edit.addParameter("prm_action", "edit");
			edit.addParameter("id", app.getPrimaryKey().toString());
			
			Link delete = new Link(this.iwb.getImage("delete.png", this.iwrb.getLocalizedString("remove", "Remove")));
			delete.addParameter("prm_action", "delete");
			delete.addParameter("id", app.getPrimaryKey().toString());

			if (iRow % 2 == 0) {
				row.setStyleClass("evenRow");
			}
			else {
				row.setStyleClass("oddRow");
			}

			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.setStyleClass("application");
//			cell.add(new Text(app.getName()));
			cell.add(new Text(app.getNameByLocale()));
			
			cell = row.createCell();
			cell.setStyleClass("category");
//			cell.add(new Text(app.getCategory().getName()));
			cell.add(new Text(app.getCategory().getLocalizedName()));

			cell = row.createCell();
			cell.setStyleClass("caseCode");
			if (code != null) {
				cell.add(new Text(code.getCode()));
			}
			else {
				cell.add(new Text("-"));
			}

			cell = row.createCell();
			cell.setStyleClass("ageFrom");
			cell.add(new Text(app.getAgeFrom() > -1 ? Integer.toString(app.getAgeFrom()) : "-"));

			cell = row.createCell();
			cell.setStyleClass("ageTo");
			cell.add(new Text(app.getAgeTo() > -1 ? Integer.toString(app.getAgeTo()) : "-"));

			cell = row.createCell();
			cell.setStyleClass("electronic");
			cell.add(new Text(this.iwrb.getLocalizedString(Boolean.toString(app.getElectronic()), Boolean.toString(app.getElectronic()))));

			cell = row.createCell();
			cell.setStyleClass("requiresLogin");
			cell.add(new Text(this.iwrb.getLocalizedString(Boolean.toString(app.getRequiresLogin()), Boolean.toString(app.getRequiresLogin()))));

			cell = row.createCell();
			cell.setStyleClass("new_window");
			cell.add(new Text(this.iwrb.getLocalizedString(Boolean.toString(app.getOpensInNewWindow()), Boolean.toString(app.getOpensInNewWindow()))));

			
			String URL = app.getUrl() != null ? app.getUrl() : "";
			if (URL.length() > this.urlLength) {
				URL = URL.substring(0, this.urlLength) + "...";
			}
			cell = row.createCell();
			cell.setStyleClass("url");
			cell.add(new Text(URL));

			cell = row.createCell();
			cell.setStyleClass("edit");
			cell.add(edit);

			cell = row.createCell();
			cell.setStyleClass("lastColumn");
			cell.setStyleClass("remove");
			cell.add(delete);

			iRow++;
		}

		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);
		
		SubmitButton newLink = new SubmitButton(this.iwrb.getLocalizedString("new_application", "New Application"), "prm_action", "create");
		buttonLayer.add(newLink);
		
		add(form);
	}

	private void getApplicationCreationForm(IWContext iwc, int applicationID) throws RemoteException {
		
		List localesInUse = ICLocaleBusiness.listOfLocales(true);
		
		List localizedNames = new ArrayList();
		List localizedUrls = new ArrayList();
		
		Form form = new Form();
		form.setID("applicationCreator");
		form.setStyleClass("adminForm");
		
		TextInput name = new TextInput("name");
		
		TextInput url = new TextInput("url");
		BooleanInput electronic = new BooleanInput("elec");
		BooleanInput requiresLogin = new BooleanInput("reqLogin");
		BooleanInput visible = new BooleanInput("visible");
		BooleanInput newin = new BooleanInput("newin");
		BooleanInput hidden = new BooleanInput("hidden");
		TextInput ageFrom = new TextInput("ageFrom");
		TextInput ageTo = new TextInput("ageTo");

		DropdownMenu category = new DropdownMenu("cat");
		try {
			category.addMenuElements(getApplicationBusiness(iwc).getApplicationCategoryHome().findAllOrderedByName());
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		DropdownMenu caseCode = new DropdownMenu("code");
		caseCode.addMenuElementFirst("-1", this.iwrb.getLocalizedString("no_code", "No Code"));
		Collection caseCodes = getApplicationBusiness(iwc).getCaseCodes();
		Iterator iter = caseCodes.iterator();
		while (iter.hasNext()) {
			CaseCode code = (CaseCode) iter.next();
			caseCode.addMenuElement(code.getPrimaryKey().toString(), code.getDescriptionLocalizedKey() != null ? this.iwrb.getLocalizedString(code.getDescriptionLocalizedKey(), code.getDescription()) : code.getDescription());
		}

		if (applicationID > 0) {
			try {
				Application application = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(
						new Integer(applicationID));
				name.setContent(application.getName());
				for (int i = 0; i < localesInUse.size(); i++) {
					TextInput localizedName = new TextInput(LOCALIZED_NAME_PREFIX+localesInUse.get(i).toString());
//					localizedName.setContent(application.getNameByLocale(((ICLocale)localesInUse.get(i)).getLocaleObject()));
					localizedName.setContent(application.getLocalizedName(((ICLocale)localesInUse.get(i)).getLocaleObject()));
					localizedNames.add(localizedName);
					
					TextInput localizedUrl = new TextInput(LOCALIZED_URL_PREFIX+localesInUse.get(i).toString());
//					localizedUrl.setContent(application.getUrlByLocale(((ICLocale)localesInUse.get(i)).getLocaleObject()));
					localizedUrl.setContent(application.getLocalizedUrl(((ICLocale)localesInUse.get(i)).getLocaleObject()));
					localizedUrls.add(localizedUrl);					
				}
				url.setContent(application.getUrl());
				electronic.setSelected(application.getElectronic());
				requiresLogin.setSelected(application.getRequiresLogin());
				visible.setSelected(application.getVisible());
				ageFrom.setContent(application.getAgeFrom() > -1 ? Integer.toString(application.getAgeFrom()) : "");
				ageTo.setContent(application.getAgeFrom() > -1 ? Integer.toString(application.getAgeTo()) : "");
				category.setSelectedElement(application.getCategory().getPrimaryKey().toString());
				if (application.getCaseCode() != null) {
					caseCode.setSelectedElement(application.getCaseCode().getPrimaryKey().toString());
				}
				newin.setSelected(application.getOpensInNewWindow());
				form.add(new HiddenInput("id", Integer.toString(applicationID)));
			}
			catch (FinderException f) {
				f.printStackTrace();
			}
		}
		else{
			for (int i = 0; i < localesInUse.size(); i++) {
				TextInput localizedName = new TextInput(LOCALIZED_NAME_PREFIX+localesInUse.get(i).toString());
				localizedNames.add(localizedName);
				TextInput localizedUrl = new TextInput(LOCALIZED_URL_PREFIX+localesInUse.get(i).toString());
				localizedUrls.add(localizedUrl);
			}
		}

		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("formSection");
		form.add(layer);
		
		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label(this.iwrb.getLocalizedString("name", "Name"), name);
		formItem.add(label);
		formItem.add(name);
		layer.add(formItem);

		for (int i = 0; i < localizedNames.size(); i++) {
			formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			label = new Label(this.iwrb.getLocalizedString("name", "Name")+"("+localesInUse.get(i).toString()+")", name);
			formItem.add(label);
			formItem.add(localizedNames.get(i));
			layer.add(formItem);			
		}
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("category", "category"), category);
		formItem.add(label);
		formItem.add(category);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("case_code", "Case code"), caseCode);
		formItem.add(label);
		formItem.add(caseCode);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("url", "url"), url);
		formItem.add(label);
		formItem.add(url);
		layer.add(formItem);

		for (int i = 0; i < localizedUrls.size(); i++) {
			formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			label = new Label(this.iwrb.getLocalizedString("url", "Url")+"("+localesInUse.get(i).toString()+")", name);
			formItem.add(label);
			formItem.add(localizedUrls.get(i));
			layer.add(formItem);			
		}
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("electronic", "Electronic"), electronic);
		formItem.add(label);
		formItem.add(electronic);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("requires_login", "Requires login"), requiresLogin);
		formItem.add(label);
		formItem.add(requiresLogin);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("visible", "Visible"), visible);
		formItem.add(label);
		formItem.add(visible);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("opens_in_new_window", "Opens in a new window"), newin);
		formItem.add(label);
		formItem.add(newin);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("hidden_from_guests", "Hidden from guests"), hidden);
		formItem.add(label);
		formItem.add(hidden);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("age_from", "Age from"), ageFrom);
		formItem.add(label);
		formItem.add(ageFrom);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("age_to", "Age to"), ageTo);
		formItem.add(label);
		formItem.add(ageTo);
		layer.add(formItem);

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		
		layer.add(clearLayer);
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);
		
		SubmitButton back = new SubmitButton(this.iwrb.getLocalizedString("back", "Back"), "prm_action", "list");
		buttonLayer.add(back);
		
		SubmitButton save = new SubmitButton(this.iwrb.getLocalizedString("save", "Save"), "prm_action", "save");
		buttonLayer.add(save);

		add(form);
	}

	
	public void setURLLength(int urlLength) {
		this.urlLength = urlLength;
	}
}