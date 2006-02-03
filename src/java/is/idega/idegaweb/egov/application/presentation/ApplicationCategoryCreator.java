/*
 * $Id: ApplicationCategoryCreator.java,v 1.3 2006/02/03 13:39:25 laddi Exp $ Created on
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
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

public class ApplicationCategoryCreator extends ApplicationBlock {

	private IWResourceBundle iwrb;
	private IWBundle iwb;

	public void present(IWContext iwc) throws Exception {
		iwrb = getResourceBundle(iwc);
		iwb = getBundle(iwc);
		
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
		Form form = new Form();
		form.setID("applicationCategoryCreator");
		form.setStyleClass("adminForm");
		form.addParameter("action", iwc.getParameter("action"));
		
		TextInput tName = new TextInput("name");
		TextInput tDesc = new TextInput("desc");
		if (categoryId > 0) {
			try {
				ApplicationCategory cat = getApplicationBusiness(iwc).getApplicationCategoryHome().findByPrimaryKey(
						new Integer(categoryId));
				tName.setContent(cat.getName());
				tDesc.setContent(cat.getDescription());
				form.add(new HiddenInput("id", Integer.toString(categoryId)));
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
		Label label = new Label(iwrb.getLocalizedString("name", "Name"), tName);
		formItem.add(label);
		formItem.add(tName);
		layer.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(iwrb.getLocalizedString("description", "Description"), tDesc);
		formItem.add(label);
		formItem.add(tDesc);
		layer.add(formItem);

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		
		layer.add(clearLayer);
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonLayer");
		form.add(buttonLayer);
		
		SubmitButton back = new SubmitButton(iwrb.getLocalizedString("back", "Back"));
		back.setValueOnClick("action", "list");
		buttonLayer.add(back);
		
		SubmitButton save = new SubmitButton(iwrb.getLocalizedString("save", "Save"));
		save.setValueOnClick("action", "save");
		buttonLayer.add(save);

		add(form);
	}

	private void listExisting(IWContext iwc) throws FinderException, RemoteException {
		Collection categories = getApplicationBusiness(iwc).getApplicationCategoryHome().findAllOrderedByName();
		
		Form form = new Form();
		form.setID("applicationCategoryCreator");
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
		cell.setStyleClass("category");
		cell.add(new Text(iwrb.getLocalizedString("category", "Category")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("description");
		cell.add(new Text(iwrb.getLocalizedString("description", "Description")));
		
		cell = row.createHeaderCell();
		cell.setStyleClass("edit");
		cell.add(Text.getNonBrakingSpace());

		cell = row.createHeaderCell();
		cell.setStyleClass("remove");
		cell.setStyleClass("lastColumn");
		cell.add(Text.getNonBrakingSpace());

		group = table.createBodyRowGroup();
		int iRow = 1;
		
		Iterator iter = categories.iterator();
		while (iter.hasNext()) {
			ApplicationCategory cat = (ApplicationCategory) iter.next();
			row = table.createRow();
			
			Link edit = new Link(iwb.getImage("edit.png", iwrb.getLocalizedString("edit", "Edit")));
			edit.addParameter("action", "edit");
			edit.addParameter("id", cat.getPrimaryKey().toString());
			
			Link delete = new Link(iwb.getImage("delete.png", iwrb.getLocalizedString("remove", "Remove")));
			delete.addParameter("action", "delete");
			delete.addParameter("id", cat.getPrimaryKey().toString());

			if (iRow % 2 == 0) {
				row.setStyleClass("evenRow");
			}
			else {
				row.setStyleClass("oddRow");
			}

			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.setStyleClass("category");
			cell.add(new Text(cat.getName()));

			cell = row.createCell();
			cell.setStyleClass("description");
			cell.add(new Text(cat.getDescription() != null ? cat.getDescription() : ""));

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
		
		SubmitButton newLink = new SubmitButton(iwrb.getLocalizedString("new_category", "New Category"), "action", "create");
		buttonLayer.add(newLink);
		
		add(form);
	}
}