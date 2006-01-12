/*
 * $Id: ApplicationCreator.java,v 1.1 2006/01/12 20:02:31 gimmi Exp $ Created on Jan 12,
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
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableHeaderRowGroup;
import com.idega.presentation.TableRow;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.BooleanInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

public class ApplicationCreator extends ApplicationBlock {

	IWResourceBundle iwrb;

	public void main(IWContext iwc) throws Exception {
		iwrb = super.getResourceBundle(iwc);
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
		String ageFrom = iwc.getParameter("ageFrom");
		String ageTo = iwc.getParameter("ageTo");
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
			app.setAgeFrom(Integer.parseInt(ageFrom));
			app.setAgeTo(Integer.parseInt(ageTo));
			if (code != null && !code.equals("-1")) {
				app.setCaseCode(getApplicationBusiness(iwc).getCaseCode(code));
			}
			app.setCategory(getApplicationBusiness(iwc).getApplicationCategoryHome().findByPrimaryKey(new Integer(cat)));
			app.store();
		}
	}

	private void listExisting(IWContext iwc) throws RemoteException, FinderException {
		Collection categories = getApplicationBusiness(iwc).getApplicationHome().findAll();
		Table2 table = new Table2();
		TableHeaderRowGroup headerRow = table.createHeaderRowGroup();
		TableRow hRow = headerRow.createRow();
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("application", "Application")));
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("category", "Category")));
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("case_code", "CaseCode")));
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("age_from", "Age From")));
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("age_to", "Age To")));
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("electronic", "Electronic")));
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("url", "URL")));
		Iterator iter = categories.iterator();
		while (iter.hasNext()) {
			Application app = (Application) iter.next();
			CaseCode code = app.getCaseCode();
			TableRow row = table.createRow();
			row.createCell().add(getText(app.getName()));
			row.createCell().add(getText(app.getCategory().getName()));
			if (code != null) {
				row.createCell().add(getText(code.getCode()));
			}
			else {
				row.createCell().add(getText("-"));
			}
			row.createCell().add(getText(Integer.toString(app.getAgeFrom())));
			row.createCell().add(getText(Integer.toString(app.getAgeTo())));
			row.createCell().add(getText(Boolean.toString(app.getElectronic())));
			row.createCell().add(getText(app.getUrl()));
			Link edit = new Link(getText(iwrb.getLocalizedString("edit", "Edit")));
			edit.addParameter("action", "edit");
			edit.addParameter("id", app.getPrimaryKey().toString());
			Link delete = new Link(getText(iwrb.getLocalizedString("delete", "Delete")));
			delete.addParameter("action", "delete");
			delete.addParameter("id", app.getPrimaryKey().toString());
			TableCell2 cell = row.createCell();
			cell.add(edit);
			cell.add(Text.getNonBrakingSpace());
			cell.add(delete);
		}
		add(table);
		Link create = new Link(iwrb.getLocalizedString("new_application", "New Application"));
		create.addParameter("action", "create");
		add(create);
	}

	private void getApplicationCreationForm(IWContext iwc, int categoryId) throws RemoteException {
		TextInput tName = new TextInput("name");
		TextInput tUrl = new TextInput("url");
		BooleanInput tElec = new BooleanInput("elec");
		TextInput tFrom = new TextInput("ageFrom");
		TextInput tTo = new TextInput("ageTo");
		DropdownMenu tCat = new DropdownMenu("cat");
		DropdownMenu tCode = new DropdownMenu("code");
		try {
			tCat.addMenuElements(getApplicationBusiness(iwc).getApplicationCategoryHome().findAllOrderedByName());
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		tCode.addMenuElementFirst("-1", iwrb.getLocalizedString("no_code", "No Code"));
		tCode.addMenuElements(getApplicationBusiness(iwc).getCaseCodes());
		if (categoryId > 0) {
			try {
				Application app = getApplicationBusiness(iwc).getApplicationHome().findByPrimaryKey(
						new Integer(categoryId));
				tName.setContent(app.getName());
				tUrl.setContent(app.getUrl());
				tElec.setSelected(app.getElectronic());
				tFrom.setContent(Integer.toString(app.getAgeFrom()));
				tTo.setContent(Integer.toString(app.getAgeTo()));
				tCat.setSelectedElement(app.getCategory().getPrimaryKey().toString());
				if (app.getCaseCode() != null) {
					tCode.setSelectedElement(app.getCaseCode().getPrimaryKey().toString());
				}
			}
			catch (FinderException f) {
				f.printStackTrace();
			}
		}
		Form form = new Form();
		Table2 table = new Table2();
		TableHeaderRowGroup headerRow = table.createHeaderRowGroup();
		TableRow hRow = headerRow.createRow();
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("create_an_application", "Create an Application")));
		TableRow row = table.createRow();
		row.createCell().add(getText(iwrb.getLocalizedString("name", "Name")));
		row.createCell().add(tName);
		row = table.createRow();
		row.createCell().add(getText(iwrb.getLocalizedString("category", "Category")));
		row.createCell().add(tCat);
		row = table.createRow();
		row.createCell().add(getText(iwrb.getLocalizedString("case_code", "Case Code")));
		row.createCell().add(tCode);
		row = table.createRow();
		row.createCell().add(getText(iwrb.getLocalizedString("url", "URL")));
		row.createCell().add(tUrl);
		row = table.createRow();
		row.createCell().add(getText(iwrb.getLocalizedString("electronic", "Electronic")));
		row.createCell().add(tElec);
		row = table.createRow();
		row.createCell().add(getText(iwrb.getLocalizedString("age_from", "Age From")));
		row.createCell().add(tFrom);
		row = table.createRow();
		row.createCell().add(getText(iwrb.getLocalizedString("age_to", "Age To")));
		row.createCell().add(tTo);
		row = table.createRow();
		row.createCell();
		TableCell2 cell = row.createCell();
		if (categoryId > 0) {
			cell.add(new HiddenInput("id", Integer.toString(categoryId)));
		}
		cell.setHorizontalAlignment("right");
		cell.add(new SubmitButton(iwrb.getLocalizedString("save", "Save"), "action", "save"));
		form.add(table);
		add(form);
		Link create = new Link(iwrb.getLocalizedString("list_applications", "List Applications"));
		create.addParameter("action", "list");
		add(create);
	}

	private Text getText(String content) {
		Text text = new Text(content);
		return text;
	}
}
