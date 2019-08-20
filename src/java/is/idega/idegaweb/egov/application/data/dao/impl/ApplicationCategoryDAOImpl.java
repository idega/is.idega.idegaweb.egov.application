package is.idega.idegaweb.egov.application.data.dao.impl;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.util.ListUtil;

import is.idega.idegaweb.egov.application.data.bean.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.dao.ApplicationCategoryDAO;

@Repository(ApplicationCategoryDAO.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Transactional(readOnly = false)
public class ApplicationCategoryDAOImpl extends GenericDaoImpl implements ApplicationCategoryDAO {

	@Override
	public ApplicationCategory getPriorityCategory() {
		List<ApplicationCategory> results = getResultList(
				ApplicationCategory.QUERY_GET_CATEGORY_ORDERED_BY_PRIORITY_ASC, 
				ApplicationCategory.class
		);
		if(ListUtil.isEmpty(results)) {
			return null;
		}
		return results.get(0);
	}
	
	public List<ApplicationCategory> getAll(
			int page,
			int pageSize
	) {
		return getResultList(
				ApplicationCategory.QUERY_GET_CATEGORY_ORDERED_BY_PRIORITY_ASC, 
				ApplicationCategory.class,
				page*pageSize,
				pageSize,
				null
		);
	}

}