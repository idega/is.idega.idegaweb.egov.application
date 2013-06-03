package is.idega.idegaweb.egov.application.data;


import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.process.data.CaseCode;
import com.idega.data.IDOHome;

public interface ApplicationHome extends IDOHome {

	public Application create() throws CreateException;

	public Application findByPrimaryKey(Object pk) throws FinderException;
	
	public Collection<Application> findByMultiplePrimaryKey(Collection<Object> pk) throws FinderException;

	public Application findByCaseCode(CaseCode caseCode) throws FinderException;

	public Application findByCaseCode(String caseCode) throws FinderException;

	public Collection<Application> findAllByCategory(ApplicationCategory category) throws FinderException;

	public Collection<Application> findAll() throws FinderException;

	public Collection<Application> findElectronicApplications() throws FinderException;

	public Collection<Application> findMostClicked(int numberOfEntries) throws FinderException;
	
	public Collection<Application> findAllByCategoryOrderedByPriority(ApplicationCategory category) throws FinderException;
	
	public Application findByCategoryAndPriority(ApplicationCategory category, int priority) throws FinderException;

	public Collection<Application> findAllByApplicationUrl(String appUrl) throws FinderException;
	
	public Collection<Application> findAllByCaseCode(String caseCode) throws FinderException;
	
	public Collection<Application> findAllByGroups(Collection<String> ids) throws FinderException;
	
	public Collection<Application> findAllWithAssignedGroups() throws FinderException;
	
	public Collection<Application> findAllByType(String type) throws FinderException;
}