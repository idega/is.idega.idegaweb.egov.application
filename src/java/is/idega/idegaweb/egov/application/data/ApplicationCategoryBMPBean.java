/*
 * $Id: ApplicationCategoryBMPBean.java,v 1.2 2008/01/09 08:04:59 alexis Exp $ Created on
 * Jan 12, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.data;

import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.block.text.data.LocalizedText;
import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDORelationshipException;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;

public class ApplicationCategoryBMPBean extends GenericEntity implements ApplicationCategory {

	private static final String TABLE_NAME = "EGOV_APPLICATION_CATEGORY";
	private static final String NAME = "category_name";
	private static final String DESCRIPTION = "category_description";
	private static final String PRIORITY = "category_priority";

	public String getEntityName() {
		return TABLE_NAME;
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
		addManyToManyRelationShip(LocalizedText.class, "EGOV_APPLICATION_CATEGORY_NAME");
		addAttribute(NAME, "name", String.class, 50);
		addAttribute(DESCRIPTION, "name", String.class);
		addAttribute(PRIORITY, "Priority", Integer.class);
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
	
	public void setName(String name) {
		setStringColumn(NAME, name);
	}

	public String getName() {
		return getStringColumnValue(NAME);
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
}
