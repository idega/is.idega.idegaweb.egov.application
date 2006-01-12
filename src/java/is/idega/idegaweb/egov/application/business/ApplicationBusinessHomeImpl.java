/*
 * $Id: ApplicationBusinessHomeImpl.java,v 1.2 2006/01/12 17:19:31 laddi Exp $
 * Created on Jan 12, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.business;

import com.idega.business.IBOHomeImpl;


/**
 * <p>
 * TODO laddi Describe Type ApplicationBusinessHomeImpl
 * </p>
 *  Last modified: $Date: 2006/01/12 17:19:31 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class ApplicationBusinessHomeImpl extends IBOHomeImpl implements ApplicationBusinessHome {

	protected Class getBeanInterfaceClass() {
		return ApplicationBusiness.class;
	}

	public ApplicationBusiness create() throws javax.ejb.CreateException {
		return (ApplicationBusiness) super.createIBO();
	}
}
