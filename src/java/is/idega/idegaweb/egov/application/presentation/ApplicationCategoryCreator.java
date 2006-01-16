/*
 * $Id: ApplicationCategoryCreator.java,v 1.2 2006/01/16 10:04:13 laddi Exp $ Created on
 * Jan 12, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.presentation;

import is.idega.idegaweb.egov.application.data.ApplicationCategory;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableHeaderRowGroup;
import com.idega.presentation.TableRow;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

public class ApplicationCategoryCreator extends ApplicationBlock {

	protected String getUniqueIdentifier() {
		return "applicationCategoryCreator";
	}

	IWResourceBundle iwrb;

	public void present(IWContext iwc) throws Exception {
		iwrb = super.getResourceBundle(iwc);
		String action = iwc.getParameter("action");
		if ("create".equals(action)) {
			getCategoryCreationForm(iwc, -1);
		}
		else if ("edit".equals(action)) {
			getCategoryCreationForm(iwc, Integer.parseInt(iwc.getParameter("id")));
		}
		else if ("save".equals(action)) {
			String id = iwc.getParameter("id");
			String name = iwc.getParameter("name");
			String desc = iwc.getParameter("desc");
			if (name != null && !name.trim().equals("")) {
				ApplicationCategory cat = null;
				if (id != null) {
					try {
						cat = getApplicationBusiness(iwc).getApplicationCategoryHome().findByPrimaryKey(
								new Integer(iwc.getParameter("id")));
					}
					catch (FinderException f) {
						f.printStackTrace();
					}
				}
				else {
					cat = getApplicationBusiness(iwc).getApplicationCategoryHome().create();
				}
				cat.setName(name);
				cat.setDescription(desc);
				cat.store();
			}
			listExisting(iwc);
		}
		else if ("delete".equals(action)) {
			try {
				ApplicationCategory cat = getApplicationBusiness(iwc).getApplicationCategoryHome().findByPrimaryKey(
						new Integer(iwc.getParameter("id")));
				cat.remove();
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

	/**
	 * <p>
	 * TODO gimmi describe method getCategoryCreationForm
	 * </p>
	 * 
	 * @throws RemoteException
	 */
	private void getCategoryCreationForm(IWContext iwc, int categoryId) throws RemoteException {
		TextInput tName = new TextInput("name");
		TextInput tDesc = new TextInput("desc");
		if (categoryId > 0) {
			try {
				ApplicationCategory cat = getApplicationBusiness(iwc).getApplicationCategoryHome().findByPrimaryKey(
						new Integer(categoryId));
				tName.setContent(cat.getName());
				tDesc.setContent(cat.getDescription());
			}
			catch (FinderException f) {
				f.printStackTrace();
			}
		}
		Form form = new Form();
		Table2 table = new Table2();
		TableHeaderRowGroup headerRow = table.createHeaderRowGroup();
		TableRow hRow = headerRow.createRow();
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("Create a category", "Craete a Category")));
		TableRow row = table.createRow();
		row.createCell().add(getText(iwrb.getLocalizedString("name", "Name")));
		row.createCell().add(tName);
		row = table.createRow();
		row.createCell().add(getText(iwrb.getLocalizedString("description", "Description")));
		row.createCell().add(tDesc);
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
		Link create = new Link(iwrb.getLocalizedString("list_categories", "List Categories"));
		create.addParameter("action", "list");
		add(create);
	}

	private void listExisting(IWContext iwc) throws FinderException, RemoteException {
		Collection categories = getApplicationBusiness(iwc).getApplicationCategoryHome().findAllOrderedByName();
		Table2 table = new Table2();
		TableHeaderRowGroup headerRow = table.createHeaderRowGroup();
		TableRow hRow = headerRow.createRow();
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("category", "Category")));
		hRow.createHeaderCell().add(new Heading1(iwrb.getLocalizedString("description", "Description")));
		Iterator iter = categories.iterator();
		while (iter.hasNext()) {
			ApplicationCategory cat = (ApplicationCategory) iter.next();
			TableRow row = table.createRow();
			row.createCell().add(getText(cat.getName()));
			row.createCell().add(getText(cat.getDescription()));
			Link edit = new Link(getText(iwrb.getLocalizedString("edit", "Edit")));
			edit.addParameter("action", "edit");
			edit.addParameter("id", cat.getPrimaryKey().toString());
			Link delete = new Link(getText(iwrb.getLocalizedString("delete", "Delete")));
			delete.addParameter("action", "delete");
			delete.addParameter("id", cat.getPrimaryKey().toString());
			TableCell2 cell = row.createCell();
			cell.add(edit);
			cell.add(Text.getNonBrakingSpace());
			cell.add(delete);
		}
		add(table);
		Link create = new Link(iwrb.getLocalizedString("new_category", "New Category"));
		create.addParameter("action", "create");
		add(create);
	}

	private Text getText(String content) {
		Text text = new Text(content);
		return text;
	}
}
