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
@Table(name = ApplicationConsultant.TABLE_NAME)
@Cacheable
@NamedQueries({
	@NamedQuery(name = ApplicationConsultant.FIND_BY_IDS, query = "select r from ApplicationConsultant r where r.id in (:" + ApplicationConsultant.PARAM_IDS + ")"),
	@NamedQuery(
			name = ApplicationConsultant.GET_ALL,
			query = "from ApplicationConsultant"
	)
})
public class ApplicationConsultant implements Serializable {
	private static final long serialVersionUID = 4753803128004640450L;

	public static final String	TABLE_NAME = "egov_application_consultant",
								COLUMN_ID = TABLE_NAME + "_id",
								COLUMN_NAME = "name",
								COLUMN_QUANTITY = "quantity",
								COLUMN_PRICE = "price",

								FIND_BY_IDS = "ApplicationConsultant.findByIds",
								GET_ALL = "ApplicationConsultant.getAll",
								PARAM_IDS = "applicationConsultantIds";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = COLUMN_ID)
	private Integer id;

	@Column(name = COLUMN_NAME, nullable = false)
	private String name;

	@Column(name = COLUMN_QUANTITY)
	private Integer quantity;

	@Column(name = COLUMN_PRICE)
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}



}