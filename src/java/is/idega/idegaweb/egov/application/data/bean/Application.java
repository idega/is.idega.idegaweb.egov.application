package is.idega.idegaweb.egov.application.data.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import com.idega.block.process.data.bean.CaseCode;
import com.idega.block.process.data.model.CaseCodeModel;
import com.idega.block.text.data.LocalizedTextBMPBean;
import com.idega.block.text.data.bean.LocalizedText;
import com.idega.block.text.model.LocalizedTextModel;
import com.idega.util.CoreConstants;
import com.idega.util.DBUtil;
import com.idega.util.expression.ELUtil;

import is.idega.idegaweb.egov.application.ApplicationUtil;
import is.idega.idegaweb.egov.application.data.ApplicationBMPBean;
import is.idega.idegaweb.egov.application.data.dao.ApplicationDAO;
import is.idega.idegaweb.egov.application.model.ApplicationModel;

@Entity
@Table(name = Application.TABLE_NAME)
@Cacheable
@NamedNativeQueries({
	@NamedNativeQuery(name = Application.QUERY_GET_BY_ID, query = "from Application a where a.id = :id")
})
public class Application implements Serializable, ApplicationModel {

	private static final long serialVersionUID = 2834396283048626441L;

	static final String TABLE_NAME = ApplicationBMPBean.TABLE_NAME;

	public static final String QUERY_GET_BY_ID = "application.getById";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = ApplicationBMPBean.EGOV_APPLICATION_ID)
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

	@Column(name = ApplicationBMPBean.TIMES_CLICKED)
	private Integer timesClicked;

	@Column(name = ApplicationBMPBean.COLUMN_LOGIN_PAGE_URL)
	private String loginPageUrl;

	@Column(name = ApplicationBMPBean.COLUMN_SHOW_IN_IFRAME, length = 1)
	private Character showInIframe;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = ApplicationBMPBean.EGOV_APPLICATION_NAME_LOC_TEXT,
		joinColumns = @JoinColumn(name = ApplicationBMPBean.EGOV_APPLICATION_ID),
		inverseJoinColumns = @JoinColumn(name = LocalizedTextBMPBean.TABLE_NAME + "_ID")
	)
	private List<LocalizedText> nameLocalizedTexts;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = ApplicationBMPBean.EGOV_APPLICATION_URL_LOC_TEXT,
		joinColumns = @JoinColumn(name = ApplicationBMPBean.EGOV_APPLICATION_ID),
		inverseJoinColumns = @JoinColumn(name = LocalizedTextBMPBean.TABLE_NAME + "_ID")
	)
	private List<LocalizedText> urlLocalizedTexts;

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
	public LocalizedTextModel getLocalizedText(int icLocaleId) {
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

	@Override
	public int getTimesClicked() {
		return timesClicked == null ? 0 : timesClicked;
	}

	@Override
	public void setTimesClicked(int timesClicked) {
		this.timesClicked = timesClicked;
	}

	@Override
	public void store() {
		ApplicationDAO applicationDAO = ELUtil.getInstance().getBean(ApplicationDAO.BEAN_NAME);
		applicationDAO.store(this);
	}

	public String getLoginPageUrl() {
		return loginPageUrl;
	}

	public void setLoginPageUrl(String loginPageUrl) {
		this.loginPageUrl = loginPageUrl;
	}

	@Override
	public String getUrlByLocale(Locale locale) {
		return ApplicationUtil.getUrlByLocale(this, locale);
	}

	@Override
	public String getLoginPageURL() {
		return getLoginPageUrl();
	}

	public List<LocalizedText> getNameLocalizedTexts() {
		nameLocalizedTexts = DBUtil.getInstance().lazyLoad(nameLocalizedTexts);
		return nameLocalizedTexts;
	}

	public void setNameLocalizedTexts(List<LocalizedText> nameLocalizedTexts) {
		this.nameLocalizedTexts = nameLocalizedTexts;
	}

	public List<LocalizedText> getUrlLocalizedTexts() {
		urlLocalizedTexts = DBUtil.getInstance().lazyLoad(urlLocalizedTexts);
		return urlLocalizedTexts;
	}

	public void setUrlLocalizedTexts(List<LocalizedText> urlLocalizedTexts) {
		this.urlLocalizedTexts = urlLocalizedTexts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LocalizedText> getLocalizedTexts() {
		return getUrlLocalizedTexts();
	}

	@Override
	public String getLocalizedName() {
		return ApplicationUtil.getLocalizedName(this);
	}

	@Override
	public String getLocalizedName(int icLocaleId) {
		return ApplicationUtil.getLocalizedName(this, icLocaleId);
	}

	@Override
	public Boolean getShowInIframe() {
		return showInIframe == null ? false : showInIframe == CoreConstants.CHAR_Y;
	}

	@Override
	public void setShowInIframe(Boolean showInIframe) {
		this.showInIframe = (showInIframe == null || !showInIframe.booleanValue()) ? CoreConstants.CHAR_N: CoreConstants.CHAR_Y;
	}

}