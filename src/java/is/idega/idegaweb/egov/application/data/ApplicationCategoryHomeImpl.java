/*
 * $Id: ApplicationCategoryHomeImpl.java,v 1.1 2006/01/12 17:04:20 gimmi Exp $
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
import com.idega.data.IDOFactory;


/**
 * <p>
 * TODO gimmi Describe Type ApplicationCategoryHomeImpl
 * </p>
 *  Last modified: $Date: 2006/01/12 17:04:20 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.1 $
 */
public class ApplicationCategoryHomeImpl extends IDOFactory implements ApplicationCategoryHome {

	protected Class getEntityInterfaceClass() {
		return ApplicationCategory.class;
	}

	public ApplicationCategory create() throws javax.ejb.CreateException {
		return (ApplicationCategory) super.createIDO();
	}

	public ApplicationCategory findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (ApplicationCategory) super.findByPrimaryKeyIDO(pk);
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
}
