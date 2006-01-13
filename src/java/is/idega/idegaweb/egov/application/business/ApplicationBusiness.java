/*
 * $Id: ApplicationBusiness.java,v 1.4 2006/01/13 02:11:50 gimmi Exp $
 * Created on Jan 13, 2006
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
import java.rmi.RemoteException;
import javax.ejb.FinderException;
import com.idega.block.process.business.CaseBusiness;
import com.idega.business.IBOService;
import com.idega.user.data.User;
import com.idega.util.Age;


/**
 * <p>
 * TODO gimmi Describe Type ApplicationBusiness
 * </p>
 *  Last modified: $Date: 2006/01/13 02:11:50 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.4 $
 */
public interface ApplicationBusiness extends IBOService, CaseBusiness {

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplication
	 */
	public Application getApplication(String caseCode) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getAgesForUserAndChildren
	 */
	public Age[] getAgesForUserAndChildren(User user) throws RemoteException;

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
