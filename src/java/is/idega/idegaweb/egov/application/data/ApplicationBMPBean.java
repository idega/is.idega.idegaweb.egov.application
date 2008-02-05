/*
 * <<<<<<< ApplicationBMPBean.java $Id: ApplicationBMPBean.java,v 1.1
 * 2006/01/12 17:04:20 gimmi Exp $ Created on Jan 12, 2006 ======= $Id:
 * ApplicationBMPBean.java,v 1.2 2006/01/12 17:19:31 laddi Exp $ Created on Jan
 * 12, 2006 >>>>>>> 1.2
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
import com.idega.block.process.data.CaseCode;
import com.idega.block.text.data.LocalizedText;
import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDORelationshipException;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.Order;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;

public class ApplicationBMPBean extends GenericEntity implements Application {

	private static final long serialVersionUID = 4244056022577759101L;
	
	private static final String TABLE_NAME = "EGOV_APPLICATION";
	private static final String NAME = "application_name";
	private static final String CATEGORY = "application_category_id";
	private static final String CASE_CODE = "case_code";
	private static final String URL = "application_url";
	private static final String ELECTRONIC = "is_electronic";
	private static final String APP_TYPE = "app_type";
	private static final String REQUIRES_LOGIN = "requires_login";
	private static final String VISIBLE = "is_visible";
	private static final String AGE_FROM = "age_from";
	private static final String AGE_TO = "age_to";
	private static final String TIMES_CLICKED = "times_clicked";
	private static final String OPENS_IN_NEW_WINDOW = "opens_in_new_window";
	private static final String HIDDEN_FROM_GUESTS = "hidden_from_guests";
	private static final String PRIORITY = "app_priority";

	public String getEntityName() {
		return TABLE_NAME;
	}

	public void setDefaultValues() {
		setTimesClicked(0);
		super.setDefaultValues();
	}

	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(NAME, "name", String.class, 50);
		
		addManyToManyRelationShip(LocalizedText.class, "EGOV_APPLICATION_NAME");
		addManyToOneRelationship(CATEGORY, ApplicationCategory.class);
		addManyToOneRelationship(CASE_CODE, CaseCode.class);
		setNullable(CASE_CODE, true);
		
		addAttribute(URL, "URL", String.class);
		addAttribute(ELECTRONIC, "Is electronic application", Boolean.class);
		addAttribute(APP_TYPE, "Application type", String.class);
		addAttribute(REQUIRES_LOGIN, "Requires login", Boolean.class);
		addAttribute(VISIBLE, "Is visible", Boolean.class);
		addAttribute(OPENS_IN_NEW_WINDOW, "Opens in new window", Boolean.class);
		addAttribute(HIDDEN_FROM_GUESTS, "Hidden from guests", Boolean.class);
		addAttribute(AGE_FROM, "Age from", Integer.class);
		addAttribute(AGE_TO, "Age to", Integer.class);
		addAttribute(TIMES_CLICKED, "Time clicked", Integer.class);
		addAttribute(PRIORITY, "Priority", Integer.class);
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
	
	public void addLocalizedName(LocalizedText text) throws IDOAddRelationshipException {
	  	idoAddTo(text);
	}
	
	public void setPriority(Integer priority) {
		setColumn(PRIORITY, priority);
	}
	
	public Integer getPriority() {
		return (Integer) getColumnValue(PRIORITY);
	}

	public void setAgeFrom(int age) {
		setColumn(AGE_FROM, age);
	}

	public int getAgeFrom() {
		return getIntColumnValue(AGE_FROM);
	}

	public void setAgeTo(int age) {
		setColumn(AGE_TO, age);
	}

	public int getAgeTo() {
		return getIntColumnValue(AGE_TO);
	}
	
	public int getTimesClicked() {
		return getIntColumnValue(TIMES_CLICKED);
	}
	
	public void setTimesClicked(int clicked) {
		setColumn(TIMES_CLICKED, clicked);
	}

	public void setCategory(ApplicationCategory category) {
		setColumn(CATEGORY, category);
	}

	public ApplicationCategory getCategory() {
		return (ApplicationCategory) getColumnValue(CATEGORY);
	}

	public void setCaseCode(CaseCode caseCode) {
		setColumn(CASE_CODE, caseCode);
	}

	public CaseCode getCaseCode() {
		return (CaseCode) getColumnValue(CASE_CODE);
	}

	public void setElectronic(boolean isElectronic) {
		setColumn(ELECTRONIC, isElectronic);
	}

	public boolean getElectronic() {
		return getBooleanColumnValue(ELECTRONIC, false);
	}
	
	public void setRequiresLogin(boolean requiresLogin) {
		if (requiresLogin) {
			setElectronic(true);
		}
		setColumn(REQUIRES_LOGIN, requiresLogin);
	}

	public boolean getRequiresLogin() {
		return getBooleanColumnValue(REQUIRES_LOGIN, false);
	}
	
	public void setVisible(boolean visible) {
		setColumn(VISIBLE, visible);
	}
	
	public boolean getVisible() {
		return getBooleanColumnValue(VISIBLE, true);
	}

	public void setName(String name) {
		setColumn(NAME, name);
	}

	public String getName() {
		return getStringColumnValue(NAME);
	}

	public void setUrl(String url) {
		setColumn(URL, url);
	}

	public String getUrl() {
		return getStringColumnValue(URL);
	}
	
	public void setAppType(String appType) {
		setColumn(APP_TYPE, appType);
	}

	public String getAppType() {
		return getStringColumnValue(APP_TYPE);
	}

	public void setOpensInNewWindow(boolean opensInNew) {
		setColumn(OPENS_IN_NEW_WINDOW, opensInNew);
	}
	
	public boolean getOpensInNewWindow() {
		return getBooleanColumnValue(OPENS_IN_NEW_WINDOW, false);
	}
	
	public void setHiddenFromGuests(boolean hiddenFromGuests) {
		setColumn(HIDDEN_FROM_GUESTS, hiddenFromGuests);
	}
	
	public boolean getHiddenFromGuests() {
		return getBooleanColumnValue(HIDDEN_FROM_GUESTS, false);
	}
	
	public Object ejbFindByCaseCode(CaseCode caseCode) throws FinderException {
		return ejbFindByCaseCode(caseCode.getCode());
	}

	public Object ejbFindByCaseCode(String caseCode) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addCriteria(new MatchCriteria(new Column(table, CASE_CODE), MatchCriteria.EQUALS, caseCode));
		return idoFindOnePKByQuery(query);
	}

	public Collection ejbFindAllByCategory(ApplicationCategory category) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addCriteria(new MatchCriteria(new Column(table, CATEGORY), MatchCriteria.EQUALS, category));
		return this.idoFindPKsByQuery(query);
	}
	
	public Collection ejbFindAllByCategoryOrderedByPriority(ApplicationCategory category) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(new Column(table, CATEGORY), MatchCriteria.EQUALS, category));
		query.addOrder(table, PRIORITY, true);
		return this.idoFindPKsByQuery(query);
	}
	
	public Object ejbFindByCategoryAndPriority(ApplicationCategory category, int priority) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(new Column(table, CATEGORY), MatchCriteria.EQUALS, category));
		query.addCriteria(new MatchCriteria(new Column(table, PRIORITY), MatchCriteria.EQUALS, priority));
		return this.idoFindOnePKByQuery(query);
	}

	public Collection ejbFindAll() throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addOrder(table, NAME, true);
		return this.idoFindPKsByQuery(query);
	}
	
	public Collection ejbFindElectronicApplications() throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addCriteria(new MatchCriteria(new Column(table, ELECTRONIC), MatchCriteria.EQUALS, true));
		return this.idoFindPKsByQuery(query);
	}
	
	public Collection ejbFindMostClicked(int numberOfEntries) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addOrder(new Order(new Column(table, TIMES_CLICKED), false));
		return this.idoFindPKsByQuery(query, numberOfEntries);
	}
}