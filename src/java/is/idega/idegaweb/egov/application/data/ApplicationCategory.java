/*
 * $Id: ApplicationCategory.java,v 1.3 2008/06/27 12:23:15 alexis Exp $
 * Created on Jan 12, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.data;

import java.util.Map;

import com.idega.block.text.data.LocalizedText;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOEntity;

import is.idega.idegaweb.egov.application.model.ApplicationCategoryModel;


/**
 * <p>
 * TODO gimmi Describe Type ApplicationCategory
 * </p>
 *  Last modified: $Date: 2008/06/27 12:23:15 $ by $Author: alexis $
 *
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.3 $
 */
public interface ApplicationCategory extends IDOEntity, ApplicationCategoryModel {

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setLocalizedNames
	 */
	public void setLocalizedNames(Map localizedNames);

	public LocalizedText getLocalizedText(int icLocaleId);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#setPriority
	 */
	public void setPriority(Integer priority);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getDefaultName
	 */
	public String getDefaultName();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getNameByLocale
	 */
	public String getLocalizedName();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getPriority
	 */
	public Integer getPriority();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#getNameByLocale
	 */
	public String getLocalizedName(int localeId);

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

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#removeLocalizedTextEntries
	 */
	public void removeLocalizedTextEntries();
}
