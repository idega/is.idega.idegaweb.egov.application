/*
<<<<<<< ApplicationHomeImpl.java
 * $Id: ApplicationHomeImpl.java,v 1.3 2006/01/12 20:01:31 gimmi Exp $
=======
 * $Id: ApplicationHomeImpl.java,v 1.3 2006/01/12 20:01:31 gimmi Exp $
>>>>>>> 1.2
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
import com.idega.data.IDOFactory;


/**
 * <p>
 * TODO laddi Describe Type ApplicationHomeImpl
 * </p>
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
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
}
