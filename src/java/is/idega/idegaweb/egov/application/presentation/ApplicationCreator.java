/*
 * $Id: ApplicationCreator.java,v 1.5 2006/02/03 13:39:25 laddi Exp $ Created on Jan 12,
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
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableHeaderRowGroup;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
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
			app.setAgeFrom(ageFrom);
			app.setAgeTo(ageTo);
			if (code != null && !code.equals("-1")) {
				app.setCaseCode(getApplicationBusiness(iwc).getCaseCode(code));
			}
			app.setCategory(getApplicationBusiness(iwc).getApplicationCategoryHome().findByPrimaryKey(new Integer(cat)));
			app.store();
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
				row.createCell().add(new Text(code.getCode()));
			}
			else {
				row.createCell().add(new Text("-"));
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

			String URL = app.getUrl();
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
				tFrom.setContent(app.getAgeFrom() > 0 ? Integer.toString(app.getAgeFrom()) : "");
				tTo.setContent(app.getAgeFrom() > 0 ? Integer.toString(app.getAgeTo()) : "");
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
		row.createCell().add(new Text(iwrb.getLocalizedString("name", "Name")));
		row.createCell().add(tName);
		row = table.createRow();
		row.createCell().add(new Text(iwrb.getLocalizedString("category", "Category")));
		row.createCell().add(tCat);
		row = table.createRow();
		row.createCell().add(new Text(iwrb.getLocalizedString("case_code", "Case Code")));
		row.createCell().add(tCode);
		row = table.createRow();
		row.createCell().add(new Text(iwrb.getLocalizedString("url", "URL")));
		row.createCell().add(tUrl);
		row = table.createRow();
		row.createCell().add(new Text(iwrb.getLocalizedString("electronic", "Electronic")));
		row.createCell().add(tElec);
		row = table.createRow();
		row.createCell().add(new Text(iwrb.getLocalizedString("age_from", "Age From")));
		row.createCell().add(tFrom);
		row = table.createRow();
		row.createCell().add(new Text(iwrb.getLocalizedString("age_to", "Age To")));
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

	
	public void setURLLength(int urlLength) {
		this.urlLength = urlLength;
	}
}