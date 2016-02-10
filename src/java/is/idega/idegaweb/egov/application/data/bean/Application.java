package is.idega.idegaweb.egov.application.data.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.idega.block.process.data.bean.CaseCode;
import com.idega.block.process.data.model.CaseCodeModel;
import com.idega.block.text.data.LocalizedText;
import com.idega.util.CoreConstants;

import is.idega.idegaweb.egov.application.ApplicationUtil;
import is.idega.idegaweb.egov.application.data.ApplicationBMPBean;
import is.idega.idegaweb.egov.application.model.ApplicationModel;

@Entity
@Table(name = ApplicationBMPBean.TABLE_NAME)
@Cacheable
public class Application implements Serializable, ApplicationModel {

	private static final long serialVersionUID = 2834396283048626441L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = ApplicationBMPBean.TABLE_NAME + "_ID")
	private Integer id;

	@Column(name = ApplicationBMPBean.VISIBLE, length = 1)
	private Character visible;

	@Column(name = ApplicationBMPBean.ELECTRONIC, length = 1)
	private Character electronic;

	@Column(name = ApplicationBMPBean.REQUIRES_LOGIN, length = 1)
	private Character requiresLogin;

	@Column(name = ApplicationBMPBean.OPENS_IN_NEW_WINDOW, length = 1)
	private Character opensInNewWindow;

	@Column(name = ApplicationBMPBean.HIDDEN_FROM_GUESTS, length = 1)
	private Character hiddenFromGuests;

	@Column(name = ApplicationBMPBean.APP_TYPE)
	private String appType;

	@Column(name = ApplicationBMPBean.NAME, length = 50)
	private String name;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = ApplicationBMPBean.CASE_CODE)
	private CaseCode caseCode;

	@Column(name = ApplicationBMPBean.AGE_FROM)
	private Integer ageFrom;

	@Column(name = ApplicationBMPBean.AGE_TO)
	private Integer ageTo;

	@Column(name = ApplicationBMPBean.ENABLED_FROM)
	private Timestamp enabledFrom;

	@Column(name = ApplicationBMPBean.ENABLED_TO)
	private Timestamp enabledTo;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = ApplicationBMPBean.CATEGORY)
	private ApplicationCategory category;

	@Column(name = ApplicationBMPBean.PRIORITY)
	private Integer priority;

	@Column(name = ApplicationBMPBean.URL)
	private String url;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setVisible(Character visible) {
		this.visible = visible;
	}

	@Override
	public Object getPrimaryKey() {
		return getId();
	}

	@Override
	public boolean getVisible() {
		return visible == null ? false : visible == CoreConstants.CHAR_Y;
	}

	@Override
	public boolean isEnabled() {
		return ApplicationUtil.isEnabled(this);
	}

	@Override
	public boolean getElectronic() {
		return electronic == null ? false : electronic == CoreConstants.CHAR_Y;
	}

	@Override
	public boolean getRequiresLogin() {
		return requiresLogin == null ? false : requiresLogin == CoreConstants.CHAR_Y;
	}

	@Override
	public boolean getOpensInNewWindow() {
		return opensInNewWindow == null ? false : opensInNewWindow == CoreConstants.CHAR_Y;
	}

	@Override
	public boolean getHiddenFromGuests() {
		return hiddenFromGuests == null ? false : hiddenFromGuests == CoreConstants.CHAR_Y;
	}

	@Override
	public String getAppType() {
		return appType;
	}

	@Override
	public LocalizedText getLocalizedText(int icLocaleId) {
		return ApplicationUtil.getLocalizedText(this, icLocaleId);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public CaseCodeModel getCaseCode() {
		return caseCode;
	}

	@Override
	public int getAgeFrom() {
		return ageFrom == null ? 0 : ageFrom;
	}

	@Override
	public int getAgeTo() {
		return ageTo == null ? 0 : ageTo;
	}

	public void setElectronic(Character electronic) {
		this.electronic = electronic;
	}

	@Override
	public Timestamp getEnabledFrom() {
		return enabledFrom;
	}

	@Override
	public Timestamp getEnabledTo() {
		return enabledTo;
	}

	public void setRequiresLogin(Character requiresLogin) {
		this.requiresLogin = requiresLogin;
	}

	public void setOpensInNewWindow(Character opensInNewWindow) {
		this.opensInNewWindow = opensInNewWindow;
	}

	public void setHiddenFromGuests(Character hiddenFromGuests) {
		this.hiddenFromGuests = hiddenFromGuests;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCaseCode(CaseCode caseCode) {
		this.caseCode = caseCode;
	}

	public void setAgeFrom(Integer ageFrom) {
		this.ageFrom = ageFrom;
	}

	public void setAgeTo(Integer ageTo) {
		this.ageTo = ageTo;
	}

	public void setEnabledFrom(Timestamp enabledFrom) {
		this.enabledFrom = enabledFrom;
	}

	public void setEnabledTo(Timestamp enabledTo) {
		this.enabledTo = enabledTo;
	}

	@Override
	public String toString() {
		return getPrimaryKey().toString();
	}

	public ApplicationCategory getCategory() {
		return category;
	}

	public void setCategory(ApplicationCategory category) {
		this.category = category;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}