/*
 * $Id: ApplicationHomeImpl.java,v 1.1 2006/01/12 17:04:20 gimmi Exp $
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
 * TODO gimmi Describe Type ApplicationHomeImpl
 * </p>
 *  Last modified: $Date: 2006/01/12 17:04:20 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.1 $
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

	public Collection findAllByCategoryAndCaseCode(ApplicationCategory category, CaseCode caseCode)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByCategoryAndCaseCode(category, caseCode);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllByCategory(ApplicationCategory category) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByCategory(category);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}