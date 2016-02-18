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
import javax.persistence.Table;

import com.idega.block.text.data.LocalizedTextBMPBean;
import com.idega.block.text.data.bean.LocalizedText;
import com.idega.block.text.model.LocalizedTextModel;
import com.idega.util.DBUtil;
import com.idega.util.StringUtil;

import is.idega.idegaweb.egov.application.ApplicationUtil;
import is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean;
import is.idega.idegaweb.egov.application.model.ApplicationCategoryModel;

@Entity
@Table(name = ApplicationCategoryBMPBean.TABLE_NAME)
@Cacheable
public class ApplicationCategory implements Serializable, ApplicationCategoryModel {

	private static final long serialVersionUID = 5182475755851708707L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = ApplicationCategoryBMPBean.TABLE_NAME + "_ID")
	private Integer id;

	@Column(name = ApplicationCategoryBMPBean.DESCRIPTION, length = 4000)
	private String description;

	@Column(name = ApplicationCategoryBMPBean.NAME, length = 50)
	private String name;

	@Column(name = ApplicationCategoryBMPBean.PRIORITY)
	private Integer priority;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = ApplicationCategoryBMPBean.EGOV_APPLICATION_CATEGORY_NAME_LOC_TEXT,
		joinColumns = @JoinColumn(name = ApplicationCategoryBMPBean.TABLE_NAME + "_ID"),
		inverseJoinColumns = @JoinColumn(name = LocalizedTextBMPBean.TABLE_NAME + "_ID")
	)
	private List<LocalizedText> localizedTexts;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Object getPrimaryKey() {
		return getId();
	}

	@Override
	public String toString() {
		return getPrimaryKey().toString();
	}

	@Override
	public LocalizedTextModel getLocalizedText(int icLocaleId) {
		return ApplicationUtil.getLocalizedText(this, icLocaleId);
	}

	@Override
	public String getName() {
		return ApplicationUtil.getName(this);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getLocalizedName() {
		return ApplicationUtil.getLocalizedName(this, getDefaultName());
	}

	@Override
	public String getDefaultName() {
		return name;
	}

	@Override
	public String getLocalizedName(int icLocaleId) {
		LocalizedText localizedText = ApplicationUtil.getLocalizedText(this, icLocaleId);
		String localizedName = localizedText == null ? null : localizedText.getHeadline();
		if (StringUtil.isEmpty(localizedName)) {
			return getDefaultName();
		}
		return localizedName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LocalizedText> getLocalizedTexts() {
		localizedTexts = DBUtil.getInstance().lazyLoad(localizedTexts);
		return localizedTexts;
	}

	public void setLocalizedTexts(List<LocalizedText> localizedTexts) {
		this.localizedTexts = localizedTexts;
	}

}