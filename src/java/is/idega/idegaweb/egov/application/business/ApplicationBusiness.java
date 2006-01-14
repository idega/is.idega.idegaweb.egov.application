/*
 * $Id: ApplicationBusiness.java,v 1.5 2006/01/14 21:17:26 laddi Exp $
 * Created on Jan 14, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.business;

import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.data.ApplicationCategoryHome;
import is.idega.idegaweb.egov.application.data.ApplicationHome;
import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.block.process.business.CaseBusiness;
import com.idega.business.IBOService;
import com.idega.user.data.User;
import com.idega.util.Age;


/**
 * <p>
 * TODO laddi Describe Type ApplicationBusiness
 * </p>
 *  Last modified: $Date: 2006/01/14 21:17:26 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
 */
public interface ApplicationBusiness extends IBOService, CaseBusiness {

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplication
	 */
	public Application getApplication(String caseCode) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplication
	 */
	public Application getApplication(Object primaryKey) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getMostClickedApplications
	 */
	public Collection getMostClickedApplications(int numberOfEntries) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getUserApplications
	 */
	public Collection getUserApplications(User user) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getAgesForUserAndChildren
	 */
	public Age[] getAgesForUserAndChildren(User user) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#displayApplicationForAges
	 */
	public boolean displayApplicationForAges(Application application, Age[] ages) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplicationHome
	 */
	public ApplicationHome getApplicationHome() throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplicationCategoryHome
	 */
	public ApplicationCategoryHome getApplicationCategoryHome() throws java.rmi.RemoteException;
}
