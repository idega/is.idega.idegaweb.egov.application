/*
 * $Id: ApplicationCategoryBMPBean.java,v 1.1.2.1 2007/08/19 15:42:56 justinas Exp $ Created on
 * Jan 12, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.block.text.data.LocalizedText;
import com.idega.block.text.data.LocalizedTextBMPBean;
import com.idega.block.text.data.LocalizedTextHome;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.core.localisation.data.ICLocaleBMPBean;
import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDOStoreException;
import com.idega.data.query.Column;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.presentation.IWContext;

public class ApplicationCategoryBMPBean extends GenericEntity implements ApplicationCategory {

	private static final String TABLE_NAME = "EGOV_APPLICATION_CATEGORY";
	private static final String NAME = "category_name";
	private static final String DESCRIPTION = "category_description";
//	private static final String EGOV_APPLICATION_CATEGORY_NAME_LOC_TEXT = "EGOV_APPLICATION_CATEGORY_NAME_LOC_TEXT";
	private static final String EGOV_APPLICATION_CATEGORY_NAME_LOC_TEXT = "EGOV_APPLICATION_CATEGORY_NAME";
//	private static final String EGOV_APPLICATION_CATEGORY_ID = "EGOV_APPLICATION_CATEGORY_ID";

	public String getEntityName() {
		return TABLE_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(NAME, "name", String.class, 50);
		addAttribute(DESCRIPTION, "name", String.class);
		
		addManyToManyRelationShip(LocalizedText.class,EGOV_APPLICATION_CATEGORY_NAME_LOC_TEXT);
	}

	public void setName(String name) {
		setColumn(NAME, name);
	}

	public String getDefaultName(){
		return getStringColumnValue(NAME);
	}
	
	public String getName() {
		String localizedName = getLocalizedName();
		if(localizedName == null){
			return getDefaultName();
		}
		else{
			return localizedName;
		}
	}

	public void setDescription(String description) {
		setColumn(DESCRIPTION, description);
	}

	public String getDescription() {
		return getStringColumnValue(DESCRIPTION);
	}

	public Collection ejbFindAll() throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		return this.idoFindPKsByQuery(query);
	}

	public Collection ejbFindAllOrderedByName() throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addOrder(table, NAME, true);
		return this.idoFindPKsByQuery(query);
	}
	
	public String getLocalizedName(){
		IWContext iwc = IWContext.getInstance();
		String localizedName = getLocalizedName(ICLocaleBusiness.getLocaleId(iwc.getLocale()));
		if(localizedName == null){
			if(getStringColumnValue(NAME)== null){
				System.out.println("NULL");
			}
			return getStringColumnValue(NAME);
		}
		else{
			return localizedName;
		}
	}
	
	public void setLocalizedNames(Map localizedEntries){
		for (Iterator iter = localizedEntries.keySet().iterator(); iter.hasNext();) {
			ICLocale icLocale = (ICLocale) iter.next();
			LocalizedText locText = getLocalizedText(icLocale.getLocaleID());
			if (locText == null) {
				locText = ((LocalizedTextHome) com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).createLegacy();
			}

			locText.setHeadline((String)localizedEntries.get(icLocale));
			locText.setLocaleId(icLocale.getLocaleID());
			try {
				locText.store();
			} catch (IDOStoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				addLocalization(locText);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}			
		}
	}
	
	public LocalizedText getLocalizedText(int localeId){
		Collection locales = null;
		LocalizedText text = null;
		
		try {
			locales = idoGetRelatedEntities(LocalizedText.class);
		} catch (IDORelationshipException e) {
			return null;
		}
		for (Iterator iter = locales.iterator(); iter.hasNext();) {
			text = (LocalizedText) iter.next();
			if(text.getLocaleId()==localeId){
				return text;
			}
		}
		return null;
	}
	
	public String getLocalizedName(int localeId){
		try {
			return getLocalizedText(localeId).getHeadline();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return getStringColumnValue(NAME);
		}
	}
	
	public void addLocalization(LocalizedText localizedText) throws SQLException{
		try {
			this.idoAddTo(localizedText);
		} catch (IDOAddRelationshipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeLocalizedTextEntries(){
		Collection locales = null;
		LocalizedText text = null;
		try {
			locales = idoGetRelatedEntities(LocalizedText.class);
		} catch (IDORelationshipException e) {
			e.printStackTrace();
			return;
		}
		
		for (Iterator iter = locales.iterator(); iter.hasNext();) {
			text = (LocalizedText) iter.next();
			try {
				this.idoRemoveFrom(text);
				text.remove();
			} catch (IDORemoveRelationshipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EJBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
