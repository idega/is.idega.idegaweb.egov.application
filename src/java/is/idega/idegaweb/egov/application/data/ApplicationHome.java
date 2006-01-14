/*
 * $Id: ApplicationHome.java,v 1.4 2006/01/14 21:17:26 laddi Exp $
 * Created on Jan 14, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.block.process.data.CaseCode;
import com.idega.data.IDOHome;


/**
 * <p>
 * TODO laddi Describe Type ApplicationHome
 * </p>
 *  Last modified: $Date: 2006/01/14 21:17:26 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.4 $
 */
public interface ApplicationHome extends IDOHome {

	public Application create() throws javax.ejb.CreateException;

	public Application findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

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
	public Collection findAllByCategory(ApplicationCategory category) throws FinderException;

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
	public Collection findMostClicked(int numberOfEntries) throws FinderException;
}
