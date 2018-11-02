package is.idega.idegaweb.egov.application.data.dao;

import java.util.List;

import com.idega.core.persistence.GenericDao;

import is.idega.idegaweb.egov.application.data.bean.MonthlyApplicationsCount;

public interface MonthlyApplicationsCountDAO extends GenericDao {

	public static final String BEAN_NAME = "monthlyApplicationCountDAO";

	public MonthlyApplicationsCount getById(Integer id);

	public List<MonthlyApplicationsCount> getAllByYearAndMonth(Integer year, Integer month);

	public MonthlyApplicationsCount createMonthlyApplicationsCount(
			Integer year,
			Integer month,
			Integer appId,
			String appName,
			Long count,
			Double percentage,
			String color,
			String abbreviation

	);

	public MonthlyApplicationsCount createMonthlyApplicationsCount(MonthlyApplicationsCount monthlyApplicationsCount);


}