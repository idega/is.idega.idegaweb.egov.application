/*
 * $Id: ApplicationCategoryHome.java,v 1.2 2008/01/09 08:04:59 alexis Exp $
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

import com.idega.data.IDOHome;


/**
 * <p>
 * TODO gimmi Describe Type ApplicationCategoryHome
 * </p>
 *  Last modified: $Date: 2008/01/09 08:04:59 $ by $Author: alexis $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.2 $
 */
public interface ApplicationCategoryHome extends IDOHome {

	public ApplicationCategory create() throws javax.ejb.CreateException;

	/**
	 * 
	 * @param pk is {@link ApplicationCategory#getPrimaryKey()}, 
	 * not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	ApplicationCategory findByPrimaryKey(Object pk);

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#ejbFindAll
	 */
	public Collection<ApplicationCategory> findAll() throws FinderException;

	/**
	 * @see is.idega.idegaweb.egov.application.data.ApplicationCategoryBMPBean#ejbFindAllOrderedByName
	 */
	public Collection<ApplicationCategory> findAllOrderedByName() throws FinderException;
	
	public Collection<ApplicationCategory> findAllOrderedByPriority() throws FinderException;
	
	public ApplicationCategory findByPriority(int priority) throws FinderException;

	/**
	 * 
	 * @param name is {@link ApplicationCategory#getName()}, not <code>null</code>;
	 * @return entity or <code>null</code> on failure;
	 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
	 */
	ApplicationCategory findByName(String name);
}
