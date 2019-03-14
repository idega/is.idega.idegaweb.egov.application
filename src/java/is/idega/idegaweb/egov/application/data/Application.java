package is.idega.idegaweb.egov.application.data;


import java.sql.Timestamp;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.idega.block.process.data.CaseCode;
import com.idega.block.process.data.model.CaseCodeModel;
import com.idega.block.text.data.LocalizedText;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOEntity;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.user.data.Group;

import is.idega.idegaweb.egov.application.model.ApplicationModel;

public interface Application extends IDOEntity, ApplicationModel {

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setPriority
	 */
	public void setPriority(Integer priority);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getPriority
	 */
	public Integer getPriority();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setAgeFrom
	 */
	public void setAgeFrom(int age);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getAgeFrom
	 */
	@Override
	public int getAgeFrom();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setAgeTo
	 */
	public void setAgeTo(int age);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getAgeTo
	 */
	@Override
	public int getAgeTo();

	@Override
	public LocalizedText getLocalizedText(int icLocaleId);

	public void addLocalizedName(LocalizedText text) throws IDOAddRelationshipException;

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getTimesClicked
	 */
	@Override
	public int getTimesClicked();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setTimesClicked
	 */
	@Override
	public void setTimesClicked(int clicked);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setCategory
	 */
	public void setCategory(ApplicationCategory category);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getCategory
	 */
	public ApplicationCategory getCategory();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setCaseCode
	 */
	public void setCaseCode(CaseCode caseCode);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getCaseCode
	 */
	@Override
	public CaseCodeModel getCaseCode();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setElectronic
	 */
	public void setElectronic(boolean isElectronic);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getElectronic
	 */
	@Override
	public boolean getElectronic();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setRequiresLogin
	 */
	public void setRequiresLogin(boolean requiresLogin);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getRequiresLogin
	 */
	@Override
	public boolean getRequiresLogin();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setVisible
	 */
	public void setVisible(boolean visible);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getVisible
	 */
	@Override
	public boolean getVisible();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getLoginPageURL
	 */
	@Override
	public String getLoginPageURL();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setLoginPageURL
	 */
	public void setLoginPageURL(String url);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getQueryForMiddleTable
	 */
	public String getQueryForMiddleTable(String tableName, int applicationId);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getQueryForTxLocalizedText
	 */
	public String getQueryForTxLocalizedText(int localeId, Collection localNamesIds);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#updateLocalizedTextEntry
	 */
	public void updateLocalizedTextEntry(Object primaryKey, String headline);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#insertLocalizedTextEntry
	 */
	public void insertLocalizedTextEntry(int localeId, String headline,
			boolean settingNames);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setLocalizedUrls
	 */
	public void setLocalizedUrls(Map localizedEntries, boolean isNewApplication);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setLocalizedNames
	 */
	public void setLocalizedNames(Map localizedEntries, boolean isNewApplication);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getName
	 */
	@Override
	public String getName();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getLocalizedName
	 */
	public String getLocalizedName(Locale locale);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getNameByLocale
	 */
	public String getNameByLocale(Locale locale);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getNameByLocale
	 */
	public String getNameByLocale();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getUrlByLocale
	 */
	public String getUrlByLocale();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getUrlByLocale
	 */
	@Override
	public String getUrlByLocale(Locale locale);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getLocalizedUrl
	 */
	public String getLocalizedUrl(Locale locale);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setUrl
	 */
	public void setUrl(String url);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getUrl
	 */
	@Override
	public String getUrl();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setOpensInNewWindow
	 */
	public void setOpensInNewWindow(boolean opensInNew);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getOpensInNewWindow
	 */
	@Override
	public boolean getOpensInNewWindow();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setHiddenFromGuests
	 */
	public void setHiddenFromGuests(boolean hiddenFromGuests);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getHiddenFromGuests
	 */
	@Override
	public boolean getHiddenFromGuests();

	public void setAppType(String appType);

	@Override
	public String getAppType();
	
	public void setIdentifierPrefix(String prefix);
	public String getIdentifierPrefix();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#removeLocalizedEntries
	 */
	public boolean removeLocalizedEntries();

	public void addGroup(Group group) throws IDOAddRelationshipException;

	public Collection<Group> getGroups();

	public void removeGroup(Group group) throws IDORemoveRelationshipException;

	@Override
	public Timestamp getEnabledFrom();
	public void setEnabledFrom(Timestamp enabledFrom);

	@Override
	public Timestamp getEnabledTo();
	public void setEnabledTo(Timestamp enabledTo);

	@Override
	public boolean isEnabled();

	/**
	 *
	 * @param isPaymentRequired tells if {@link Application}s of this type
	 * should be payed;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	void setPaymentRequired(boolean isPaymentRequired);

	/**
	 *
	 * @return flag if payment for this {@link Application} is required.
	 * <code>false</code> if not set;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	boolean isPaymentRequired();

	public Timestamp getCreated();

	public void setCreated(Timestamp created);

}