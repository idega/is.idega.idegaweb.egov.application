/*
 * $Id: ApplicationBusinessHome.java,v 1.3 2006/01/13 02:11:50 gimmi Exp $
 * Created on Jan 13, 2006
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
 * TODO gimmi Describe Type ApplicationBusinessHome
 * </p>
 *  Last modified: $Date: 2006/01/13 02:11:50 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.3 $
 */
public interface ApplicationBusinessHome extends IBOHome {

	public ApplicationBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
