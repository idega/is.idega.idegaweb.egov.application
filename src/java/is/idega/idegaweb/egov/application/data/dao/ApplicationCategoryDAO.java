package is.idega.idegaweb.egov.application.data.dao;

import java.util.List;

import com.idega.core.persistence.GenericDao;

import is.idega.idegaweb.egov.application.data.bean.ApplicationCategory;

public interface ApplicationCategoryDAO extends GenericDao {

	public static final String BEAN_NAME = "applicationCategoryDAO";

	public ApplicationCategory getPriorityCategory();
	
	public List<ApplicationCategory> getAll(
			int page,
			int pageSize
	);

}