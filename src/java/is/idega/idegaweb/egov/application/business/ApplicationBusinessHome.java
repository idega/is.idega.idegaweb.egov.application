/*
 * $Id: ApplicationBusinessHome.java,v 1.5 2006/01/16 10:04:13 laddi Exp $
 * Created on Jan 16, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.business;

import com.idega.business.IBOHome;


/**
 * <p>
 * TODO laddi Describe Type ApplicationBusinessHome
 * </p>
 *  Last modified: $Date: 2006/01/16 10:04:13 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
 */
public interface ApplicationBusinessHome extends IBOHome {

	public ApplicationBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
