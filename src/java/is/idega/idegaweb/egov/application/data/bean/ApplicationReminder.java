package is.idega.idegaweb.egov.application.data.bean;

import java.io.Serializable;
import java.sql.Timestamp;
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
import com.idega.user.data.bean.User;
import com.idega.util.DBUtil;

@Entity
@Table(name = ApplicationReminder.TABLE_NAME)
@Cacheable
@NamedQueries({
	@NamedQuery(name = ApplicationReminder.FIND_BY_IDS, query = "select r from ApplicationReminder r where r.id in (:" + ApplicationReminder.PARAM_IDS + ")"),
})
public class ApplicationReminder implements Serializable, ReminderModel {

	private static final long serialVersionUID = -2223620257575509494L;

	public static final String	TABLE_NAME = "egov_application_reminder",
								COLUMN_ID = TABLE_NAME + "_id",

								FIND_BY_IDS = "ApplicationReminder.findByIds",
								PARAM_IDS = "applicationReminderIds";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ID)
	private Integer id;

	@ManyToMany(fetch = FetchType.LAZY, targetEntity = User.class)
	@JoinTable(name = TABLE_NAME + "_rec", joinColumns = { @JoinColumn(name = COLUMN_ID) }, inverseJoinColumns = { @JoinColumn(name = User.COLUMN_USER_ID) })
	private List<User> receivers;

	@Column(name = "timestamp")
	private Timestamp timestamp;

	@Column(name = "message", length = 65000)
	private String message;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public void setReceivers(List<User> receivers) {
		this.receivers = receivers;
	}

	@Override
	public List<User> getReceivers() {
		receivers = DBUtil.getInstance().lazyLoad(receivers);
		return receivers;
	}

	@Override
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public Timestamp getTimestamp() {
		return timestamp;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}