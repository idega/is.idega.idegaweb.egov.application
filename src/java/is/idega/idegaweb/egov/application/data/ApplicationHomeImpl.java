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

	@Override
	public Application create() throws CreateException {
		return (Application) super.createIDO();
	}

	@Override
	public Application findByPrimaryKey(Object pk) throws FinderException {
		return (Application) super.findByPrimaryKeyIDO(pk);
	}

	@Override
	public Collection<Application> findByMultiplePrimaryKey(Collection pks) throws javax.ejb.FinderException{
		Collection<Application> apps = new ArrayList<Application>();

		for(String pk : (Collection<String>)pks) {
			apps.add(findByPrimaryKey(pk));
		}

		return apps;
	}

	@Override
	public Application findByCaseCode(CaseCode caseCode) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ApplicationBMPBean) entity).ejbFindByCaseCode(caseCode);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Application findByCaseCode(String caseCode) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ApplicationBMPBean) entity).ejbFindByCaseCode(caseCode);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection findAllByCategory(ApplicationCategory category) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByCategory(category);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ApplicationBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findElectronicApplications() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ApplicationBMPBean) entity).ejbFindElectronicApplications();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findMostClicked(int numberOfEntries) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((ApplicationBMPBean) entity).ejbFindMostClicked(numberOfEntries);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findAllByCategoryOrderedByPriority(ApplicationCategory category) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByCategoryOrderedByPriority(category);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Application findByCategoryAndPriority(ApplicationCategory category, int priority) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ApplicationBMPBean) entity).ejbFindByCategoryAndPriority(category, priority);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	@Override
	public Collection findAllByApplicationUrl(String appUrl) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByApplicationUrl(appUrl);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection findAllByCaseCode(String caseCode) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByCaseCode(caseCode);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Application> findAllByCasesIds(Collection<Integer> casesIds) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection<?> ids = ((ApplicationBMPBean) entity).ejbFindAllByCasesIds(casesIds);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Application> findAllByGroups(Collection<String> groupsIds) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByGroups(groupsIds);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Application> findAllWithAssignedGroups() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllWithAssignedGroups();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Application> findAllByType(String type) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllByType(type);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	@Override
	public Collection<Application> findAllWithPaymentRequired() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((ApplicationBMPBean) entity).ejbFindAllWithPaymentRequired();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}