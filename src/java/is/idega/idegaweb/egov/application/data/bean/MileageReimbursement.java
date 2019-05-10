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
@Table(name = MileageReimbursement.TABLE_NAME)
@Cacheable
@NamedQueries({
	@NamedQuery(name = MileageReimbursement.FIND_BY_IDS, query = "select r from MileageReimbursement r where r.id in (:" + MileageReimbursement.PARAM_IDS + ")"),
	@NamedQuery(
			name = MileageReimbursement.GET_ALL,
			query = "from MileageReimbursement"
	)
})
public class MileageReimbursement implements Serializable {
	private static final long serialVersionUID = -1703676422761262088L;

	public static final String	TABLE_NAME = "egov_application_mileage",
								COLUMN_ID = TABLE_NAME + "_id",
								COLUMN_NAME = "name",
								COLUMN_PRICE = "price",
								COLUMN_RATE_TYPE = "type",

								FIND_BY_IDS = "MileageReimbursement.findByIds",
								GET_ALL = "MileageReimbursement.getAll",
								PARAM_IDS = "mileageReimbursementIds";

	public static final String MILEAGE_RATE_KM = "KM";
	public static final String MILEAGE_RATE_MIL = "Mil";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ID)
	private Integer id;

	@Column(name = COLUMN_NAME, nullable = false)
	private String name;

	@Column(name = COLUMN_PRICE)
	private Double price;

	@Column(name = COLUMN_RATE_TYPE, nullable = false)
	private String rateType;

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

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}



}