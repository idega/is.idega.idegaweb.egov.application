/*
 * $Id: Application.java,v 1.2 2006/01/12 17:19:31 laddi Exp $
 * Created on Jan 12, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.data;

import com.idega.block.process.data.CaseCode;
import com.idega.data.IDOEntity;


/**
 * <p>
 * TODO laddi Describe Type Application
 * </p>
 *  Last modified: $Date: 2006/01/12 17:19:31 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public interface Application extends IDOEntity {

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setAgeFrom
	 */
	public void setAgeFrom(int age);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getAgeFrom
	 */
	public int getAgeFrom();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setAgeTo
	 */
	public void setAgeTo(int age);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getAgeTo
	 */
	public int getAgeTo();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setCategory
	 */
	public void setCategory(ApplicationCategory category);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getCategory
	 */
	public ApplicationCategory getCategory();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setCaseCode
	 */
	public void setCaseCode(CaseCode caseCode);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getCaseCode
	 */
	public CaseCode getCaseCode();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setElectronic
	 */
	public void setElectronic(boolean isElectronic);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getElectronic
	 */
	public boolean getElectronic();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setName
	 */
	public void setName(String name);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getName
	 */
	public String getName();

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#setUrl
	 */
	public void setUrl(String url);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationBMPBean#getUrl
	 */
	public String getUrl();
}
