package is.idega.idegaweb.egov.application.data.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.idega.block.process.data.model.ReminderModel;
import com.idega.block.process.data.model.SettingsModel;
import com.idega.core.accesscontrol.data.bean.ICRole;
import com.idega.user.data.bean.User;
import com.idega.util.DBUtil;

@Entity
@Table(name = ApplicationSettings.TABLE_NAME)
@Cacheable
@NamedQueries({
	@NamedQuery(name = ApplicationSettings.FIND_BY_ID, query = "select s from ApplicationSettings s where s.id = :" + ApplicationSettings.PARAM_ID),
	@NamedQuery(name = ApplicationSettings.FIND_BY_APPLICATION_ID, query = "select s from ApplicationSettings s where s.applicationId = :" + ApplicationSettings.PARAM_APPLICATION_ID)
})
public class ApplicationSettings implements Serializable, SettingsModel {

	private static final long serialVersionUID = 3355823446588878757L;

	public static final String 	TABLE_NAME = "egov_application_settings",
								COLUMN_ID = TABLE_NAME + "_id",
								COLUMN_APPLICATION_ID = "application_id",

								FIND_BY_ID = "ApplicationSettings.findById",
								FIND_BY_APPLICATION_ID = "ApplicationSettings.findByApplicationId",

								PARAM_ID = "applicationSettingId",
								PARAM_APPLICATION_ID = "applicationId";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ID)
	private Integer id;

	@Column(name = ApplicationSettings.COLUMN_APPLICATION_ID, unique = true)
	private Integer applicationId;

	@Column(name = "months_of_innactivity")
	private Integer numberOfMonthsOfInnactivity;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = ApplicationReminder.class)
	@JoinTable(name = TABLE_NAME + "_rem", joinColumns = { @JoinColumn(name = COLUMN_ID) }, inverseJoinColumns = { @JoinColumn(name = ApplicationReminder.COLUMN_ID, table = ApplicationReminder.TABLE_NAME) })
	private List<ReminderModel> reminders;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = User.class)
	@JoinTable(name = TABLE_NAME + "_inv", joinColumns = { @JoinColumn(name = COLUMN_ID) }, inverseJoinColumns = { @JoinColumn(name = User.COLUMN_USER_ID) })
	private List<User> thirdPartiesToInvite;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = ICRole.class)
	@JoinTable(name = TABLE_NAME + "_rol", joinColumns = { @JoinColumn(name = COLUMN_ID) }, inverseJoinColumns = { @JoinColumn(name = ICRole.COLUMN_ROLE_KEY, table = ICRole.ENTITY_NAME) })
	private List<ICRole> roles;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public void setAutoCloseAfterInnactiveForMonths(Integer numberOfMonthsOfInnactivity) {
		this.numberOfMonthsOfInnactivity = numberOfMonthsOfInnactivity;
	}

	@Override
	public Integer getAutoCloseAfterInnactiveForMonths() {
		return numberOfMonthsOfInnactivity;
	}

	@Override
	public void setReminders(List<ReminderModel> reminders) {
		this.reminders = reminders;
	}

	@Override
	public List<ReminderModel> getReminders() {
		reminders = DBUtil.getInstance().lazyLoad(reminders);
		return reminders;
	}

	@Override
	public void setPredefinedListOfThirdPartiesToInvite(List<User> thirdPartiesToInvite) {
		this.thirdPartiesToInvite = thirdPartiesToInvite;
	}

	@Override
	public List<User> getPredefinedListOfThirdPartiesToInvite() {
		thirdPartiesToInvite = DBUtil.getInstance().lazyLoad(thirdPartiesToInvite);
		return thirdPartiesToInvite;
	}

	@Override
	public void setRolesToHandle(List<ICRole> roles) {
		this.roles = roles;
	}

	@Override
	public List<ICRole> getRolesToHandle() {
		roles = DBUtil.getInstance().lazyLoad(roles);
		return roles;
	}

	public Integer getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}


}