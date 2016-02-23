/*
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.application.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.block.process.data.CaseCode;
import com.idega.block.text.data.LocalizedText;
import com.idega.block.text.data.LocalizedTextBMPBean;
import com.idega.block.text.data.LocalizedTextHome;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOException;
import com.idega.data.IDORelationshipException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDOStoreException;
import com.idega.data.SimpleQuerier;
import com.idega.data.query.AND;
import com.idega.data.query.Column;
import com.idega.data.query.Criteria;
import com.idega.data.query.InCriteria;
import com.idega.data.query.JoinCriteria;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.OR;
import com.idega.data.query.Order;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.user.data.Group;
import com.idega.user.data.GroupBMPBean;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.StringUtil;

import is.idega.idegaweb.egov.application.ApplicationUtil;

public class ApplicationBMPBean extends GenericEntity implements Application {

	private static final long serialVersionUID = 4244056022577759101L;

	public static final String TABLE_NAME = "EGOV_APPLICATION";
	public static final String NAME = "application_name";
	public static final String CATEGORY = "application_category_id";
	public static final String CASE_CODE = "case_code";
	public static final String URL = "application_url";
	public static final String ELECTRONIC = "is_electronic";
	public static final String APP_TYPE = "app_type";
	public static final String REQUIRES_LOGIN = "requires_login";
	public static final String VISIBLE = "is_visible";
	public static final String AGE_FROM = "age_from";
	public static final String AGE_TO = "age_to";
	public static final String ENABLED_FROM = "enabled_from";
	public static final String ENABLED_TO = "enabled_to";
	public static final String TIMES_CLICKED = "times_clicked";
	public static final String OPENS_IN_NEW_WINDOW = "opens_in_new_window";
	public static final String HIDDEN_FROM_GUESTS = "hidden_from_guests";
	public static final String PRIORITY = "app_priority";
	public static final String COLUMN_LOGIN_PAGE_URL = "login_page_url";
	private static final String COLUMN_PAYMENT_REQUIRED = "is_payment_required";

	public static final String EGOV_APPLICATION_NAME_LOC_TEXT = "EGOV_APPLICATION_NAME";
	public static final String EGOV_APPLICATION_URL_LOC_TEXT = "EGOV_APPLICATION_URL_LOC_TEXT";
	private static final String EGOV_APPLICATION_GROUP = "EGOV_APPLICATION_GROUP";
	public static final String EGOV_APPLICATION_ID = "EGOV_APPLICATION_ID";
	public static final String TX_LOCALIZED_TEXT = "TX_LOCALIZED_TEXT";
	public static final String TX_LOCALIZED_TEXT_ID = "TX_LOCALIZED_TEXT_ID";
	public static final String IC_LOCALE_ID = "IC_LOCALE_ID";

	@Override
	public String getEntityName() {
		return TABLE_NAME;
	}

	public String getNameOrUrlByLocale(Locale currentLocale) {
		Collection<LocalizedText> localNamesIds = null;
		Collection<LocalizedTextBMPBean> localizedName = null;

		try {
			//	Getting IDs of entries representing selected headline
			String sqlQuery = "select * from " + ApplicationBMPBean.EGOV_APPLICATION_URL_LOC_TEXT +" where "+ApplicationBMPBean.EGOV_APPLICATION_ID+" = "+ getPrimaryKey();
			localNamesIds = idoGetRelatedEntitiesBySQL(LocalizedText.class, sqlQuery);
			if (localNamesIds == null || localNamesIds.isEmpty()) {
				return null;
			}
		} catch (IDORelationshipException e) {
			e.printStackTrace();
			return null;
		}

		try {
			//	Getting localized headline
			localizedName = idoGetRelatedEntitiesBySQL(LocalizedTextBMPBean.class, getQueryForTxLocalizedText(ICLocaleBusiness.getLocaleId(currentLocale), localNamesIds));
			if (localizedName == null || localizedName.isEmpty()) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ((LocalizedText) localizedName.toArray()[0]).getHeadline();
	}

	@Override
	public String getUrlByLocale(Locale locale){
		return ApplicationUtil.getUrlByLocale(this, locale);
	}

	//	returns url by current locale, if local url is null, then returns empty string
	@Override
	public String getLocalizedUrl(Locale locale){
		String localizedName = getNameOrUrlByLocale(locale);
		if(localizedName != null){
			return localizedName;
		}
		else {
			return CoreConstants.EMPTY;
		}
	}

	//returns name by locale, if name is not set then returns empty string
	@Override
	public String getLocalizedName(Locale locale){
		String localizedName = getNameOrUrlByLocale(locale);
		if(localizedName != null){
			return localizedName;
		}
		else {
			return CoreConstants.EMPTY;
		}
	}

	@Override
	public String getNameByLocale(Locale locale){
		String localizedName = getNameOrUrlByLocale(locale);
		if(localizedName != null){
			return localizedName;
		}
		else {
			return getStringColumnValue(NAME);
		}
	}
	//returns name by current locale, if localized name is null, then returns default name
	@Override
	public String getNameByLocale(){
		IWContext iwc = IWContext.getInstance();
		return getNameByLocale(iwc.getLocale());
	}

	//	returns url by current locale, if localized url is null, then returns default url
	@Override
	public String getUrlByLocale(){
		IWContext iwc = IWContext.getInstance();
		return getUrlByLocale(iwc.getLocale());
	}

	@Override
	public void setDefaultValues() {
		setTimesClicked(0);
		super.setDefaultValues();
	}

	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(NAME, "name", String.class, 50);

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
		addAttribute(ENABLED_FROM, "Enabled from", Timestamp.class);
		addAttribute(ENABLED_TO, "Enabled to", Timestamp.class);
		addAttribute(TIMES_CLICKED, "Time clicked", Integer.class);
		addAttribute(PRIORITY, "Priority", Integer.class);
		addAttribute(COLUMN_LOGIN_PAGE_URL, "Login page url", String.class);
		addAttribute(COLUMN_PAYMENT_REQUIRED, "Is payment required", Boolean.class);

		addManyToManyRelationShip(LocalizedText.class, EGOV_APPLICATION_NAME_LOC_TEXT);
		addManyToManyRelationShip(LocalizedText.class, EGOV_APPLICATION_URL_LOC_TEXT);
		addManyToManyRelationShip(Group.class, EGOV_APPLICATION_GROUP);
	}

	@Override
	public LocalizedText getLocalizedText(int icLocaleId) {
		return ApplicationUtil.getLocalizedText(this, icLocaleId);
	}

	@SuppressWarnings("unchecked")
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
	public void setAgeFrom(int age) {
		setColumn(AGE_FROM, age);
	}

	@Override
	public int getAgeFrom() {
		return getIntColumnValue(AGE_FROM);
	}

	@Override
	public void setAgeTo(int age) {
		setColumn(AGE_TO, age);
	}

	@Override
	public int getAgeTo() {
		return getIntColumnValue(AGE_TO);
	}

	@Override
	public int getTimesClicked() {
		return getIntColumnValue(TIMES_CLICKED);
	}

	@Override
	public void setTimesClicked(int clicked) {
		setColumn(TIMES_CLICKED, clicked);
	}

	@Override
	public void setCategory(ApplicationCategory category) {
		setColumn(CATEGORY, category);
	}

	@Override
	public ApplicationCategory getCategory() {
		return (ApplicationCategory) getColumnValue(CATEGORY);
	}

	@Override
	public void setCaseCode(CaseCode caseCode) {
		setColumn(CASE_CODE, caseCode);
	}

	@Override
	public CaseCode getCaseCode() {
		return (CaseCode) getColumnValue(CASE_CODE);
	}

	@Override
	public void setElectronic(boolean isElectronic) {
		setColumn(ELECTRONIC, isElectronic);
	}

	@Override
	public boolean getElectronic() {
		return getBooleanColumnValue(ELECTRONIC, false);
	}

	@Override
	public void setRequiresLogin(boolean requiresLogin) {
		if (requiresLogin) {
			setElectronic(true);
		}
		setColumn(REQUIRES_LOGIN, requiresLogin);
	}

	@Override
	public boolean getRequiresLogin() {
		return getBooleanColumnValue(REQUIRES_LOGIN, false);
	}

	@Override
	public void setVisible(boolean visible) {
		setColumn(VISIBLE, visible);
	}

	@Override
	public boolean getVisible() {
		return getBooleanColumnValue(VISIBLE, true);
	}

	@Override
	public void setPaymentRequired(boolean isPaymentRequired) {
		setColumn(COLUMN_PAYMENT_REQUIRED, isPaymentRequired);
	}

	@Override
	public boolean isPaymentRequired() {
		return getBooleanColumnValue(COLUMN_PAYMENT_REQUIRED, Boolean.FALSE);
	}

	private void saveAllLocalizedEntries(Map localizedEntries, boolean settingNames){
		if(settingNames){
			for (Iterator iter = localizedEntries.keySet().iterator(); iter.hasNext();) {
				ICLocale icLocale = (ICLocale) iter.next();
				insertLocalizedTextEntry(icLocale.getLocaleID(), (String)localizedEntries.get(icLocale), settingNames);
			}
		}
		else{
			for (Iterator iter = localizedEntries.keySet().iterator(); iter.hasNext();) {
				ICLocale icLocale = (ICLocale) iter.next();
				insertLocalizedTextEntry(icLocale.getLocaleID(), (String)localizedEntries.get(icLocale), settingNames);
			}
		}
	}

	@Override
	public void setLocalizedUrls(Map localizedEntries, boolean isNewApplication){
		setLocalizedNamesOrUrls(localizedEntries, isNewApplication, false);
	}

	@Override
	public void setLocalizedNames(Map localizedEntries, boolean isNewApplication){
		setLocalizedNamesOrUrls(localizedEntries, isNewApplication, true);
	}

	private void setLocalizedNamesOrUrls(Map localizedEntries, boolean isNewApplication, boolean settingNames){
		if(isNewApplication){	//if we are saving new application there are no localization still
			saveAllLocalizedEntries(localizedEntries, settingNames);
		}
		else{
			String queryForLocalizedEntriesIds = null;
			if(settingNames){
				queryForLocalizedEntriesIds = getQueryForMiddleTable(EGOV_APPLICATION_NAME_LOC_TEXT, getID());
			}
			else{
				queryForLocalizedEntriesIds = getQueryForMiddleTable(EGOV_APPLICATION_URL_LOC_TEXT, getID());
			}

			Collection localNamesIds = null;
			try {
				localNamesIds = idoGetRelatedEntitiesBySQL(LocalizedText.class, queryForLocalizedEntriesIds);
			} catch (IDORelationshipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

			if(localNamesIds == null || localNamesIds.isEmpty()){
				//there are no localized text entries for that application
				saveAllLocalizedEntries(localizedEntries, settingNames);
			}
			else{
				// there are several localized text entries, so we have to check which of them are updated and which are new
				for (Iterator iter = localizedEntries.keySet().iterator(); iter.hasNext();) {
					ICLocale icLocale = (ICLocale)iter.next();

					String queryForLocalizedEntry = getQueryForTxLocalizedText(icLocale.getLocaleID(), localNamesIds);
					Collection localizedEntry = null;
					try {
						localizedEntry = idoGetRelatedEntitiesBySQL(LocalizedText.class, queryForLocalizedEntry);
					} catch (IDORelationshipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if(localizedEntry == null || localizedEntry.isEmpty()){
						//insert entry
						insertLocalizedTextEntry(icLocale.getLocaleID(), (String)localizedEntries.get(icLocale), settingNames);
					}
					else{
						//update entry

//						if(!((LocalizedText)(localizedEntry.toArray()[0])).getHeadline().equals((String)localizedEntries.get(icLocale))){
							// localized text entry has been changed
							updateLocalizedTextEntry(((LocalizedText)(localizedEntry.toArray()[0])).getPrimaryKey(), (String)localizedEntries.get(icLocale));
//						}
					}
				}
			}
		}
	}

	@Override
	public void setName(String name) {
		setColumn(NAME, name);
	}

	@Override
	public String getLoginPageURL() {
		return getStringColumnValue(COLUMN_LOGIN_PAGE_URL);
	}

	@Override
	public void setLoginPageURL(String url) {
		setColumn(COLUMN_LOGIN_PAGE_URL, url);
	}

	@Override
	public String getQueryForMiddleTable(String tableName, int applicationId){
		return "select * from "+tableName+" where "+EGOV_APPLICATION_ID+"="+applicationId;
	}

	@Override
	public String getQueryForTxLocalizedText(int localeId, Collection localNamesIds){
		return ApplicationUtil.getQueryForTxLocalizedText(localeId, localNamesIds);
	}

	@Override
	public void updateLocalizedTextEntry(Object primaryKey, String headline){
		LocalizedText localizedText = null;
		try {
			localizedText = ((LocalizedTextHome) com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).findByPrimaryKey(primaryKey);
		} catch (EJBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FinderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localizedText.setHeadline(headline);
		localizedText.store();
	}

	@Override
	public void insertLocalizedTextEntry(int localeId, String headline, boolean settingNames){
		if(headline == null || headline.equals(CoreConstants.EMPTY)){
			return;
		}
		LocalizedText localizedText = ((LocalizedTextHome) com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).createLegacy();
		localizedText.setHeadline(headline);
		localizedText.setLocaleId(localeId);

		try {
			localizedText.store();
		} catch (IDOStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String queryForMiddleTable = null;
		if(settingNames){
			queryForMiddleTable = "insert into "+EGOV_APPLICATION_NAME_LOC_TEXT+" ("+EGOV_APPLICATION_ID+", "+TX_LOCALIZED_TEXT_ID+") values ("+getID()+", "+localizedText.getID()+")";
		}
		else{
			queryForMiddleTable = "insert into "+EGOV_APPLICATION_URL_LOC_TEXT+" ("+EGOV_APPLICATION_ID+", "+TX_LOCALIZED_TEXT_ID+") values ("+getID()+", "+localizedText.getID()+")";
		}
		try {
			idoExecuteGlobalUpdate(queryForMiddleTable);
		} catch (IDOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return getStringColumnValue(NAME);
	}

	@Override
	public void setUrl(String url) {
		setColumn(URL, url);
	}

	@Override
	public String getUrl() {
		String url = getStringColumnValue(URL);
		if (StringUtil.isEmpty(url)) {
			String query = "select " + URL + " from " + getEntityName() + " where " + getIDColumnName() + " = " + getPrimaryKey().toString();
			String[] results = null;
			try {
				results = SimpleQuerier.executeStringQuery(query);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error executing query: " + query, e);
			}
			if (!ArrayUtil.isEmpty(results)) {
				url = results[0];
			}
		}
		return url;
	}

	@Override
	public void setAppType(String appType) {
		setColumn(APP_TYPE, appType);
	}

	@Override
	public String getAppType() {
		return getStringColumnValue(APP_TYPE);
	}

	@Override
	public void setOpensInNewWindow(boolean opensInNew) {
		setColumn(OPENS_IN_NEW_WINDOW, opensInNew);
	}

	@Override
	public boolean getOpensInNewWindow() {
		return getBooleanColumnValue(OPENS_IN_NEW_WINDOW, false);
	}

	@Override
	public void setHiddenFromGuests(boolean hiddenFromGuests) {
		setColumn(HIDDEN_FROM_GUESTS, hiddenFromGuests);
	}

	@Override
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

	private void addValidFromToCriteria(Table table, SelectQuery query) {
		if (IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("app.filter_out_disabled", Boolean.FALSE)) {
			MatchCriteria enabledFromIsNull = new MatchCriteria(new Column(table, ENABLED_FROM), MatchCriteria.IS, MatchCriteria.NULL);
			MatchCriteria enabledToIsNull = new MatchCriteria(new Column(table, ENABLED_TO), MatchCriteria.IS, MatchCriteria.NULL);
			Criteria checkForNulls = new AND(enabledFromIsNull, enabledToIsNull);

			String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(IWTimestamp.RightNow().getDate());
			MatchCriteria enabledFrom = new MatchCriteria(new Column(table, ENABLED_FROM), MatchCriteria.LESSEQUAL, timestamp);
			MatchCriteria enabledTo = new MatchCriteria(new Column(table, ENABLED_TO), MatchCriteria.GREATEREQUAL, timestamp);
			Criteria checkForDates = new AND(enabledFrom, enabledTo);

			query.addCriteria(new OR(checkForNulls, checkForDates));
		}
	}

	public Collection ejbFindAllByCategory(ApplicationCategory category) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addCriteria(new MatchCriteria(new Column(table, CATEGORY), MatchCriteria.EQUALS, category));

		addValidFromToCriteria(table, query);

		return this.idoFindPKsByQuery(query);
	}

	public Collection ejbFindAllByCategoryOrderedByPriority(ApplicationCategory category) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(new Column(table, CATEGORY), MatchCriteria.EQUALS, category));
		query.addOrder(table, PRIORITY, true);

		addValidFromToCriteria(table, query);

		return this.idoFindPKsByQuery(query);
	}

	public Object ejbFindByCategoryAndPriority(ApplicationCategory category, int priority) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(new Column(table, CATEGORY), MatchCriteria.EQUALS, category));
		query.addCriteria(new MatchCriteria(new Column(table, PRIORITY), MatchCriteria.EQUALS, priority));

		addValidFromToCriteria(table, query);

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

	public Collection ejbFindAllByApplicationUrl(String appUrl) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(URL), MatchCriteria.EQUALS, appUrl));

		return this.idoFindPKsByQuery(query);
	}

	public Collection ejbFindAllByCaseCode(String caseCode) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(CASE_CODE), MatchCriteria.EQUALS, caseCode));
		return this.idoFindPKsByQuery(query);
	}

	public Collection ejbFindAllByGroups(Collection<String> ids) throws FinderException {
		Table appsAndGroups = new Table(EGOV_APPLICATION_GROUP);
		SelectQuery query = new SelectQuery(appsAndGroups);
		query.addColumn(new Column(appsAndGroups, getIDColumnName()));
		query.addCriteria(new InCriteria(appsAndGroups.getColumn(GroupBMPBean.getColumnNameGroupID()), ids));
		return this.idoFindPKsByQuery(query);
	}

	public Collection ejbFindAllWithAssignedGroups() throws FinderException {
		Table apps = new Table(this);
		Table appsAndGroups = new Table(EGOV_APPLICATION_GROUP);

		SelectQuery query = new SelectQuery(apps);
		query.addColumn(apps, getIDColumnName(), true);

		query.addCriteria(new JoinCriteria(apps.getColumn(getIDColumnName()), appsAndGroups.getColumn(getIDColumnName())));
		query.addCriteria(new MatchCriteria(apps.getColumn(VISIBLE), MatchCriteria.EQUALS, true));

		return this.idoFindPKsByQuery(query);
	}

	public Collection ejbFindAllByType(String type) throws FinderException {
		Table apps = new Table(this);

		SelectQuery query = new SelectQuery(apps);
		query.addColumn(apps.getColumn(getIDColumnName()));

		query.addCriteria(new MatchCriteria(apps.getColumn(APP_TYPE), MatchCriteria.EQUALS, type));

		query.addGroupByColumn(apps.getColumn(getIDColumnName()));

		return this.idoFindPKsByQuery(query);
	}

	public Collection<?> ejbFindAllWithPaymentRequired() throws FinderException {
		Table apps = new Table(this);

		SelectQuery query = new SelectQuery(apps);
		query.addColumn(apps.getColumn(getIDColumnName()));

		query.addCriteria(new MatchCriteria(apps.getColumn(COLUMN_PAYMENT_REQUIRED), MatchCriteria.EQUALS, true));
		query.addGroupByColumn(apps.getColumn(getIDColumnName()));

		return this.idoFindPKsByQuery(query);
	}

	private String getQueryForDeletingLocalizedTextEntries(Collection localizedNamesIds, Collection localizedUrlIds){
		String query = null;

		if(localizedNamesIds != null && !localizedNamesIds.isEmpty()){
			query = "delete from "+TX_LOCALIZED_TEXT+" where ";

			for (Iterator localNameIdsIterator = localizedNamesIds.iterator(); localNameIdsIterator.hasNext();) {
				LocalizedText element = (LocalizedText) localNameIdsIterator.next();
				if(localNameIdsIterator.hasNext()){
					query += TX_LOCALIZED_TEXT_ID+" = "+element.getPrimaryKey()+ " OR ";
				}
				else{
					query += TX_LOCALIZED_TEXT_ID+" = "+element.getPrimaryKey();
				}
			}

			if(localizedUrlIds == null || localizedUrlIds.isEmpty()){
				return query;
			}
			else{
				query += " OR ";
			}
		}
		if(localizedUrlIds != null && !localizedUrlIds.isEmpty()){
			if(query == null){
				query = "delete from "+TX_LOCALIZED_TEXT+" where ";
			}
			for (Iterator localNameIdsIterator = localizedUrlIds.iterator(); localNameIdsIterator.hasNext();) {
				LocalizedText element = (LocalizedText) localNameIdsIterator.next();
				if(localNameIdsIterator.hasNext()){
					query += TX_LOCALIZED_TEXT_ID+" = "+element.getPrimaryKey()+ " OR ";
				}
				else{
					query += TX_LOCALIZED_TEXT_ID+" = "+element.getPrimaryKey();
				}
			}
		}
		return query;
	}

	@Override
	public boolean removeLocalizedEntries(){

		//remove Ids of localized names
		Collection localizedNamesIds = null;
		String query = getQueryForMiddleTable(EGOV_APPLICATION_NAME_LOC_TEXT, getID());
		try {
			localizedNamesIds = idoGetRelatedEntitiesBySQL(LocalizedText.class, query);
		} catch (IDORelationshipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		query = "delete from "+EGOV_APPLICATION_NAME_LOC_TEXT+" where "+EGOV_APPLICATION_ID+" = "+getID();
		try {
			idoExecuteGlobalUpdate(query);
		} catch (IDOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		//remove Ids of localized names
		Collection localizedURLsIds = null;
		query = getQueryForMiddleTable(EGOV_APPLICATION_URL_LOC_TEXT, getID());
		try {
			localizedURLsIds = idoGetRelatedEntitiesBySQL(LocalizedText.class, query);
		} catch (IDORelationshipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		query = "delete from "+EGOV_APPLICATION_URL_LOC_TEXT+" where "+EGOV_APPLICATION_ID+" = "+getID();
		try {
			idoExecuteGlobalUpdate(query);
		} catch (IDOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		query = getQueryForDeletingLocalizedTextEntries(localizedNamesIds, localizedURLsIds);
		System.out.println(query);
		try {
			idoExecuteGlobalUpdate(query);
		} catch (IDOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void addGroup(Group group) throws IDOAddRelationshipException {
		this.idoAddTo(group);
	}

	@Override
	public Collection<Group> getGroups() {
		try {
			return super.idoGetRelatedEntities(Group.class);
		} catch (IDORelationshipException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void removeGroup(Group group) throws IDORemoveRelationshipException {
		super.idoRemoveFrom(group);
	}

	@Override
	public Timestamp getEnabledFrom() {
		return getTimestampColumnValue(ENABLED_FROM);
	}
	@Override
	public void setEnabledFrom(Timestamp enabledFrom) {
		setColumn(ENABLED_FROM, enabledFrom);
	}

	@Override
	public Timestamp getEnabledTo() {
		return getTimestampColumnValue(ENABLED_TO);
	}
	@Override
	public void setEnabledTo(Timestamp enabledTo) {
		setColumn(ENABLED_TO, enabledTo);
	}

	@Override
	public boolean isEnabled() {
		return ApplicationUtil.isEnabled(this);
	}

	@Override
	public String getLocalizedName() {
		return ApplicationUtil.getLocalizedName(this);
	}

	@Override
	public String getLocalizedName(int icLocaleId) {
		return ApplicationUtil.getLocalizedName(this, icLocaleId);
	}

}