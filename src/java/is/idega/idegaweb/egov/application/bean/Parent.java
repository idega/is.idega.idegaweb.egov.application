package is.idega.idegaweb.egov.application.bean;

import java.util.Date;

public class Parent extends Applicant {

	private String workPlace;

	private String relation;
	private String maritalStatus;

	public Parent() {}
	
	public Parent(String id, String personalID, String name, Date dateOfBirth) {
		super(id, personalID, name, dateOfBirth);
	}
	
	public String getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
}