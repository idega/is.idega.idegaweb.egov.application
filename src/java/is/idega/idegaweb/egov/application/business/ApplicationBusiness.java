/*
 * $Id: ApplicationBusiness.java,v 1.2 2006/01/12 17:19:31 laddi Exp $
 * Created on Jan 12, 2006
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
import javax.ejb.FinderException;
import com.idega.business.IBOService;


/**
 * <p>
 * TODO laddi Describe Type ApplicationBusiness
 * </p>
 *  Last modified: $Date: 2006/01/12 17:19:31 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public interface ApplicationBusiness extends IBOService {

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplication
	 */
	public Application getApplication(String caseCode) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplicationHome
	 */
	public ApplicationHome getApplicationHome() throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplicationCategoryHome
	 */
	public ApplicationCategoryHome getApplicationCategoryHome() throws java.rmi.RemoteException;
}
