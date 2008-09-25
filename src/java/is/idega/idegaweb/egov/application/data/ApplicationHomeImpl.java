package is.idega.idegaweb.egov.application.data;


import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.process.data.CaseCode;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class ApplicationHomeImpl extends IDOFactory implements ApplicationHome {

	@Override
	public Class getEntityInterfaceClass() {
		return Application.class;
	}

	public Application create() throws CreateException {
		return (Application) super.createIDO();
	}

	public Application findByPrimaryKey(Object pk) throws FinderException {
		return (Application) super.findByPrimaryKeyIDO(pk);
	}
	
	public Collection<Application> findByMultiplePrimaryKey(Collection pks) throws javax.ejb.FinderException{
		Collection<Application> apps = new ArrayList<Application>();
		
		for(String pk : (Collection<String>)pks) {
			apps.add(findByPrimaryKey(pk));
		}
		
		return apps;
	}

	public Application findByCaseCode(CaseCode caseCode) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ApplicationBMPBean) entity).ejbFindByCaseCode(caseCode);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Application findByCaseCode(String caseCode) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ApplicationBMPBean) entity).ejbFindByCaseCode(caseCode);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllByCategory(ApplicationCategory category) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByCategory(category);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ApplicationBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findElectronicApplications() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ApplicationBMPBean) entity).ejbFindElectronicApplications();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findMostClicked(int numberOfEntries) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ApplicationBMPBean) entity).ejbFindMostClicked(numberOfEntries);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public Collection findAllByCategoryOrderedByPriority(ApplicationCategory category) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByCategoryOrderedByPriority(category);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public Application findByCategoryAndPriority(ApplicationCategory category, int priority) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ApplicationBMPBean) entity).ejbFindByCategoryAndPriority(category, priority);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllByApplicationUrl(String appUrl) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByApplicationUrl(appUrl);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}