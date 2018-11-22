package is.idega.idegaweb.egov.application.data.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.idega.util.CoreConstants;

@Entity
@Table(name = DecisionTemplate.TABLE_NAME)
@Cacheable
@NamedQueries({
	@NamedQuery(name = DecisionTemplate.FIND_BY_ID, query = "select dt from DecisionTemplate dt where dt.id = :" + DecisionTemplate.PARAM_ID),
	@NamedQuery(name = DecisionTemplate.FIND_BY_IDS, query = "select dt from DecisionTemplate dt where dt.id in (:" + DecisionTemplate.PARAM_IDS + ")"),
	@NamedQuery(name = DecisionTemplate.FIND_BY_APPLICATION_SETTINGS_ID, query = "select dt from DecisionTemplate dt where dt.applicationSettingsId = :" + DecisionTemplate.PARAM_APPLICATION_SETTINGS_ID),
	@NamedQuery(name = DecisionTemplate.FIND_IDS_BY_APPLICATION_SETTINGS_ID, query = "select dt.id from DecisionTemplate dt where dt.applicationSettingsId = :" + DecisionTemplate.PARAM_APPLICATION_SETTINGS_ID),
	@NamedQuery(name = DecisionTemplate.DELETE_BY_IDS, query = "DELETE FROM DecisionTemplate dt WHERE dt.id IN (:" + DecisionTemplate.PARAM_IDS + ")")
})
public class DecisionTemplate implements Serializable {

	private static final long serialVersionUID = 3983491425482184648L;

	public static final String 	TABLE_NAME = "egov_decision_template",
								COLUMN_ID = TABLE_NAME + "_id",
								COLUMN_APPLICATION_SETTINGS_ID = "app_settings_id",
								COLUMN_NAME = "name",
								COLUMN_TEMPLATE_CONTENT = "template_content",
								COLUMN_SIGNATURE = "signature",
								COLUMN_TYPE = "type",
								COLUMN_DEFAULT = "is_default",

								FIND_BY_ID = "DecisionTemplate.findById",
								FIND_BY_APPLICATION_SETTINGS_ID = "DecisionTemplate.findByApplicationSettingsId",
								FIND_IDS_BY_APPLICATION_SETTINGS_ID = "DecisionTemplate.findIdsByApplicationSettingsId",
								FIND_BY_IDS = "DecisionTemplate.findByIds",
								DELETE_BY_IDS = "DecisionTemplate.deleteByIds",

								PARAM_ID = "decisionTemplateId",
								PARAM_APPLICATION_SETTINGS_ID = "applicationSettingsId",
								PARAM_IDS = "decisionTemplateIds";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ID)
	private Integer id;

	@Column(name = DecisionTemplate.COLUMN_APPLICATION_SETTINGS_ID)
	private Integer applicationSettingsId;

	@Column(name = DecisionTemplate.COLUMN_NAME)
	private String name;

	@Column(name = DecisionTemplate.COLUMN_TEMPLATE_CONTENT)
	@Type(type="text")
	private String templateContent;

	@Column(name = DecisionTemplate.COLUMN_SIGNATURE)
	@Lob
	private SignatureProfile signatureProfile;

	@Column(name = DecisionTemplate.COLUMN_TYPE)
	private String type;

	@Column(name = DecisionTemplate.COLUMN_DEFAULT, length = 1)
	private Character isDefault;

	@Column(name = "created")
	private Timestamp created;

	@Column(name = "edited")
	private Timestamp edited;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "edited_by")
	private Integer editedBy;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getApplicationSettingsId() {
		return applicationSettingsId;
	}

	public void setApplicationSettingsId(Integer applicationSettingsId) {
		this.applicationSettingsId = applicationSettingsId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}

	public SignatureProfile getSignatureProfile() {
		return signatureProfile;
	}

	public void setSignatureProfile(SignatureProfile signatureProfile) {
		this.signatureProfile = signatureProfile;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getIsDefault() {
		return isDefault == null ? false : isDefault == CoreConstants.CHAR_Y;
	}

	public void setIsDefault(Character isDefault) {
		this.isDefault = isDefault;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Timestamp getEdited() {
		return edited;
	}

	public void setEdited(Timestamp edited) {
		this.edited = edited;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getEditedBy() {
		return editedBy;
	}

	public void setEditedBy(Integer editedBy) {
		this.editedBy = editedBy;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " ID: " + getId();
	}

}