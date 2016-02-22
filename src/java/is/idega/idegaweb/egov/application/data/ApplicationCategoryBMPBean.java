/*
 * $Id: ApplicationCategoryBMPBean.java,v 1.4 2008/07/15 09:57:57 laddi Exp $ Created on
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
import java.util.logging.Level;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.block.text.data.LocalizedText;
import com.idega.block.text.data.LocalizedTextHome;
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
import com.idega.util.StringUtil;

import is.idega.idegaweb.egov.application.ApplicationUtil;

public class ApplicationCategoryBMPBean extends GenericEntity implements ApplicationCategory {

	private static final long serialVersionUID = 9134629768292794378L;

	public static final String	TABLE_NAME = "EGOV_APPLICATION_CATEGORY",
								NAME = "category_name",
								DESCRIPTION = "category_description",
								PRIORITY = "category_priority",

								EGOV_APPLICATION_CATEGORY_NAME_LOC_TEXT = "EGOV_APPLICATION_CATEGORY_NAME";

	@Override
	public String getEntityName() {
		return TABLE_NAME;
	}

	@Override
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

	@Override
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

	@Override
	public LocalizedText getLocalizedText(int icLocaleId) {
		return ApplicationUtil.getLocalizedText(this, icLocaleId);
	}

	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(NAME, "name", String.class, 50);
		addAttribute(DESCRIPTION, "name", String.class, 4000);
		addAttribute(PRIORITY, "Priority", Integer.class);

		addManyToManyRelationShip(LocalizedText.class, EGOV_APPLICATION_CATEGORY_NAME_LOC_TEXT);
	}

	@Override
	public void addLocalizedName(LocalizedText text) throws IDOAddRelationshipException {
	  	idoAddTo(text);
	}

	@Override
	public void setPriority(Integer priority) {
		setColumn(PRIORITY, priority);
	}

	@Override
	public Integer getPriority() {
		return (Integer) getColumnValue(PRIORITY);
	}

	@Override
	public String getDefaultName() {
		return getStringColumnValue(NAME);
	}

	@Override
	public void setName(String name) {
		setStringColumn(NAME, name);
	}

	@Override
	public String getName() {
		return ApplicationUtil.getName(this);
	}

	@Override
	public void setDescription(String description) {
		setStringColumn(DESCRIPTION, description);
	}

	@Override
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

	@Override
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

	@Override
	public String getLocalizedName() {
		return ApplicationUtil.getLocalizedName(this, getStringColumnValue(NAME));
	}

	/**
	 *
	 * @param name is {@link ApplicationCategory#getName()}, not <code>null</code>;
	 * @return {@link ApplicationCategory#getPrimaryKey()} or <code>null</code>
	 * on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	public Integer ejbFindByName(String name) {
		if (!StringUtil.isEmpty(name)) {
			StringBuilder query = new StringBuilder();
			query.append("SELECT eac.EGOV_APPLICATION_CATEGORY_ID ");
			query.append("FROM EGOV_APPLICATION_CATEGORY eac ");
			query.append("WHERE eac.CATEGORY_NAME = '").append(name).append("'");
			try {
				return idoFindOnePKBySQL(query.toString(), null);
			} catch (FinderException e) {
				getLogger().log(Level.WARNING,
						"Failed to get primary key by query: '" + query + "'");
			}
		}

		return null;
	}

	@Override
	public Collection<LocalizedText> getLocalizedTexts() {
		try {
			return idoGetRelatedEntities(LocalizedText.class);
		} catch (IDORelationshipException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return getLocalizedName();
	}

}