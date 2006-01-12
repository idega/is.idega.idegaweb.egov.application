/*
 * $Id: ApplicationBusiness.java,v 1.1 2006/01/12 17:11:32 gimmi Exp $
 * Created on Jan 12, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.business;

import is.idega.idegaweb.egov.application.data.ApplicationCategory;
import is.idega.idegaweb.egov.application.data.ApplicationCategoryHome;
import is.idega.idegaweb.egov.application.data.ApplicationHome;
import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.business.IBOService;


/**
 * <p>
 * TODO gimmi Describe Type ApplicationBusiness
 * </p>
 *  Last modified: $Date: 2006/01/12 17:11:32 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.1 $
 */
public interface ApplicationBusiness extends IBOService {

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplications
	 */
	public Collection getApplications(ApplicationCategory category, String caseCode) throws FinderException,
			java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplicationHome
	 */
	public ApplicationHome getApplicationHome() throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.application.business.ApplicationBusinessBean#getApplicationCategoryHome
	 */
	public ApplicationCategoryHome getApplicationCategoryHome() throws java.rmi.RemoteException;
}
