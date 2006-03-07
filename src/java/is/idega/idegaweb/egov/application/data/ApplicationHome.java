/**
 * 
 */
package is.idega.idegaweb.egov.application.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.block.process.data.CaseCode;
import com.idega.data.IDOHome;

/**
 * @author gimmi
 *
 */
public interface ApplicationHome extends IDOHome {
	public Application create() throws javax.ejb.CreateException;

	public Application findByPrimaryKey(Object pk)
			throws javax.ejb.FinderException;

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#ejbFindByCaseCode
	 */
	public Application findByCaseCode(CaseCode caseCode) throws FinderException;

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#ejbFindByCaseCode
	 */
	public Application findByCaseCode(String caseCode) throws FinderException;

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#ejbFindAllByCategory
	 */
	public Collection findAllByCategory(ApplicationCategory category)
			throws FinderException;

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#ejbFindElectronicApplications
	 */
	public Collection findElectronicApplications() throws FinderException;

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#ejbFindMostClicked
	 */
	public Collection findMostClicked(int numberOfEntries)
			throws FinderException;

}
