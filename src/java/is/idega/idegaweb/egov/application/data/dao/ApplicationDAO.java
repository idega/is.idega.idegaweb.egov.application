package is.idega.idegaweb.egov.application.data.dao;

import java.util.List;

import com.idega.block.process.data.model.ReminderModel;
import com.idega.core.persistence.GenericDao;

import is.idega.idegaweb.egov.application.data.bean.Application;
import is.idega.idegaweb.egov.application.data.bean.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.bean.ApplicationSettings;

public interface ApplicationDAO extends GenericDao {

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

	public ReminderModel updateReminder(Integer reminderId, List<String> receiversUUIDs, Long timestamp, String message);

	public ApplicationSettings updateApplicationSettings(
			Integer applicationId,
			Integer settingsId,
			Integer numberOfMonthsOfInnactivity,
			List<String> thirdPartiesUUIDs,
			List<Integer> remindersIds,
			List<String> rolesKeys
	);

	public void removeApplicationSettings(Integer applicationId);
}