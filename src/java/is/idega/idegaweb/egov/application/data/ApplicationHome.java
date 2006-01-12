/*
 * $Id: ApplicationHome.java,v 1.1 2006/01/12 17:04:20 gimmi Exp $
 * Created on Jan 12, 2006
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
 * TODO gimmi Describe Type ApplicationHome
 * </p>
 *  Last modified: $Date: 2006/01/12 17:04:20 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.1 $
 */
public interface ApplicationHome extends IDOHome {

	public Application create() throws javax.ejb.CreateException;

	public Application findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#ejbFindAllByCategoryAndCaseCode
	 */
	public Collection findAllByCategoryAndCaseCode(ApplicationCategory category, CaseCode caseCode)
			throws FinderException;

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#ejbFindAllByCategory
	 */
	public Collection findAllByCategory(ApplicationCategory category) throws FinderException;
}
