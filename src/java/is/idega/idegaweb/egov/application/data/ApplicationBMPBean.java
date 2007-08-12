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
import java.util.Locale;

import javax.ejb.FinderException;
import com.idega.block.process.data.CaseCode;
import com.idega.block.text.data.LocalizedText;
import com.idega.block.text.data.LocalizedTextBMPBean;
import com.idega.core.localisation.data.ICLocale;
import com.idega.core.localisation.data.ICLocaleBMPBean;
import com.idega.data.GenericEntity;
import com.idega.data.IDORelationshipException;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.Order;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.presentation.IWContext;

public class ApplicationBMPBean extends GenericEntity implements Application {

	private static final String TABLE_NAME = "EGOV_APPLICATION";
	private static final String NAME = "application_name";
	private static final String CATEGORY = "application_category_id";
	private static final String CASE_CODE = "case_code";
	private static final String URL = "application_url";
	private static final String ELECTRONIC = "is_electronic";
	private static final String REQUIRES_LOGIN = "requires_login";
	private static final String VISIBLE = "is_visible";
	private static final String AGE_FROM = "age_from";
	private static final String AGE_TO = "age_to";
	private static final String TIMES_CLICKED = "times_clicked";
	private static final String OPENS_IN_NEW_WINDOW = "opens_in_new_window";
	private static final String HIDDEN_FROM_GUESTS = "hidden_from_guests";

	private static final String EGOV_APPLICATION_NAME_LOC_TEXT = "EGOV_APPLICATION_NAME_LOC_TEXT";
	private static final String EGOV_APPLICATION_URL_LOC_TEXT = "EGOV_APPLICATION_URL_LOC_TEXT";
	private static final String EGOV_APPLICATION_ID = "EGOV_APPLICATION_ID";
	private static final String HEADLINE = "HEADLINE";
	private static final String TX_LOCALIZED_TEXT = "TX_LOCALIZED_TEXT";
	private static final String TX_LOCALIZED_ID = "TX_LOCALIZED_ID";
	
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
		
		addManyToOneRelationship(CATEGORY, ApplicationCategory.class);
		addManyToOneRelationship(CASE_CODE, CaseCode.class);
		setNullable(CASE_CODE, true);
		
		addAttribute(URL, "URL", String.class);
		addAttribute(ELECTRONIC, "Is electronic application", Boolean.class);
		addAttribute(REQUIRES_LOGIN, "Requires login", Boolean.class);
		addAttribute(VISIBLE, "Is visible", Boolean.class);
		addAttribute(OPENS_IN_NEW_WINDOW, "Opens in new window", Boolean.class);
		addAttribute(HIDDEN_FROM_GUESTS, "Hidden from guests", Boolean.class);
		addAttribute(AGE_FROM, "Age from", Integer.class);
		addAttribute(AGE_TO, "Age to", Integer.class);
		addAttribute(TIMES_CLICKED, "Time clicked", Integer.class);
		
		//localization
		addManyToManyRelationShip(LocalizedText.class,EGOV_APPLICATION_NAME_LOC_TEXT);
		addManyToManyRelationShip(LocalizedText.class,EGOV_APPLICATION_URL_LOC_TEXT);

//		addManyToManyRelationShip("EGOV_APPLICATION","EGOV_APPLICATION_NAME_LOC_TEXT");
//		addManyToManyRelationShip("EGOV_APPLICATION","EGOV_APPLICATION_URL_LOC_TEXT");
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
		String localizedName = getNameByLocale(); 
		if(localizedName != null){
			return localizedName;
		}
		else {
			return getStringColumnValue(NAME);
		}
	}

	public String getNameOrUrlByLocale(String table){
		IWContext iwc = IWContext.getInstance();

		Collection localNamesIds = null;
//		Collection localNames = null;
		Collection locale = null;
		Collection localizedName = null;
		
//		String queryForLocalizedName = null;
		String queryForLocale = null;
		String queryForLocalizedNamesHeaders = null;
		try { //getting ids of entries representing selected headline
			String sqlQuery = "select * from " + table +" where "+EGOV_APPLICATION_ID+" = "+getID();
			localNamesIds = idoGetRelatedEntitiesBySQL(LocalizedText.class, sqlQuery);
			if(localNamesIds == null || localNamesIds.isEmpty())
				return null;
		} catch (IDORelationshipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
//		try{
//			queryForLocalizedName = "select * from " + TX_LOCALIZED_TEXT +" where ";//+ TX_LOCALIZED_ID+" = "+;					
//			for (Iterator iter = localNamesIds.iterator(); iter.hasNext();) {
//				LocalizedTextBMPBean element = (LocalizedTextBMPBean) iter.next();
//				if(iter.hasNext()){
//					queryForLocalizedName += element.getIDColumnName()+" = "+element.getID()+ " OR ";
//				}
//				else{
//					queryForLocalizedName += element.getIDColumnName()+" = "+element.getID();
//				}
//			}
//			localNames = idoGetRelatedEntitiesBySQL(LocalizedText.class, queryForLocalizedName);
//			if(localNames == null || localNames.isEmpty()){
//				return null;
//			}
//		} catch (IDORelationshipException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
		try {	//getting current locale id
			queryForLocale = "select * from IC_LOCALE where "+ICLocaleBMPBean.getColumnNameLocale()+" = '" + iwc.getLocale()+"'";
			locale = idoGetRelatedEntitiesBySQL(ICLocale.class, queryForLocale);
			if(locale == null || locale.isEmpty()){
				return null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		try { //getting localized headline
			queryForLocalizedNamesHeaders = "select * from TX_LOCALIZED_TEXT where IC_LOCALE_ID = "+((ICLocale)(locale.toArray()[0])).getPrimaryKey()+" and (";
//			queryForLocalizedNamesHeaders = "select "+HEADLINE+" from TX_LOCALIZED_TEXT where IC_LOCALE_ID = "+((ICLocale)(locale.toArray()[0])).getPrimaryKey()+" and (";
			for (Iterator iter = localNamesIds.iterator(); iter.hasNext();) {
				LocalizedTextBMPBean element = (LocalizedTextBMPBean) iter.next();
				if(iter.hasNext()){
					queryForLocalizedNamesHeaders += element.getIDColumnName()+" = "+element.getID()+ " OR ";
				}
				else{
					queryForLocalizedNamesHeaders += element.getIDColumnName()+" = "+element.getID()+")";
	//					localeBean = element;
				}
			}
			localizedName = idoGetRelatedEntitiesBySQL(LocalizedTextBMPBean.class, queryForLocalizedNamesHeaders);
			if (localizedName == null) {
				return null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return ((LocalizedTextBMPBean)(localizedName.toArray()[0])).getHeadline();
//		return (String)(localizedName.toArray()[0]);
	}
	
	public String getNameByLocale(){
		return getNameOrUrlByLocale(EGOV_APPLICATION_NAME_LOC_TEXT);
	}

	public String getUrlByLocale(){
		return getNameOrUrlByLocale(EGOV_APPLICATION_URL_LOC_TEXT);		
	}	
	
	public void setUrl(String url) {
		setColumn(URL, url);
	}

	public String getUrl() {
		String urlByLocale = getUrlByLocale();
		if(urlByLocale != null){
			return urlByLocale;
		}
		return getStringColumnValue(URL);
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