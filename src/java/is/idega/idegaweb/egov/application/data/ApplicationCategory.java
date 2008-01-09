/*
 * $Id: ApplicationCategory.java,v 1.2 2008/01/09 08:04:59 alexis Exp $
 * Created on Jan 12, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.data;

import com.idega.block.text.data.LocalizedText;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOEntity;


/**
 * <p>
 * TODO gimmi Describe Type ApplicationCategory
 * </p>
 *  Last modified: $Date: 2008/01/09 08:04:59 $ by $Author: alexis $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.2 $
 */
public interface ApplicationCategory extends IDOEntity {

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setName
	 */
	public void setName(String name);
	
	public LocalizedText getLocalizedText(int icLocaleId);
	
	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setPriority
	 */
	public void setPriority(Integer priority);
	
	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getPriority
	 */
	public Integer getPriority();
	
	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#addLocalizedName
	 */
	public void addLocalizedName(LocalizedText text) throws IDOAddRelationshipException;

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
