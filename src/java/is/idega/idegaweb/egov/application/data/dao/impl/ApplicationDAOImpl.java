package is.idega.idegaweb.egov.application.data.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.ArrayUtil;
import com.idega.util.IWTimestamp;

import is.idega.idegaweb.egov.application.data.bean.Application;
import is.idega.idegaweb.egov.application.data.bean.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.dao.ApplicationDAO;

@Repository(ApplicationDAO.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Transactional(readOnly = false)
public class ApplicationDAOImpl extends GenericDaoImpl implements ApplicationDAO {

	@Override
	public List<ApplicationCategory> getAllCategoriesOrderedByPriority() {
		try {
			return getResultListByInlineQuery("select ac from " + ApplicationCategory.class.getName() + " ac order by ac.priority", ApplicationCategory.class);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting application categories", e);
		}
		return Collections.emptyList();
	}

	@Override
	public List<Application> getApplicationsByCategoryOrderedByPriority(ApplicationCategory category) {
		StringBuilder query = null;
		try {
			List<Param> params = new ArrayList<>();
			params.add(new Param("catId", category.getId()));
			query = new StringBuilder("select a from ");
			query.append(Application.class.getName()).append(" a where a.category.id = :catId ");
			if (IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("app.filter_out_disabled", Boolean.FALSE)) {
				query.append(" and ((a.enabledFrom is null and a.enabledTo is null) or (a.enabledFrom <= :timestamp and a.enabledTo >= :timestamp))");
				params.add(new Param("timestamp", IWTimestamp.RightNow().getTimestamp()));
			}
			query.append(" order by a.priority");

			return getResultListByInlineQuery(
					query.toString(),
					Application.class,
					ArrayUtil.convertListToArray(params)
			);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting applications by category " + category + ". Query: " + query, e);
		}
		return Collections.emptyList();
	}

}