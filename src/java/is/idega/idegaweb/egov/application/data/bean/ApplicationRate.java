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

@Entity
@Table(name = ApplicationRate.TABLE_NAME)
@Cacheable
@NamedQueries({
	@NamedQuery(name = ApplicationRate.FIND_BY_IDS, query = "select r from ApplicationRate r where r.id in (:" + ApplicationRate.PARAM_IDS + ")"),
	@NamedQuery(
			name = ApplicationRate.GET_ALL,
			query = "from ApplicationRate"
	)
})
public class ApplicationRate implements Serializable {
	private static final long serialVersionUID = 2475741041508888354L;

	public static final String	TABLE_NAME = "egov_application_rate",
								COLUMN_ID = TABLE_NAME + "_id",
								COLUMN_RATE_NAME = "name",
								COLUMN_RATE_PRICE = "price",

								FIND_BY_IDS = "ApplicationRate.findByIds",
								GET_ALL = "ApplicationRate.getAll",
								PARAM_IDS = "applicationRateIds";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ID)
	private Integer id;

	@Column(name = COLUMN_RATE_NAME, nullable = false)
	private String name;

	@Column(name = COLUMN_RATE_PRICE)
	private Double price;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}



}