/*
 * $Id: ApplicationCreator.java,v 1.10 2006/02/16 14:58:45 laddi Exp $ Created on Jan 12,
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
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.block.process.data.CaseCode;
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

	public void present(IWContext iwc) throws Exception {
		iwrb = super.getResourceBundle(iwc);
		iwb = getBundle(iwc);
		
		String action = iwc.getParameter("action");
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
		String id = iwc.getParameter("id");
		String name = iwc.getParameter("name");
		String url = iwc.getParameter("url");
		String elec = iwc.getParameter("elec");
		String visible = iwc.getParameter("visible");
		int ageFrom = iwc.isParameterSet("ageFrom") ? Integer.parseInt(iwc.getParameter("ageFrom")) : -1;
		int ageTo = iwc.isParameterSet("ageTo") ? Integer.parseInt(iwc.getParameter("ageTo")) : -1;
		String cat = iwc.getParameter("cat");
		String code = iwc.getParameter("code");
		if (name != null && !name.trim().equals("")) {
			Application app = null;
			if (id != null) {
				try {
					app = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(
							new Integer(iwc.getParameter("id")));
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
			app.setVisible("Y".equalsIgnoreCase(visible));
			app.setAgeFrom(ageFrom);
			app.setAgeTo(ageTo);
			if (code != null && !code.equals("-1")) {
				app.setCaseCode(getApplicationBusiness(iwc).getCaseCode(code));
			}
			app.setCategory(getApplicationBusiness(iwc).getApplicationCategoryHome().findByPrimaryKey(new Integer(cat)));
			app.store();

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
		cell.add(new Text(iwrb.getLocalizedString("application", "Application")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("category");
		cell.add(new Text(iwrb.getLocalizedString("category", "Category")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("caseCode");
		cell.add(new Text(iwrb.getLocalizedString("case_code", "CaseCode")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("ageFrom");
		cell.add(new Text(iwrb.getLocalizedString("age_from", "Age From")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("ageTo");
		cell.add(new Text(iwrb.getLocalizedString("age_to", "Age To")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("electronic");
		cell.add(new Text(iwrb.getLocalizedString("electronic", "Electronic")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("url");
		cell.add(new Text(iwrb.getLocalizedString("url", "URL")));
		
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
			
			Link edit = new Link(iwb.getImage("edit.png", iwrb.getLocalizedString("edit", "Edit")));
			edit.addParameter("action", "edit");
			edit.addParameter("id", app.getPrimaryKey().toString());
			
			Link delete = new Link(iwb.getImage("delete.png", iwrb.getLocalizedString("remove", "Remove")));
			delete.addParameter("action", "delete");
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
			cell.setStyleClass("electronic");
			cell.add(new Text(iwrb.getLocalizedString(Boolean.toString(app.getElectronic()), Boolean.toString(app.getElectronic()))));

			String URL = app.getUrl() != null ? app.getUrl() : "";
			if (URL.length() > urlLength) {
				URL = URL.substring(0, urlLength) + "...";
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
		
		SubmitButton newLink = new SubmitButton(iwrb.getLocalizedString("new_application", "New Application"), "action", "create");
		buttonLayer.add(newLink);
		
		add(form);
	}

	private void getApplicationCreationForm(IWContext iwc, int applicationID) throws RemoteException {
		Form form = new Form();
		form.setID("applicationCreator");
		form.setStyleClass("adminForm");
		
		TextInput name = new TextInput("name");
		TextInput url = new TextInput("url");
		BooleanInput electronic = new BooleanInput("elec");
		BooleanInput visible = new BooleanInput("visible");
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
		caseCode.addMenuElementFirst("-1", iwrb.getLocalizedString("no_code", "No Code"));
		Collection caseCodes = getApplicationBusiness(iwc).getCaseCodes();
		Iterator iter = caseCodes.iterator();
		while (iter.hasNext()) {
			CaseCode code = (CaseCode) iter.next();
			caseCode.addMenuElement(code.getPrimaryKey().toString(), code.getDescriptionLocalizedKey() != null ? iwrb.getLocalizedString(code.getDescriptionLocalizedKey(), code.getDescription()) : code.getDescription());
		}

		if (applicationID > 0) {
			try {
				Application application = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(
						new Integer(applicationID));
				name.setContent(application.getName());
				url.setContent(application.getUrl());
				electronic.setSelected(application.getElectronic());
				visible.setSelected(application.getVisible());
				ageFrom.setContent(application.getAgeFrom() > -1 ? Integer.toString(application.getAgeFrom()) : "");
				ageTo.setContent(application.getAgeFrom() > -1 ? Integer.toString(application.getAgeTo()) : "");
				category.setSelectedElement(application.getCategory().getPrimaryKey().toString());
				if (application.getCaseCode() != null) {
					caseCode.setSelectedElement(application.getCaseCode().getPrimaryKey().toString());
				}
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
		Label label = new Label(iwrb.getLocalizedString("name", "Name"), name);
		formItem.add(label);
		formItem.add(name);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(iwrb.getLocalizedString("category", "category"), category);
		formItem.add(label);
		formItem.add(category);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(iwrb.getLocalizedString("case_code", "Case code"), caseCode);
		formItem.add(label);
		formItem.add(caseCode);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(iwrb.getLocalizedString("url", "url"), url);
		formItem.add(label);
		formItem.add(url);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(iwrb.getLocalizedString("electronic", "Electronic"), electronic);
		formItem.add(label);
		formItem.add(electronic);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(iwrb.getLocalizedString("visible", "Visible"), visible);
		formItem.add(label);
		formItem.add(visible);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(iwrb.getLocalizedString("age_from", "Age from"), ageFrom);
		formItem.add(label);
		formItem.add(ageFrom);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(iwrb.getLocalizedString("age_to", "Age to"), ageTo);
		formItem.add(label);
		formItem.add(ageTo);
		layer.add(formItem);

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		
		layer.add(clearLayer);
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);
		
		SubmitButton back = new SubmitButton(iwrb.getLocalizedString("back", "Back"), "action", "list");
		buttonLayer.add(back);
		
		SubmitButton save = new SubmitButton(iwrb.getLocalizedString("save", "Save"), "action", "save");
		buttonLayer.add(save);

		add(form);
	}

	
	public void setURLLength(int urlLength) {
		this.urlLength = urlLength;
	}
}