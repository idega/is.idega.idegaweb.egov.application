package is.idega.idegaweb.egov.application.data.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.idega.block.process.data.dao.SettingsDAO;
import com.idega.core.file.data.bean.ICFile;
import com.idega.core.persistence.GenericDao;
import com.idega.user.data.bean.Group;
import com.idega.user.data.bean.User;

import is.idega.idegaweb.egov.application.data.bean.Application;
import is.idega.idegaweb.egov.application.data.bean.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.bean.ApplicationSettings;
import is.idega.idegaweb.egov.application.data.bean.DecisionTemplate;
import is.idega.idegaweb.egov.application.data.bean.SignatureProfile;

public interface ApplicationDAO extends GenericDao, SettingsDAO {

	public static final String BEAN_NAME = "egovApplicationDAO";
	
	Application findById(Integer primaryKey);

	Application getById(Integer id);

	List<Application> getAll();

	List<ApplicationCategory> getAllCategoriesOrderedByPriority();

	List<Application> getApplicationsByCategoryOrderedByPriority(ApplicationCategory category);

	Application store(Application app);

	Application findByUri(String uri);

	public List<String> getDistinctApplicationURLByAppType(String appType);

	public ApplicationSettings getSettingsByApplicationId(Integer applicationId);

	public ApplicationSettings getSettingsById(Integer settingsId);

	public void removeApplicationSettings(Integer applicationId);

	public SignatureProfile getSignatureProfileById(Integer signatureProfileId);

	public SignatureProfile updateSignatureProfile(
			Integer signatureProfileId,
			Integer applicationSettingsId,
			String name,
			String roleTitle,
			String information,
			ICFile signature,
			boolean mandatoryEditPicture
	);

	public void removeSignatureProfile(Integer signatureProfileId);
	public void removeAllSignatureProfileForSettings(Integer applicationSettingsId);

	public DecisionTemplate getDecisionTemplateById(Integer decisionTemplateId);

	public DecisionTemplate updateDecisionTemplate(
			Integer decisionTemplateId,
			Integer applicationSettingsId,
			String name,
			String templateContent,
			Integer signatureProfileId,
			String type,
			Boolean isDefault
	);

	public void removeDecisionTemplate(Integer decisionTemplateId);
	public void removeAllDecisionTemplatesForSettings(Integer applicationSettingsId);

	public List<SignatureProfile> getSignatureProfilesByApplicationSettingsId(Integer applicationSettingsId);

	public List<DecisionTemplate> getDecisionTemplatesByApplicationSettingsId(Integer applicationSettingsId);

	public ApplicationSettings createNewApplicationSettings();

	public List<Integer> getSignatureProfileIdsByApplicationSettingsId(Integer applicationSettingsId);

	public List<Integer> getDecisionTemplateIdsByApplicationSettingsId(Integer applicationSettingsId);

	public void removeSignatureProfilesByIds(List<Integer> signatureProfileIds);
	public void removeDecisionTemplatesByIds(List<Integer> decisionTemplateIds);

	/**
	 * 
	 * @param groupPrimaryKeys is {@link Collection} of {@link Group#getId()} to get {@link Application}s for, not <code>null</code>
	 * @return {@link Collection} of {@link Application#getId()} or {@link Collections#emptyList()} on failure;
	 */
	Collection<Long> getApplicationKeys(Collection<Integer> groupPrimaryKeys);

	/**
	 * 
	 * @param groups is {@link Collection} of {@link Group#getId()} to get {@link Application}s for, not <code>null</code>
	 * @return {@link Collection} of {@link Application#getUrl()} or {@link Collections#emptyList()} on failure;
	 */
	Collection<String> getApplicationLinks(Collection<Integer> groupPrimaryKeys);

	/**
	 * @param group to get {@link Application}s for, not <code>null</code>
	 * @param applications to be filtered, not <code>null</code>
	 * @return filtered {@link Application}s or {@link Collections#emptyList()} on failure
	 */
	Collection<Application> getFilteredApplications(Integer groupId, Collection<Application> applications);

	/**
	 * 
	 * @param user to get {@link Application}s for, not <code>null</code>
	 * @param applications to be filtered, not <code>null</code>
	 * @return filtered {@link Application}s or {@link Collections#emptyList()} on failure
	 */
	Collection<Application> getFilteredApplications(User user, Collection<Application> applications);

	/**
	 * 
	 * @param applications to be filtered, not <code>null</code>
	 * @return filtered {@link Application}s or {@link Collections#emptyList()} on failure
	 */
	Collection<Application> getFilteredApplications(Collection<Application> applications);

	Application insert(Application application, Group group);

	Application remove(Application application, Group group);

	Application insert(Integer applicationId, Integer groupId);

	Application remove(Integer applicationId, Integer groupId);
}