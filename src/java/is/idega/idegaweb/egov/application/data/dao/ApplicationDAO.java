package is.idega.idegaweb.egov.application.data.dao;

import java.util.List;

import com.idega.core.persistence.GenericDao;

import is.idega.idegaweb.egov.application.data.bean.Application;
import is.idega.idegaweb.egov.application.data.bean.ApplicationCategory;

public interface ApplicationDAO extends GenericDao {

	public static final String BEAN_NAME = "egovApplicationDAO";

	List<ApplicationCategory> getAllCategoriesOrderedByPriority();

	List<Application> getApplicationsByCategoryOrderedByPriority(ApplicationCategory category);

}