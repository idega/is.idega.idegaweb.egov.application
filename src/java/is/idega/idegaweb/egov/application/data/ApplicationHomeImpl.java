/*
 * $Id: ApplicationHomeImpl.java,v 1.7 2006/03/23 10:30:00 laddi Exp $
 * Created on Mar 23, 2006
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
import com.idega.data.IDOFactory;


/**
 * <p>
 * TODO laddi Describe Type ApplicationHomeImpl
 * </p>
 *  Last modified: $Date: 2006/03/23 10:30:00 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.7 $
 */
public class ApplicationHomeImpl extends IDOFactory implements ApplicationHome {

	protected Class getEntityInterfaceClass() {
		return Application.class;
	}

	public Application create() throws javax.ejb.CreateException {
		return (Application) super.createIDO();
	}

	public Application findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (Application) super.findByPrimaryKeyIDO(pk);
	}

	public Application findByCaseCode(CaseCode caseCode) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ApplicationBMPBean) entity).ejbFindByCaseCode(caseCode);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Application findByCaseCode(String caseCode) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ApplicationBMPBean) entity).ejbFindByCaseCode(caseCode);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllByCategory(ApplicationCategory category) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByCategory(category);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findElectronicApplications() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindElectronicApplications();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findMostClicked(int numberOfEntries) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindMostClicked(numberOfEntries);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

}
