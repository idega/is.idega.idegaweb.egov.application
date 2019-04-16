package is.idega.idegaweb.egov.application.data.dao.impl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.block.process.data.bean.CaseSettings;
import com.idega.block.process.data.model.ReminderModel;
import com.idega.block.process.data.model.SettingsModel;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.core.file.data.bean.ICFile;
import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.user.dao.GroupDAO;
import com.idega.user.dao.UserDAO;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.egov.application.data.bean.Application;
import is.idega.idegaweb.egov.application.data.bean.ApplicationAccess;
import is.idega.idegaweb.egov.application.data.bean.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.bean.ApplicationReminder;
import is.idega.idegaweb.egov.application.data.bean.ApplicationSettings;
import is.idega.idegaweb.egov.application.data.bean.DecisionTemplate;
import is.idega.idegaweb.egov.application.data.bean.SignatureProfile;
import is.idega.idegaweb.egov.application.data.dao.ApplicationDAO;

@Repository(ApplicationDAO.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Transactional(readOnly = false)
public class ApplicationDAOImpl extends GenericDaoImpl implements ApplicationDAO {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private GroupDAO groupDAO;

	private GroupDAO getGroupDAO() {
		if (this.groupDAO == null) {
			ELUtil.getInstance().autowire(this);
		}

		return this.groupDAO;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.dao.ApplicationDAO#findById(java.lang.Integer)
	 */
	@Override
	public Application findById(Integer primaryKey) {
		return getById(primaryKey);
	}

	@Override
	public Application getById(Integer id) {
		if (id == null) {
			return null;
		}

		try {
			return getSingleResult(Application.QUERY_GET_BY_ID, Application.class, new Param("id", id));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting application by ID: " + id, e);
		}
		return null;
	}

	@Override
	public List<ApplicationCategory> getAllCategoriesOrderedByPriority() {
		try {
			return getResultListByInlineQuery("select ac from " + ApplicationCategory.class.getName() + " ac order by ac.priority", ApplicationCategory.class);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting application categories", e);
		}
		return Collections.emptyList();
	}

	@Override
	public List<Application> getApplicationsByCategoryOrderedByPriority(ApplicationCategory category) {
		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<>();
			params.add(new Param("catId", category.getId()));
			query = new StringBuilder("select a from ");
			query.append(Application.class.getName()).append(" a where a.category.id = :catId ");
			if (IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("app.filter_out_disabled", Boolean.FALSE)) {
				query.append(" and ((a.enabledFrom is null and a.enabledTo is null) or (a.enabledFrom <= :timestamp and a.enabledTo >= :timestamp))");
				params.add(new Param("timestamp", IWTimestamp.RightNow().getTimestamp()));
			}
			query.append(" order by a.priority");

			return getResultListByInlineQuery(
					query.toString(),
					Application.class,
					ArrayUtil.convertListToArray(params)
			);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting applications by category " + category + ". Query: " + query, e);
		}
		return Collections.emptyList();
	}

	@Override
	@Transactional(readOnly = false)
	public Application store(Application app) {
		if (app == null) {
			return null;
		}

		if (app.getId() == null) {
			persist(app);
		} else {
			merge(app);
		}

		return app.getId() == null ? null : app;
	}

	@Override
	public Application findByUri(String uri) {
		if (StringUtil.isEmpty(uri)) {
			return null;
		}

		try {
			if (uri.endsWith(CoreConstants.SLASH)) {
				uri = uri.substring(0, uri.length() - 1);
			}

			String query = "select a from ".concat(Application.class.getName()).concat(" a where a.url = :uri");
			List<Application> applications = getResultListByInlineQuery(query, Application.class, new Param("uri", uri));
			return ListUtil.isEmpty(applications) ? null : applications.get(0);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting application by uri: " + uri, e);
		}

		return null;
	}

	@Override
	public List<String> getDistinctApplicationURLByAppType(String appType) {
		if (StringUtil.isEmpty(appType)) {
			return null;
		}

		try {
			return getResultList(Application.QUERY_GET_DISTINCT_APPLICATION_URL_BY_APP_TYPE, String.class, new Param("appType", appType));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting application URLs by application type: " + appType, e);
		}

		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Application> getAll() {
		try {
			return getResultList(Application.QUERY_GET_ALL, Application.class);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting all applications", e);
		}

		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeApplicationSettings(Integer applicationId) {
		if (applicationId == null) {
			getLogger().warning("Application ID is not provided");
			return;
		}

		try {
			Application application = getById(applicationId);
			if (application != null && application.getSettings() != null) {
				remove(application.getSettings());
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Could not remove application settings for application with ID " + applicationId  + ". Error message was: " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	public ApplicationSettings getSettingsByApplicationId(Integer applicationId) {
		if (applicationId == null) {
			return null;
		}

		try {
			return getSingleResult(ApplicationSettings.FIND_BY_APPLICATION_ID, ApplicationSettings.class, new Param(ApplicationSettings.PARAM_APPLICATION_ID, applicationId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting application settings by application ID: " + applicationId, e);
		}

		return null;
	}

	@Override
	public ApplicationSettings getSettingsById(Integer settingsId) {
		if (settingsId == null) {
			return null;
		}

		try {
			return getSingleResult(ApplicationSettings.FIND_BY_ID, ApplicationSettings.class, new Param(ApplicationSettings.PARAM_ID, settingsId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting applicataion settings by ID: " + settingsId, e);
		}

		return null;
	}


	@Override
	@Transactional(readOnly = false)
	public ReminderModel updateReminder(Integer reminderId, List<String> receiversUUIDs, Long timestamp, String message, List<Integer> dashboardRoleIds) {
		ApplicationReminder reminder = null;
		if (reminderId == null) {
			reminder = new ApplicationReminder();
		} else {
			List<ApplicationReminder> reminders = getResultList(ApplicationReminder.FIND_BY_IDS, ApplicationReminder.class, new Param(ApplicationReminder.PARAM_IDS, Arrays.asList(reminderId)));
			reminder = ListUtil.isEmpty(reminders) ? null : reminders.iterator().next();
		}
		if (reminder == null) {
			return null;
		}

		reminder.setReceivers(getUsers(receiversUUIDs));
		reminder.setTimestamp(timestamp == null ? null : new Timestamp(timestamp));
		reminder.setMessage(message);
		reminder.setDashboardRoles(dashboardRoleIds);

		if (reminder.getId() == null) {
			persist(reminder);
		} else {
			merge(reminder);
		}

		return reminder.getId() == null ? null : reminder;
	}

	@Override
	@Transactional(readOnly = false)
	public ApplicationSettings createNewApplicationSettings() {
		ApplicationSettings settings = new ApplicationSettings();
		persist(settings);
		return settings;
	}

	@Override
	@Transactional(readOnly = false)
	public <T extends Serializable> SettingsModel updateSettings(
			T id,
			Integer settingsId,
			Integer numberOfMonthsOfInnactivity,
			Set<String> thirdPartiesUUIDs,
			List<Integer> remindersIds,
			List<String> rolesKeys,
			List<Integer> signatureProfileIds,
			List<Integer> decisionTemplateIds,
			String invoicingType,
			Double price,
			Integer fixedInvoicedHours,
			List<ICFile> settingsFiles
	) {
		if (!(id instanceof Integer)) {
			return null;
		}

		Integer applicationId = (Integer) id;
		try {
			ApplicationSettings settings = null;
			if (settingsId == null) {
				settings = getSettingsByApplicationId(applicationId);
			} else {
				settings = getSettingsById(settingsId);
			}
			settings = settings == null ? new ApplicationSettings() : settings;

			settings.setAutoCloseAfterInnactiveForMonths(numberOfMonthsOfInnactivity);

			List<User> thirdParties = getUsers(thirdPartiesUUIDs);
			settings.setPredefinedListOfThirdPartiesToInvite(thirdParties);

			List<ReminderModel> reminders = null;
			if (!ListUtil.isEmpty(remindersIds)) {
				reminders = getResultList(ApplicationReminder.FIND_BY_IDS, ReminderModel.class, new Param(ApplicationReminder.PARAM_IDS, remindersIds));
			}
			settings.setReminders(reminders);

			List<ICRole> roles = null;
			if (!ListUtil.isEmpty(rolesKeys)) {
				roles = new ArrayList<>();
				for (String roleKey: rolesKeys) {
					if (StringUtil.isEmpty(roleKey)) {
						continue;
					}

					ICRole role = null;
					try {
						role = getSingleResult(ICRole.QUERY_FIND_ROLE_BY_KEY, ICRole.class, new Param("key", roleKey));
					} catch (Exception e) {
						getLogger().log(Level.WARNING, "Error getting role " + roleKey, e);
					}

					if (role != null) {
						roles.add(role);
					}
				}
			}
			settings.setRolesToHandle(roles);

			//Signature profiles
			List<SignatureProfile> signatureProfiles = null;
			if (!ListUtil.isEmpty(signatureProfileIds)) {
				signatureProfiles = getResultList(SignatureProfile.FIND_BY_IDS, SignatureProfile.class, new Param(SignatureProfile.PARAM_IDS, signatureProfileIds));
			}
			settings.setSignatureProfiles(signatureProfiles);

			//Decision templates
			List<DecisionTemplate> decisionTemplates = null;
			if (!ListUtil.isEmpty(decisionTemplateIds)) {
				decisionTemplates = getResultList(DecisionTemplate.FIND_BY_IDS, DecisionTemplate.class, new Param(DecisionTemplate.PARAM_IDS, decisionTemplateIds));
			}
			settings.setDecisionTemplates(decisionTemplates);

			//Invoicing data
			settings.setInvoicingType(invoicingType);
			settings.setFixedInvoicedHours(fixedInvoicedHours);
			settings.setPrice(price);

			settings.setApplicationId(applicationId);

			//Files
			if (!ListUtil.isEmpty(settingsFiles)) {
				settings.setFiles(settingsFiles);
			}

			if (settings.getId() == null) {
				persist(settings);
			} else {
				merge(settings);
			}

			return settings == null || settings.getId() == null ? null : settings;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error updating settings " + (settingsId == null ? CoreConstants.EMPTY : "(ID: " + settingsId + ") ") + "for application with ID " + applicationId, e);
		}

		return null;
	}

	private List<User> getUsers(Collection<String> uuids) {
		if (ListUtil.isEmpty(uuids)) {
			return null;
		}

		List<User> users = new ArrayList<>();
		for (String uuid: uuids) {
			User user = null;
			try {
				user = userDAO.getUserByUUID(uuid);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error getting user by UUID " + uuid, e);
			}
			if (user != null) {
				users.add(user);
			}
		}

		return users;
	}

	@Override
	@Transactional(readOnly = false)
	public SignatureProfile updateSignatureProfile(
			Integer signatureProfileId,
			Integer applicationSettingsId,
			String name,
			String roleTitle,
			String information,
			ICFile signature,
			boolean mandatoryEditPicture
	) {
		SignatureProfile signatureProfile = null;
		if (signatureProfileId == null) {
			signatureProfile = new SignatureProfile();
		} else {
			signatureProfile = getSignatureProfileById(signatureProfileId);
		}

		if (signatureProfile == null) {
			return null;
		}

		if (applicationSettingsId != null) {
			signatureProfile.setApplicationSettingsId(applicationSettingsId);
		}
		signatureProfile.setName(name);
		signatureProfile.setRoleTitle(roleTitle);
		signatureProfile.setInformation(information);
		if (mandatoryEditPicture) {
			signatureProfile.setSignature(signature);
		}

		if (signatureProfile.getId() == null) {
			persist(signatureProfile);
		} else {
			merge(signatureProfile);
		}

		return signatureProfile.getId() == null ? null : signatureProfile;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeSignatureProfile(Integer signatureProfileId) {
		if (signatureProfileId == null) {
			getLogger().warning("Signature profile ID is not provided");
			return;
		}

		try {
			SignatureProfile signatureProfile = getSignatureProfileById(signatureProfileId);
			if (signatureProfile != null) {
				remove(signatureProfile);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Could not remove the signature profile with ID " + signatureProfileId  + ". Error message was: " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public DecisionTemplate updateDecisionTemplate(
			Integer decisionTemplateId,
			Integer applicationSettingsId,
			String name,
			String templateContent,
			Integer signatureProfileId,
			String type,
			Boolean isDefault

	) {
		DecisionTemplate decisionTemplate = null;
		if (decisionTemplateId == null) {
			decisionTemplate = new DecisionTemplate();
		} else {
			decisionTemplate = getDecisionTemplateById(decisionTemplateId);
		}

		if (decisionTemplate == null) {
			return null;
		}

		if (applicationSettingsId != null) {
			decisionTemplate.setApplicationSettingsId(applicationSettingsId);
		}
		decisionTemplate.setName(name);
		decisionTemplate.setTemplateContent(templateContent);
		if (signatureProfileId != null && signatureProfileId.intValue() > -1) {
			decisionTemplate.setSignatureProfile(getSignatureProfileById(signatureProfileId));
		}

		if (!StringUtil.isEmpty(type)) {
			decisionTemplate.setType(type);
		}

		if (isDefault != null) {
			if (isDefault.booleanValue() == Boolean.TRUE) {
				decisionTemplate.setIsDefault(CoreConstants.CHAR_Y);
			} else {
				decisionTemplate.setIsDefault(CoreConstants.CHAR_N);
			}
		}

		IWContext iwc = CoreUtil.getIWContext();
		Integer userId = iwc == null || !iwc.isLoggedOn() ? null : iwc.getCurrentUserId();
		Timestamp now = IWTimestamp.RightNow().getTimestamp();

		if (decisionTemplate.getId() == null) {
			decisionTemplate.setCreatedBy(userId);
			decisionTemplate.setCreated(now);
			persist(decisionTemplate);
		} else {
			decisionTemplate.setEditedBy(userId);
			decisionTemplate.setEdited(now);
			merge(decisionTemplate);
		}

		return decisionTemplate.getId() == null ? null : decisionTemplate;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeDecisionTemplate(Integer decisionTemplateId) {
		if (decisionTemplateId == null) {
			getLogger().warning("Decision template ID is not provided");
			return;
		}

		try {
			DecisionTemplate decisionTemplate = getDecisionTemplateById(decisionTemplateId);
			if (decisionTemplate != null) {
				remove(decisionTemplate);
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Could not remove the decision template with ID " + decisionTemplateId  + ". Error message was: " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	public SignatureProfile getSignatureProfileById(Integer signatureProfileId) {
		if (signatureProfileId == null) {
			return null;
		}

		try {
			return getSingleResult(SignatureProfile.FIND_BY_ID, SignatureProfile.class, new Param(SignatureProfile.PARAM_ID, signatureProfileId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting signature profile by ID: " + signatureProfileId, e);
		}

		return null;
	}

	@Override
	public DecisionTemplate getDecisionTemplateById(Integer decisionTemplateId) {
		if (decisionTemplateId == null) {
			return null;
		}

		try {
			return getSingleResult(DecisionTemplate.FIND_BY_ID, DecisionTemplate.class, new Param(DecisionTemplate.PARAM_ID, decisionTemplateId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting decision template by ID: " + decisionTemplateId, e);
		}

		return null;
	}

	@Override
	public List<SignatureProfile> getSignatureProfilesByApplicationSettingsId(Integer applicationSettingsId) {
		if (applicationSettingsId == null) {
			return null;
		}

		try {
			return getResultList(SignatureProfile.FIND_BY_APPLICATION_SETTINGS_ID, SignatureProfile.class, new Param(SignatureProfile.PARAM_APPLICATION_SETTINGS_ID, applicationSettingsId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting signature profiles by application settings ID: " + applicationSettingsId, e);
		}

		return null;
	}

	@Override
	public List<Integer> getSignatureProfileIdsByApplicationSettingsId(Integer applicationSettingsId) {
		if (applicationSettingsId == null) {
			return null;
		}

		try {
			return getResultList(SignatureProfile.FIND_IDS_BY_APPLICATION_SETTINGS_ID, Integer.class, new Param(SignatureProfile.PARAM_APPLICATION_SETTINGS_ID, applicationSettingsId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting signature profile ids by application settings ID: " + applicationSettingsId, e);
		}

		return null;
	}

	@Override
	public List<DecisionTemplate> getDecisionTemplatesByApplicationSettingsId(Integer applicationSettingsId) {
		if (applicationSettingsId == null) {
			return null;
		}

		try {
			return getResultList(DecisionTemplate.FIND_BY_APPLICATION_SETTINGS_ID, DecisionTemplate.class, new Param(DecisionTemplate.PARAM_APPLICATION_SETTINGS_ID, applicationSettingsId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting decision templates by application settings ID: " + applicationSettingsId, e);
		}

		return null;
	}

	@Override
	public List<Integer> getDecisionTemplateIdsByApplicationSettingsId(Integer applicationSettingsId) {
		if (applicationSettingsId == null) {
			return null;
		}

		try {
			return getResultList(DecisionTemplate.FIND_IDS_BY_APPLICATION_SETTINGS_ID, Integer.class, new Param(DecisionTemplate.PARAM_APPLICATION_SETTINGS_ID, applicationSettingsId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting decision template ids by application settings ID: " + applicationSettingsId, e);
		}

		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeAllSignatureProfileForSettings(Integer applicationSettingsId) {
		if (applicationSettingsId == null) {
			return;
		}

		List<SignatureProfile> signatureProfiles = getSignatureProfilesByApplicationSettingsId(applicationSettingsId);
		if (!ListUtil.isEmpty(signatureProfiles)) {
			for (SignatureProfile sp : signatureProfiles) {
				if (sp != null) {
					removeSignatureProfile(sp.getId());
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void removeAllDecisionTemplatesForSettings(Integer applicationSettingsId) {
		if (applicationSettingsId == null) {
			return;
		}

		List<DecisionTemplate> decisionTemplates = getDecisionTemplatesByApplicationSettingsId(applicationSettingsId);
		if (!ListUtil.isEmpty(decisionTemplates)) {
			for (DecisionTemplate dt : decisionTemplates) {
				if (dt != null) {
					removeDecisionTemplate(dt.getId());
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void removeSignatureProfilesByIds(List<Integer> signatureProfileIds) {
		if (ListUtil.isEmpty(signatureProfileIds)) {
			return;
		}

		try {
			Query query = getEntityManager().createNamedQuery(SignatureProfile.DELETE_BY_IDS);
			query.setParameter(SignatureProfile.PARAM_IDS, signatureProfileIds);
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error deleting signature profile", e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void removeDecisionTemplatesByIds(List<Integer> decisionTemplateIds) {
		if (ListUtil.isEmpty(decisionTemplateIds)) {
			return;
		}

		try {
			Query query = getEntityManager().createNamedQuery(DecisionTemplate.DELETE_BY_IDS);
			query.setParameter(DecisionTemplate.PARAM_IDS, decisionTemplateIds);
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error deleting decision templates " + decisionTemplateIds, e);
		}
	}

	@Override
	public List<Integer> getAllApplicationsAndCasesThirdPartyUsers() {
		List<Integer> thirdPartyUserIds = new ArrayList<Integer>();
		List<Serializable[]> allThirdPartyUsersSerializableList = new ArrayList<Serializable[]>();
		try {
			//Third party users from all the applications
			String queryApp = "select distinct ainv.ic_user_id from " + ApplicationSettings.TABLE_NAME + "_inv ainv";
			List<Serializable[]> appThirdPartyUsers = null;
			try {
				appThirdPartyUsers = SimpleQuerier.executeQuery(queryApp, 1);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error executing query: " + queryApp, e);
			}
			if (!ListUtil.isEmpty(appThirdPartyUsers)) {
				allThirdPartyUsersSerializableList.addAll(appThirdPartyUsers);
			}

			//Third party users from all the cases
			String queryCases = "select distinct cinv.ic_user_id from " + CaseSettings.TABLE_NAME + "_inv cinv";
			List<Serializable[]> caseThirdPartyUsers = null;
			try {
				caseThirdPartyUsers = SimpleQuerier.executeQuery(queryCases, 1);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error executing query: " + queryCases, e);
			}
			if (!ListUtil.isEmpty(caseThirdPartyUsers)) {
				allThirdPartyUsersSerializableList.addAll(caseThirdPartyUsers);
			}

			//Add the unique user ids into the final list
			if (!ListUtil.isEmpty(allThirdPartyUsersSerializableList)) {
				for (Serializable[] userId: allThirdPartyUsersSerializableList) {
					if (ArrayUtil.isEmpty(userId)) {
						continue;
					}

					Serializable idSer = userId[0];
					if (idSer instanceof Number) {
						Integer userInt = ((Number) idSer).intValue();
						if (!thirdPartyUserIds.contains(userInt)) {
							thirdPartyUserIds.add(userInt);
						}
					}
				}
			}
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Could not fetch the all applications and cases third party users.", e);
		}

		return thirdPartyUserIds;
	}

	@Override
	public List<ReminderModel> getRemindersBySettingsId(Integer settingsId) {
		if (settingsId == null) {
			return null;
		}

		try {
			return getResultList(ApplicationSettings.FIND_REMINDERS_BY_APPLICATION_ID, ReminderModel.class, new Param(ApplicationSettings.PARAM_ID, settingsId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting reminders by settings ID: " + settingsId, e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.dao.ApplicationDAO#getApplicationKeys(com.idega.user.data.bean.Group)
	 */
	@Override
	public List<Integer> getApplicationKeys(Collection<Integer> groupPrimaryKeys) {
		if (ListUtil.isEmpty(groupPrimaryKeys)) {
			return Collections.emptyList();
		}

		try {
			return getResultList(ApplicationAccess.QUERY_GET_APPLICATIONS_IDS_BY_GROUPS_IDS, Integer.class, new Param("groupsIds", groupPrimaryKeys));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting applications IDs by groups IDs " + groupPrimaryKeys, e);
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.dao.ApplicationDAO#getApplicationLinks(com.idega.user.data.bean.Group)
	 */
	@Override
	public Collection<String> getApplicationLinks(Collection<Integer> groupPrimaryKeys) {
		List<String> applicationKeys = new ArrayList<>();

		if (!ListUtil.isEmpty(groupPrimaryKeys)) {
			Collection<Integer> applicationPrimaryKeys = getApplicationKeys(groupPrimaryKeys);
			if (!ListUtil.isEmpty(applicationPrimaryKeys)) {
				Map<String, Collection<? extends Serializable>> arguments = new HashMap<>();
				arguments.put("id", applicationPrimaryKeys);

				List<Application> applications = findAll(Application.class, arguments);
				for (Application application : applications) {
					if (!StringUtil.isEmpty(application.getUrl())) {
						applicationKeys.add(application.getUrl());
					}
				}
			}
		}

		return applicationKeys;
	}

	@Override
	public Collection<Application> getFilteredApplications(List<Integer> groupsIds, Collection<Application> applications) {
		Collection<Application> filteredApplications = new ArrayList<>();
		if (ListUtil.isEmpty(groupsIds) || ListUtil.isEmpty(applications)) {
			return filteredApplications;
		}

		List<Integer> keys = getApplicationKeys(groupsIds);
		if (!ListUtil.isEmpty(keys)) {
			for (Application application : applications) {
				if (keys.contains(application.getPrimaryKey())) {
					filteredApplications.add(application);
				}
			}
		}

		return filteredApplications;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.dao.ApplicationDAO#getFilteredApplications(com.idega.user.data.bean.User, java.util.Collection)
	 */
	@Override
	public Collection<Application> getFilteredApplications(User user, Collection<Application> applications) {
		if (user == null || ListUtil.isEmpty(applications)) {
			return Collections.emptyList();
		}

		List<Integer> groupsIds = getGroupDAO().getAllGroupsIdsForUser(user, CoreUtil.getIWContext());
		if (ListUtil.isEmpty(groupsIds)) {
			return Collections.emptyList();
		}

		return getFilteredApplications(groupsIds, applications);
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.dao.ApplicationDAO#getFilteredApplications(java.util.Collection)
	 */
	@Override
	public Collection<Application> getFilteredApplications(Collection<Application> applications) {
		if (ListUtil.isEmpty(applications)) {
			return Collections.emptyList();
		}

		IWContext context = CoreUtil.getIWContext();
		if (context == null || !context.isLoggedOn()) {
			return Collections.emptyList();
		}

		return getFilteredApplications(context.getLoggedInUser(), applications);
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.dao.ApplicationDAO#insert(is.idega.idegaweb.egov.application.data.bean.Application, com.idega.user.data.bean.Group)
	 */
	@Transactional(readOnly = false)
	@Override
	public Application insert(Application application, Group group, Integer level) {
		if (application != null && group != null) {
			return insert(application.getId(), group, level);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.dao.ApplicationDAO#insert(java.lang.Integer, java.lang.Integer)
	 */
	@Transactional(readOnly = false)
	@Override
	public Application insert(Integer applicationId, Group group, Integer level) {
		if (applicationId == null || group == null) {
			return null;
		}

		try {
			ApplicationAccess aa = getSingleResult(
					ApplicationAccess.QUERY_GET_BY_APPLICATION_ID_AND_GROUP_ID,
					ApplicationAccess.class,
					new Param("applicationId", applicationId),
					new Param("groupId", group.getID())
			);
			if (aa == null) {
				aa = new ApplicationAccess();
			}
			aa.setApplicationId(applicationId);
			aa.setGroup(group);
			aa.setLevel(level);

			if (aa.getId() == null) {
				persist(aa);
			} else {
				merge(aa);
			}

			if (aa.getId() == null) {
				return null;
			}

			return findById(applicationId);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error inserting application access for application " + applicationId + ", group " + group + (level == null ? CoreConstants.EMPTY : " and level " + level), e);
		}

		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public ApplicationAccess updateApplicationAccess(Long appAccId, Integer applicationId, Integer groupId, Integer level) {
		if (applicationId == null || groupId == null || groupId < 0) {
			return null;
		}

		ApplicationAccess access = null;
		try {
			Group group = getGroupDAO().findGroup(groupId);
			if (group == null) {
				getLogger().warning("Invalid group ID: " + groupId);
				return null;
			}

			if (appAccId != null) {
				access = getSingleResult(ApplicationAccess.QUERY_GET_BY_ID, ApplicationAccess.class, new Param("id", appAccId));
			}
			if (access == null) {
				access = new ApplicationAccess();
			}
			access.setApplicationId(applicationId);
			access.setGroup(group);
			access.setLevel(level);

			if (access.getId() == null) {
				persist(access);
			} else {
				merge(access);
			}

			if (access.getId() == null) {
				return null;
			}

			return access;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error updating application access " + access, e);
		}

		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean removeReminderById(Integer reminderId) {
		if (reminderId == null) {
			return false;
		}

		try {
			//Remove reminder users
			Query qReminderUsers = getEntityManager().createNativeQuery("delete from " + ApplicationReminder.TABLE_NAME + "_rec" + " where " + ApplicationReminder.COLUMN_ID + " = ?");
			qReminderUsers.setParameter(1, reminderId);
			qReminderUsers.executeUpdate();

			//Remove reminder dashboard roles
			Query qReminderDashboardRoles = getEntityManager().createNativeQuery("delete from " + ApplicationReminder.TABLE_NAME + "_dr" + " where " + ApplicationReminder.JOIN_COLUMN_REMINDER_ID + " = ?");
			qReminderDashboardRoles.setParameter(1, reminderId);
			qReminderDashboardRoles.executeUpdate();

			//Remove reminder itself
			Query qReminder = getEntityManager().createNativeQuery("delete from " + ApplicationReminder.TABLE_NAME + " where " + ApplicationReminder.COLUMN_ID + " = ?");
			qReminder.setParameter(1, reminderId);
			qReminder.executeUpdate();

			return true;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error removing the reminder and all it's children: " + reminderId, e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.dao.ApplicationDAO#remove(is.idega.idegaweb.egov.application.data.bean.Application, com.idega.user.data.bean.Group)
	 */
	@Transactional(readOnly = false)
	@Override
	public Application remove(Application application, Group group) {
		if (application != null && group != null) {
			return remove(application.getId(), group);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.dao.ApplicationDAO#remove(java.lang.Integer, java.lang.Integer)
	 */
	@Transactional(readOnly = false)
	@Override
	public Application remove(Integer applicationId, Group group) {
		if (applicationId == null || group == null) {
			return null;
		}

		try {
			ApplicationAccess aa = getSingleResult(
					ApplicationAccess.QUERY_GET_BY_APPLICATION_ID_AND_GROUP_ID,
					ApplicationAccess.class,
					new Param("applicationId", applicationId),
					new Param("groupId", group.getID())
			);
			if (aa == null) {
				return null;
			}

			remove(aa);

			return findById(applicationId);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error removing application access for application " + applicationId + " and group " + group, e);
		}

		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeApplicationAccesses(Integer applicationId) {
		List<ApplicationAccess> accesses = getAllForApplication(applicationId);
		if (ListUtil.isEmpty(accesses)) {
			return;
		}

		for (ApplicationAccess access: accesses) {
			if (access == null || access.getId() == null) {
				continue;
			}

			try {
				remove(access);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error removing application access " + access, e);
			}
		}
	}

	@Override
	public List<ApplicationAccess> getAllForApplication(Integer applicationId) {
		if (applicationId == null) {
			return null;
		}

		try {
			return getResultList(ApplicationAccess.QUERY_GET_BY_APPLICATION_ID, ApplicationAccess.class, new Param("applicationId", applicationId));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting accesses for application: " + applicationId, e);
		}

		return null;
	}

}