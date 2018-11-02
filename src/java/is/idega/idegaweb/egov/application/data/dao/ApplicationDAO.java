package is.idega.idegaweb.egov.application.data.dao;

import java.util.List;

import com.idega.block.process.data.dao.SettingsDAO;
import com.idega.block.process.data.model.ReminderModel;
import com.idega.core.file.data.bean.ICFile;
import com.idega.core.persistence.GenericDao;

import is.idega.idegaweb.egov.application.data.bean.Application;
import is.idega.idegaweb.egov.application.data.bean.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.bean.ApplicationSettings;
import is.idega.idegaweb.egov.application.data.bean.DecisionTemplate;
import is.idega.idegaweb.egov.application.data.bean.SignatureProfile;

public interface ApplicationDAO extends GenericDao, SettingsDAO {

	public static final String BEAN_NAME = "egovApplicationDAO";

	Application getById(Integer id);

	List<Application> getAll();

	List<ApplicationCategory> getAllCategoriesOrderedByPriority();

	List<Application> getApplicationsByCategoryOrderedByPriority(ApplicationCategory category);

	Application store(Application app);

	Application findByUri(String uri);

	public List<String> getDistinctApplicationURLByAppType(String appType);

	public ApplicationSettings getSettingsByApplicationId(Integer applicationId);

	public ApplicationSettings getSettingsById(Integer settingsId);

	@Override
	public ReminderModel updateReminder(Integer reminderId, List<String> receiversUUIDs, Long timestamp, String message);

	public ApplicationSettings updateApplicationSettings(
			Integer applicationId,
			Integer settingsId,
			Integer numberOfMonthsOfInnactivity,
			List<String> thirdPartiesUUIDs,
			List<Integer> remindersIds,
			List<String> rolesKeys,
			List<Integer> signatureProfileIds,
			List<Integer> decisionTemplateIds,
			String invoicingType,
			Double price,
			Integer fixedInvoicedHours

	);

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
			Integer signatureProfileId
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


}