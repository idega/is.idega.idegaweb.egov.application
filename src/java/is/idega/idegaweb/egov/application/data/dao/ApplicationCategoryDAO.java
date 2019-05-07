package is.idega.idegaweb.egov.application.data.dao;

import com.idega.core.persistence.GenericDao;

import is.idega.idegaweb.egov.application.data.bean.ApplicationCategory;

public interface ApplicationCategoryDAO extends GenericDao {

	public static final String BEAN_NAME = "applicationCategoryDAO";

	public ApplicationCategory getPriorityCategory();

}