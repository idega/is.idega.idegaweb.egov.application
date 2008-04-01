package is.idega.idegaweb.egov.application.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import com.idega.block.process.data.CaseCode;
import javax.ejb.FinderException;

public interface ApplicationHome extends IDOHome {
	public Application create() throws CreateException;

	public Application findByPrimaryKey(Object pk) throws FinderException;

	public Application findByCaseCode(CaseCode caseCode) throws FinderException;

	public Application findByCaseCode(String caseCode) throws FinderException;

	public Collection findAllByCategory(ApplicationCategory category)
			throws FinderException;

	public Collection findAll() throws FinderException;

	public Collection findElectronicApplications() throws FinderException;

	public Collection findMostClicked(int numberOfEntries)
			throws FinderException;
}