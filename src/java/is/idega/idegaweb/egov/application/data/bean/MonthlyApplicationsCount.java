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

import is.idega.idegaweb.egov.application.ApplicationConstants;

@Entity
@Table(
		name = MonthlyApplicationsCount.TABLE_NAME
)
@Cacheable
@NamedQueries({
	@NamedQuery(name = MonthlyApplicationsCount.FIND_BY_ID, query = "select ma from MonthlyApplicationsCount ma where ma.id = :" + MonthlyApplicationsCount.PARAM_ID),
	@NamedQuery(name = MonthlyApplicationsCount.FIND_BY_YEAR_AND_MONTH, query = "select ma from MonthlyApplicationsCount ma where ma.year = :" + MonthlyApplicationsCount.PARAM_YEAR
				+ " AND ma.month = :" + MonthlyApplicationsCount.PARAM_MONTH
				+ " and ma.appName != '" + ApplicationConstants.SUPPORT_ISSUE_PROCESS_NAME  + "'"),
	@NamedQuery(name = MonthlyApplicationsCount.FIND_ALL, query = "select ma from MonthlyApplicationsCount ma WHERE ma.appName != '" + ApplicationConstants.SUPPORT_ISSUE_PROCESS_NAME  + "'")
})
public class MonthlyApplicationsCount implements Serializable {

	private static final long serialVersionUID = 6736624426272959588L;

	public static final String 	TABLE_NAME = "monthly_applications_count",

								COLUMN_ID = TABLE_NAME + "_id",
								COLUMN_YEAR = "year",
								COLUMN_MONTH = "month",
								COLUMN_APP_ID = "app_id",
								COLUMN_APP_NAME = "app_name",
								COLUMN_COUNT = "count",
								COLUMN_COLOR = "color",
								COLUMN_PERCENTAGE = "percentage",
								COLUMN_ABBREVIATION = "abbreviation",

								FIND_BY_ID = "MonthlyApplicationsCount.findById",
								FIND_BY_YEAR_AND_MONTH = "MonthlyApplicationsCount.findByYearAndMonth",
								FIND_ALL = "MonthlyApplicationsCount.findAll",

								PARAM_ID = "monthlyApplicationsId",
								PARAM_YEAR = "year",
								PARAM_MONTH = "month";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ID)
	private Integer id;

	@Column(name = MonthlyApplicationsCount.COLUMN_YEAR, nullable = false)
	private Integer year;

	@Column(name = MonthlyApplicationsCount.COLUMN_MONTH, nullable = false)
	private Integer month;

	@Column(name = MonthlyApplicationsCount.COLUMN_APP_ID)
	private Integer appId;

	@Column(name = MonthlyApplicationsCount.COLUMN_APP_NAME)
	private String appName;

	@Column(name = MonthlyApplicationsCount.COLUMN_COUNT)
	private Long count;

	@Column(name = MonthlyApplicationsCount.COLUMN_PERCENTAGE)
	private Double percentage;

	@Column(name = MonthlyApplicationsCount.COLUMN_COLOR)
	private String color;

	@Column(name = MonthlyApplicationsCount.COLUMN_ABBREVIATION)
	private String abbreviation;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}



}