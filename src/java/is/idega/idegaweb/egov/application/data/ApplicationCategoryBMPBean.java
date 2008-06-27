/*
 * $Id: ApplicationCategoryBMPBean.java,v 1.3 2008/06/27 12:25:59 alexis Exp $ Created on
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
import com.idega.block.text.data.LocalizedTextHome;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDOStoreException;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.presentation.IWContext;

public class ApplicationCategoryBMPBean extends GenericEntity implements ApplicationCategory {

	private static final String TABLE_NAME = "EGOV_APPLICATION_CATEGORY";
	private static final String NAME = "category_name";
	private static final String DESCRIPTION = "category_description";
	private static final String PRIORITY = "category_priority";
	private static final String EGOV_APPLICATION_CATEGORY_NAME_LOC_TEXT = "EGOV_APPLICATION_CATEGORY_NAME";

	public String getEntityName() {
		return TABLE_NAME;
	}
	
	public String getLocalizedName(int localeId) {
		try {
			return getLocalizedText(localeId).getHeadline();
		}
		catch (RuntimeException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
			return getStringColumnValue(NAME);
		}
	}
	
	public void setLocalizedNames(Map localizedEntries) {
		for (Iterator iter = localizedEntries.keySet().iterator(); iter.hasNext();) {
			ICLocale icLocale = (ICLocale) iter.next();
			LocalizedText locText = getLocalizedText(icLocale.getLocaleID());
			if (locText == null) {
				locText = ((LocalizedTextHome) com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).createLegacy();
			}

			locText.setHeadline((String) localizedEntries.get(icLocale));
			locText.setLocaleId(icLocale.getLocaleID());
			try {
				locText.store();
			}
			catch (IDOStoreException e1) {
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
	
	public void addLocalization(LocalizedText localizedText) throws SQLException {
		try {
			this.idoAddTo(localizedText);
		}
		catch (IDOAddRelationshipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LocalizedText getLocalizedText(int icLocaleId) {
		Collection<LocalizedText> result = null;
		try {
			result = idoGetRelatedEntities(LocalizedText.class);
		} catch(IDORelationshipException e) {
			e.printStackTrace();
		}
		if(result != null) {
			for(Iterator<LocalizedText> it = result.iterator(); it.hasNext(); ) {
				LocalizedText temp = it.next();
				if(temp.getLocaleId() == icLocaleId) {
					return temp;
				}
			}
		}
		return null;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(NAME, "name", String.class, 50);
		addAttribute(DESCRIPTION, "name", String.class);
		addAttribute(PRIORITY, "Priority", Integer.class);
		
		addManyToManyRelationShip(LocalizedText.class, EGOV_APPLICATION_CATEGORY_NAME_LOC_TEXT);
	}
	
	public void addLocalizedName(LocalizedText text) throws IDOAddRelationshipException {
	  	idoAddTo(text);
	}
	
	public void setPriority(Integer priority) {
		setColumn(PRIORITY, priority);
	}
	
	public Integer getPriority() {
		return (Integer) getColumnValue(PRIORITY);
	}
	
	public String getDefaultName() {
		return getStringColumnValue(NAME);
	}
	
	public void setName(String name) {
		setStringColumn(NAME, name);
	}

	public String getName() {
		String localizedName = getLocalizedName();
		if (localizedName == null) {
			return getDefaultName();
		}
		else {
			return localizedName;
		}
	}

	public void setDescription(String description) {
		setStringColumn(DESCRIPTION, description);
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
	
	public Collection ejbFindAllOrderedByPriority() throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addOrder(table, PRIORITY, true);
		return this.idoFindPKsByQuery(query);
	}
	
	public Object ejbFindByPriority(int priority) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(new Column(table, PRIORITY), MatchCriteria.EQUALS, priority));
		return this.idoFindOnePKByQuery(query);
	}

	public void removeLocalizedTextEntries() {
		Collection locales = null;
		LocalizedText text = null;
		try {
			locales = idoGetRelatedEntities(LocalizedText.class);
		}
		catch (IDORelationshipException e) {
			e.printStackTrace();
			return;
		}

		for (Iterator iter = locales.iterator(); iter.hasNext();) {
			text = (LocalizedText) iter.next();
			try {
				this.idoRemoveFrom(text);
				text.remove();
			}
			catch (IDORemoveRelationshipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (EJBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (RemoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getLocalizedName() {
		IWContext iwc = IWContext.getInstance();
		String localizedName = getLocalizedName(ICLocaleBusiness.getLocaleId(iwc.getLocale()));
		if (localizedName == null) {
			if (getStringColumnValue(NAME) == null) {
				System.out.println("NULL");
			}
			return getStringColumnValue(NAME);
		}
		else {
			return localizedName;
		}
	}

}
