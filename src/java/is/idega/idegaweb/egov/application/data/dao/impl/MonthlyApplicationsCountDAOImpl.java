package is.idega.idegaweb.egov.application.data.dao.impl;

import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.idega.core.persistence.Param;
import com.idega.core.persistence.impl.GenericDaoImpl;

import is.idega.idegaweb.egov.application.data.bean.MonthlyApplicationsCount;
import is.idega.idegaweb.egov.application.data.dao.MonthlyApplicationsCountDAO;

@Repository(MonthlyApplicationsCountDAO.BEAN_NAME)
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Transactional(readOnly = false)
public class MonthlyApplicationsCountDAOImpl extends GenericDaoImpl implements MonthlyApplicationsCountDAO {


	@Override
	public MonthlyApplicationsCount getById(Integer id) {
		if (id == null) {
			return null;
		}

		MonthlyApplicationsCount mac = find(MonthlyApplicationsCount.class, id);
		return mac;
	}

	@Override
	public List<MonthlyApplicationsCount> getAllByYearAndMonth(Integer year, Integer month) {
		if (year == null || month == null) {
			return null;
		}

		try {
			return getResultList(MonthlyApplicationsCount.FIND_BY_YEAR_AND_MONTH,
								 MonthlyApplicationsCount.class,
								 new Param(MonthlyApplicationsCount.PARAM_YEAR, year),
								 new Param(MonthlyApplicationsCount.PARAM_MONTH, month));
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error getting monthy application counts by year: " + year + " and month: " + month, e);
		}

		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public MonthlyApplicationsCount createMonthlyApplicationsCount(Integer year,
															  Integer month,
															  Integer appId,
															  String appName,
															  Long count,
															  Double percentage,
															  String color,
															  String abbreviation
	) {
		MonthlyApplicationsCount monthlyApplicationsCount = new MonthlyApplicationsCount();
		monthlyApplicationsCount.setYear(year);
		monthlyApplicationsCount.setMonth(month);
		monthlyApplicationsCount.setAppId(appId);
		monthlyApplicationsCount.setAppName(appName);
		monthlyApplicationsCount.setCount(count);
		monthlyApplicationsCount.setPercentage(percentage);
		monthlyApplicationsCount.setColor(color);
		monthlyApplicationsCount.setAbbreviation(abbreviation);

		persist(monthlyApplicationsCount);

		return monthlyApplicationsCount;
	}


	@Override
	@Transactional(readOnly = false)
	public MonthlyApplicationsCount createMonthlyApplicationsCount(MonthlyApplicationsCount monthlyApplicationsCount) {
		if (monthlyApplicationsCount != null) {
			persist(monthlyApplicationsCount);
		}

		return monthlyApplicationsCount;
	}

}
