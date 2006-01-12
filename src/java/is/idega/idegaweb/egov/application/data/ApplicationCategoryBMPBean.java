/*
 * $Id: ApplicationCategoryBMPBean.java,v 1.1 2006/01/12 17:04:20 gimmi Exp $ Created on
 * Jan 12, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.GenericEntity;
import com.idega.data.query.Column;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;

public class ApplicationCategoryBMPBean extends GenericEntity implements ApplicationCategory {

	private static final String TABLE_NAME = "EGOV_APPLICATION_CATEGORY";
	private static final String NAME = "category_name";
	private static final String DESCRIPTION = "category_description";

	public String getEntityName() {
		return TABLE_NAME;
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(NAME, "name", String.class, 50);
		addAttribute(DESCRIPTION, "name", String.class);
	}

	public void setName(String name) {
		setColumn(NAME, name);
	}

	public String getName() {
		return getStringColumnValue(NAME);
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
}
