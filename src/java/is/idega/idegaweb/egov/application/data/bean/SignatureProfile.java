package is.idega.idegaweb.egov.application.data.bean;

import java.io.Serializable;

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

import com.idega.core.file.data.bean.ICFile;

@Entity
@Table(name = SignatureProfile.TABLE_NAME)
@Cacheable
@NamedQueries({
	@NamedQuery(name = SignatureProfile.FIND_BY_ID, query = "select s from SignatureProfile s where s.id = :" + SignatureProfile.PARAM_ID),
	@NamedQuery(name = SignatureProfile.FIND_BY_IDS, query = "select s from SignatureProfile s where s.id in (:" + SignatureProfile.PARAM_IDS + ")"),
	@NamedQuery(name = SignatureProfile.FIND_BY_APPLICATION_SETTINGS_ID, query = "select s from SignatureProfile s where s.applicationSettingsId = :" + SignatureProfile.PARAM_APPLICATION_SETTINGS_ID),
	@NamedQuery(name = SignatureProfile.FIND_IDS_BY_APPLICATION_SETTINGS_ID, query = "select s.id from SignatureProfile s where s.applicationSettingsId = :" + SignatureProfile.PARAM_APPLICATION_SETTINGS_ID),
	@NamedQuery(name = SignatureProfile.DELETE_BY_IDS, query = "DELETE FROM SignatureProfile s WHERE s.id IN (:" + SignatureProfile.PARAM_IDS + ")")
})
public class SignatureProfile implements Serializable {
	private static final long serialVersionUID = 8345581266897727172L;

	public static final String 	TABLE_NAME = "egov_signature_profile",
								COLUMN_ID = TABLE_NAME + "_id",
								COLUMN_APPLICATION_SETTINGS_ID = "app_settings_id",
								COLUMN_NAME = "name",
								COLUMN_ROLE_TITLE = "role_title",
								COLUMN_INFORMATION = "information",
								COLUMN_SIGNATURE = "signature",

								FIND_BY_ID = "SignatureProfile.findById",
								FIND_BY_APPLICATION_SETTINGS_ID = "SignatureProfile.findByApplicationSettingsId",
								FIND_IDS_BY_APPLICATION_SETTINGS_ID = "SignatureProfile.findIdsByApplicationSettingsId",
								FIND_BY_IDS = "SignatureProfile.findByIds",
								DELETE_BY_IDS = "SignatureProfile.deleteByIds",

								PARAM_ID = "signatureProfileId",
								PARAM_APPLICATION_SETTINGS_ID = "applicationSettingsId",
								PARAM_IDS = "signatureProfileIds";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ID)
	private Integer id;

	@Column(name = SignatureProfile.COLUMN_APPLICATION_SETTINGS_ID)
	private Integer applicationSettingsId;

	@Column(name = SignatureProfile.COLUMN_NAME)
	private String name;

	@Column(name = SignatureProfile.COLUMN_ROLE_TITLE)
	private String roleTitle;

	@Column(name = SignatureProfile.COLUMN_INFORMATION)
	private String information;

	@Column(name = SignatureProfile.COLUMN_SIGNATURE)
	@Lob
	private ICFile signature;



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

	public String getRoleTitle() {
		return roleTitle;
	}

	public void setRoleTitle(String roleTitle) {
		this.roleTitle = roleTitle;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public ICFile getSignature() {
		return signature;
	}

	public void setSignature(ICFile signature) {
		this.signature = signature;
	}




}