/*
 * $Id: ApplicationCategory.java,v 1.1 2006/01/12 17:04:20 gimmi Exp $
 * Created on Jan 12, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.data;

import com.idega.data.IDOEntity;


/**
 * <p>
 * TODO gimmi Describe Type ApplicationCategory
 * </p>
 *  Last modified: $Date: 2006/01/12 17:04:20 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.1 $
 */
public interface ApplicationCategory extends IDOEntity {

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getName
	 */
	public String getName();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setDescription
	 */
	public void setDescription(String description);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getDescription
	 */
	public String getDescription();
}
