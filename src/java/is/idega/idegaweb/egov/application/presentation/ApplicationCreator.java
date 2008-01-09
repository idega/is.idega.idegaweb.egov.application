/*
 * $Id: ApplicationCreator.java,v 1.18 2008/01/09 08:04:59 alexis Exp $ Created on Jan 12,
 * 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.application.data.Application;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.process.data.CaseCode;
import com.idega.block.text.data.LocalizedText;
import com.idega.block.text.data.LocalizedTextHome;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOLookup;
import com.idega.formbuilder.presentation.beans.FormDocument;
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

	public static final String APP_FORM_NAME_PARAM = FormDocument.APP_FORM_NAME_PARAM;
	public static final String APP_ID_PARAM = FormDocument.APP_ID_PARAM;
	public static final String FROM_APP_REQ_PARAM = FormDocument.FROM_APP_REQ_PARAM;
	public static final String FORMBUILDER_REDIRECT_PATH = "/workspace/forms/formbuilder/";
	
	private IWResourceBundle iwrb;
	private IWBundle iwb;
	
	private int urlLength = 50;

	public void present(IWContext iwc) throws Exception {
		
		this.iwrb = super.getResourceBundle(iwc);
		this.iwb = getBundle(iwc);
		
		List<ICLocale> locales = ICLocaleBusiness.listOfLocales();
		
		String action = iwc.getParameter("prm_action");
		
		if ("create".equals(action)) {
			getApplicationCreationForm(iwc, -1, locales);
		}
		else if ("edit".equals(action)) {
			getApplicationCreationForm(iwc, Integer.parseInt(iwc.getParameter("id")), locales);
		}
		else if ("save".equals(action)) {
			saveApplication(iwc, locales);
			listExisting(iwc);
		}
		else if ("delete".equals(action)) {
			try {
				Application app = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(
						new Integer(iwc.getParameter("id")));
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

	private void saveApplication(IWContext iwc, List<ICLocale> locales) throws RemoteException, CreateException, FinderException, IDOAddRelationshipException {
		
		String id = iwc.getParameter("id");
		String name = iwc.getParameter("name");
		String url = iwc.getParameter("url");
		String elec = iwc.getParameter("elec");
		String app_type = iwc.getParameter("appType");
		String requiresLogin = iwc.getParameter("reqLogin");
		String visible = iwc.getParameter("visible");
		int ageFrom = iwc.isParameterSet("ageFrom") ? Integer.parseInt(iwc.getParameter("ageFrom")) : -1;
		int ageTo = iwc.isParameterSet("ageTo") ? Integer.parseInt(iwc.getParameter("ageTo")) : -1;
		String cat = iwc.getParameter("cat");
		String code = iwc.getParameter("code");
		String opensInNew = iwc.getParameter("newin");
		String hiddenFromGuests = iwc.getParameter("hidden");
		if (name != null && !name.trim().equals("")) {
			Application app = null;
			if (id != null) {
				try {
					app = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(
							new Integer(id));
				}
				catch (FinderException f) {
					f.printStackTrace();
				}
			}
			else {
				app = getApplicationBusiness(iwc).getApplicationHome().create();
			}
			app.setName(name);
			app.setUrl(url);
			app.setElectronic("Y".equalsIgnoreCase(elec));
			
			Integer at = 0;
			
			if(app_type != null) {
				
				try {
					at = Integer.parseInt(app_type);
					
					if(at == FORMBUILDER_TYPE)
						app.setElectronic(true);
					
					app.setAppType(at);
					
				} catch (Exception e) {
					// TODO: use logger
					e.printStackTrace();
				}
			}
			
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

			IWCacheManager.getInstance(iwc.getIWMainApplication()).invalidateCache(ApplicationCategoryViewer.CACHE_KEY);
			IWCacheManager.getInstance(iwc.getIWMainApplication()).invalidateCache(ApplicationFavorites.CACHE_KEY);
			
//			id == null means it's new app
			if(id == null && at == FORMBUILDER_TYPE && name != null && !name.equals("")) {
				
				try {
					
					iwc.setSessionAttribute(APP_FORM_NAME_PARAM, name);
					iwc.setSessionAttribute(APP_ID_PARAM, String.valueOf(app.getPrimaryKey()));
					iwc.getResponse().sendRedirect(
							new StringBuilder(FORMBUILDER_REDIRECT_PATH)
							.append("?")
							.append(FROM_APP_REQ_PARAM)
							.append("=1&encParams=1")
							.toString()
					);
					
				} catch (IOException e) {
//					TODO: use logger
					System.out.println("probably some old component was used? and redirect was called when component was actually already been started rendering");
					e.printStackTrace();
				}
			}
			
			for(Iterator<ICLocale> it = locales.iterator(); it.hasNext(); ) {
				ICLocale locale = it.next();
				String locName = iwc.getParameter(locale.getName() + "_locale");
				
				if(locName != null && !locName.equals("")) {
					LocalizedText locText = app.getLocalizedText(locale.getLocaleID());
					boolean newText = false;
					if(locText == null) {
						locText = getLocalizedTextHome().create();
						newText = true;
					}
					
					locText.setLocaleId(locale.getLocaleID());
					locText.setBody(locName);
					
					locText.store();
					
					if(newText) {
						app.addLocalizedName(locText);
						
						
					}
					
				}
			}
		}
		
	}
	
	protected LocalizedTextHome getLocalizedTextHome() throws RemoteException {
		return (LocalizedTextHome) IDOLookup.getHome(LocalizedText.class);
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
		cell.setStyleClass("appType");
		cell.add(new Text(this.iwrb.getLocalizedString("appType", "Application type")));
		
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
			cell.add(new Text(app.getName()));

			cell = row.createCell();
			cell.setStyleClass("category");
			cell.add(new Text(app.getCategory().getName()));

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
			cell.setStyleClass("appType");
			
			Integer app_type = app.getAppType();
			
			if(app_type != null)
				cell.add(new Text(getAppTypesIdToNameMappings().get(app_type)));
			else
				cell.add(new Text(""));
			
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
	
	private DropdownMenu getAppTypesMenu() {
		
		DropdownMenu menu = new DropdownMenu("appType");
		SortedMap<Integer, String> mappings = getAppTypesIdToNameMappings();
		
		for (Integer app_type_id : mappings.keySet())
			menu.addMenuElement(app_type_id, mappings.get(app_type_id));
		
		return menu;
	}
	
	private static final int FORMBUILDER_TYPE = 1;
	
	private SortedMap<Integer, String> getAppTypesIdToNameMappings() {
		
//		TODO: discard hardcoding and make it configurable - use ConfigFactory and Config from core
		SortedMap<Integer, String> mappings = new TreeMap<Integer, String>();
		mappings.put(FORMBUILDER_TYPE, iwrb.getLocalizedString("app_type.formbuilder", "Formbuilder"));
		mappings.put(2, iwrb.getLocalizedString("app_type.url", "Url"));
		
		return mappings;
	}

	private void getApplicationCreationForm(IWContext iwc, int applicationID, List<ICLocale> locales) throws RemoteException {
		
		Form form = new Form();
		form.setID("applicationCreator");
		form.setStyleClass("adminForm");
		
		TextInput name = new TextInput("name");
		TextInput url = new TextInput("url");
		BooleanInput electronic = new BooleanInput("elec");
		DropdownMenu app_types = getAppTypesMenu();
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
		Application application = null;

		if (applicationID > 0) {
			
			try {
				application = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(new Integer(applicationID));
				name.setContent(application.getName());
				url.setContent(application.getUrl());
				electronic.setSelected(application.getElectronic());
				Integer temp = application.getAppType();
				if(application.getAppType() != null) {
					app_types.setSelectedElement(application.getAppType());
				}
				
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
		
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("formSection");
		form.add(layer);
		
		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label(this.iwrb.getLocalizedString("default_name", "Default name"), name);
		formItem.add(label);
		formItem.add(name);
		layer.add(formItem);

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
		label = new Label(this.iwrb.getLocalizedString("app_type", "Application type"), app_types);
		formItem.add(label);
		formItem.add(app_types);
		layer.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("electronic", "Electronic"), electronic);
		formItem.add(label);
		formItem.add(electronic);
		layer.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("url", "url"), url);
		formItem.add(label);
		formItem.add(url);
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
		
		layer = new Layer(Layer.DIV);
		layer.setStyleClass("formSection");
		form.add(layer);
		
		Text heading = new Text("Localized name");
		heading.setStyleClass("formSectionTitle");
		layer.add(heading);
		
		for(Iterator<ICLocale> it = locales.iterator(); it.hasNext(); ) {
			ICLocale locale = it.next();
			Locale javaLocale = ICLocaleBusiness.getLocaleFromLocaleString(locale.getLocale());
				
			TextInput locInput = new TextInput(locale.getName() + "_locale");
				
			if(application != null) {
				LocalizedText text = application.getLocalizedText(locale.getLocaleID());
				locInput.setValue(text == null ? "" : text.getBody());
			}
				
			formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			label = new Label(javaLocale.getDisplayLanguage(), locInput);
			formItem.add(label);
			formItem.add(locInput);
			layer.add(formItem);
		}
			
		clearLayer = new Layer(Layer.DIV);
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