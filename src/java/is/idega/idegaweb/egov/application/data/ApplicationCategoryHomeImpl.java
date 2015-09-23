/*
 * $Id: ApplicationCategoryHomeImpl.java,v 1.2 2008/01/09 08:04:59 alexis Exp $
 * Created on Jan 12, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.application.data;

import java.util.Collection;
import java.util.logging.Level;

import javax.ejb.FinderException;

import com.idega.data.IDOFactory;


/**
 * <p>
 * TODO gimmi Describe Type ApplicationCategoryHomeImpl
 * </p>
 *  Last modified: $Date: 2008/01/09 08:04:59 $ by $Author: alexis $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.2 $
 */
public class ApplicationCategoryHomeImpl extends IDOFactory implements ApplicationCategoryHome {

	protected Class getEntityInterfaceClass() {
		return ApplicationCategory.class;
	}

	public ApplicationCategory create() throws javax.ejb.CreateException {
		return (ApplicationCategory) super.createIDO();
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryHome#findByPrimaryKey(java.lang.Object)
	 */
	public ApplicationCategory findByPrimaryKey(Object pk) {
		try {
			return (ApplicationCategory) super.findByPrimaryKeyIDO(pk);
		} catch (FinderException e) {
			java.util.logging.Logger.getLogger(getClass().getName()).log(
					Level.WARNING, "Failed to get " + getEntityInterfaceClass() + 
					" by primary key: " + pk);
		}

		return null;
	}

	public Collection findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationCategoryBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllOrderedByName() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationCategoryBMPBean) entity).ejbFindAllOrderedByName();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public Collection findAllOrderedByPriority() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationCategoryBMPBean) entity).ejbFindAllOrderedByPriority();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public ApplicationCategory findByPriority(int priority) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ApplicationCategoryBMPBean) entity).ejbFindByPriority(priority);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	/*
	 * (non-Javadoc)
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryHome#findByName(java.lang.String)
	 */
	@Override
	public ApplicationCategory findByName(String name) {
		ApplicationCategoryBMPBean entity = (ApplicationCategoryBMPBean) idoCheckOutPooledEntity();
		Integer primaryKey = entity.ejbFindByName(name);
		return findByPrimaryKey(primaryKey);
	}
}
