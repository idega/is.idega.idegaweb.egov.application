package is.idega.idegaweb.egov.application.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.idega.user.data.GroupBMPBean;
import com.idega.user.data.bean.Group;

@Entity
@Table(name = Application.TABLE_NAME)
@Cacheable
@NamedQueries({
	@NamedQuery(name = ApplicationAccess.QUERY_GET_ALL, query = "from is.idega.idegaweb.egov.application.data.bean.ApplicationAccess aa"),
	@NamedQuery(name = ApplicationAccess.QUERY_GET_BY_ID, query = "from is.idega.idegaweb.egov.application.data.bean.ApplicationAccess aa where aa.id = :id order by aa.level"),
	@NamedQuery(name = ApplicationAccess.QUERY_GET_BY_APPLICATION_ID, query = "from is.idega.idegaweb.egov.application.data.bean.ApplicationAccess aa where aa.applicationId = :applicationId order by aa.level"),
	@NamedQuery(
			name = ApplicationAccess.QUERY_GET_BY_APPLICATION_ID_AND_GROUP_ID,
			query = "from is.idega.idegaweb.egov.application.data.bean.ApplicationAccess aa where aa.applicationId = :applicationId and aa.groupId = :groupId"
	),
	@NamedQuery(
			name = ApplicationAccess.QUERY_GET_APPLICATIONS_IDS_BY_GROUPS_IDS,
			query = "select distinct aa.applicationId from is.idega.idegaweb.egov.application.data.bean.ApplicationAccess aa where aa.groupId in (:groupsIds)"
	)
})
public class ApplicationAccess implements Serializable {

	private static final long serialVersionUID = -6823563722830804602L;

	public static final String	TABLE_NAME = Application.TABLE_NAME + "_ACCESS",
								QUERY_GET_ALL = "ApplicationAccess.getAll",
								QUERY_GET_BY_ID = "ApplicationAccess.getById",
								QUERY_GET_BY_APPLICATION_ID = "ApplicationAccess.getByApplicationId",
								QUERY_GET_BY_APPLICATION_ID_AND_GROUP_ID = "ApplicationAccess.getByApplicationIdAndGroupId",
								QUERY_GET_APPLICATIONS_IDS_BY_GROUPS_IDS = "ApplicationAccess.getApplicationsIdsByGroupsIds";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = ApplicationAccess.TABLE_NAME + "_ID")
	private Long id;

	@Column(name = Application.TABLE_NAME + "_ID")
	private Integer applicationId;

	@Column(name = GroupBMPBean.COLUMN_GROUP_ID)
	private Integer groupId;

	@Column(name = GroupBMPBean.COLUMN_GROUP_ID, insertable = false, updatable = false)
	private Group group;

	@Column(name = "level")
	private Integer level;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

}