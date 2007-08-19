/*
 * $Id: ApplicationCategory.java,v 1.1.2.1 2007/08/19 15:42:33 justinas Exp $
 * Created on Jan 12, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.data;

import java.util.Map;

import com.idega.data.IDOEntity;


/**
 * <p>
 * TODO gimmi Describe Type ApplicationCategory
 * </p>
 *  Last modified: $Date: 2007/08/19 15:42:33 $ by $Author: justinas $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.1.2.1 $
 */
public interface ApplicationCategory extends IDOEntity {

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setLocalizedNames
	 */
	public void setLocalizedNames(Map localizedNames);
	
	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getName
	 */
	public String getName();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getDefaultName
	 */
	public String getDefaultName();
	
	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getNameByLocale
	 */
	public String getLocalizedName();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getNameByLocale
	 */
	public String getLocalizedName(int localeId);
	
	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setDescription
	 */
	public void setDescription(String description);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getDescription
	 */
	public String getDescription();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#removeLocalizedTextEntries
	 */
	public void removeLocalizedTextEntries();
}
