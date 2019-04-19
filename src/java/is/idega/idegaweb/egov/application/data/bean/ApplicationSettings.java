package is.idega.idegaweb.egov.application.data.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
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
import com.idega.core.file.data.bean.ICFile;
import com.idega.user.data.bean.User;
import com.idega.util.DBUtil;

@Entity
@Table(name = ApplicationSettings.TABLE_NAME)
@Cacheable
@NamedQueries({
	@NamedQuery(name = ApplicationSettings.FIND_BY_ID, query = "select s from ApplicationSettings s where s.id = :" + ApplicationSettings.PARAM_ID),
	@NamedQuery(name = ApplicationSettings.FIND_BY_APPLICATION_ID, query = "select s from ApplicationSettings s where s.applicationId = :" + ApplicationSettings.PARAM_APPLICATION_ID),
	@NamedQuery(name = ApplicationSettings.FIND_REMINDERS_BY_APPLICATION_ID, query = "select s.reminders from ApplicationSettings s where s.id = :" + ApplicationSettings.PARAM_ID)
})
public class ApplicationSettings implements Serializable, SettingsModel {

	private static final long serialVersionUID = 3355823446588878757L;

	public static final String 	TABLE_NAME = "egov_application_settings",
								COLUMN_ID = TABLE_NAME + "_id",
								COLUMN_APPLICATION_ID = "application_id",
								COLUMN_PRICE = "price",
								COLUMN_INVOICING_TYPE = "invoicing_type",
								COLUMN_FIXED_INVOICED_HOURS = "fixed_invoiced_hours",
								COLUMN_FILE = "file",

								FIND_BY_ID = "ApplicationSettings.findById",
								FIND_BY_APPLICATION_ID = "ApplicationSettings.findByApplicationId",
								FIND_REMINDERS_BY_APPLICATION_ID = "ApplicationSettings.findRemindersByApplicationId",

								PARAM_ID = "applicationSettingId",
								PARAM_APPLICATION_ID = "applicationId";

	private static final String TABLE_APPLICATION_SETTINGS_FILES = TABLE_NAME + "_fl";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ID)
	private Integer id;

	@Column(name = ApplicationSettings.COLUMN_APPLICATION_ID, unique = true)
	private Integer applicationId;

	@Column(name = "months_of_innactivity")
	private Integer numberOfMonthsOfInnactivity;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = ApplicationReminder.class, cascade = { CascadeType.REMOVE })
	@JoinTable(name = TABLE_NAME + "_rem", joinColumns = { @JoinColumn(name = COLUMN_ID) }, inverseJoinColumns = { @JoinColumn(name = ApplicationReminder.COLUMN_ID, table = ApplicationReminder.TABLE_NAME) })
	private List<ReminderModel> reminders;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = User.class)
	@JoinTable(name = TABLE_NAME + "_inv", joinColumns = { @JoinColumn(name = COLUMN_ID) }, inverseJoinColumns = { @JoinColumn(name = User.COLUMN_USER_ID) })
	private List<User> thirdPartiesToInvite;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = ICRole.class)
	@JoinTable(name = TABLE_NAME + "_rol", joinColumns = { @JoinColumn(name = COLUMN_ID) }, inverseJoinColumns = { @JoinColumn(name = ICRole.COLUMN_ROLE_KEY, table = ICRole.ENTITY_NAME) })
	private List<ICRole> roles;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = SignatureProfile.class)
	@JoinTable(name = TABLE_NAME + "_sp", joinColumns = { @JoinColumn(name = COLUMN_ID) }, inverseJoinColumns = { @JoinColumn(name = SignatureProfile.COLUMN_ID, table = SignatureProfile.TABLE_NAME) })
	private List<SignatureProfile> signatureProfiles;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = DecisionTemplate.class)
	@JoinTable(name = TABLE_NAME + "_dt", joinColumns = { @JoinColumn(name = COLUMN_ID) }, inverseJoinColumns = { @JoinColumn(name = DecisionTemplate.COLUMN_ID, table = DecisionTemplate.TABLE_NAME) })
	private List<DecisionTemplate> decisionTemplates;

	@Column(name = COLUMN_INVOICING_TYPE)
	private String invoicingType;

	@Column(name = COLUMN_PRICE)
	private Double price;

	@Column(name = COLUMN_FIXED_INVOICED_HOURS)
	private Integer fixedInvoicedHours;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = ICFile.class)
	@JoinTable(name = TABLE_APPLICATION_SETTINGS_FILES, joinColumns = { @JoinColumn(name = COLUMN_ID) }, inverseJoinColumns = { @JoinColumn(name = ICFile.COLUMN_FILE_ID, table = ICFile.ENTITY_NAME) })
	private List<ICFile> files;

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

	public List<SignatureProfile> getSignatureProfiles() {
		signatureProfiles = DBUtil.getInstance().lazyLoad(signatureProfiles);
		return signatureProfiles;
	}

	public void setSignatureProfiles(List<SignatureProfile> signatureProfiles) {
		this.signatureProfiles = signatureProfiles;
	}

	public List<DecisionTemplate> getDecisionTemplates() {
		decisionTemplates = DBUtil.getInstance().lazyLoad(decisionTemplates);
		return decisionTemplates;
	}

	public void setDecisionTemplates(List<DecisionTemplate> decisionTemplates) {
		this.decisionTemplates = decisionTemplates;
	}

	public String getInvoicingType() {
		return invoicingType;
	}

	public void setInvoicingType(String invoicingType) {
		this.invoicingType = invoicingType;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getFixedInvoicedHours() {
		return fixedInvoicedHours;
	}

	public void setFixedInvoicedHours(Integer fixedInvoicedHours) {
		this.fixedInvoicedHours = fixedInvoicedHours;
	}

	public List<ICFile> getFiles() {
		files = DBUtil.getInstance().lazyLoad(files);
		return files;
	}

	public void setFiles(List<ICFile> files) {
		this.files = files;
	}

}